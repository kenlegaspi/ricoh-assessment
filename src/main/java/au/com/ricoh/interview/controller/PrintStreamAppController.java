package au.com.ricoh.interview.controller;

import java.io.File;
import java.util.ArrayList;
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
import au.com.ricoh.interview.config.ApplicationConfiguration;
import au.com.ricoh.interview.service.RetrieveJPLFilesService;

@Controller
public class PrintStreamAppController {
	private File[] fileNameArray;
	private Map<String,String> mappedHeader;
	private List<Value> pjlFilesSelected;
	
	@Autowired
	RetrieveJPLFilesService retrieveJPLFilesService;
	
	@Autowired
	ApplicationConfiguration appConfig;
	
    @RequestMapping(value="/home", method = RequestMethod.GET)
    public String displayMainPage() {        
        return "home";
    }	
    
    @RequestMapping(value="/checkDirectory", method = RequestMethod.GET)
    public String checkDirectory(HttpServletRequest request, ModelMap model) {
    		
    		pjlFilesSelected = new ArrayList<Value>();
    		
    		String directoryPath = retrieveJPLFilesService.getDirectoryPath();
    				
    		fileNameArray = retrieveJPLFilesService.retrievePJLFiles(directoryPath);    
    		
    		if (fileNameArray != null && fileNameArray.length != 0) {
    			pjlFilesSelected = retrieveJPLFilesService.getFileNames(fileNameArray);
    		}
    		
        model.addAttribute("pjlFilesSelected", pjlFilesSelected);
        
        return "home";
    }	
    
    @RequestMapping(value="/displayHeaders", method = RequestMethod.POST)
    public String displayHeaders(HttpServletRequest request, ModelMap model) {
    		
    		String[] selectedFiles = request.getParameterMap().get("pjlFile");
    		
    		if (selectedFiles != null) {
    			pjlFilesSelected = retrieveJPLFilesService.getSelectedFileNames(selectedFiles, pjlFilesSelected);
    			mappedHeader = retrieveJPLFilesService.displayHeaderValues(fileNameArray);
    		} else {
    			retrieveJPLFilesService.uncheckFileNameList(pjlFilesSelected);
    		}
    		
    		model.addAttribute("pjlFilesSelected", pjlFilesSelected);        
    		model.addAttribute("mappedHeader", mappedHeader);
    		
    		return "home";
    }
    
	@RequestMapping(value = "/updateHeaders", method = RequestMethod.POST)
	public String updateHeaders(@RequestParam("inputHeaderNewValue") String inputHeaderNewValue,
			@RequestParam("inputHeaderKey") String inputHeaderKey, @RequestParam("checkedBoxValue") String checkedBoxValue, 
			@RequestParam("successValue") String successValue, ModelMap model) {
    	    		    		
    		String[] selectedFiles = checkedBoxValue.split(",");
    		
    		if (selectedFiles != null) {
    			pjlFilesSelected = retrieveJPLFilesService.getSelectedFileNames(selectedFiles, pjlFilesSelected);
    			mappedHeader = retrieveJPLFilesService.displayHeaderValues(fileNameArray);
    		} else {
    			retrieveJPLFilesService.uncheckFileNameList(pjlFilesSelected);
    		}

    		if (!inputHeaderKey.isEmpty() && !inputHeaderNewValue.isEmpty()) {
        		retrieveJPLFilesService.updateHeaderValues(inputHeaderKey, inputHeaderNewValue, pjlFilesSelected);    			
    		}
    		    		
    		model.addAttribute("success", successValue);
    		model.addAttribute("pjlFilesSelected", pjlFilesSelected);        
    		model.addAttribute("mappedHeader", mappedHeader);
    		
    		return "home";
    }   
    
}
