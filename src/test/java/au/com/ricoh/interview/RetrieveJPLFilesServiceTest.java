package au.com.ricoh.interview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import au.com.ricoh.interview.config.AppConstants;
import au.com.ricoh.interview.config.ApplicationConfiguration;
import au.com.ricoh.interview.service.RetrieveJPLFilesService;
import au.com.ricoh.interview.service.RetrieveJPLFilesServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RetrieveJPLFilesServiceTest {
	private File firstFile, secondFile, thirdFile, fourthFile, fifthFile;

	@Configuration
	static class AccountServiceTestContextConfiguration {

		@Bean
		public ApplicationConfiguration appConfig() {
			return new ApplicationConfiguration();
		}

		@Bean
		public RetrieveJPLFilesService retrieveService() {
			return new RetrieveJPLFilesServiceImpl();
		}
	}

	@Autowired
	private RetrieveJPLFilesService retrieveService;
	
	@Autowired
	private ApplicationConfiguration appConfig;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		firstFile = folder.newFile("file1.pjl");
		secondFile = folder.newFile("file2.pjl");
		thirdFile = folder.newFile("file3.txt");
		fourthFile = folder.newFile("file4.pjl");
		fifthFile = folder.newFile("file5.pjl");
	}
	
	@Test()
	public void testIfFilesExist() {				
		List<String> directoryPaths = new ArrayList<String>();
		
		directoryPaths.add(retrieveService.getDirectoryPath(AppConstants.FILE_SAMPLES));    			
		directoryPaths.add(retrieveService.getDirectoryPath(AppConstants.FILE_OUTPUTS));
		
		List<File> fileArray = retrieveService.retrievePJLFiles(directoryPaths);
	
		for (File f : fileArray) {
			if (!f.isDirectory()) {
				assertNotNull(f);
				assertNotEquals(0,f.length());				
				
			}
		}
	}

	@Test()
	public void testGetFileNames() {		
		List<Value> files = retrieveService.getFileNames(java.util.Arrays.asList(firstFile, secondFile));
		
		assertEquals("file1.pjl", files.get(0).getId());
		assertEquals("file2.pjl", files.get(1).getId());
		assertEquals(2, files.size());
	}

	@Test()
	public void testGetPJLFileNamesOnly() {
		List<Value> files = retrieveService.getFileNames(java.util.Arrays.asList(firstFile, secondFile, thirdFile));
		
		assertEquals("file1.pjl", files.get(0).getId());
		assertEquals("file2.pjl", files.get(1).getId());
		assertEquals(2, files.size());
	}
	
	@Test()
	public void testGetSelectedFileNames() {
		Value first = new ValueBuilder().withId(firstFile.getName()).withValue(AppConstants.UNCHECKED).build();
		Value second = new ValueBuilder().withId(secondFile.getName()).withValue(AppConstants.CHECKED).build();
		Value fourth = new ValueBuilder().withId(fourthFile.getName()).withValue(AppConstants.UNCHECKED).build();
		Value fifth = new ValueBuilder().withId(fifthFile.getName()).withValue(AppConstants.CHECKED).build();
		
		List<Value> pjlFilesSelected = java.util.Arrays.asList(first, second, fourth, fifth);
		
		List<Value> filesSelected = retrieveService.getSelectedFileNames(java.util.Arrays.asList(secondFile.getName(), fifthFile.getName()), pjlFilesSelected);
		
		assertEquals(4, filesSelected.size());
		assertEquals("file1.pjl", filesSelected.get(0).getId());
		assertEquals("file2.pjl", filesSelected.get(1).getId());
		assertEquals("file4.pjl", filesSelected.get(2).getId());
		assertEquals("file5.pjl", filesSelected.get(3).getId());
		assertEquals(AppConstants.UNCHECKED, filesSelected.get(0).getValue());
		assertEquals(AppConstants.CHECKED, filesSelected.get(1).getValue());
		assertEquals(AppConstants.UNCHECKED, filesSelected.get(2).getValue());
		assertEquals(AppConstants.CHECKED, filesSelected.get(1).getValue());
	}

	@Test()
	public void testUncheckFileNames() {
		Value first = new ValueBuilder().withId(firstFile.getName()).withValue(AppConstants.CHECKED).build();
		Value second = new ValueBuilder().withId(secondFile.getName()).withValue(AppConstants.CHECKED).build();
		Value fourth = new ValueBuilder().withId(fourthFile.getName()).withValue(AppConstants.CHECKED).build();
		Value fifth = new ValueBuilder().withId(fifthFile.getName()).withValue(AppConstants.CHECKED).build();
		
		List<Value> pjlFilesSelected = java.util.Arrays.asList(first, second, fourth, fifth);
		
		retrieveService.uncheckFileNameList(pjlFilesSelected);
		
		assertEquals(AppConstants.UNCHECKED, pjlFilesSelected.get(0).getValue());
		assertEquals(AppConstants.UNCHECKED, pjlFilesSelected.get(1).getValue());
		assertEquals(AppConstants.UNCHECKED, pjlFilesSelected.get(2).getValue());
		assertEquals(AppConstants.UNCHECKED, pjlFilesSelected.get(1).getValue());		
	}
	
	@Test()
	public void testDisplayHeaderValues() {
		List<String> directoryPaths = new ArrayList<String>();
		
		directoryPaths.add(retrieveService.getDirectoryPath(AppConstants.FILE_SAMPLES));    			
		directoryPaths.add(retrieveService.getDirectoryPath(AppConstants.FILE_OUTPUTS));

		
		List<File> fileArray = retrieveService.retrievePJLFiles(directoryPaths);
		
		Map<String, String> mappedHeaderValues = retrieveService.displayHeaderValues(fileArray, true);
		
		if (fileArray.size() > 0) {
			assertEquals(true, mappedHeaderValues.containsKey("USERID"));
			assertEquals(true, mappedHeaderValues.containsKey("AUTHENTICATIONUSERNAME"));
			assertEquals(true, mappedHeaderValues.containsKey("JOBNAME"));
			assertEquals(true, mappedHeaderValues.containsKey("HOSTPRINTERNAME"));
			assertEquals(true, mappedHeaderValues.containsKey("PUNCH"));
			assertEquals(true, mappedHeaderValues.containsKey("COPIES"));
			assertEquals(true, mappedHeaderValues.containsKey("QTY"));
			assertEquals(true, mappedHeaderValues.containsKey("DUPLEX"));
		}
	}
		
	@Test()
	public void testUpdateHeaderValue() {

		List<String> directoryPaths = new ArrayList<String>();
		
		directoryPaths.add(retrieveService.getDirectoryPath(AppConstants.FILE_SAMPLES));    			
		directoryPaths.add(retrieveService.getDirectoryPath(AppConstants.FILE_OUTPUTS));
		
		List<File> fileInputputArray = retrieveService.retrievePJLFiles(directoryPaths);

		List<Value> files = retrieveService.getFileNames(fileInputputArray);
		
		if (files.size() > 0) {
			files.get(0).setValue(AppConstants.CHECKED);
			files.get(1).setValue(AppConstants.CHECKED);

			retrieveService.updateHeaderValues("PUNCH", "ON", files);

			files = retrieveService.updateCheckedFiles(Arrays.asList(files.get(0).getId(), files.get(1).getId()),
					files);

			retrieveService.updateHeaderValues("USERID", "GUEST 123", files);
			retrieveService.updateHeaderValues("COPIES", "500", files);
			retrieveService.updateHeaderValues("QTY", "3295", files);
			retrieveService.updateHeaderValues("DUPLEX", "OFF", files);
		}
		
		String outputDirectoryPath = appConfig.getUserDirectory() + AppConstants.FILE_OUTPUTS;
		List<File> fileOutputArray = retrieveService.retrievePJLFiles(java.util.Arrays.asList(outputDirectoryPath));
		
		Map<String, String> mappedHeaderValues = retrieveService.displayHeaderValues(fileOutputArray, false);

		if (mappedHeaderValues.size() > 0) {
			assertEquals("GUEST 123", mappedHeaderValues.get("USERID").toString());
			assertEquals("ON", mappedHeaderValues.get("PUNCH").toString());
			assertEquals("500", mappedHeaderValues.get("COPIES").toString());
			assertEquals("3295", mappedHeaderValues.get("QTY").toString());
			assertEquals("OFF", mappedHeaderValues.get("DUPLEX").toString());
			assertEquals(5, files.size());
		}
	}
	
	@Test()
	public void testGetDirectoryPath() {
		String input = retrieveService.getDirectoryPath(AppConstants.FILE_SAMPLES);
		String output = retrieveService.getDirectoryPath(AppConstants.FILE_OUTPUTS);
		String processed = retrieveService.getDirectoryPath(AppConstants.FILE_PROCESSED);
		
		assertEquals(appConfig.getUserDirectory() + "/src/test/samples", input);
		assertEquals(appConfig.getUserDirectory() + "/src/test/outputs", output);
		assertEquals(appConfig.getUserDirectory() + "/src/test/processed", processed);
	}
	
	@Test
	public void testMatchingProcessedFilenames() {
		
		List<String> directoryPaths = new ArrayList<String>();
		
		directoryPaths.add(retrieveService.getDirectoryPath(AppConstants.FILE_PROCESSED)); 
		
		List<File> fileArray = retrieveService.retrievePJLFiles(directoryPaths);
		
		if (fileArray.size() > 0) {
			assertEquals("sample1.pjl", fileArray.get(0).getName());
			assertEquals("sample2.pjl", fileArray.get(1).getName());			
		}
	}

	@Test
	public void testGetFile() {
		File file_1 = retrieveService.getFile("file1.pjl", java.util.Arrays.asList(firstFile, secondFile, thirdFile, fourthFile, fifthFile));
		File file_2 = retrieveService.getFile("file2.pjl", java.util.Arrays.asList(firstFile, secondFile, thirdFile, fourthFile, fifthFile));
		File file_3 = retrieveService.getFile("test_file.pjl", java.util.Arrays.asList(firstFile, secondFile, thirdFile, fourthFile, fifthFile));
		
		assertEquals("file1.pjl", file_1.getName());
		assertEquals("file2.pjl", file_2.getName());
		assertNull(file_3);
	}
}
