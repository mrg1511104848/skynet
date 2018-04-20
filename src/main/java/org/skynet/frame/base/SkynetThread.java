package org.skynet.frame.base;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skynet.frame.util.mongo.MongoUtil;

import com.mongodb.client.MongoCollection;

public class SkynetThread extends Thread{
	public Log log = LogFactory.getLog(this.getClass().getName());
	public org.bson.Document executeBefore(String collectionName){
		return executeBefore(MongoUtil.getCollection(collectionName), 0, 1);
	}
	public org.bson.Document executeBefore(MongoCollection<org.bson.Document> collection){
		return executeBefore(collection, 0, 1);
	}
	public org.bson.Document executeBefore(MongoCollection<org.bson.Document> collection, int qSt , int upSt){
		Map<String,Object> queryMap = new HashMap<String, Object>();
		queryMap.put("st", qSt);
//		queryMap.put("typeUrl" , "http://www.11467.com/shenzhen/search/4029.htm");
		Map<String,Object> updateMap = new HashMap<String, Object>();
		updateMap.put("st", upSt);
		org.bson.Document document = null;
		synchronized (this) {
			document = MongoUtil.findOneAndUpdate(collection, queryMap, updateMap);	
		}
		if(document==null){
			log.info("--is all over！");
			//System.out.println(Thread.currentThread().getName()+"--over");
			return null;
		}
		return document;
	}
	public void executeAfter(String collectionName,Map<String,Object> queryMap,Map<String,Object> updateMap){
		MongoUtil.findOneAndUpdate(collectionName, queryMap, updateMap);
	}
	public void executeAfter(String collectionName,String qname,Object qvalue,String uname,Object uvalue){
		Map<String, Object> queryMap = new HashMap<String, Object>();
		Map<String, Object> updateMap = new HashMap<String, Object>();
		queryMap.put(qname, qvalue);
		updateMap.put(uname, uvalue);
		MongoUtil.findOneAndUpdate(collectionName, queryMap, updateMap);
	}
	public void executeAfter(MongoCollection<org.bson.Document> collection,Map<String,Object> queryMap,Map<String,Object> updateMap){
		MongoUtil.findOneAndUpdate(collection, queryMap, updateMap);
	}
}
