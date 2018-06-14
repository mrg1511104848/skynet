package org.skynet.frame;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.skynet.frame.util.mongo.MongoUtil;

import com.mongodb.client.MongoCursor;

public class SFDADeal {
	private static Logger log = Logger.getLogger(SFDADeal.class);
	public static void main(String[] args) {
//		dealStep2();
		System.out.println(StringUtils.isBlank(Jsoup.parse("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head></head><body></body></html>").select("body").text()));
	}
	private static void dealStep1(){
		MongoCursor<Document> cursor = MongoUtil.iterator("sfda_each_page_html_no_rep");
		List<String> hrefList = new ArrayList<String>();
		/*int idx = 1;
		
		while (idx<=11070) {
			Map<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("href", idx);
			boolean documentHas  = MongoUtil.has("sfda_each_page_html_no_rep",queryMap);
			if(!documentHas){
				System.err.println(idx);
				try {
					FileUtils.writeStringToFile(new File("C://errorLines.txt"), idx+"\n",true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
//			System.out.println(idx);
			idx++;
		}
		System.out.println();*/
		
//		MongoUtil.cpCollection("sfda_each_page_html", "sfda_each_page_html_no_rep");
		List<Integer> nums = new ArrayList<Integer>();
		while (cursor.hasNext()) {
			Document doc  = cursor.next();
			String html = doc.getString("html");
			Elements elements = Jsoup.parse(html).select("a:contains(国药)");
			for (Element element : elements) {
//				nums.add(Integer.parseInt(StringUtils.substringBefore(element.text(),".")));
				hrefList.add(element.text());
			}
		}
//		Collections.sort(nums);  
		
		System.out.println(hrefList.size());
		try {
			FileUtils.writeLines(new File("C://sfda校验集.txt"), hrefList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void dealStep2(){
		System.setProperty("webdriver.chrome.driver",
				"E://学习分享/新建文件夹/chromedriver.exe");
		
		
		//取消 chrome正受到自动测试软件的控制的信息栏  
        ChromeOptions options = new ChromeOptions();  
        options.addArguments("disable-infobars"); 
        
		WebDriver driver = new ChromeDriver(options);
		int currRun = 0;
		MongoCursor<Document> cursor = MongoUtil.iterator("sfda_each_page_html_no_rep",true);
		while (cursor.hasNext()) {
			Document doc  = cursor.next();
			String html = doc.getString("html");
			Elements elements = Jsoup.parse(html).select("a:contains(国药)");
			for (Element element : elements) {
				log.info("当前："+currRun++);
//				nums.add(Integer.parseInt(StringUtils.substringBefore(element.text(),".")));
				String href = StringUtils.substringBetween(element.attr("href"), "javascript:commitForECMA(callbackC,'","',null)");
				if(StringUtils.isBlank(href)){
					log.error(element.text());
					continue;
				}
				href = "http://app1.sfda.gov.cn/datasearch/face3/"+href;
				if(MongoUtil.has("sfda_each_page_html_no_rep_detail", "href", href)) continue;
				
				driver.get(href);
				
				if(driver.getPageSource()!=null){
					org.jsoup.nodes.Document pageSourceDoc = Jsoup.parse(driver.getPageSource());
					String bodyText = pageSourceDoc.select("body").text();
					if(StringUtils.isBlank(bodyText)){
						log.error(String.format("The html of href “%s” is blank ", href));
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						continue;
					}
				}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("href", href);
				map.put("html", driver.getPageSource());
				MongoUtil.saveDoc("sfda_each_page_html_no_rep_detail", map);
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private static void dealStep3(){
		
	}
	
	
	
	
	
	
	
	
	
}
