package org.skynet.frame.fullFetch;

import java.util.List;
import java.util.Map;

public class CrawlEntity {
	private String urlCollectionName;
	private String contentCollectionName;
	private List<String> blankUrlRegexList;
	private List<String> whiteUrlRegexList;
	private List<String> specialMatcherRegexList;
	private Map<String, Object> queryMap ; 
	private Map<String, Object> updateMap ; 
	
	
	public String getUrlCollectionName() {
		return urlCollectionName;
	}

	public void setUrlCollectionName(String urlCollectionName) {
		this.urlCollectionName = urlCollectionName;
	}

	public String getContentCollectionName() {
		return contentCollectionName;
	}

	public void setContentCollectionName(String contentCollectionName) {
		this.contentCollectionName = contentCollectionName;
	}

	public List<String> getBlankUrlRegexList() {
		return blankUrlRegexList;
	}

	public void setBlankUrlRegexList(List<String> blankUrlRegexList) {
		this.blankUrlRegexList = blankUrlRegexList;
	}

	public List<String> getWhiteUrlRegexList() {
		return whiteUrlRegexList;
	}

	public void setWhiteUrlRegexList(List<String> whiteUrlRegexList) {
		this.whiteUrlRegexList = whiteUrlRegexList;
	}

	public List<String> getSpecialMatcherRegexList() {
		return specialMatcherRegexList;
	}

	public void setSpecialMatcherRegexList(List<String> specialMatcherRegexList) {
		this.specialMatcherRegexList = specialMatcherRegexList;
	}

	public Map<String, Object> getQueryMap() {
		return queryMap;
	}

	public void setQueryMap(Map<String, Object> queryMap) {
		this.queryMap = queryMap;
	}

	public Map<String, Object> getUpdateMap() {
		return updateMap;
	}

	public void setUpdateMap(Map<String, Object> updateMap) {
		this.updateMap = updateMap;
	}
}
