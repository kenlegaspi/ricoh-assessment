package au.com.ricoh.interview.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.ricoh.interview.Value;
import au.com.ricoh.interview.config.AppConstants;
import au.com.ricoh.interview.config.ApplicationConfiguration;

@Service
public class RetrieveJPLFilesServiceImpl implements RetrieveJPLFilesService {
	
	private static final Logger log = LoggerFactory.getLogger(RetrieveJPLFilesServiceImpl.class);
		
	@Autowired
	ApplicationConfiguration appConfig;

	@Override
	public List<File> retrievePJLFiles(List<String> directoryPath) {
		List<File> fileArray = new ArrayList<File>();

		for (String filePath : directoryPath) {

			log.info("Input files found at: " + filePath);

			File directory = new File(filePath);

			if (!directory.isDirectory()) {
				directory.mkdir();

				log.info("Input File Directory Created at: " + directory.toString());
			} else {
				fileArray.addAll(Arrays.asList(directory.listFiles()).stream().filter(f -> f.getName().contains(".pjl"))
						.collect(Collectors.toList()));
			}
		}

		if (fileArray.size() > 0) {
			Collections.sort(fileArray);
		}
		
		return fileArray;
	}

	@Override
	public List<Value> getFileNames(List<File> pjlFiles) {
		List<Value> fileNames = new ArrayList<>();

		for (File f : pjlFiles) {
			if (f.getName().contains(".pjl")) {
				fileNames.add(new Value(f.getName(), AppConstants.UNCHECKED));
			}
		}

		return fileNames;
	}

	@Override
	public List<Value> getSelectedFileNames(List<String> selectedFiles, List<Value> pjlFilesSelected) {
		Map<String, String> mappedKeys = new HashMap<String, String>();

		for (String s : selectedFiles) {
			mappedKeys.put(s, s);
		}

		for (Value v : pjlFilesSelected) {
			if (mappedKeys.containsKey(v.getId()))
				v.setValue(AppConstants.CHECKED);
			else
				v.setValue(AppConstants.UNCHECKED);
		}

		return pjlFilesSelected;
	}

	@Override
	public void uncheckFileNameList(List<Value> pjlFilesSelected) {

		pjlFilesSelected.stream().forEach(p -> p.setValue(AppConstants.UNCHECKED));

	}

	@Override
	public Map<String, String> displayHeaderValues(List<File> fileNameArray, boolean multipleFiles) {
		Map<String, String> mappedHeader = new LinkedHashMap<String, String>();

		for (File f : fileNameArray) {

			try (FileInputStream fis = new FileInputStream(f)) {
				byte[] buffer = new byte[(int) f.length()];

				fis.read(buffer);

				String sourceString = new String(buffer, StandardCharsets.UTF_8);

				Pattern p = Pattern.compile("@PJL " + appConfig.toString() + ".*\n*");

				Matcher m = p.matcher(sourceString);

				while (m.find()) {
					String matched = m.group();					
					String header, value;
					
					if (matched.contains(appConfig.getPJLHeaderComment()) && !matched.contains(appConfig.getPJLHeaderCommentOther())) {
						header = appConfig.getPJLHeaderComment();
						value = matched.split(appConfig.getPJLHeaderComment())[1].trim();
					} else {			
						if (matched.contains(appConfig.getPJLHeaderCommentOther())) {
							header = appConfig.getPJLHeaderCommentOther();
						} else {
							header = matched.split("=",2)[0].split(" ")[2];
						}
						
						value = matched.split("=",2)[1].trim().replaceAll("\"", "");
					}
					
					if (!multipleFiles) {
						mappedHeader.put(header, value);											
					} else {
						mappedHeader.put(header, "");
					}
					
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return mappedHeader;
	}

	@Override
	public void updateHeaderValues(String inputHeaderKey, String inputHeaderNewValue, List<Value> pjlFilesSelected) {

		String outputPath = appConfig.getUserDirectory() + AppConstants.FILE_OUTPUTS;

		createDirectoryPath(outputPath);

		for (Value files : pjlFilesSelected) {
			if (files.getValue() == AppConstants.CHECKED) {

				String outputFileDirectory = appConfig.getUserDirectory() + AppConstants.FILE_OUTPUTS + "/";
				String inputFileDirectory = appConfig.getUserDirectory() + AppConstants.FILE_SAMPLES + "/";
				String processedFileDirectory = appConfig.getUserDirectory() + AppConstants.FILE_PROCESSED + "/";
						
				File inputFile, outputfile, processedFile = null;
						
				if (files.getId().contains(AppConstants.NEW)) {
					inputFile = new File(outputFileDirectory + files.getId());
					outputfile = new File(outputFileDirectory + "temp_" + files.getId());
				} else {
					inputFile = new File(inputFileDirectory + files.getId());
					outputfile = new File(outputFileDirectory + AppConstants.NEW +  files.getId());
					processedFile = new File(processedFileDirectory + files.getId());
				}
				
				try (FileInputStream fis = new FileInputStream(inputFile); FileOutputStream fop = new FileOutputStream(outputfile);) {

					byte[] inputFileInBytes = new byte[(int) inputFile.length()];

					fis.read(inputFileInBytes);

					String sourceString = new String(inputFileInBytes, StandardCharsets.UTF_8);
					
					Pattern pattern = Pattern.compile(inputHeaderKey + ".*\n*");
					Matcher match = pattern.matcher(sourceString);
					String matched = "";

					while (match.find()) {
						matched = match.group();
						
						if (matched.contains(appConfig.getPJLHeaderComment()) && !matched.contains(appConfig.getPJLHeaderCommentOther())) {
							break;
						}
					}

					StringBuilder newStringValue = new StringBuilder();
					int start, end;
					
					if (matched.contains("\"")) {
						start = matched.indexOf("\"");
						end = matched.lastIndexOf("\"");
					} else {
						if (matched.contains("=")) {
							start = matched.indexOf("=") + 1;
							end = matched.length();							
						} else {
							start = matched.indexOf(appConfig.getPJLHeaderComment()) + appConfig.getPJLHeaderComment().length();
							end = matched.length();
						}
					}

					createOutputFileWithNewValue(inputHeaderNewValue, fop, inputFileInBytes, sourceString, matched, newStringValue,
							start, end);

					updateExistingFiles(inputFile, outputfile, processedFile, inputFileInBytes);
					

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void createOutputFileWithNewValue(String inputHeaderNewValue, FileOutputStream fop, byte[] inputFileInBytes,
			String sourceString, String matched, StringBuilder newStringValue, int start, int end) throws IOException {
		
		newStringValue.append(matched.substring(0, start + 1));
		newStringValue.append(inputHeaderNewValue.trim());
		newStringValue.append(matched.substring(end, matched.length()));

		int startIndex = sourceString.indexOf(matched);

		byte[] firstPartOfBytes = Arrays.copyOfRange(inputFileInBytes, 0, startIndex);
		byte[] byteArrayToReplace = new byte[newStringValue.length()];
		byte[] lastPartOfBytes = Arrays.copyOfRange(inputFileInBytes, startIndex + matched.length(), inputFileInBytes.length);

		byteArrayToReplace = newStringValue.toString().getBytes();

		ByteArrayOutputStream output = new ByteArrayOutputStream();

		output.write(firstPartOfBytes);
		output.write(byteArrayToReplace);
		output.write(lastPartOfBytes);

		byte[] out = output.toByteArray();

		fop.write(out);
	}

	private void updateExistingFiles(File inputFile, File outputfile, File processedFile, byte[] inputFileInBytes)
			throws FileNotFoundException, IOException {
		
		if (outputfile.getName().contains("temp")) {
			outputfile.renameTo(inputFile);
		} else {
			FileOutputStream pr = new FileOutputStream(processedFile);
			pr.write(inputFileInBytes);
			inputFile.delete();
			pr.close();
		}
	}

	@Override
	public String getDirectoryPath(String path) {
		String directoryPath = appConfig.getUserDirectory() + path;
		
		return directoryPath;
	}
	
	@Override
	public void createDirectoryPath(String path) {
		File directory = new File(path);

		if (!directory.isDirectory()) {
			directory.mkdir();

			log.info("Directory Created at: " + directory.toString());
		}		
	}
	
	@Override
	public List<String> getInputDirectories(){
		List<String> directoryPaths = new ArrayList<String>();

		directoryPaths.add(getDirectoryPath(AppConstants.FILE_SAMPLES));
		directoryPaths.add(getDirectoryPath(AppConstants.FILE_OUTPUTS));
		
		return directoryPaths;
	}
	
	@Override
	public File getFile(String filename, List<File> fileNameArray) {
		File existingFile = null;
		
		for (File f : fileNameArray){
			if (f.getName().equals(filename)) {
				existingFile = f;
			}
		}
			
		return existingFile;
	}

	@Override
	public List<Value> updateCheckedFiles(List<String> selectedFiles, List<Value> pjlFilesSelected) {

		Map<String, String> mappedKeys = new HashMap<String, String>();

		for (String s : selectedFiles) {
			mappedKeys.put(s, s);
		}

		for (Value v : pjlFilesSelected) {
			if (mappedKeys.containsKey(v.getId()) && v.getValue().equals(AppConstants.CHECKED)) {
				if (!v.getId().contains(AppConstants.NEW)) {
					v.setId(AppConstants.NEW + v.getId());
				}
			}				
		}

		return pjlFilesSelected;
	}
}
