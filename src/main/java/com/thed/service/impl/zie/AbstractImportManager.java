package com.thed.service.impl.zie;

import com.google.common.collect.ImmutableMap;
import com.thed.model.FieldConfig;
import com.thed.model.FieldTypeMetadata;
import com.thed.model.ImportJob;
import com.thed.model.Testcase;
import com.thed.service.zie.ImportManager;
import com.thed.util.Constants;
import com.thed.util.Discriminator;
import com.thed.util.ObjectUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.*;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static com.thed.util.Discriminator.*;

public abstract class AbstractImportManager extends ImportManagerSupport implements ImportManager {
	private final static Log log = LogFactory.getLog(AbstractImportManager.class);
	// To fetch testcase priority, requirementPriority preferences
	protected static final int EXTRA_ROWS_IN_END = 2;
	private FileSystemManager fsManager = null;

	public boolean importAllFiles(ImportJob importJob, String action, Long userId)
			throws Exception {
		try {
			String folderName = importJob.getFolder();
			if (StringUtils.isEmpty(folderName) || !new File(folderName).exists()) {
				throw new FileNotFoundException("Invalid fileName " + folderName);
			}
			fsManager = VFS.getManager();

			String path = null;
			if (folderName.charAt(0) != '\\') // check to check if supplied path is
																				// absoulte path or not (VFS require
																				// absolute path)
				path = "\\" + folderName;
			else
				path = folderName;
			FileObject fileObj = fsManager.resolveFile(path);
			FileObject[] files = null;
			boolean isSuccess = false;
			if (fileObj.getType().equals(FileType.FOLDER)) {
				files = getAllExcelFile(fileObj);
			} else {
				files = new FileObject[1];
				files[0] = fileObj;
			}

			boolean currentResult = false;
			boolean lastResult = true;
			boolean allImportFails = false;
			// Initializing the LOVs
			// TODO: externilize this to a cache
			ObjectUtil.loadLOVs();
			if (isFieldMapInputvalid(importJob)) {
				if (files.length > 0) {
					for (FileObject file : files) {
						if (file.getType().equals(FileType.FOLDER)) {
							continue;
						}
						String prev = file.getParent().toString() + File.separator
								+ "success" + File.separator + file.getName().getBaseName();
						FileObject temp = fsManager.resolveFile(prev);
						if (!(file.getType().equals(FileType.FOLDER))
								&& !temp.exists()) {
							currentResult = importSingleFiles(file, importJob, action, userId);
							lastResult = isImportSuccess(currentResult, lastResult);
							allImportFails = isAllImportsFails(currentResult, allImportFails);
						} else {
							addJobHistory(importJob, file + " already processed");
						}

					}
				} else {
					addJobHistory(importJob, "No files exist.");
				}
			}

			if (files != null && files.length > 1) {
				if (lastResult == true && allImportFails == true) {
					isSuccess = true;
				} else if (lastResult == false && allImportFails == true) {
					isSuccess = true;
				}
				if (!allImportFails) {
					isSuccess = false;
				}
			} else {
				if (currentResult) {
					isSuccess = true;
				}
			}

			return isSuccess;
		} catch (Exception e) {
			addJobHistory(importJob,
					"Exception while performing job " + e.getMessage());
			log.fatal("", e);
			throw e;
		}
	}

	protected boolean isImportSuccess(boolean currentResult, boolean lastResult) {
		if (currentResult == true && lastResult == true)
			return true;
		else
			return false;
	}

	protected boolean isAllImportsFails(boolean currentResult, boolean lastResult) {
		if (currentResult || lastResult)
			return true;
		else
			return false;
	}

	public boolean importSingleFiles(FileObject file, ImportJob importJob,
			String action, Long userId) throws Exception {
		boolean isValidFile = false;
		Discriminator discriminator = importJob.getFieldMap().getDiscriminator();
		if (discriminator == BY_EMPTY_ROW) {
			isValidFile = validateFileByEmptyRow(file, importJob, false);
		}
		if (discriminator == BY_SHEET) {
			isValidFile = validateFileByEmptyRow(file, importJob, true);
		}
		if (discriminator == BY_ID_CHANGE) {
			isValidFile = validateFileByIdChange(file, importJob);
		}
		if (discriminator == BY_TESTCASE_NAME_CHANGE) {
			isValidFile = validateFileByNameChange(file, importJob);
		}
		boolean isFileProcessed = false;

		if (isValidFile) {

			addJobHistory(importJob, file.getName() + " normalization success..!");

			if (discriminator == BY_EMPTY_ROW) {
				isFileProcessed = importFileByEmptyRow(file, importJob, userId, false);
			} else if (discriminator == BY_SHEET) {
				isFileProcessed = importFileByEmptyRow(file, importJob, userId, true);
			}
			if (discriminator == BY_ID_CHANGE) {
				isFileProcessed = importFileById(file, importJob, userId);
			}
			if (discriminator == BY_TESTCASE_NAME_CHANGE) {
				isFileProcessed = importFileForByName(file, importJob, userId);
			}
		} else {
			return isValidFile;
		}
		if (isFileProcessed) {
			String successFileLocation = file.getParent().toString() + File.separator
					+ "success";
			FileObject newObj = fsManager.resolveFile(successFileLocation);
			if (!newObj.exists()) {
				newObj.createFolder();
			}
			String successFilePath = successFileLocation + File.separator
					+ file.getName().getBaseName();
			copyFile(file, successFilePath);
			addJobHistory(importJob, file.getName().getBaseName()
					+ " imported successfully..!");
		} else {
			addJobHistory(importJob, file.getName().getBaseName()
					+ " imported failed..!");
		}

		return isFileProcessed;
	}

	protected boolean importFileForByName(FileObject file, ImportJob importJob,
			Long userId) throws IOException {
		return false;
	}

	protected boolean importFileById(FileObject file, ImportJob importJob,
			Long userId) throws IOException {
		return false;
	}

	protected boolean importFileByEmptyRow(FileObject file, ImportJob importJob,
			Long userId, boolean stopAfterFirst) throws IOException {
		return false;
	}

	protected boolean validateFileByNameChange(FileObject file,
			ImportJob importJob) throws IOException {
		return false;
	}

	protected boolean validateFileByIdChange(FileObject file, ImportJob importJob)
			throws IOException {
		return false;
	}

	protected boolean validateFileByEmptyRow(FileObject file, ImportJob importJob, boolean stopAfterFirst)
			throws IOException {
		return false;
	}

	protected String getUniqueID(Row row, short externalIdColumn) {
		String uniID = null;
		;
		if (row.getCell(externalIdColumn) != null) {
			if (row.getCell(externalIdColumn).getCellType() == Cell.CELL_TYPE_STRING) {
				uniID = row.getCell(externalIdColumn).getStringCellValue();
			} else if (row.getCell(externalIdColumn).getCellType() == Cell.CELL_TYPE_NUMERIC) {
				uniID = "" + row.getCell(externalIdColumn).getNumericCellValue();
			}
		}
		return uniID;
	}

	/**
	 * This methods attempts to parse the dateString into date using HSSFDateUtil
	 * If that fails, then it attempst to parse String as yyyy:MM:dd if both
	 * parsing fails, it returns today's date
	 * 
	 * @param createdOnValue
	 * @return
	 */
	protected Date convertStringToDate(String createdOnValue) {
		Date creationDate = null;
		if (createdOnValue != null) {
			if (creationDate == null) {
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					creationDate = dateFormat.parse(createdOnValue);
				} catch (Exception e) {
				}
			}
			if (creationDate == null) {
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd");
					creationDate = dateFormat.parse(createdOnValue);
				} catch (Exception e) {
				}
			}
			try {
				// If excel has dateType column, then POI retrieves it as long
				creationDate = HSSFDateUtil.getJavaDate(Double.valueOf(createdOnValue));
			} catch (Exception ex) {
			}
		}
		return creationDate != null ? creationDate : new Date();
	}

	/**
	 * Optimization, loads preferences once at start of import.
	 * 
	 * @param columns
	 * @return
	 */
	protected void loadCustomPreferences(Map<String, ColumnValueHolder> columns) {

		for (Map.Entry<String, ColumnValueHolder> entry : columns.entrySet()) {
			ColumnValueHolder holder = entry.getValue();
			if (holder.getFieldConfig().getSystemField().equals(Boolean.FALSE)
					&& holder.getFieldConfig().getFieldTypeMetadata()
							.equals(FieldTypeMetadata.TYPE_LIST_ID)) {

				Map<String, String> preferenceMap = new HashMap<String, String>();
				ObjectUtil.populateHashMapFromPreference(
						generatePreferenceName(holder.getFieldConfig()), preferenceMap);
				holder.setPreferenceMap(preferenceMap);
			}
		}
	}

	/**
	 * Converts value into appropriate type using field information
	 * 
	 * @param fieldConfig
	 * @param metadata
	 *          fieldTypeMetadata for fieldConfig
	 * @param value
	 *          value to be converted
	 * @return
	 */
	protected Object convertCustomField(ColumnValueHolder holder,
			FieldConfig fieldConfig, FieldTypeMetadata metadata, String value) {
		if (value == null || StringUtils.isBlank(value))
			return null;
		String datatype = metadata.getId();
		if (datatype.equals(FieldTypeMetadata.TYPE_TEXT_ID)
				|| datatype.equals(FieldTypeMetadata.TYPE_LONGTEXT_ID)) {
			return value;
		} else if (datatype.equals(FieldTypeMetadata.TYPE_DATE_ID)) {
			return convertStringToDate(value);
		} else if (datatype.equals(FieldTypeMetadata.TYPE_CHECKBOX_ID)) {
			return Boolean.parseBoolean(value);
		} else if (datatype.equals(FieldTypeMetadata.TYPE_LIST_ID)) {
			for (Map.Entry<String, String> entry : holder.getPreferenceMap()
					.entrySet()) {
				if (entry.getValue().equals(value)) {
					return new Integer(entry.getKey());
				}
			}
			return null;
		} else if (datatype.equals(FieldTypeMetadata.TYPE_DECIMAL_ID)) {
			return Double.parseDouble(value.trim());
		}
		return null;
	}

	protected String generatePreferenceName(FieldConfig fieldConfig) {
		return fieldConfig.getEntityName().toLowerCase() + "."
				+ fieldConfig.getColumnName() + ".LOV";
	}

	private Cell getCell(int[] cellRef, Sheet sheet, Row currentRow) {
		if (cellRef == null) {
			return null;
		}
		if (cellRef[1] == -1) {
			return currentRow.getCell(cellRef[0]);
		} else {
			return sheet.getRow(cellRef[1]).getCell(cellRef[0]);
		}
	}
	
	protected String getCellValue(Cell originalCell, FormulaEvaluator evaluator) {
		if (originalCell != null && evaluator != null
				&& originalCell.getCellType() != Cell.CELL_TYPE_BLANK) {
			CellValue cell = evaluator.evaluate(originalCell);
			switch (originalCell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC:
				Double val = Double.valueOf(cell.getNumberValue());
				if ((val - val.longValue()) == 0) {
					return String.valueOf(val.longValue());
				} else {
					return String.valueOf(val);
				}
			case Cell.CELL_TYPE_STRING:
				return cell.getStringValue();
			case Cell.CELL_TYPE_BOOLEAN:
				return String.valueOf(cell.getBooleanValue());
			case Cell.CELL_TYPE_ERROR:
				return String.valueOf(cell.getErrorValue());
			case Cell.CELL_TYPE_FORMULA: // This should never be called
				// return cell.getCellFormula();
			default:
				return null;
			}
		}
		return null;
	}
	
	protected String getCellValue(String cellMapping, Sheet sheet, Row currentRow, FormulaEvaluator evaluator) {
		
		String staticText = convertToStatic(cellMapping);
		if (staticText != null) {
			return staticText;// allow the cell mapping to contain a static value between inverted commas
		}
		int[] cellRef = convertField(cellMapping);
		
		if (cellRef != null ) {
			Cell originalCell = getCell(cellRef, sheet, currentRow);
			return getCellValue(originalCell, evaluator);
			
		} else {
			return null; 
		}
	}
	
	protected boolean isValidMapping(String cellMapping) {
		
		return isStaticMapping(cellMapping) || convertField(cellMapping) != null;
	}

	private String convertToStatic(String cellMapping) {
		if (isStaticMapping(cellMapping)) {
			return StringUtils.substringBetween(cellMapping, "\"");
		}
		return null;
	}

	private boolean isStaticMapping(String cellMapping) {
		return StringUtils.startsWith(cellMapping, "\"") 
				&& StringUtils.endsWith(cellMapping, "\"");
	}

	protected void copyFile(FileObject srcFile, String dest) throws IOException {

		FileObject newObj = fsManager.resolveFile(dest);
		FileSelector fs = new FileSelector() {

			public boolean traverseDescendents(FileSelectInfo arg0) throws Exception {
				return false;
			}

			public boolean includeFile(FileSelectInfo arg0) throws Exception {
				arg0.getFile();
				return true;
			}
		};

		newObj.copyFrom(srcFile, fs);
	}

	protected int[] convertField(String fieldRef) {

		try {
			if (StringUtils.isEmpty(fieldRef)) {
				return null;
			}
			// Handle reference to cell by returning [colIndex, rowIndex]
			CellReference cellReference = new CellReference(fieldRef);
			return new int[] { cellReference.getCol(), cellReference.getRow() };
		} catch (Exception iae) {
			
			// Handle reference to column only by returning [colIndex, -1]
			int colIndex = CellReference.convertColStringToIndex(fieldRef);
			if (colIndex < 0) {
				return null; // return null so validation works
			}
			return new int[] { colIndex, -1 };
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
					if (cell.getCellType() == Cell.CELL_TYPE_STRING
							&& StringUtils.isBlank(cell.getStringCellValue())) {
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

	public boolean isFieldMapInputvalid(ImportJob importJob) {
		return false;
	}

	protected void addJobHistoryAndUpdateStatus(ImportJob importJob,
			String status, String msg) {
		addJobHistory(importJob, msg);
	}

	private FileObject[] getAllExcelFile(FileObject fileObj)
			throws FileSystemException {
		return findAllFiles(fileObj, "xls", "xlsx");
	}

	@SuppressWarnings("serial")
	protected void populateCustomField(Testcase testcase, FieldConfig fldConfig,
			final String rawValue) {
		if (!fldConfig.getSystemField()
				&& StringUtils.startsWith(fldConfig.getFieldName(), "custom")
				&& rawValue != null) {
			FieldTypeMetadata fldMetadata = Constants.fieldTypeMetadataMap
					.get(fldConfig.getFieldTypeMetadata());
			// Treat arrays and customField with allowedValues the same way.
			switch(fldMetadata.getJiraDataTypeEnum()) {
				case array:
					populateArrayTypeCustomField(testcase, fldConfig, rawValue, fldMetadata);
					break;
				case group:
					testcase.getCustomProperties().put(fldConfig.getId(), new SingleValueMap("name", rawValue));
					break;
				case project:
					testcase.getCustomProperties().put(fldConfig.getId(), new SingleValueMap("key", rawValue));
					break;
				case user:
					testcase.getCustomProperties().put(fldConfig.getId(), new SingleValueMap("name", rawValue));
					break;
				case version:
					testcase.getCustomProperties().put(fldConfig.getId(), new SingleValueMap("name", rawValue));
					break;
				case string:
					if((fldConfig.getAllowedValues() != null && fldConfig.getAllowedValues().size() > 0)){
						testcase.getCustomProperties().put(fldConfig.getId(), new SingleValueMap("value", rawValue));
					}
                    /*Multitextfield*/
                    else{
                        testcase.getCustomProperties().put(fldConfig.getId(), rawValue);
                    }
					break;
				case number:
					testcase.getCustomProperties().put(fldConfig.getId(), Double.parseDouble(rawValue));
					break;
				case date:
					String inputPattern = System.getProperty("DATE_FORMAT", DateFormatUtils.ISO_DATE_FORMAT.getPattern());
					setDateTypeCustomField(testcase, fldConfig, rawValue, inputPattern, DateFormatUtils.ISO_DATE_FORMAT);
					break;
				case datetime:
					inputPattern = System.getProperty("DATE_TIME_FORMAT", DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern());
					setDateTypeCustomField(testcase, fldConfig, rawValue, inputPattern, DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT);
					break;
				default:
					testcase.getCustomProperties().put(fldConfig.getId(), rawValue);
			}
		}
	}

	private void setDateTypeCustomField(Testcase testcase, FieldConfig fldConfig, String rawValue, String inputPattern, FastDateFormat pattern) {
		SimpleDateFormat df = (inputPattern == null) ? new SimpleDateFormat() : new SimpleDateFormat(inputPattern);
		try {
            testcase.getCustomProperties().put(fldConfig.getId(), DateFormatUtils.format(df.parse(rawValue), pattern.getPattern()));
        } catch (ParseException e) {
            log.fatal("Error in parsing date for custom field " + fldConfig.getId() + ", value " + rawValue, e);
            testcase.getCustomProperties().put(fldConfig.getId(), rawValue);
        }
	}

	private void populateArrayTypeCustomField(Testcase testcase, FieldConfig fldConfig, String rawValue, FieldTypeMetadata fldMetadata) {
		if (StringUtils.equals("string", fldMetadata.getItemsDataType())) {
			/* Labels */
            if(StringUtils.equals(FieldTypeMetadata.LABEL_TYPE, fldMetadata.getCustomType())){
                testcase.getCustomProperties().put(fldConfig.getId(), rawValue.split(","));
            }
			//Special handling of JIRA Agile EPIC and Sprint Field
			else if(StringUtils.equals(FieldTypeMetadata.GH_EPIC_LINK_TYPE, fldMetadata.getCustomType())){
				if(!Pattern.matches(Constants.ISSUE_KEY_REGEX, rawValue)){
					log.error("EPIC key doesn't seem to be a valid JIRA Key. If this errors, try with a valid JIRA Key");
				};
				testcase.getCustomProperties().put(fldConfig.getId(), rawValue);
			}else if(StringUtils.equals(FieldTypeMetadata.GH_SPRINT_TYPE, fldMetadata.getCustomType())){
				if(!Pattern.matches("\\d+", rawValue)){
					log.warn("Sprint id doesn't seem to be valid. Please check if import fails");
				};
				testcase.getCustomProperties().put(fldConfig.getId(), rawValue);
			}else{
				/* Multi select and Multi radio buttons */
                ArrayMap valueList = getArrayOfMapsWithKey(rawValue, "value");
                testcase.getCustomProperties().put(fldConfig.getId(), valueList);
            }
        } else if(StringUtils.equals("user", fldMetadata.getItemsDataType()) && StringUtils.equals(FieldTypeMetadata.MULTI_USER_PICKER_TYPE, fldMetadata.getCustomType())){
			ArrayMap valueList = getArrayOfMapsWithKey(rawValue, "name");
            testcase.getCustomProperties().put(fldConfig.getId(), valueList);
        }else if(StringUtils.equals("version", fldMetadata.getItemsDataType()) && StringUtils.equals(FieldTypeMetadata.MULTI_VERSION_TYPE, fldMetadata.getCustomType())){
            ArrayMap valueList = getArrayOfMapsWithKey(rawValue, "name");
            testcase.getCustomProperties().put(fldConfig.getId(), valueList);
        } else if(StringUtils.equals("group", fldMetadata.getItemsDataType()) && StringUtils.equals(FieldTypeMetadata.MULTI_GROUP_PICKER_TYPE, fldMetadata.getCustomType())){
            ArrayMap valueList = getArrayOfMapsWithKey(rawValue, "name");
            testcase.getCustomProperties().put(fldConfig.getId(), valueList);
        } else
            log.error("Unknown items data type for Array " + fldMetadata.getItemsDataType());
	}

	private ArrayMap getArrayOfMapsWithKey(String rawValue, final String mapKeyName) {
		return getArrayOfMapsWithKey(rawValue, mapKeyName, ",");
	}

	private ArrayMap getArrayOfMapsWithKey(String rawValue, final String mapKeyName, final String seperator) {
        return new ArrayMap(mapKeyName, rawValue.split(seperator));
//		ArrayList<Map> valueList = new ArrayList<Map>();
//		for(String val : rawValue.split(seperator)){
//            Map<String, String> m = new HashMap<String, String>(1);
//            m.put(mapKeyName, val);
//            valueList.add(m);
//        }
//		return valueList.toArray(new Map[valueList.size()]);
	}

    public static class SingleValueMap{
        private String mapKey;
        private String value;

        public SingleValueMap(String mapKey, String value) {
            this.mapKey = mapKey;
            this.value = value;
        }

        public String getMapKey() {
            return mapKey;
        }

        public String getValue() {
            return value;
        }
    }

    public static class ArrayMap{
        private String mapKey;
        private String[] values;

        public ArrayMap(String mapKey, String[] values) {
            this.mapKey = mapKey;
            this.values = values;
        }

        public String getMapKey() {
            return mapKey;
        }

        public String[] getValues() {
            return values;
        }
    }

	/**
	 * We assume that there are 2 lines at the bottom of the sheet
	 * 
	 * @param count
	 * @return true if the line count is greater then 2, else false
	 */
	/*
	 * protected boolean isSheetEmpty(int count) { return count >
	 * EXTRA_ROWS_IN_END; }
	 */

	/**
	 * Bean to hold column value
	 * 
	 * @author rajeevg
	 * 
	 */
	public static class ColumnValueHolder {

		/* column value */
		private String value;

		/* FieldConfig details */
		private FieldConfig fieldConfig;

		/* holds index number of excel row */
		private List<Integer> truncateRowIndex = new ArrayList<Integer>();

		private boolean truncateInfoRequired;

		/* if it is a list type custom field, holds preference values */
		private Map<String, String> preferenceMap;

		private String mappedField;

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

		public void setMappedField(String mappedField) {
			this.mappedField = mappedField;
			
		}

		public String getMappedField() {
			return mappedField;
		}

	}

}