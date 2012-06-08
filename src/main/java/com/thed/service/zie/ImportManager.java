package com.thed.service.zie;

import java.io.File;

import com.thed.model.ImportJob;

public interface ImportManager {
	public boolean importAllFiles(ImportJob importJob,String action,Long userId) throws Exception;
    public boolean importSingleFiles(File file,ImportJob importJob,String action,Long userId)throws Exception;
    public boolean cleanUp(ImportJob importJob)throws Exception;
}