package com.thed.service.impl.zie;

import java.util.Date;
import java.util.LinkedHashSet;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.vfs.FileFilter;
import org.apache.commons.vfs.FileFilterSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSystemException;

import com.thed.model.HasJobHistory;
import com.thed.model.JobHistory;

public abstract class ImportManagerSupport {

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

	protected void addJobHistory(HasJobHistory importJob, String comment) {
		if (importJob.getHistory() == null) {
			importJob.setHistory(new LinkedHashSet<JobHistory>(1));
		}
		JobHistory jobHistory;
		jobHistory = new JobHistory();
		jobHistory.setActionDate(new Date());
		jobHistory.setComments(comment);
		importJob.getHistory().add(jobHistory);
	}

	protected FileObject[] findAllFiles(FileObject file, final String ... ext)
			throws FileSystemException {
				FileFilter ff = new FileFilter() {
			
					public boolean accept(FileSelectInfo arg0) {
						FileObject fo = arg0.getFile();
						return ArrayUtils.contains(ext, fo.getName().getExtension());
					}
				};
				return file.findFiles(new FileFilterSelector(ff));
			
			}

}
