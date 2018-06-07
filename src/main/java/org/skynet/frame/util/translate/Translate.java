package org.skynet.frame.util.translate;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.skynet.frame.util.mongo.MongoUtil;

public abstract class Translate implements ITranslate{
	private static final String default_fayi_collection = "fanyi";
	public static void saveFanYi(String sentence,String fanYiResult){
		saveFanYi(sentence, fanYiResult, default_fayi_collection);
	}
	public static void saveFanYi(String sentence,String fanYiResult,String collectionName){
		if(fanYiResult==null) return;
		Map<String, Object> fanyiMap = new HashMap<String, Object>();
		fanyiMap.put("sentenceCn", sentence);
		fanyiMap.put("sentenceEn", fanYiResult);
		MongoUtil.saveDoc(collectionName, fanyiMap);
	}
	public static String getFanYi(String sentenceCn){
		return getFanYi(sentenceCn, default_fayi_collection);
	}
	public static String getFanYi(String sentenceCn,String collectionName){
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("sentenceCn", sentenceCn);
		Document doc = MongoUtil.findOne(collectionName, queryMap);
		if(doc == null) 
			return null;
		return doc.getString("sentenceEn");
	}
	public String translate(String sentence,String collectionName){
		if(sentence == null) return "";
		String dbFanYi = getFanYi(sentence,collectionName);
		if(dbFanYi!=null){
			return dbFanYi;
		}
		String translateResult = startTranslate(sentence);
		saveFanYi(sentence,translateResult,collectionName);
		return translateResult;
	}
	public abstract String startTranslate(String sentence);
}
