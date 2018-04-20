package org.skynet.frame.fullFetch;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.skynet.frame.base.SkynetThread;
import org.skynet.frame.config.Config;
import org.skynet.frame.util.encrypt.MD5Util;
import org.skynet.frame.util.http.HttpUtil;
import org.skynet.frame.util.http.IpUtil;
import org.skynet.frame.util.mongo.MongoUtil;
import org.skynet.frame.util.zlib.ZLib;

public class Crawl extends SkynetThread {
	private static String IP ;
	private CrawlEntity crawlConfig;
	static{
		IP = IpUtil.getOutterIp();
	}
	public Crawl() {
	}
	public Crawl(CrawlEntity crawlConfig) {
		this.crawlConfig = crawlConfig;
	}

	@Override
	public void run() {
		while (true) {
			//获取配置信息
			Map<String, Object> queryMap = crawlConfig.getQueryMap();
			Map<String, Object> updateMap = crawlConfig.getUpdateMap();
			String urlCollection = crawlConfig.getUrlCollectionName();
			List<String> regexList = crawlConfig.getWhiteUrlRegexList();
			List<String> blackUrlRegexList = crawlConfig.getBlankUrlRegexList();
			if(StringUtils.isBlank(urlCollection)){
				throw new InvalidParameterException(" urlCollection is null !");
			}
			if(queryMap == null || updateMap == null || queryMap.size() == 0 || updateMap.size() == 0){
				throw new InvalidParameterException(" queryMap or updateMap is null !");
			}
			
			org.bson.Document find = null;
			synchronized (this) {
				find = MongoUtil.findOneAndUpdate(urlCollection, queryMap,
						updateMap);
			}
			if (find == null) {
				log.warn("find no data....");
				break;
			}
			String url = find.get("url").toString();
//			url = "http://shop.99114.com/51127952";
			//对于已录入的url检查，如果不合法，则标识一下，并continue
			if ((matchRegexList(url, blackUrlRegexList)) || !matchRegexList(url, regexList)) {
				log.info(String.format(" -- url %s is not valid , continue",url));
				Map<String, Object> query = new HashMap<String, Object>();
				query.put("url", url);
				Map<String, Object> update = new HashMap<String, Object>();
				update.put("del", 1);
				MongoUtil.updateDoc(urlCollection, query, update);
				continue;
			}
			Document doc = null;
			try {
				String html = HttpUtil.get(url, null, Config.REQEUST_TIME).get(1);
				doc = Jsoup.parse(html);
			} catch (Exception e) {
				log.error("crawl >> " + url + " >> " + e.getMessage());
				continue;
			}
			if (doc == null) {
				log.warn("crawl >> " + url + " >> doc is null, continue!" );
				continue;
			}
			commitUrl(url);
			analysisUrls(url,doc);
			commitContent(doc.html(), url);
		}
	}
	private boolean matchRegexList(String str,List<String> regexList){
		if(regexList!=null && regexList.size()>0){
			for (int i = 0; i < regexList.size(); i++) {
				if(str.matches(regexList.get(i))){
					return true;
				}
			}
		}
		return false;
	}
	private void analysisUrls(String baseUrl,Document doc) {
		List<String> regexList = crawlConfig.getWhiteUrlRegexList();
		List<String> blackUrlRegexList = crawlConfig.getBlankUrlRegexList();
		Elements select = doc.select("a[href]");
		for (Element link : select) {
			if (link!=null && link.attr("href").startsWith("//")) {
				link.attr("href", "http:" + link.attr("href"));
			}
			String url = HttpUtil.getAbsolutePath(baseUrl, link.attr("href"));
			if ((matchRegexList(url, blackUrlRegexList))||
					url == null || !matchRegexList(url, regexList)) {
				log.info(String.format(" -- url %s is not valid , continue",url));
				continue;
			}
			saveUrl(url);
		}
	}

	private void saveUrl(String url) {
		String urlCollection = crawlConfig.getUrlCollectionName();
		if(StringUtils.isBlank(urlCollection)){
			throw new InvalidParameterException(" urlCollectionName is null !");
		}
		
		List<String> specialMatchList = crawlConfig.getSpecialMatcherRegexList();
		int isSpecial = 0;
		if(specialMatchList!=null&&specialMatchList.size()>0 && url.matches(specialMatchList.get(0))){
			isSpecial = 1;
		}
		if(MongoUtil.has(urlCollection, "url", url)){
			log.info(String.format(" has this url [url] %s",url));
			return;
		}
		Map<String, Object> urlSaveMap = new HashMap<String, Object>();
		urlSaveMap.put("url", url);
		urlSaveMap.put("md5", MD5Util.encode(url));
		urlSaveMap.put("st", 0);
		urlSaveMap.put("date", new Date());
		urlSaveMap.put("isSpecial", isSpecial);
		urlSaveMap.put("ip", IP);
		
		boolean saveResult = MongoUtil.saveDoc(urlCollection, urlSaveMap);
		if(saveResult){
			log.info(" -- save the url " + url);
		}
	}

	private void commitUrl(String url) {
		String urlCollection = crawlConfig.getUrlCollectionName();
		if(StringUtils.isBlank(urlCollection)){
			throw new InvalidParameterException(" urlCollection is null !");
		}
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("url", url);
		Map<String, Object> update = new HashMap<String, Object>();
		update.put("st", 2);
		MongoUtil.updateDoc(urlCollection, query, update);
	}

	private void commitContent(String content, String url) {
		String contentCollection = crawlConfig.getContentCollectionName();
		
		if(StringUtils.isBlank(contentCollection)){
			throw new InvalidParameterException(" contentCollection is null !");
		}
		
		List<String> specialMatchList = crawlConfig.getSpecialMatcherRegexList();
		int isSpecial = 0;
		if(specialMatchList!=null&&specialMatchList.size()>0 && url.matches(specialMatchList.get(0))){
			isSpecial = 1;
		}
		try {
			Document doc = Jsoup.parse(content);
			String title = doc.select("#signtitle") != null ? doc.select("#signtitle").text() : "";
			Map<String, Object> jo = new HashMap<String, Object>();
			jo.put("url", url);
			jo.put("content", ZLib.compress(content.getBytes()));
			jo.put("title", title);
			jo.put("isSpecial", isSpecial);
			jo.put("date", new Date());
			jo.put("ip", IP);
			MongoUtil.saveDoc(contentCollection, jo);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}