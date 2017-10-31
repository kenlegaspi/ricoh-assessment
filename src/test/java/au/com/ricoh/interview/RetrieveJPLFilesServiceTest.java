package au.com.ricoh.interview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Arrays;
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
		String directoryPath = appConfig.getUserDirectory() + AppConstants.FILE_SAMPLES;
		
		File fileArray[] = retrieveService.retrievePJLFiles(directoryPath);
	
		for (File f : fileArray) {
			if (!f.isDirectory()) {
				assertNotNull(f);
				assertNotEquals(0,f.length());				
				
			}
		}
	}

	@Test()
	public void testGetFileNames() {		
		List<Value> files = retrieveService.getFileNames(Arrays.array(firstFile, secondFile));
		
		assertEquals("file1.pjl", files.get(0).getId());
		assertEquals("file2.pjl", files.get(1).getId());
		assertEquals(2, files.size());
	}

	@Test()
	public void testGetPJLFileNamesOnly() {
		List<Value> files = retrieveService.getFileNames(Arrays.array(firstFile, secondFile, thirdFile));
		
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
		
		List<Value> filesSelected = retrieveService.getSelectedFileNames(Arrays.array(secondFile.getName(), fifthFile.getName()), pjlFilesSelected);
		
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
		String directoryPath = appConfig.getUserDirectory() + AppConstants.FILE_SAMPLES;
		
		File fileArray[] = retrieveService.retrievePJLFiles(directoryPath);
		
		Map<String, String> mappedHeaderValues = retrieveService.displayHeaderValues(fileArray);
		
		assertEquals(true, mappedHeaderValues.containsKey("USERID"));
		assertEquals(true, mappedHeaderValues.containsKey("AUTHENTICATIONUSERNAME"));
		assertEquals(true, mappedHeaderValues.containsKey("JOBNAME"));
		assertEquals(true, mappedHeaderValues.containsKey("HOSTPRINTERNAME"));		
	}
		
	@Test()
	public void testUpdateHeaderValue() {

		String inputDirectoryPath = appConfig.getUserDirectory() + AppConstants.FILE_SAMPLES;
		File fileInputputArray[] = retrieveService.retrievePJLFiles(inputDirectoryPath);

		List<Value> files = retrieveService.getFileNames(fileInputputArray);

		files.get(0).setValue(AppConstants.CHECKED);
		files.get(1).setValue(AppConstants.CHECKED);
		
		retrieveService.updateHeaderValues("USERID", "GUEST 123", files);
		
		String outputDirectoryPath = appConfig.getUserDirectory() + AppConstants.FILE_OUTPUTS;
		File fileOutputArray[] = retrieveService.retrievePJLFiles(outputDirectoryPath);
		
		Map<String, String> mappedHeaderValues = retrieveService.displayHeaderValues(fileOutputArray);
				
		assertEquals("GUEST 123", mappedHeaderValues.get("USERID").toString());
		assertEquals(5, files.size());
	}
	
	@Test()
	public void testGetDirectoryPath() {
		String s = retrieveService.getDirectoryPath();
		
		assertEquals(appConfig.getUserDirectory() + "/src/test/samples", s);
	}
	
	@Test
	public void testMatchingPJLFilenames() {
		
		String directoryPath = appConfig.getUserDirectory() + AppConstants.FILE_SAMPLES;
		
		File fileArray[] = retrieveService.retrievePJLFiles(directoryPath);
		
		assertEquals("sample1.pjl", fileArray[0].getName());
		assertEquals("sample2.pjl", fileArray[1].getName());
	}
}
