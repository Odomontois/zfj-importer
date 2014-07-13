package com.thed.service.zie;

import com.thed.model.ImportJob;

public interface ImportManager  {
	public boolean importAllFiles(ImportJob importJob,String action,Long userId) throws Exception;
}