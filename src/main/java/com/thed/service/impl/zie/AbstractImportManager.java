package com.thed.service.impl.zie;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.thed.model.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileFilter;
import org.apache.commons.vfs.FileFilterSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSelector;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;

import com.thed.util.Constants;
import com.thed.service.zie.ImportManager;
import com.thed.util.ObjectUtil;

public abstract class AbstractImportManager implements ImportManager {
    private final static Log log = LogFactory.getLog(AbstractImportManager.class);
	//To fetch testcase priority, requirementPriority preferences
	protected static final int EXTRA_ROWS_IN_END = 2;
	private FileSystemManager fsManager = null ;
	public boolean importAllFiles(ImportJob importJob, String action,Long userId)throws Exception {
		try{
		String folderName = importJob.getFolder();
        if(StringUtils.isEmpty(folderName) || !new File(folderName).exists()){
            throw new FileNotFoundException("Invalid fileName " + folderName);
        }
		fsManager = VFS.getManager();
		
		String path = null;
		if(folderName.charAt(0)!='\\') // check to check if supplied path is absoulte path or not (VFS require absolute path)
			   path ="\\"+folderName;
			else 
				path =folderName ;
		FileObject fileObj=fsManager.resolveFile(path);
		FileObject[] files = null;
		boolean isSuccess = false;
		if(fileObj.getType().toString().equalsIgnoreCase("folder")){
			files =getAllExcelFile(fileObj);
		}else {
			files = new FileObject[1];
			files[0] = fileObj;
		}

		boolean currentResult = false;
		boolean lastResult = true;
		boolean allImportFails = false;
		//Initializing the LOVs
		//TODO: externilize this to a cache
		ObjectUtil.loadLOVs();
			if (isFieldMapInputvalid(importJob)) {
				if (files.length > 0) {
					for (FileObject file : files) {
						if (file.getType().toString().equalsIgnoreCase("folder")) {
							continue;
						}
						String prev = file.getParent().toString()
								+ File.separator + "success" + File.separator
								+ file.getName().getBaseName();
						FileObject temp = fsManager.resolveFile(prev);
						if (!(file.getType().toString().equalsIgnoreCase("folder"))	&& !temp.exists()) {
							currentResult = importSingleFiles(file, importJob,action, userId);
							lastResult = isImportSuccess(currentResult,lastResult);
							allImportFails = isAllImportsFails(currentResult,allImportFails);
						} else {
							addJobHistory(importJob, file+ " already processed");
						}

					}
				} else {
					addJobHistory(importJob, "No files exist.");
				}
			}
		
		if(files!=null && files.length > 1){
			if(lastResult ==true && allImportFails == true){
				importJob.setStatus(Constants.IMPORT_JOB_IMPORT_SUCCESS);
				importJob.setTreeId(null);
				isSuccess = true;
			}else if(lastResult == false && allImportFails == true){
				importJob.setStatus(Constants.IMPORT_JOB_IMPORT_PARTIAL_SUCCESS);
				isSuccess = true;
			}
			if(!allImportFails){
				importJob.setStatus(Constants.IMPORT_JOB_IMPORT_FAILED);
				isSuccess = false;
			}
		}else{
			if(currentResult){
				isSuccess=true;
				importJob.setTreeId(null);
			}
		}

		return isSuccess;
		}catch(Exception e){
			addJobHistory(importJob, "Exception while performing job " + e.getMessage());
            log.fatal("", e);
			throw e;
		}
	}

	protected boolean isImportSuccess(boolean currentResult,boolean lastResult){
		if(currentResult == true && lastResult == true)
		   return  true;
		else
			return false;
	}

	protected boolean isAllImportsFails(boolean currentResult,boolean lastResult){
		if(currentResult || lastResult)
		   return  true;
		else
			return false;
	}

	public boolean importSingleFiles(FileObject file, ImportJob importJob,String action,Long userId) throws Exception {
		boolean isValidFile=false;
		if(importJob.getFieldMap().getDiscriminator().equalsIgnoreCase(Constants.BY_EMPTY_ROW)){
			isValidFile = validateFileByEmptyRow(file, importJob);
		}
		if(importJob.getFieldMap().getDiscriminator().equalsIgnoreCase(Constants.BY_ID_CHANGE)){
			isValidFile = validateFileByIdChange(file, importJob);
		}
		if(importJob.getFieldMap().getDiscriminator().equalsIgnoreCase(Constants.BY_TESTCASE_NAME_CHANGE)){
			isValidFile = validateFileByNameChange(file, importJob);
		}
		boolean isFileProcessed = false;

		if (isValidFile) {
			
			importJob.setStatus(Constants.IMPORT_JOB_NORMALIZATION_SUCCESS);
			addJobHistory(importJob, file.getName()+" normalization success..!");

			if(importJob.getFieldMap().getDiscriminator().equalsIgnoreCase(Constants.BY_EMPTY_ROW)){
				isFileProcessed = importFileByEmptyRow(file, importJob,userId);
			}
			if(importJob.getFieldMap().getDiscriminator().equalsIgnoreCase(Constants.BY_ID_CHANGE)){
				isFileProcessed = importFileById(file, importJob,userId);
			}
			if(importJob.getFieldMap().getDiscriminator().equalsIgnoreCase(Constants.BY_TESTCASE_NAME_CHANGE)){
				isFileProcessed = importFileForByName(file, importJob,userId);
			}
		} else {
			return isValidFile;
		}
		if(isFileProcessed){
			importJob.setStatus(Constants.IMPORT_JOB_IMPORT_SUCCESS);
			String  successFileLocation = file.getParent().toString()+File.separator+"success";
			FileObject newObj = fsManager.resolveFile(successFileLocation);
			if (!newObj.exists()) {
				newObj.createFolder();
			}
			String successFilePath = successFileLocation + File.separator+ file.getName().getBaseName();
			copyFile(file, successFilePath);
			addJobHistory(importJob, file.getName().getBaseName()+" imported successfully..!" );
		}else{
			importJob.setStatus(Constants.IMPORT_JOB_IMPORT_FAILED);
			addJobHistory(importJob, file.getName().getBaseName()+" imported failed..!");
		}

		return isFileProcessed;
	}
	
	protected boolean importFileForByName(FileObject file, ImportJob importJob, Long userId)throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	protected boolean importFileById(FileObject file, ImportJob importJob, Long userId)throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	protected boolean importFileByEmptyRow(FileObject file, ImportJob importJob, Long userId)throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	protected boolean validateFileByNameChange(FileObject file, ImportJob importJob)throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	protected boolean validateFileByIdChange(FileObject file, ImportJob importJob) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	protected boolean validateFileByEmptyRow(FileObject file, ImportJob importJob)throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	protected String getUniqueID(Row row,short externalIdColumn) {
		String uniID=null;;
		if(row.getCell(externalIdColumn)!=null){
			if(row.getCell(externalIdColumn).getCellType()==Cell.CELL_TYPE_STRING){
				uniID=row.getCell(externalIdColumn).getStringCellValue();
			}else if (row.getCell(externalIdColumn).getCellType()==Cell.CELL_TYPE_NUMERIC){
					uniID=""+row.getCell(externalIdColumn).getNumericCellValue();
			}
		}
		return uniID;
	}

	/**
	 * This methods attempts to parse the dateString into date using HSSFDateUtil
	 * If that fails, then it attempst to parse String as yyyy:MM:dd 
	 * if both parsing fails, it returns today's date
	 * @param createdOnValue
	 * @return
	 */
	protected Date convertStringToDate(String createdOnValue) {
		Date creationDate = null;
		if (createdOnValue != null) {
			if(creationDate == null){
				try{
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					creationDate = dateFormat.parse(createdOnValue);
				}catch(Exception e){}
			}
			if(creationDate == null){
				try{
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd");
					creationDate = dateFormat.parse(createdOnValue);
				}catch(Exception e){}
			}
			try{
				//If excel has dateType column, then POI retrieves it as long 
				creationDate = HSSFDateUtil.getJavaDate(Double.valueOf(createdOnValue));
			}catch(Exception ex){}
		}
		return creationDate!=null?creationDate:new Date();
	}

	/**
	 * Optimization, loads preferences once at start of import.
	 * 
	 * @param columns
	 * @return
	 */
	protected void loadCustomPreferences(Map<String, ColumnValueHolder> columns) {
		
		for (Map.Entry<String, ColumnValueHolder> entry: columns.entrySet()) {
			ColumnValueHolder holder = entry.getValue();
			if (holder.getFieldConfig().getSystemField().equals(Boolean.FALSE)
				&& holder.getFieldConfig().getFieldTypeMetadata().equals(FieldTypeMetadata.TYPE_LIST_ID)) {

				Map<String, String> preferenceMap = new HashMap<String, String>();				
				ObjectUtil.populateHashMapFromPreference(generatePreferenceName(holder.getFieldConfig()), preferenceMap);
				holder.setPreferenceMap(preferenceMap);
			}
		}
	}

	/**
	 * Converts value into appropriate type using field information
	 * 
	 * @param fieldConfig
	 * @param metadata	fieldTypeMetadata for fieldConfig
	 * @param value		value to be converted
	 * @return
	 */
	protected Object convertCustomField(ColumnValueHolder holder, FieldConfig fieldConfig, FieldTypeMetadata metadata, String value) {
		if (value==null||StringUtils.isBlank(value))
			return null;
		String datatype = metadata.getId();
		if (datatype.equals(FieldTypeMetadata.TYPE_TEXT_ID) || datatype.equals(FieldTypeMetadata.TYPE_LONGTEXT_ID)) {
			return value ;
		} else if (datatype.equals(FieldTypeMetadata.TYPE_DATE_ID)) {
			return convertStringToDate(value);
		} else if (datatype.equals(FieldTypeMetadata.TYPE_CHECKBOX_ID)) {
			return Boolean.parseBoolean(value);
		} else if (datatype.equals(FieldTypeMetadata.TYPE_LIST_ID)) {
			for (Map.Entry<String,String> entry: holder.getPreferenceMap().entrySet()) {
				if (entry.getValue().equals(value)) {
					return new Integer(entry.getKey());
				}
			}
			return null;
		} else if (datatype.equals(FieldTypeMetadata.TYPE_DECIMAL_ID)) {
			return Double.parseDouble(value.trim());
		}
		return null ;
	}
	
	protected Preference loadPreference(String name) {
		return new Preference(name, name, name);
	}
	
	protected String generatePreferenceName(FieldConfig fieldConfig) {
		return fieldConfig.getEntityName().toLowerCase() + "." + fieldConfig.getColumnName() + ".LOV";
	}
	
	protected String getCellValue(Cell originalCell, FormulaEvaluator evaluator){
		if(originalCell!=null && evaluator != null && originalCell.getCellType()!=originalCell.CELL_TYPE_BLANK){
			CellValue cell = evaluator.evaluate(originalCell);
			switch(originalCell.getCellType()){
				case Cell.CELL_TYPE_NUMERIC:
					Double val = Double.valueOf(cell.getNumberValue());
					if((val - val.longValue()) == 0){
						return String.valueOf(val.longValue());
					}else{
						return String.valueOf(val);
					}
				case Cell.CELL_TYPE_STRING:
					return cell.getStringValue();
				case Cell.CELL_TYPE_BOOLEAN:
					return String.valueOf(cell.getBooleanValue());
				case Cell.CELL_TYPE_ERROR:
					return String.valueOf(cell.getErrorValue());
				case Cell.CELL_TYPE_FORMULA:	//This should never be called
					//return cell.getCellFormula();
				default:
					return null;
			}
		}
		return null;
	}
		
	protected void copyFile(FileObject srcFile, String dest) throws IOException {
		
		FileObject newObj = fsManager.resolveFile(dest);
		FileSelector fs = new FileSelector() {
		
		public boolean traverseDescendents(FileSelectInfo arg0) throws Exception {
			// TODO Auto-generated method stub
			return false;
		}
		
		public boolean includeFile(FileSelectInfo arg0) throws Exception {
			FileObject fo =arg0.getFile();
			return true ;
		}
	};
	
	newObj.copyFrom(srcFile, fs);
}
	
	protected int[] converField(String filedNumber) {
		
		try {
			if (StringUtils.isEmpty(filedNumber)) {
				return null;
			}
			CellReference cellReference = new CellReference(filedNumber);
			return new int[] {cellReference.getCol(), cellReference.getRow()};
		} catch (Exception iae) {
			int colIndex = CellReference.convertColStringToIndex(filedNumber);
			if (colIndex < 0 ) {
				return null; // return null so validation works
			}
			return new int[] {colIndex , -1};
		}
		
		
		
	}

	protected boolean isRowNull(Row row) {
		boolean isNull = true;
		if (row == null) {
			isNull = true;
		} else {
			short lastCellNo = row.getLastCellNum();
			for (short i = 0; i < lastCellNo; i++) {
				Cell cell = row.getCell(i);
				if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
					isNull = true;
				} else {
					if (cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isBlank(cell.getStringCellValue())) {
						isNull = true;
					} else {
						isNull = false;
						break;
					}
				}
			}
		}
		return isNull;
	}
	
	protected void addJobHistory(ImportJob importJob, String comment) {
		if(importJob.getHistory()==null){
			importJob.setHistory(new HashSet<JobHistory>(1));
		}
		JobHistory jobHistory;
		jobHistory = new JobHistory();
		jobHistory.setActionDate(new Date());
		jobHistory.setComments(comment);
		importJob.getHistory().add(jobHistory);
		System.out.println(comment);
	}

	public boolean isFieldMapInputvalid(ImportJob importJob){
		return false;
	}
	
	protected void addJobHistoryAndUpdateStatus(ImportJob importJob, String status, String msg) {
		addJobHistory(importJob, msg);
		importJob.setStatus(status);
	}
	
	
	private FileObject[] getAllExcelFile(FileObject fileObj) throws FileSystemException{
		FileObject []xlx = findAllFiles(fileObj,"xls");
		FileObject []xlsx = findAllFiles(fileObj,"xlsx");
		return CopyAllFile(xlx,xlsx);
	}
	
	
	private FileObject[] findAllFiles(FileObject file, final String ext) throws FileSystemException{
		FileFilter ff = new FileFilter() {
			
			public boolean accept(FileSelectInfo arg0) {
				FileObject fo = arg0.getFile();
				return fo.getName().getBaseName().endsWith(ext);
			}
		};
		 return  file.findFiles(new FileFilterSelector(ff));
		 
	}
	
	private FileObject[] CopyAllFile(FileObject[]xlx,FileObject[]xlsx){
		FileObject[] files = new FileObject[(xlx.length+xlsx.length)];
		 int t =0;
		 for(int i =0;i<xlx.length;i++){
			 files[t++]=xlx[i];
		 }
		 for(int i =0;i<xlsx.length;i++){
			 files[t++]=xlsx[i];
		 }
		 return files ;
	}

    protected void populateCustomField(Testcase testcase, FieldConfig fldConfig, final String rawValue) {
        if (!fldConfig.getSystemField() && StringUtils.startsWith(fldConfig.getFieldName(), "custom") && rawValue != null) {
            FieldTypeMetadata fldMetadata = Constants.fieldTypeMetadataMap.get(fldConfig.getFieldTypeMetadata());
            //Treat arrays and customField with allowedValues the same way.
            if(StringUtils.equals("array", fldMetadata.getJiraDataType())){
                if(StringUtils.equals("string", fldMetadata.getItemsDataType()) || StringUtils.equals("string", fldMetadata.getJiraDataType())) {
                    testcase.getCustomProperties().put(fldConfig.getId(), rawValue.split(","));
                }else
                    //todo handle user, group, attachment, version, project etc arrays here
                    log.error("Unknown items data type for Array " + fldMetadata.getItemsDataType());
            }else if(StringUtils.equals("string", fldMetadata.getJiraDataType()) && (fldConfig.getAllowedValues() != null && fldConfig.getAllowedValues().size() > 0)){
                testcase.getCustomProperties().put(fldConfig.getId(), new HashMap<String, String>() {{ put("value", rawValue);}});
            }else if(StringUtils.equals("number", fldMetadata.getJiraDataType())){
                testcase.getCustomProperties().put(fldConfig.getId(), Double.parseDouble(rawValue));
            }else if(StringUtils.equals("date", fldMetadata.getJiraDataType())){
                SimpleDateFormat df = new SimpleDateFormat();
                try {
                    testcase.getCustomProperties().put(fldConfig.getId(), df.parse(rawValue));
                } catch (ParseException e) {
                    log.fatal("Error in parsing date for custom field " + fldConfig.getId() + ", value " + rawValue, e);
                }
            }else if(StringUtils.equals("datetime", fldMetadata.getJiraDataType())){
                testcase.getCustomProperties().put(fldConfig.getId(), Double.parseDouble(rawValue));
            }else{
                testcase.getCustomProperties().put(fldConfig.getId(), rawValue);
            }
        }
    }


    /**
	 * We assume that there are 2 lines at the bottom of the sheet
	 * @param count
	 * @return true if the line count is greater then 2, else false
	 */
	/*protected boolean isSheetEmpty(int count) {
		return count > EXTRA_ROWS_IN_END;
	}*/

	/**
	 * Bean to hold column value
	 * @author rajeevg
	 *
	 */
	public static class ColumnValueHolder {
		
		/* excel sheet column index */
		private int[] cellRef ;
		
		/* column value */
		private String value ;
		
		/* FieldConfig details */
		private FieldConfig fieldConfig ;
		
		/* holds index number of excel row */
		private List<Integer> truncateRowIndex = new ArrayList<Integer>();
		
		private boolean truncateInfoRequired ;
		
		/* if it is a list type custom field, holds preference values */ 
		private Map<String, String> preferenceMap ;

		public int getColumnIndex() {
			return cellRef == null ? -1 : cellRef[0];
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public FieldConfig getFieldConfig() {
			return fieldConfig;
		}

		public void setFieldConfig(FieldConfig fieldConfig) {
			this.fieldConfig = fieldConfig;
		}

		public void addTruncatedRowIndex(int index) {
			truncateRowIndex.add(index);
		}
		
		public List<Integer> getTruncateRowIndex() {
			return truncateRowIndex;
		}

		public boolean isTruncateInfoRequired() {
			return truncateInfoRequired;
		}

		public void setTruncateInfoRequired(boolean truncateInfoRequired) {
			this.truncateInfoRequired = truncateInfoRequired;
		}

		public Map<String, String> getPreferenceMap() {
			return preferenceMap;
		}

		public void setPreferenceMap(Map<String, String> preferenceMap) {
			this.preferenceMap = preferenceMap;
		}

		public void setCellRef(int[] cellRef) {
			this.cellRef = cellRef;
			if (cellRef != null && cellRef.length != 2) {
				throw new IllegalArgumentException("Cell ref should be [column,row]");
			}
			
		}

		public int[] getCellRef() {
			return cellRef;
		}

		
	}
	
}