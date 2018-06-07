package org.skynet.frame.util.translate.impl;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.skynet.frame.util.http.HttpUtil;
import org.skynet.frame.util.translate.Translate;

public class IcibaTranslate extends Translate{
	private Logger log = Logger.getLogger(IcibaTranslate.class);
	@Override
	public String startTranslate(String sentence) {
		if(sentence == null) return "";
		String dbFanYi = getFanYi(sentence);
		if(dbFanYi!=null){
			return dbFanYi;
		}
		String newSentence;
		String sentenceHtm = null;
		String sencenceCn = null;
		try {
			newSentence = URLEncoder.encode(sentence, "utf-8");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("w", sentence);
			sentenceHtm = HttpUtil.doHttpPost("http://fy.iciba.com/ajax.php?a=fy", params);
			JSONObject contentJsonObject = JSONObject.fromObject(sentenceHtm).getJSONObject("content");
			if(contentJsonObject.has("out")){
				sencenceCn = contentJsonObject.getString("out");
			}else if(contentJsonObject.has("word_mean")){
				sencenceCn = contentJsonObject.getJSONArray("word_mean").get(0).toString();
			}
		} catch (Exception e) {
			log.error("Translate error",e);
			return null;
		}
		/*org.jsoup.nodes.Document doc = Jsoup.parse(sentenceHtm);
		Element sencenceEnEle = doc.select(".js-base-info").first();
		String sencenceEn = null;
		if(sencenceEnEle != null){
			Elements enElements  = doc.select(".clearfix .prop+p");
			int s = enElements.size();
			if(s > 0){
				sencenceEn = enElements.text();
			}else{
				sencenceEn = sencenceEnEle.text();
				sencenceEn = sencenceEn.replace(sentence.trim(), "").replace("一键安装桌面版，查词翻译快三倍 选择要添加的生词本 新建生词本 进入生词本", "");
			}
		}else{
			sencenceEnEle = doc.select(".in-base-top div").first();
			
			if(sencenceEnEle != null){
				sencenceEn = sencenceEnEle.text();
			}
		}*/
		saveFanYi(sentence,sencenceCn);
		return sencenceCn;
	}
	public static void main(String[] args) {
		System.out.println(new IcibaTranslate().startTranslate("Adult, Effective; Pediatric, Effective"));;
	}
}
