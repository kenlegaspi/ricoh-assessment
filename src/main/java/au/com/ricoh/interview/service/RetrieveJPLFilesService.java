package au.com.ricoh.interview.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import au.com.ricoh.interview.Value;

public interface RetrieveJPLFilesService {
	String getDirectoryPath(String path);
	void createDirectoryPath(String path);
	List<String> getInputDirectories();
	File getFile(String filename, List<File> fileNameArray);
	List<File> retrievePJLFiles(List<String> directoryPath);
	List<Value> getFileNames(List<File> pjlFiles);
	List<Value> getSelectedFileNames(List<String> selectedFiles, List<Value> pjlFilesSelected);
	List<Value> updateCheckedFiles(List<String> selectedFiles, List<Value> pjlFilesSelected);
	void uncheckFileNameList(List<Value> pjlFilesSelected);
	Map<String,String> displayHeaderValues(List<File> fileNameArray, boolean multipleFiles);
	void updateHeaderValues(String inputHeaderKey, String inputHeaderNewValue, List<Value> pjlFilesSelected);
}
