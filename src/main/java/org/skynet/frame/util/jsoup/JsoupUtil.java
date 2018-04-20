package org.skynet.frame.util.jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.skynet.frame.config.Config;
import org.skynet.frame.util.http.HttpUtil;

public class JsoupUtil {
	public Document get(String href){
		String html = null;
		try {
			html = HttpUtil.get(href, null,Config.REQEUST_TIME).get(1);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
		return parse(html);
	}
	public Document parse(String html){
		return Jsoup.parse(html);
	}
	public Elements getElements(Document doc,String selector){
		return doc.select(selector);
	}
	public Element getElement(Document doc,String selector){
		Elements elements = getElements(doc, selector);
		if(elements==null||elements.size()==0) 
			return null;
		return elements.first();
	}
	public List<String> getHrefList(Document doc,String selector,String attr){
		return getHrefList(doc, selector, attr, true);
	}
	public List<String> getHrefList(Document doc,String selector,String attr,boolean needAbsolute){
		List<String> list = new ArrayList<String>();
		Elements elements = doc.select(selector);
		for (Element element : elements) {
			list.add(element.attr(attr));
		}
		return list;
	}
	public List<String> getTextList(Document doc,String selector){
		List<String> list = new ArrayList<String>();
		Elements elements = doc.select(selector);
		for (Element element : elements) {
			list.add(element.text());
		}
		return list;
	}
	public Map<String, List<String>> getHrefAndTextList(Document doc,String selector,String attr){
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		Elements elements = doc.select(selector);
		if(StringUtils.isBlank(attr)){
			attr = "href";
		}
		List<String> attrList = new ArrayList<String>();
		List<String> textList = new ArrayList<String>();
		for (Element element : elements) {
			String attribute = element.attr(attr);
			String text = element.text();
			attrList.add(attribute);
			textList.add(text);
		}
		map.put("attrList", attrList);
		map.put("textList", textList);
		return map;
	}
	public Map<String, List<String>> getHrefAndTextList(Document doc,String selector){
		return getHrefAndTextList(doc, selector,null);
	}
}
