## Technical Assessment

### The application
A Spring boot web application use to edit PJL header files.

### Running the application
Once repository is cloned, import as a maven project into your IDE (with Spring Tool plugin and support) of choice. Spring Tool Suite recommended.

Run the test suite class (TestSuiteClass.java)

Run the project as a Spring Boot App or run PrintstreamApplication.java as a Java application. Alternatively, please build the project using mvn install or mvn package and deploy the war file (rename to printstream.war) in the webapps folder of your local Tomcat repository.

### Navigating the application

1. If project is run in IDE open http://localhost:8080/home to a browser. Otherwise, if project is deployed in a standalone Tomcat server access it at http://localhost:8080/printstream/home. This should take you to the home page

<img width="611" alt="screen shot 2017-10-31 at 7 29 49 am" src="https://user-images.githubusercontent.com/33222508/32194047-59a52e8c-be0d-11e7-80f8-3b0ea5b2731f.png">

2. Click Check Directory to display the files used. You may select 1 or more .pjl files to edit.

<img width="639" alt="screen shot 2017-10-31 at 7 12 16 am" src="https://user-images.githubusercontent.com/33222508/32193287-ea756524-be0a-11e7-975a-1a106cc9a47a.png">

3. Once files are selected, click on Display Headers to display the list of headers you can update. 

<img width="361" alt="screen shot 2017-10-31 at 7 15 42 am" src="https://user-images.githubusercontent.com/33222508/32193426-57c62c8a-be0b-11e7-80e6-5a9f667e738f.png">

4. Please select USERID from the dropdown list. It should display the current value from the file. Update the value as needed.

<img width="357" alt="updateheader" src="https://user-images.githubusercontent.com/33222508/32208874-74d91546-be59-11e7-8aef-99bc475d0d71.png">

5. Update headers and output file (e.g new_sample1.pjl) should be created inside the directory where you have imported the project.

   E.g: /Users/kenlegaspi/RICOH/ricoh-assessment/**src/test/output**
