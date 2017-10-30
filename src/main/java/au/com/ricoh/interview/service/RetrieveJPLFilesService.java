package au.com.ricoh.interview.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import au.com.ricoh.interview.Value;

public interface RetrieveJPLFilesService {
	String getDirectoryPath();
	File[] retrievePJLFiles(String directoryPath);
	List<Value> getFileNames(File[] pjlFiles);
	List<Value> getSelectedFileNames(String[] selectedFiles, List<Value> pjlFilesSelected);
	void uncheckFileNameList(List<Value> pjlFilesSelected);
	Map<String,String> displayHeaderValues(File[] fileNameArray);
	void updateHeaderValues(String inputHeaderKey, String inputHeaderNewValue, List<Value> pjlFilesSelected);
}
