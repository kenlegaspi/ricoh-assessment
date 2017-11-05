package au.com.ricoh.interview.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import au.com.ricoh.interview.Value;
import au.com.ricoh.interview.config.AppConstants;
import au.com.ricoh.interview.config.ApplicationConfiguration;
import au.com.ricoh.interview.service.RetrieveJPLFilesService;

@Controller
public class PrintStreamAppController {
	private List<File> fileNameArray;
	private Map<String, String> mappedHeader;
	private List<Value> pjlFilesSelected;

	@Autowired
	RetrieveJPLFilesService retrieveJPLFilesService;

	@Autowired
	ApplicationConfiguration appConfig;

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String displayMainPage() {
		return "home";
	}

	@RequestMapping(value = "/checkDirectory", method = RequestMethod.GET)
	public String checkDirectory(ModelMap model) {

		pjlFilesSelected = new ArrayList<Value>();

		fileNameArray = retrieveJPLFilesService.retrievePJLFiles(retrieveJPLFilesService.getInputDirectories());

		if (fileNameArray != null && fileNameArray.size() != 0) {
			pjlFilesSelected = retrieveJPLFilesService.getFileNames(fileNameArray);
		}

		retrieveJPLFilesService.createDirectoryPath(retrieveJPLFilesService.getDirectoryPath(AppConstants.FILE_PROCESSED));

		model.addAttribute("pjlFilesSelected", pjlFilesSelected);
		model.addAttribute("inputPath", retrieveJPLFilesService.getDirectoryPath(AppConstants.FILE_SAMPLES));

		return "home";
	}

	@RequestMapping(value = "/displayHeaders", method = RequestMethod.POST)
	public String displayHeaders(HttpServletRequest request, ModelMap model) {

		String[] selectedFiles = request.getParameterMap().get("pjlFile");

		if (selectedFiles != null) {
			pjlFilesSelected = retrieveJPLFilesService.getSelectedFileNames(Arrays.asList(selectedFiles), pjlFilesSelected);
			mappedHeader = retrieveJPLFilesService.displayHeaderValues(fileNameArray, selectedFiles.length > 1 ? true : false);
		} else {
			retrieveJPLFilesService.uncheckFileNameList(pjlFilesSelected);
		}

		model.addAttribute("pjlFilesSelected", pjlFilesSelected);
		model.addAttribute("mappedHeader", mappedHeader);

		return "home";
	}

	@RequestMapping(value = "/displayFileHeader", method = RequestMethod.GET)
	public String displayFileHeader(@RequestParam("fileId") String fileId, ModelMap model) {

		List<String> selectedFiles = new ArrayList<String>();
		selectedFiles.add(fileId);

		fileNameArray = retrieveJPLFilesService.retrievePJLFiles(retrieveJPLFilesService.getInputDirectories());

		File file = retrieveJPLFilesService.getFile(fileId, fileNameArray);
		pjlFilesSelected = retrieveJPLFilesService.getSelectedFileNames(selectedFiles, pjlFilesSelected);
		mappedHeader = retrieveJPLFilesService.displayHeaderValues(Arrays.asList(file), false);

		model.addAttribute("pjlFilesSelected", pjlFilesSelected);
		model.addAttribute("mappedHeader", mappedHeader);
		model.addAttribute("fileId", fileId);

		return "home";
	}

	@RequestMapping(value = "/updateHeaders", method = RequestMethod.POST)
	public String updateHeaders(@RequestParam("inputHeaderNewValue") String inputHeaderNewValue,
			@RequestParam("inputHeaderKey") String inputHeaderKey,
			@RequestParam("checkedAllFiles") String checkedAllFiles, @RequestParam("successValue") String successValue,
			ModelMap model) {

		String[] selectedFiles = checkedAllFiles.split(",");

		if (!inputHeaderKey.isEmpty() && !inputHeaderNewValue.isEmpty()) {
			retrieveJPLFilesService.updateHeaderValues(inputHeaderKey, inputHeaderNewValue, pjlFilesSelected);
		}

		if (selectedFiles != null) {
			pjlFilesSelected = retrieveJPLFilesService.updateCheckedFiles(Arrays.asList(selectedFiles), pjlFilesSelected);
		} else {
			retrieveJPLFilesService.uncheckFileNameList(pjlFilesSelected);
		}

		if (selectedFiles != null && selectedFiles.length > 1) {
			mappedHeader = retrieveJPLFilesService.displayHeaderValues(fileNameArray, true);
		} else {
			fileNameArray = retrieveJPLFilesService.retrievePJLFiles(retrieveJPLFilesService.getInputDirectories());
			File file = retrieveJPLFilesService.getFile(selectedFiles[0].contains(AppConstants.NEW) ? selectedFiles[0]
					: AppConstants.NEW + selectedFiles[0], fileNameArray);
			mappedHeader = retrieveJPLFilesService.displayHeaderValues(Arrays.asList(file), false);
		}

		model.addAttribute("success", successValue);
		model.addAttribute("pjlFilesSelected", pjlFilesSelected);
		model.addAttribute("mappedHeader", mappedHeader);
		model.addAttribute("outputPath", retrieveJPLFilesService.getDirectoryPath(AppConstants.FILE_OUTPUTS));

		return "home";
	}

}
