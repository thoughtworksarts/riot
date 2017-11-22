package uk.co.riot;
import java.io.File;
import java.net.MalformedURLException;


public class Utils {
    // Turns C:/ into File:/C:/ 
    public static String UrlifyPath(String path) throws MalformedURLException
    {
    	if(path.startsWith("http://") || path.startsWith("https://"))
    		return path;
    	
    	return new File(path).toURI().toURL().toString();
    }
}
