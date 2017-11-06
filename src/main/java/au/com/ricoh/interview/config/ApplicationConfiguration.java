package au.com.ricoh.interview.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:config.properties")
public class ApplicationConfiguration {

    @Autowired
    private Environment env;
    
    public String getUserDirectory() {
    		return env.getProperty("user.dir");
    }
}
