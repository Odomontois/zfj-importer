package com.thed.service.impl.zie;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.poi.hssf.record.RecordFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.thed.model.FieldMap;
import com.thed.model.FieldMapDetail;
import com.thed.model.FieldTypeMetadata;
import com.thed.model.ImportJob;
import com.thed.model.JobHistory;
import com.thed.model.TestStepDetailBase;
import com.thed.model.Testcase;
import com.thed.model.ZephyrFieldEnum;
import com.thed.util.Constants;
import com.thed.zfj.model.*;
import com.thed.zfj.rest.*;

public class TestcaseImportManagerImpl extends AbstractImportManager {

	private final static Log log = LogFactory
			.getLog(TestcaseImportManagerImpl.class);

	protected boolean validateFileByNameChange(FileObject file,
			ImportJob importJob) throws IOException {
		FileContent fc = file.getContent();
		InputStream fis = fc.getInputStream();
		if (importJob.getHistory() == null) {
			importJob.setHistory(new HashSet<JobHistory>(0));
		}
		int oldJobHistorySize = importJob.getHistory().size();
		try {
			importJob.setStatus(Constants.IMPORT_JOB_NORMALIZATION_IN_PROGRESS);

			FieldMap map = importJob.getFieldMap();

			// fis = new FileInputStream(file);
			// fs = new POIFSFileSystem(fis);

			Workbook wb = WorkbookFactory.create(fis);
			// for(int sheetNo=0;sheetNo<wb.getNumberOfSheets();sheetNo++){
			Sheet sheet = wb.getSheetAt(0);
			FormulaEvaluator evaluator = wb.getCreationHelper()
					.createFormulaEvaluator();// As a best practice, evaluator should be
																		// one per sheet
			sheet.setDisplayGridlines(false);

			int lastRow = sheet.getLastRowNum() + EXTRA_ROWS_IN_END;
			/*
			 * if(!isSheetEmpty(lastRow)){ continue; }
			 */
			// Start processing the sheet
			Set<FieldMapDetail> fieldMapDetails = map.getFieldMapDetails();

			int[] nameColumn = null, stepsColumn = null, resultsColumn = null;
			for (Object obj : fieldMapDetails.toArray()) {
				FieldMapDetail fieldMapDetail = (FieldMapDetail) obj;
				if (ZephyrFieldEnum.NAME.equalsIgnoreCase(fieldMapDetail
						.getZephyrField())) {
					nameColumn = converField(fieldMapDetail.getMappedField());
				} else if (ZephyrFieldEnum.STEPS.equalsIgnoreCase(fieldMapDetail
						.getZephyrField())) {
					stepsColumn = converField(fieldMapDetail.getMappedField());
				} else if (ZephyrFieldEnum.RESULT.equalsIgnoreCase(fieldMapDetail
						.getZephyrField())) {
					resultsColumn = converField(fieldMapDetail.getMappedField());
				} 
			}// for end

			boolean isNameExist = false, isStepsExist = false, isExpectedresultsExist = false, blockStart = false, isTestcaseNameExist = false, isLastRow = false;
			int startPoint = map.getStartingRowNumber() - 1;
			String uniqueId = "";

			for (int i = startPoint; i < lastRow; i++) {
				blockStart = false;
				Row row = sheet.getRow(i);
				if (isRowNull(row)) {
					continue;
				}
				if (i == (lastRow - 2)) {
					isLastRow = true;
				}
				if (i == startPoint) {// || blockStart==true ){
					uniqueId = getCellValue(getCell(nameColumn, sheet, row), evaluator);
				}
				if (getCellValue(getCell(nameColumn, sheet, row), evaluator) != null
						&& uniqueId != null
						&& !(uniqueId.equalsIgnoreCase(getCellValue(
								getCell(nameColumn, sheet, row), evaluator)))) {
					uniqueId = getCellValue(getCell(nameColumn, sheet, row), evaluator);
					// isTestcaseNameExist = false;
					blockStart = true;
				}
				isNameExist = false;
				isStepsExist = false;
				isExpectedresultsExist = false;
				/*
				 * if (row.getLastCellNum() > maximumCount) { maximumCount =
				 * row.getLastCellNum(); }
				 */

				String nameValue = null;
				String stepValue = null;
				String resultValue = null;
				Cell nameCell = getCell(nameColumn, sheet, row);
				Cell stepsCell = getCell(stepsColumn, sheet, row);
				Cell resultsCell = getCell(resultsColumn, sheet, row);

				if (blockStart && !isTestcaseNameExist) {
					addJobHistory(importJob, file.getName()
							+ " Testcase name not exists at '" + (i + 1) + "' row");
				}

				if (blockStart) { // After next block start
					isTestcaseNameExist = false;
				}

				if (nameCell != null) {
					nameValue = getCellValue(nameCell, evaluator);
				}
				if (stepsCell != null) {
					stepValue = getCellValue(stepsCell, evaluator);
				}
				if (resultsCell != null) {
					resultValue = getCellValue(resultsCell, evaluator);
				}
				if (nameValue != null && !"".equalsIgnoreCase(nameValue)) {
					isNameExist = true;
					if (isNameExist) {
						isTestcaseNameExist = isTestcaseNameExist | isNameExist;
					}
				}
				if (stepValue != null && !"".equalsIgnoreCase(stepValue)) {
					isStepsExist = true;
				}
				if (resultValue != null && !"".equalsIgnoreCase(resultValue)) {
					isExpectedresultsExist = true;
				}
				if (isExpectedresultsExist && !isStepsExist) {
					addJobHistory(importJob, file.getName() + " Invalid at " + (i + 1)
							+ " Result without step");
				}
				if (isLastRow && !isTestcaseNameExist) { // if lastRow check for
																									// testCase exists
					addJobHistory(importJob, file.getName()
							+ " Testcase name not exists at '" + (i + 1) + "' row");
				}
			}// for ends

			// }
			/*
			 * if (editedFile) { cleanFile(file, map.getStartingRowNumber(),
			 * maximumCount - 1); maximumCount = maximumCount - 1; }
			 */
			if (importJob.getHistory().size() > oldJobHistorySize) {
				importJob.setStatus(Constants.IMPORT_JOB_NORMALIZATION_FAILED);

				addJobHistory(importJob, file.getName() + " normalization failed..!");
				return false;
			} else {
				return true;
			}
		} catch (RecordFormatException e) {
			String msg = ((file != null) ? (file.getName()) : (""))
					+ " Records contain invalid format/data";
			addJobHistoryAndUpdateStatus(importJob,
					Constants.IMPORT_JOB_NORMALIZATION_FAILED, msg);
			log.fatal("", e);
			return false;
		} catch (Exception e) {
			addJobHistoryAndUpdateStatus(importJob,
					Constants.IMPORT_JOB_NORMALIZATION_FAILED, e.getMessage());
			log.fatal("", e);
			return false;
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
				addJobHistoryAndUpdateStatus(importJob,
						Constants.IMPORT_JOB_NORMALIZATION_FAILED, e.getMessage());
				log.fatal("", e);
				return false;
			}
		}
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

	protected boolean validateFileByIdChange(FileObject file, ImportJob importJob)
			throws IOException {
		FileContent fc = file.getContent();
		InputStream fis = fc.getInputStream();

		if (importJob.getHistory() == null) {
			importJob.setHistory(new HashSet<JobHistory>(0));
		}
		int oldJobHistorySize = importJob.getHistory().size();
		try {
			importJob.setStatus(Constants.IMPORT_JOB_NORMALIZATION_IN_PROGRESS);

			FieldMap map = importJob.getFieldMap();
			// fis = new FileInputStream(file);
			// fs = new POIFSFileSystem(fis);
			Workbook wb = WorkbookFactory.create(fis);
			// for(int sheetNo=0;sheetNo<wb.getNumberOfSheets();sheetNo++){
			Sheet sheet = wb.getSheetAt(0);
			FormulaEvaluator evaluator = wb.getCreationHelper()
					.createFormulaEvaluator();// As a best practice, evaluator should be
																		// one per sheet

			sheet.setDisplayGridlines(false);

			int lastRow = sheet.getLastRowNum() + EXTRA_ROWS_IN_END;
			/*
			 * if(!isSheetEmpty(lastRow)){ continue; }
			 */
			// Start processing the sheet
			Set<FieldMapDetail> fieldMapDetails = map.getFieldMapDetails();

			int[] nameColumn = null, stepsColumn = null, resultsColumn = null, externalIdColumn = null;
			for (Object obj : fieldMapDetails.toArray()) {
				FieldMapDetail fieldMapDetail = (FieldMapDetail) obj;
				if (ZephyrFieldEnum.NAME.equalsIgnoreCase(fieldMapDetail
						.getZephyrField())) {
					nameColumn = converField(fieldMapDetail.getMappedField());
				} else if (ZephyrFieldEnum.STEPS.equalsIgnoreCase(fieldMapDetail
						.getZephyrField())) {
					stepsColumn = converField(fieldMapDetail.getMappedField());
				} else if (ZephyrFieldEnum.RESULT.equalsIgnoreCase(fieldMapDetail
						.getZephyrField())) {
					resultsColumn = converField(fieldMapDetail.getMappedField());
				} else if (ZephyrFieldEnum.EXTERNAL_ID.equalsIgnoreCase(fieldMapDetail
						.getZephyrField())) {
					externalIdColumn = converField(fieldMapDetail.getMappedField());
				}
			}// for end

			boolean isStepsExist = false, isExpectedresultsExist = false, blockStart = false, isTestcaseNameExist = false;
			int startPoint = map.getStartingRowNumber() - 1;
			String uniqueId = new String();

			int i;
			for (i = startPoint; i < lastRow; i++) {
				blockStart = false;
				Row row = sheet.getRow(i);
				if (isRowNull(row)) {
					continue;
				}
				if (i == startPoint) {// || blockStart==true ){
					uniqueId = getCellValue(getCell(externalIdColumn, sheet, row), evaluator);
				}
				{
					String tempId = getCellValue(getCell(externalIdColumn, sheet, row), evaluator);
					if (!StringUtils.equalsIgnoreCase(tempId, uniqueId)) {
						/*** Lets deal with old block **/
						if (!isTestcaseNameExist) {
							addJobHistory(importJob, file.getName()
									+ " Testcase name not exists in '" + uniqueId + "' block");
						}
						if (uniqueId == null || uniqueId == "") {
							addJobHistory(importJob, file.getName()
									+ " external Identifier not exists in row(s)' " + i
									+ "' and/or above"); // one row before the current row.
						}
						// reset the flag
						isTestcaseNameExist = false;
						/*** Start with the new block **/
						uniqueId = tempId;
						// In some cases, this can cause two error rows for the same block
						// (when a empty extId block exists in the middle of the filled
						// blocks
						if (uniqueId == null || uniqueId == "") {
							addJobHistory(importJob, file.getName()
									+ " external Identifier not exists in '" + (i + 1)
									+ "th' row");
						}
						blockStart = true;
					}
				}
				isStepsExist = false;
				isExpectedresultsExist = false;

				String nameValue = null;
				String stepValue = null;
				String resultValue = null;
				Cell nameCell = getCell(nameColumn, sheet, row);
				Cell stepsCell = getCell(stepsColumn, sheet, row);
				Cell resultsCell = getCell(resultsColumn, sheet, row);

				if (nameCell != null) {
					nameValue = getCellValue(nameCell, evaluator);
				}
				if (stepsCell != null) {
					stepValue = getCellValue(stepsCell, evaluator);
				}
				if (resultsCell != null) {
					resultValue = getCellValue(resultsCell, evaluator);
				}
				if (nameValue != null && !"".equalsIgnoreCase(nameValue)) {
					isTestcaseNameExist = isTestcaseNameExist | true;
				}
				if (stepValue != null && !"".equalsIgnoreCase(stepValue)) {
					isStepsExist = true;
				}
				if (resultValue != null && !"".equalsIgnoreCase(resultValue)) {
					isExpectedresultsExist = true;
				}
				if (isExpectedresultsExist && !isStepsExist) {
					addJobHistory(importJob, file.getName() + " Invalid at " + (i + 1)
							+ " Result without step");
				}
			}// for end
				// lastRow > 2 means that there is at least one row.
			if (!blockStart && lastRow > 2) {
				if (uniqueId == null || uniqueId == "") {
					addJobHistory(importJob, file.getName()
							+ " external Identifier not exists in '" + i + "' row");
				}
			}
			// Checking the lastBlock for testCase ExtId and name validity
			if (!isTestcaseNameExist) {
				addJobHistory(importJob, file.getName()
						+ " Testcase name not exists in '" + uniqueId + "' block");
			}// end of processing
			// }
			if (importJob.getHistory().size() > oldJobHistorySize) {
				importJob.setStatus(Constants.IMPORT_JOB_NORMALIZATION_FAILED);
				addJobHistory(importJob, file.getName() + " normalization failed..!");
				return false;
			} else {
				return true;
			}
		} catch (RecordFormatException e) {
			String msg = ((file != null) ? (file.getName()) : (""))
					+ " Records contain invalid format/data";
			addJobHistoryAndUpdateStatus(importJob,
					Constants.IMPORT_JOB_NORMALIZATION_FAILED, msg);
			log.fatal("", e);
			return false;
		} catch (Exception e) {
			addJobHistoryAndUpdateStatus(importJob,
					Constants.IMPORT_JOB_NORMALIZATION_FAILED, e.getMessage());
			log.fatal("", e);
			return false;
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
				addJobHistoryAndUpdateStatus(importJob,
						Constants.IMPORT_JOB_NORMALIZATION_FAILED, e.getMessage());
				log.fatal("", e);
				return false;
			}
		}
	}

	private void processColumns(Map<String, ColumnValueHolder> columns,
			FormulaEvaluator evaluator, Row row,
			ArrayList<TestStepDetailBase> testsValue, int i) {

		TestStepDetailBase testStep = new TestStepDetailBase();

		for (Map.Entry<String, ColumnValueHolder> entry : columns.entrySet()) {
			ColumnValueHolder holder = entry.getValue();
			if (holder.getColumnIndex() < 0)
				continue;
			
			
			String idValue = holder.getFieldConfig().getId();
			Cell cell = getCell(holder.getCellRef(), row.getSheet(), row);
			if (cell != null && getCellValue(cell, evaluator) != null) {
				String value = getCellValue(cell, evaluator);

				/* field specific handling */
				if (idValue.equals(ZephyrFieldEnum.PRIORITY)) {
					// Skipping any special handling for now
					// value = ObjectUtil.reverseTranslatePreference(value,
					// ObjectUtil.TC_PRIORITY_STATUS_MAP, "");
				} else if (holder.getFieldConfig().getId().toString()
						.equals(ZephyrFieldEnum.FLAG_AUTOMATION)) {

					if (value.equalsIgnoreCase("A")
							|| value.toLowerCase().startsWith("auto")) {
						value = "true";
					} else {
						value = "false";
					}

				} else {
					if (idValue.equals(ZephyrFieldEnum.STEPS)) {
						testStep.step = value;
					} else if (idValue.equals(ZephyrFieldEnum.TESTDATA)) {
						testStep.data = value;
					} else if (idValue.equals(ZephyrFieldEnum.RESULT)) {
						testStep.result = value;
					}

				}

				if (holder.isTruncateInfoRequired()) {
					Integer length = holder.getFieldConfig().getLength();

					if (length != null && holder.getFieldConfig().getLength() > 0) {
						if (value.length() > length) {
							value = value.substring(0, length);
							holder.addTruncatedRowIndex(i + 1);
						}
					}
				}

				holder.setValue(value);
			}
		}
		/* all columns for row are processed, add test steps */
		if (!("".equalsIgnoreCase(testStep.step))) {
			testsValue.add(testStep);
		}

	}

	/*
	 * Common logic in importFileForByName() and importFileById() is abstracted in
	 * importFileByChange().
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thed.service.impl.zie.AbstractImportManager#importFileForByName(java
	 * .io.File, com.thed.model.ImportJob, java.lang.Long)
	 */

	@Override
	protected boolean importFileForByName(FileObject file, ImportJob importJob,
			Long userId) {
		return importFileByChange(file, importJob, userId, ZephyrFieldEnum.NAME);
	}

	@Override
	protected boolean importFileById(FileObject file, ImportJob importJob,
			Long userId) {
		return importFileByChange(file, importJob, userId,
				ZephyrFieldEnum.EXTERNAL_ID);
	}

	/*
	 * Common logic in importFileForByName() and importFileById() is abstracted in
	 * importFileByChange().
	 */
	protected boolean importFileByChange(FileObject file, ImportJob importJob,
			Long userId, String zephyrField) {

		InputStream fis = null;
		try {
			FileContent fc = file.getContent();
			fis = fc.getInputStream();
			initializeImport(importJob);
			FieldMap map = importJob.getFieldMap();

			Set<FieldMapDetail> fieldMapDetails = map.getFieldMapDetails();

			/*
			 * Holds custom columns. Key is fieldConfig.id as String
			 */
			Map<String, ColumnValueHolder> columns = new HashMap<String, ColumnValueHolder>();
			Map<String, FieldTypeMetadata> metadata = Constants.fieldTypeMetadataMap;
			initializeColumns(fieldMapDetails, columns, metadata);

			// fis = new FileInputStream(file);
			// fs = new POIFSFileSystem(fis);
			Workbook wb = WorkbookFactory.create(fis);
			// for(int sheetNo=0;sheetNo<wb.getNumberOfSheets();sheetNo++){
			Sheet sheet = wb.getSheetAt(0);
			FormulaEvaluator evaluator = wb.getCreationHelper()
					.createFormulaEvaluator();// As a best practice, evaluator should be
																		// one per sheet

			int startPoint = map.getStartingRowNumber() - 1;
			int lastRow = sheet.getLastRowNum() + EXTRA_ROWS_IN_END;

			/*
			 * if(!isSheetEmpty(lastRow)){ continue; }
			 */
			// Start processing the sheet
			// Initializing the fields to null
			System.out.println("Columns " + zephyrField);
			ColumnValueHolder uniqueColumnHolder = columns.get(zephyrField);
			System.out.println("Columns " + uniqueColumnHolder);
			int uniqueColumn = uniqueColumnHolder.getColumnIndex();
			System.out.println("Columns " + uniqueColumn);
			ArrayList<TestStepDetailBase> testsValue = new ArrayList<TestStepDetailBase>();
			Row row = null;
			String uniqueId = new String();
			boolean blockStart = false;

			for (int i = startPoint; i < lastRow; i++) {
				blockStart = false;
				row = sheet.getRow(i);
				if (isRowNull(row)) {
					continue;
				}
				if (i == startPoint) {// || blockStart==true ){
					uniqueId = getCellValue(row.getCell(uniqueColumn), evaluator);
				}
				if (getCellValue(row.getCell(uniqueColumn), evaluator) != null
						&& uniqueId != null
						&& !(uniqueId.equalsIgnoreCase(getCellValue(
								row.getCell(uniqueColumn), evaluator)))) {
					uniqueId = getCellValue(row.getCell(uniqueColumn), evaluator);
					// isTestcaseNameExist = false;
					blockStart = true;
				}
				if (blockStart) {
					saveTestCase(importJob, testsValue, userId, columns, i - 1);
					resetValues(columns);
					testsValue = new ArrayList<TestStepDetailBase>();
				}

				/* process columns */
				processColumns(columns, evaluator, row, testsValue, i);

			}// end for
			/* save last row */
			ColumnValueHolder holder = columns.get(zephyrField);
			String value = holder.getValue();
			if (value != null && !"".equals(value.trim())) {
				saveTestCase(importJob, testsValue, userId, columns, lastRow);
			}// end of processing

			truncateUpdate(importJob, columns);
			return true;
		} catch (Exception e) {
			addJobHistoryAndUpdateStatus(importJob,
					Constants.IMPORT_JOB_IMPORT_FAILED, e.getMessage());
			log.fatal("", e);
			return false;
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
				addJobHistoryAndUpdateStatus(importJob,
						Constants.IMPORT_JOB_NORMALIZATION_FAILED, e.getMessage());
				log.fatal("", e);
				return false;
			}
		}
	}

	private void truncateUpdate(ImportJob importJob,
			Map<String, ColumnValueHolder> columns) {

		StringBuffer truncationInfo = new StringBuffer();

		for (Map.Entry<String, ColumnValueHolder> entry : columns.entrySet()) {
			ColumnValueHolder holder = entry.getValue();
			if (holder.getTruncateRowIndex().size() > 0) {
				truncationInfo.append("in rows " + holder.getTruncateRowIndex() + " "
						+ holder.getFieldConfig().getDisplayName() + " was truncated to "
						+ holder.getFieldConfig().getLength() + " chars.\n");
			}
		}

		if (truncationInfo.length() > 0) {
			addJobHistory(importJob, truncationInfo.toString());
		}

	}

	protected boolean validateFileByEmptyRow(FileObject file, ImportJob importJob)
			throws IOException {
		FileContent fc = file.getContent();
		InputStream fis = fc.getInputStream();
		// FileInputStream fis = null;

		Set<JobHistory> jobHistoryList = importJob.getHistory();
		if (jobHistoryList == null) {
			jobHistoryList = new HashSet<JobHistory>(0);
		}
		int oldJobHistorySize = jobHistoryList.size();

		try {
			importJob.setStatus(Constants.IMPORT_JOB_NORMALIZATION_IN_PROGRESS);

			FieldMap map = importJob.getFieldMap();

			// fis = new FileInputStream(file);
			// fs = new POIFSFileSystem(fis);
			Workbook wb = WorkbookFactory.create(fis);
			//for(int sheetNo=0;sheetNo<wb.getNumberOfSheets();sheetNo++) {
			Sheet sheet = wb.getSheetAt(0);
			FormulaEvaluator evaluator = wb.getCreationHelper()
					.createFormulaEvaluator();// As a best practice, evaluator should be
																		// one per sheet

			sheet.setDisplayGridlines(false);

			int lastRow = sheet.getLastRowNum() + EXTRA_ROWS_IN_END;
			/*
			 * if(!isSheetEmpty(lastRow)){ continue; }
			 */
			// Start processing the sheet
			Set<FieldMapDetail> fieldMapDetails = map.getFieldMapDetails();

			int[] nameColumn = null, stepsColumn = null, resultsColumn = null;
			for (Object obj : fieldMapDetails.toArray()) {
				FieldMapDetail fieldMapDetail = (FieldMapDetail) obj;
				if (ZephyrFieldEnum.NAME.equalsIgnoreCase(fieldMapDetail
						.getZephyrField())) {
					nameColumn = converField(fieldMapDetail.getMappedField());
				} else if (ZephyrFieldEnum.STEPS.equalsIgnoreCase(fieldMapDetail
						.getZephyrField())) {
					stepsColumn = converField(fieldMapDetail.getMappedField());
				} else if (ZephyrFieldEnum.RESULT.equalsIgnoreCase(fieldMapDetail
						.getZephyrField())) {
					resultsColumn = converField(fieldMapDetail.getMappedField());
				}
			}

			boolean isNameExist = false, isStepsExist = false, isExpectedresultsExist = false, isSkip = false, blockFlag = false;
			int startPoint = map.getStartingRowNumber() - 1;

			for (int i = startPoint; i < lastRow; i++) {
				Row row = sheet.getRow(i);
				if (isRowNull(row)) {
					if (!blockFlag) {
						if (!(isNameExist && isStepsExist && isExpectedresultsExist)) {
							// invalidList.add(startPoint); //here startPoint is invalid
							// testcase row no.
							if (!isNameExist) {
								addJobHistory(importJob, file.getName()
										+ ", testcase name not exists at " + (startPoint + 1)
										+ " row");
							}
						}
					}
					isNameExist = false;
					isStepsExist = false;
					isExpectedresultsExist = false;
					isSkip = false;
					startPoint = i + 1;
					blockFlag = true;
				} else if (isSkip) {
					continue;
				} else {
					blockFlag = false;
					/*
					 * if (row.getLastCellNum() > maximumCount) { maximumCount =
					 * row.getLastCellNum(); }
					 */
					String nameValue = null;
					String stepValue = null;
					String resultValue = null;
					Cell nameCell = getCell(nameColumn, sheet, row);
					Cell stepsCell = getCell(stepsColumn, sheet, row);
					Cell resultsCell = getCell(resultsColumn, sheet, row);

					if (nameCell != null) {
						nameValue = getCellValue(nameCell, evaluator);
					}
					if (stepsCell != null) {
						stepValue = getCellValue(stepsCell, evaluator);
					}
					if (resultsCell != null) {
						resultValue = getCellValue(resultsCell, evaluator);
					}
					if (nameValue != null && !"".equalsIgnoreCase(nameValue)) {
						isNameExist = true;
					}
					if (stepValue != null && !"".equalsIgnoreCase(stepValue)
							&& resultValue != null && !"".equalsIgnoreCase(resultValue)) {
						isStepsExist = true;
						isExpectedresultsExist = true;
					}
					if (stepValue != null && !"".equalsIgnoreCase(stepValue)) {
						isStepsExist = true;
						isExpectedresultsExist = true;
					} else {
						if (resultValue != null && !"".equalsIgnoreCase(resultValue)) {
							isStepsExist = false;
							isExpectedresultsExist = false;
							addJobHistory(importJob, file.getName()
									+ ", Result exists without Step at " + (startPoint + 1)
									+ " row");
							isSkip = true;
						}
					}
				}
			}// for end
			// end of processing
			// }

			/*
			 * if (editedFile) { cleanFile(file, map.getStartingRowNumber(),
			 * maximumCount - 1); maximumCount = maximumCount - 1; }
			 */
			if (jobHistoryList.size() > oldJobHistorySize) {

				importJob.setHistory(jobHistoryList);
				importJob.setStatus(Constants.IMPORT_JOB_NORMALIZATION_FAILED);

				addJobHistory(importJob, file.getName() + " normalization failed..!");

				return false;
			} else {
				return true;
			}
		} catch (RecordFormatException e) {
			String msg = ((file != null) ? (file.getName()) : (""))
					+ " Records contain invalid format/data";
			addJobHistoryAndUpdateStatus(importJob,
					Constants.IMPORT_JOB_NORMALIZATION_FAILED, msg);
			log.fatal("", e);
			return false;
		} catch (Exception e) {
			addJobHistoryAndUpdateStatus(importJob,
					Constants.IMPORT_JOB_NORMALIZATION_FAILED, e.getMessage());
			log.fatal("", e);
			return false;
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
				addJobHistoryAndUpdateStatus(importJob,
						Constants.IMPORT_JOB_NORMALIZATION_FAILED, e.getMessage());
				log.fatal("", e);
				return false;
			}
		}
	}

	protected void resetValues(Map<String, ColumnValueHolder> columns) {
		for (Map.Entry<String, ColumnValueHolder> entry : columns.entrySet()) {
			entry.getValue().setValue(null);
		}
	}

	private void initializeImport(ImportJob importJob) {
		importJob.setStatus(Constants.IMPORT_JOB_IMPORT_IN_PROGRESS);
	}

	/**
	 * Initializes column details required for import.
	 * 
	 * @param fieldMapDetails
	 * @param columns
	 * @param metadata
	 */
	private void initializeColumns(Set<FieldMapDetail> fieldMapDetails,
			Map<String, ColumnValueHolder> columns,
			Map<String, FieldTypeMetadata> metadata) {
		/* process importable fields */
		for (Object obj : fieldMapDetails.toArray()) {
			FieldMapDetail fieldMapDetail = (FieldMapDetail) obj;
			ColumnValueHolder holder = new ColumnValueHolder();
			int[] cellRef = converField(fieldMapDetail.getMappedField());
			holder.setCellRef(cellRef);
			holder.setFieldConfig(Constants.fieldConfigs.get(fieldMapDetail
					.getZephyrField()));
			columns.put(holder.getFieldConfig().getId().toString(), holder);

			/* some fields require truncation info */
			String idValue = holder.getFieldConfig().getId().toString();
			if (idValue.equals(ZephyrFieldEnum.NAME)
					|| idValue.equals(ZephyrFieldEnum.EXTERNAL_ID)
					|| idValue.equals(ZephyrFieldEnum.PRIORITY)
					|| idValue.equals(ZephyrFieldEnum.LABELS)) {
				holder.setTruncateInfoRequired(true);
			}
		}

		/*
		 * String treeCrumbColumn = (String)
		 * Constants.DB_PROPERTIES.getProperty("testcase.tree.column");
		 * if(StringUtils.isNotBlank(treeCrumbColumn)){ ColumnValueHolder holder =
		 * new ColumnValueHolder();
		 * holder.setColumnIndex(converField(treeCrumbColumn));
		 * //holder.setFieldConfig
		 * (fieldConfigDao.getFieldConfig(Long.parseLong(ZephyrFieldEnum
		 * .TCR_CATALOG_TREE_NAME)));
		 * columns.put(holder.getFieldConfig().getId().toString(), holder); }
		 */

		loadCustomPreferences(columns);

	}

	protected boolean importFileByEmptyRow(FileObject file, ImportJob importJob,
			Long userId) {
		InputStream fis = null;

		try {
			FileContent fc = file.getContent();
			fis = fc.getInputStream();
			initializeImport(importJob);
			FieldMap map = importJob.getFieldMap();

			Set<FieldMapDetail> fieldMapDetails = map.getFieldMapDetails();

			/*
			 * Holds custom columns. Key is fieldConfig.id as String
			 */
			Map<String, ColumnValueHolder> columns = new HashMap<String, ColumnValueHolder>();
			Map<String, FieldTypeMetadata> metadata = Constants.fieldTypeMetadataMap;
			initializeColumns(fieldMapDetails, columns, metadata);

			// fs = new POIFSFileSystem(fis);
			Workbook wb = WorkbookFactory.create(fis);
			for(int sheetNo=0;sheetNo<wb.getNumberOfSheets();sheetNo++){
				Sheet sheet = wb.getSheetAt(sheetNo);
				if (skipSheet(sheet, importJob)) {
					continue;
				}
				
				FormulaEvaluator evaluator = wb.getCreationHelper()
						.createFormulaEvaluator();// As a best practice, evaluator should be
																			// one per sheet
	
				int startPoint = map.getStartingRowNumber() - 1;
				int lastRow = sheet.getLastRowNum() + EXTRA_ROWS_IN_END;
				/*
				 * if(!isSheetEmpty(lastRow)){ continue; }
				 */
				// Start processing the sheet
				// Initializing the fields to null
				ArrayList<TestStepDetailBase> testsValue = new ArrayList<TestStepDetailBase>();
				Row row = null;
				boolean blockFlag = false;
				for (int i = startPoint; i < lastRow; i++) {
					row = (Row) sheet.getRow(i);
					if (isRowNull(row)) {
						if (!blockFlag) {
							saveTestCase(importJob, testsValue, userId, columns, i - 1);
							resetValues(columns);
							testsValue = new ArrayList<TestStepDetailBase>();
						}
						blockFlag = true;
					} else {
						blockFlag = false;
	
						/* process columns */
						processColumns(columns, evaluator, row, testsValue, i);
	
					}
				}// end of processing
			}
			truncateUpdate(importJob, columns);
			return true;
		} catch (Exception e) {
			addJobHistoryAndUpdateStatus(importJob,
					Constants.IMPORT_JOB_IMPORT_FAILED, e.getMessage());
			log.fatal("", e);
			return false;
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
				addJobHistoryAndUpdateStatus(importJob,
						Constants.IMPORT_JOB_NORMALIZATION_FAILED, e.getMessage());
				log.fatal("", e);
				return false;
			}
		}
	}

	private boolean skipSheet(Sheet sheet, ImportJob importJob) {
		return !importJob.getSheetFilter().matcher(sheet.getSheetName()).matches();
	}

	public boolean cleanUp(ImportJob importJob) throws Exception {
		return true;
	}

	private void saveTestCase(ImportJob importJob,
			ArrayList<TestStepDetailBase> testsValue, Long userId,
			Map<String, ColumnValueHolder> columns, int lastRowIdForThisTC)
			throws Exception {

		Testcase testcase = new Testcase();
		for (Map.Entry<String, ColumnValueHolder> entry : columns.entrySet()) {
			ColumnValueHolder holder = entry.getValue();

			String id = holder.getFieldConfig().getId().toString();
			if (ZephyrFieldEnum.NAME.equals(id)) {
				testcase.setName(holder.getValue());

				// ZephyrFieldEnum.STEPS, ZephyrFieldEnum.TESTDATA,
				// ZephyrFieldEnum.RESULT values are in testsValue
			} else if (ZephyrFieldEnum.EXTERNAL_ID.equals(id)) {
				testcase.setExternalId(holder.getValue());
			} else if (ZephyrFieldEnum.PRIORITY.equals(id)) {
				testcase.setPriority(holder.getValue());

			} else if (ZephyrFieldEnum.LABELS.equals(id)) {
				testcase.setTag(holder.getValue());
			}
			/* Commenting creatorId as its done in end */
			else if (ZephyrFieldEnum.CREATED_BY.equals(id)) {
				testcase.setCreator(holder.getValue());
			} else if (ZephyrFieldEnum.CREATED_ON.equals(id)) {
				// testcase.setCreationDate(convertStringToDate(holder.getValue()));
			} else if (ZephyrFieldEnum.COMMENTS.equals(id)) {
				testcase.setComments(holder.getValue());
			} else if (ZephyrFieldEnum.DESCRIPTION.equals(id)) {
				testcase.setDescription(holder.getValue());
			} else if (ZephyrFieldEnum.FIX_VERSION.equals(id)) {
				testcase.setFixVersions(holder.getValue());
			} else if (ZephyrFieldEnum.COMPONENT.equals(id)) {
				testcase.components = (holder.getValue());
			} else {
				/* custom fields */
				populateCustomField(testcase, holder.getFieldConfig(),
						holder.getValue());
			}
		}
		/*
		 * Lets see if user wants to import creatorId, if not, we 'll default it to
		 * the person performing import
		 */
		if (columns.get(ZephyrFieldEnum.CREATED_BY) != null) {
			testcase.setCreator(columns.get(ZephyrFieldEnum.CREATED_BY).getValue());
		}
		if (columns.get(ZephyrFieldEnum.ASSIGNEE) != null) {
			testcase.setAssignee(columns.get(ZephyrFieldEnum.ASSIGNEE).getValue());
		}

		/*
		 * //testcase.setReleaseId(importJob.getRelease().getId());
		 * 
		 * //testcase.setLastUpdaterId(userId);
		 * 
		 * if (testcase.getCreationDate() == null) testcase.setCreationDate(new
		 * Date()); }
		 */
		/* Capturing the error so that we can continue with other testcases */
		try {
			String issueId = JiraService.saveTestcase(testcase);
			importJob.getHistory().add(
					new JobHistory(new Date(), "Issue " + issueId + " created!"));
			setTestCaseContents(issueId, testsValue, userId);
			importJob.getHistory()
					.add(
							new JobHistory(new Date(), "Issue steps for " + issueId
									+ " created!"));
		} catch (Exception ex) {
			addJobHistory(importJob, "Unable to add testcase ending at rowNumber - "
					+ lastRowIdForThisTC + ". Error:" + ex.getMessage());
			log.error("", ex);
		}
	}

	protected void setTestCaseContents(String issueId,
			ArrayList<TestStepDetailBase> testsValue, Long userId) throws Exception {
		/*
		 * TestStep ts = new TestStep(); ts.setLastModificationDate(new Date());
		 * ts.setLastModifiedBy((userId != null ? userId : 1L));
		 * ts.setReleaseId(releaseId); ts.setTcId(testcase.getId());
		 * ts.setSteps(TestcaseContentsUtil.convert2(testsValue));
		 * ts.setMaxId(ts.getSteps().size());
		 */
		for (TestStepDetailBase ts : testsValue) {
			JiraService.saveTestStep(issueId, new TestStep(ts.getStep(),
					ts.getData(), ts.getResult()));
		}
	}

	public boolean isFieldMapInputvalid(ImportJob importJob) {
		boolean isValidFieldMap = false;
		StringBuffer missedFiledMaps = null;

		FieldMap map = importJob.getFieldMap();
		Set<FieldMapDetail> fieldMapDetails = map.getFieldMapDetails();
		int[] nameColumn = null, stepsColumn = null, resultsColumn = null, externalIdColumn = null;
		for (Object obj : fieldMapDetails.toArray()) {
			FieldMapDetail fieldMapDetail = (FieldMapDetail) obj;
			if (ZephyrFieldEnum.NAME
					.equalsIgnoreCase(fieldMapDetail.getZephyrField())) {
				nameColumn = converField(fieldMapDetail.getMappedField());
			} else if (ZephyrFieldEnum.STEPS.equalsIgnoreCase(fieldMapDetail
					.getZephyrField())) {
				stepsColumn = converField(fieldMapDetail.getMappedField());
			} else if (ZephyrFieldEnum.RESULT.equalsIgnoreCase(fieldMapDetail
					.getZephyrField())) {
				resultsColumn = converField(fieldMapDetail.getMappedField());
			} else if (ZephyrFieldEnum.EXTERNAL_ID.equalsIgnoreCase(fieldMapDetail
					.getZephyrField())) {
				externalIdColumn = converField(fieldMapDetail.getMappedField());
			}
		}// for end
		if (map.getDiscriminator().equalsIgnoreCase(Constants.BY_ID_CHANGE)) {
			if (nameColumn != null && stepsColumn != null && resultsColumn != null
					&& externalIdColumn != null) {
				isValidFieldMap = true;
			}
		}
		if (map.getDiscriminator().equalsIgnoreCase(Constants.BY_EMPTY_ROW)
				|| map.getDiscriminator().equalsIgnoreCase(
						Constants.BY_TESTCASE_NAME_CHANGE)) {
			if (nameColumn != null && stepsColumn != null && resultsColumn != null) {
				isValidFieldMap = true;
			}
		}
		if (!isValidFieldMap) { // for jobHistory purpose
			missedFiledMaps = new StringBuffer();
			if (map.getDiscriminator().equalsIgnoreCase(Constants.BY_ID_CHANGE)) {
				if (nameColumn == null) {
					missedFiledMaps.append(" Name,");
				}
				if (stepsColumn == null) {
					missedFiledMaps.append(" Step,");
				}
				if (resultsColumn == null) {
					missedFiledMaps.append(" Result,");
				}
				if (externalIdColumn == null) {
					missedFiledMaps.append(" External Id,");
				}
			}
			if (map.getDiscriminator().equalsIgnoreCase(Constants.BY_EMPTY_ROW)
					|| map.getDiscriminator().equalsIgnoreCase(
							Constants.BY_TESTCASE_NAME_CHANGE)) {
				missedFiledMaps = new StringBuffer();
				if (nameColumn == null) {
					missedFiledMaps.append(" Name,");
				}
				if (stepsColumn == null) {
					missedFiledMaps.append(" Step,");
				}
				if (resultsColumn == null) {
					missedFiledMaps.append(" Result,");
				}
			}

			addJobHistory(
					importJob,
					"The following required fields are missing :"
							+ missedFiledMaps.replace(missedFiledMaps.length() - 1,
									missedFiledMaps.length(), "."));
		}

		return isValidFieldMap;
	}

	// private void cleanFile(File file, int startRowNum, int maximumCount) throws
	// Exception {
	// POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
	// HSSFWorkbook wb = new HSSFWorkbook(fs);
	// HSSFSheet sheet = wb.getSheetAt(0);
	// evaluator = new HSSFFormulaEvaluator(sheet, wb);//As a best practice,
	// evaluator should be one per sheet
	// int lastRowNum = sheet.getLastRowNum();
	// for (int i = startRowNum; i < lastRowNum; i++) {
	// HSSFRow row = sheet.getRow(i);
	// if (row != null) {
	// HSSFCell cell = row.getCell((short) maximumCount);
	// if (cell != null) {
	// row.removeCell(cell);
	// }
	// }
	// }
	// FileOutputStream fileOut = null;
	// try {
	// fileOut = new FileOutputStream(file);
	// wb.write(fileOut);
	// } finally {
	// if (fileOut != null) {
	// fileOut.close();
	// }
	// }
	//
	// }
	//
	// private void fileupdation(File file, ArrayList<Integer> invalidList,int
	// maximumCount) throws Exception {
	// POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
	// HSSFWorkbook wb = new HSSFWorkbook(fs);
	// HSSFSheet sheet = wb.getSheetAt(0);
	// evaluator = new HSSFFormulaEvaluator(sheet, wb);//As a best practice,
	// evaluator should be one per sheet
	//
	// for (Integer rowNum : invalidList) {
	// HSSFRow row = sheet.getRow(rowNum);
	// if (row != null) {
	// HSSFCell cell = row.createCell((short) maximumCount);
	// cell.setCellType(HSSFCell.CELL_TYPE_STRING);
	// cell.setCellValue("Invalid");
	// }
	// }
	// // Write the output to a file
	// FileOutputStream fileOut = null;
	// try {
	// fileOut = new FileOutputStream(file);
	// wb.write(fileOut);
	// } finally {
	// if (fileOut != null) {
	// fileOut.close();
	// }
	// }
	// }

	public boolean importSingleFiles(File file, ImportJob importJob,
			String action, Long userId) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}