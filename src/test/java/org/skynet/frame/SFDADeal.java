package org.skynet.frame;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.skynet.frame.util.mongo.MongoUtil;

import com.mongodb.client.MongoCursor;

public class SFDADeal {
	public static void main(String[] args) {
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
}
