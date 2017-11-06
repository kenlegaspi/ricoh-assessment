package au.com.ricoh.interview.config;

public class AppConstants {
	public static final String FILE_SAMPLES = "/src/test/samples";
	public static final String FILE_PROCESSED = "/src/test/processed";
	public static final String FILE_OUTPUTS = "/src/test/outputs";
	public static final String CHECKED = "checked";
	public static final String UNCHECKED = "unchecked";
	public static final String NEW = "new_";
	public static final String SET = "SET";
	public static final String COMMENT = "COMMENT";
	public static final String COMMENT_OTHER = "COMMENT OTHER";
	public static final String ENTER = "ENTER";
    
    public String getHeaderConstants() {
    	 	return "(" + SET + "|" + COMMENT + "|" + COMMENT_OTHER + "|" + ENTER + ")";
    }
}
