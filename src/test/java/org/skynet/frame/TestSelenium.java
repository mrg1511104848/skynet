package org.skynet.frame;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.ParseException;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.seimicrawler.xpath.JXDocument;
import org.skynet.frame.persistent.MongoIndex;
import org.skynet.frame.persistent.MongoPersistent;
import org.skynet.frame.persistent.itrface.SkynetPersistent;
import org.skynet.frame.util.RegexUtils;
import org.skynet.frame.util.http.HttpUtil;
import org.skynet.frame.util.mongo.MongoUtil;

public class TestSelenium {
	private static Logger log = Logger.getLogger(TestSelenium.class);

	public static void main(String[] args) {
		/*String result = null;
		try {
			result = HttpUtil.doHttpPost("http://app1.sfda.gov.cn/datasearch/face3/search.jsp?tableId=25&curstart=1", null);
		} catch (ParseException | IOException e2) {
			e2.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(result);*/
		
		System.setProperty("webdriver.chrome.driver",
				"E://学习分享/新建文件夹/chromedriver.exe");
		
		int pageTotal = 11067;
		int currPage = 3668;
		
		//取消 chrome正受到自动测试软件的控制的信息栏  
        ChromeOptions options = new ChromeOptions();  
        options.addArguments("disable-infobars"); 
        
		WebDriver driver = new ChromeDriver(options);
		
		do {
			log.info("--start currPage ：" + currPage);
			List<String> hrefList = new ArrayList<String>();
			Document dbExistsPageDoc = MongoUtil.findOne("sfda_each_page_html", "href", currPage);
			if(dbExistsPageDoc!=null){
				org.jsoup.nodes.Document doc = Jsoup.parse(dbExistsPageDoc.getString("html"));
				Elements elements = doc.select("table:eq(1) tr a");
				for (Element element : elements) {
					String h = element.attr("href");
					hrefList.add(h);
				}
			}else{
				String href = "http://app1.sfda.gov.cn/datasearch/face3/search.jsp?tableId=25&curstart="+currPage;
				driver.get(href);
				log.info("--end currPage ：" + currPage);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("href", currPage);
				map.put("currPage", currPage);
				map.put("html", driver.getPageSource());
				MongoUtil.saveDoc("sfda_each_page_html", map);
				List<WebElement> listElements = driver.findElements(By.xpath("/html/body/table[2]/tbody/tr//a"));
				for (WebElement listEle : listElements) {
					String h = listEle.getAttribute("href");
					hrefList.add(h);
				}
			}
			
			/*for (String h : hrefList) {
				String detailhref = "http://app1.sfda.gov.cn/datasearch/face3/"+StringUtils.substringBetween(h,"javascript:commitForECMA(callbackC,'","',null)");
				if(MongoUtil.has("sfda_each_page_detail_html", "currHref",detailhref)){
					continue;
				}
				driver.get(detailhref);
				String pageSource = driver.getPageSource();
				SFDABean sfdaBean = new SFDABean();
				sfdaBean.setCurrHref(detailhref);
				sfdaBean.testByHtml(pageSource);
				sfdaBean.setSaveEngine(new MongoPersistent("sfda_each_page_detail_html"));
				sfdaBean.save();
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}*/
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (currPage++ <= pageTotal);
		driver.quit();
	}
}
