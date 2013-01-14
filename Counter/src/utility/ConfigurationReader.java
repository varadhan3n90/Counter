package utility;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationReader {
	
	// private static final String SOURCE_TO_PROPERTY = System.getProperty("user.dir");
	private static final String SERVER_INIT_CONFIG_FILE = "Resources/serverstart.properties";
	private static final Properties SERVER_INIT_PROPERTIES = getPropertyFromFile(SERVER_INIT_CONFIG_FILE);
	private static final String CLIENT_INIT_CONFIG_FILE = "Resources/clientstart.properties";
	private static final Properties CLIENT_INIT_PROPERTIES = getPropertyFromFile(CLIENT_INIT_CONFIG_FILE);
	
			
			
	private static Properties getPropertyFromFile(String fileName){
		// String absoluteFilePath;
		// absoluteFilePath = SOURCE_TO_PROPERTY + fileName;
		try { 
			Properties fileProperties = new Properties();
			fileProperties.load(new FileInputStream(fileName));
			return fileProperties;
		}
		catch (Exception err){
			System.out.println(err.getMessage());
			System.out.println("Error opening the property file  " + fileName);			
		}
		return null;
			
	}
	
   public static String getServerInit(String key){
	   String property = SERVER_INIT_PROPERTIES.getProperty(key);
	   if ( property == null)
		   return "";
	   else
		   return property;
   }
   
   public static String getClientInit(String key){
	   String property = CLIENT_INIT_PROPERTIES.getProperty(key);
	   if ( property == null)
		   return "";
	   else
		   return property;
   }
   
   public static void setClientInit(String key,String value){
	  CLIENT_INIT_PROPERTIES.setProperty(key, value);
	  try {
		CLIENT_INIT_PROPERTIES.store(new FileOutputStream(CLIENT_INIT_CONFIG_FILE), null);
	  } catch (FileNotFoundException e) {
		System.out.println("Unable to find properties file");
	  } catch (IOException e) {
		System.out.println("Error writing to properties file");
	  }
   }
   
   public static void setServerInit(String key,String value){
	   SERVER_INIT_PROPERTIES.setProperty(key, value);
	   try {
			SERVER_INIT_PROPERTIES.store(new FileOutputStream(SERVER_INIT_CONFIG_FILE), null);
		} catch (FileNotFoundException e) {
			System.out.println("Unable to find properties file");
		} catch (IOException e) {
			System.out.println("Error writing to properties file");
		}
   }

   /** 
    * Test code
    */
   /*
   public static void main(String[] args){
	   String key = "server_ip";
	   String value = "localhost";
	   ConfigurationReader.setClientInit(key, value);
	   System.out.println(ConfigurationReader.getClientInit("server_ip"));
   }
   */
}
