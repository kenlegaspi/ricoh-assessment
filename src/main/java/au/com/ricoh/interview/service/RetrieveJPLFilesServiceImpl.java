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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	public File[] retrievePJLFiles(String directoryPath) {

		log.info("Input files found at: " + directoryPath);

		File directory = new File(directoryPath);
		File[] fileArray = null;

		if (!directory.isDirectory()) {
			directory.mkdir();

			log.info("Input File Directory Created at: " + directory.toString());
		} else {
			fileArray = directory.listFiles();
		}

		return fileArray;
	}

	@Override
	public List<Value> getFileNames(File[] pjlFiles) {
		List<Value> fileNames = new ArrayList<>();

		for (int i = 0; i < pjlFiles.length; i++) {
			if (pjlFiles[i].getName().contains(".pjl")) {
				fileNames.add(new Value(pjlFiles[i].getName(), AppConstants.UNCHECKED));
			}
		}

		return fileNames;
	}

	@Override
	public List<Value> getSelectedFileNames(String[] selectedFiles, List<Value> pjlFilesSelected) {
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
	public Map<String, String> displayHeaderValues(File[] fileNameArray) {
		Map<String, String> mappedHeader = new LinkedHashMap<String, String>();

		for (File f : fileNameArray) {

			try (FileInputStream fis = new FileInputStream(f)) {
				byte[] buffer = new byte[(int) f.length()];

				fis.read(buffer);

				String sourceString = new String(buffer, StandardCharsets.UTF_8);

				Pattern p = Pattern.compile("@PJL SET.*\n*");
				Matcher m = p.matcher(sourceString);

				while (m.find()) {
					String matched = m.group();

					mappedHeader.put(matched.split("=")[0].split(" ")[2],
							matched.split("=")[1].replaceFirst("^\\s+", ""));
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

		File directory = new File(outputPath);

		if (!directory.isDirectory()) {
			directory.mkdir();

			log.info("Output File Directory Created at: " + directory.toString());
		} else {
			log.info("Output files found at: " + outputPath);
		}

		for (Value files : pjlFilesSelected) {
			if (files.getValue() == AppConstants.CHECKED) {

				File pjlFile = new File(appConfig.getUserDirectory() + AppConstants.FILE_SAMPLES + "/" + files.getId());
				File newfile = new File(
						appConfig.getUserDirectory() + AppConstants.FILE_OUTPUTS + "/" + "new_" + files.getId());

				try (FileInputStream fis = new FileInputStream(pjlFile);
						FileOutputStream fop = new FileOutputStream(newfile)) {

					byte[] buffer = new byte[(int) pjlFile.length()];

					fis.read(buffer);

					String sourceString = new String(buffer, StandardCharsets.UTF_8);

					Pattern pattern = Pattern.compile(inputHeaderKey + ".*\n*");
					Matcher match = pattern.matcher(sourceString);

					String matched = "";

					while (match.find()) {
						matched = match.group();
					}

					StringBuilder newStringValue = new StringBuilder();

					if (matched.contains("\"")) {
						int start = matched.indexOf("\"");
						int end = matched.lastIndexOf("\"");

						newStringValue.append(matched.substring(0, start + 1));
						newStringValue.append(inputHeaderNewValue);
						newStringValue.append(matched.substring(end, matched.length()));
					}

					int startIndex = sourceString.indexOf(matched);

					byte[] firstPartOfBytes = Arrays.copyOfRange(buffer, 0, startIndex);
					byte[] byteArrayToReplace = new byte[newStringValue.length()];
					byte[] lastPartOfBytes = Arrays.copyOfRange(buffer, startIndex + matched.length(), buffer.length);

					byteArrayToReplace = newStringValue.toString().getBytes();

					ByteArrayOutputStream output = new ByteArrayOutputStream();

					output.write(firstPartOfBytes);
					output.write(byteArrayToReplace);
					output.write(lastPartOfBytes);

					byte[] out = output.toByteArray();

					fop.write(out);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public String getDirectoryPath() {
		String directoryPath = appConfig.getUserDirectory() + AppConstants.FILE_SAMPLES;
		
		return directoryPath;
	}
}
