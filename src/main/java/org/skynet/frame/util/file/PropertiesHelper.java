package org.skynet.frame.util.file;


import java.util.Properties;

/**
 * Created by zyt on 2016/12/7.
 */
public class PropertiesHelper {

    private static final Properties properties = new Properties( ) ;

    // 方法一：通过java.util.ResourceBundle读取资源属性文件  
    public static String getPropertyByName(String path, String name) {  
        String result = "";  
  
        try {  
            // 方法一：通过java.util.ResourceBundle读取资源属性文件  
            result = java.util.ResourceBundle.getBundle(path).getString(name);  
//            //System.out.println("name:" + result);  
        } catch (Exception e) {  
            //System.out.println("getPropertyByName2 error:" + name);  
        }  
        return result;  
    } 
    public static final String get(String key) {
        return properties.get(key)==null? null:properties.get(key).toString();
    }
    
    @SuppressWarnings("unused")
	public static void main(String[] args) {
    	String v = getPropertyByName("config", "cityPath");
    	//System.out.println(v);
	}
}
