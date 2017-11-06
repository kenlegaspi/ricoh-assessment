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
    
    public String getPJLHeaderSet() {
		return env.getProperty("pjl.header.set");
    }

    public String getPJLHeaderComment() {
		return env.getProperty("pjl.header.comment");
    }

    public String getPJLHeaderCommentOther() {
		return env.getProperty("pjl.header.comment.other");
    }    

    public String getPJLHeaderEnterLanguage() {
		return env.getProperty("pjl.header.enter.language");
    }        
    
    public String toString() {
    	 	return "(" + getPJLHeaderSet() + "|" + getPJLHeaderComment() + "|" + getPJLHeaderCommentOther() + "|" + getPJLHeaderEnterLanguage() + ")";
    }
}
