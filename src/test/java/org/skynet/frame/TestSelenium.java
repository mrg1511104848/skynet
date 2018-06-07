package org.skynet.frame;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestSelenium {
	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", "E://学习分享/新建文件夹/chromedriver.exe");  
		// Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface, 
        // not the implementation.
        WebDriver driver = new ChromeDriver();

        // And now use this to visit Google
        driver.get("https://www.baidu.com");
        // Alternatively the same thing can be done like this
        // driver.navigate().to("http://www.google.com");


        // Should see: "cheese! - Google Search"
        System.out.println("Page title is: " + driver.getTitle());
        
        //Close the browser
        driver.quit();
	}
}
