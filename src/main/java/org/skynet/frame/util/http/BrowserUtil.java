package org.skynet.frame.util.http;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class BrowserUtil {
	/** 
     * 打开默认浏览器访问页面 
     */  
    public static void openDefaultBrowser(String url){  
        //启用系统默认浏览器来打开网址。  
        try {  
            URI uri = new URI(url);  
            Desktop.getDesktop().browse(uri);  
        } catch (URISyntaxException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    } 
}
