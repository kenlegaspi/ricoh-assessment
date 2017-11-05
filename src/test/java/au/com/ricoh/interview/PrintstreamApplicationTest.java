package au.com.ricoh.interview;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import au.com.ricoh.interview.config.ApplicationConfiguration;
import au.com.ricoh.interview.controller.PrintStreamAppController;
import au.com.ricoh.interview.service.RetrieveJPLFilesService;

@RunWith(SpringRunner.class)
@WebMvcTest(PrintStreamAppController.class)
public class PrintstreamApplicationTest {
	
	private File first, second;

	@MockBean
	RetrieveJPLFilesService serviceMock;
	
	@MockBean
	ApplicationConfiguration appConfigMock;

	@Autowired
	MockMvc mockMvc;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		first = folder.newFile("file1.pjl");
		second = folder.newFile("file2.pjl");
	}

	@Test
	public void testCheckDirectory() throws Exception {
		List<File> fileNames = java.util.Arrays.asList(first, second);

		Value firstValue = new ValueBuilder().withId(first.getName()).withValue("unchecked").build();
		Value secondValue = new ValueBuilder().withId(second.getName()).withValue("unchecked").build();

		List<Value> expectedFileNames = java.util.Arrays.asList(firstValue, secondValue);
				
		when(serviceMock.getFileNames(fileNames)).thenReturn(expectedFileNames);

		mockMvc.perform(get("/checkDirectory"))
			 .andExpect(status().isOk())
			 .andExpect(view().name("home"));
			 //.andExpect(forwardedUrl("/resources/templates/welcome.html"));
			 //.andExpect(model().attribute("pjlFilesSelected", expectedFileNames));		
	}
	
	@Test
	public void testDisplayHeaders() throws Exception {

		mockMvc.perform(post("/displayHeaders"))
			 .andExpect(status().isOk())
			 .andExpect(view().name("home"));
	}

	/**
	 *  excluded since .param is not added into mockMvc
	 *  
	@Test
	public void testUpdateHeaders() throws Exception {

		mockMvc.perform(post("/updateHeaders"))
			 .andExpect(status().isOk())
			 .andExpect(view().name("welcome"));
	}
	*/
}

