package com.thed.service.impl.zie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.thed.model.JobHistory;
import com.thed.model.Testcase;
import com.thed.util.Constants;
import com.thed.zfj.rest.JiraService;

public class TestcaseWordImportManager extends ImportManagerSupport {
	private final static Log log = LogFactory.getLog(TestcaseWordImportManager.class);
	
	private FileSystemManager fsManager;

	public boolean importAllFiles(WordImportJob importJob)
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
				files = findAllFiles(fileObj, "doc", "docx");
			} else {
				files = new FileObject[1];
				files[0] = fileObj;
			}

			boolean currentResult = false;
			boolean lastResult = true;
			boolean allImportFails = false;
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
						currentResult = importSingleFiles(file, importJob);
						lastResult = isImportSuccess(currentResult, lastResult);
						allImportFails = isAllImportsFails(currentResult, allImportFails);
					} else {
						addJobHistory(importJob, file + " already processed");
					}

				}
			} else {
				addJobHistory(importJob, "No files exist.");
			}

			if (files != null && files.length > 1) {
				if (lastResult == true && allImportFails == true) {
					importJob.setStatus(Constants.IMPORT_JOB_IMPORT_SUCCESS);
					isSuccess = true;
				} else if (lastResult == false && allImportFails == true) {
					importJob.setStatus(Constants.IMPORT_JOB_IMPORT_PARTIAL_SUCCESS);
					isSuccess = true;
				}
				if (!allImportFails) {
					importJob.setStatus(Constants.IMPORT_JOB_IMPORT_FAILED);
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

	private boolean importSingleFiles(FileObject file, WordImportJob importJob) {
		
		String title = getTitle(file);
		Testcase testcase = new Testcase();
		testcase.setName(title);
		testcase.components = importJob.getComponents();
		testcase.setTag(importJob.getLabels());
		String issueId = JiraService.saveTestcase(testcase);
		JiraService.saveAttachment(issueId, new File(file.getName().getPath()));
		importJob.getHistory().add(
				new JobHistory(new Date(), "Issue " + issueId + " created!"));
		return true;
	}

	private String getTitle(FileObject file) {
		
		String title = getDocTitle(file);
		if (title != null) {
			return title;
		}
		title = getDocxTitle(file);
		if (title != null) {
			return title;
		}
		return file.getName().getBaseName();
	}

	private String getDocTitle(FileObject file) {
		try {
			HWPFDocument doc;
			FileContent fc = file.getContent();
			InputStream fis = fc.getInputStream();
			try {
				doc = new HWPFDocument(fis);
				return doc.getSummaryInformation().getTitle();
			} finally {
				fis.close();
			}
		} catch (Exception e) {
			
		}
		return null;
	}
	private String getDocxTitle(FileObject file) {
		try {
			XWPFDocument doc;
			FileContent fc = file.getContent();
			InputStream fis = fc.getInputStream();
			try {
				doc = new XWPFDocument(fis);
				return doc.getProperties().getCoreProperties().getTitle();
			} finally {
				fis.close();
			}
		} catch (Exception e) {
			
		}
		return null;
	}

	

}
