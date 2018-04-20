package org.skynet.frame.iter;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.skynet.frame.annotation.Path;
import org.skynet.frame.base.SkynetEntity;
import org.skynet.frame.config.Config;
import org.skynet.frame.util.http.HttpUtil;
import org.skynet.frame.util.jsoup.JsoupUtil;

public abstract class HttpTestInterface extends JsoupUtil{
	@Path(needSave = false)
	private Logger log = Logger.getLogger(HttpTestInterface.class.getName());
	@Path(needSave=false)
	Map<String, String> headers = null;
	public <T  extends SkynetEntity> T test(String host,String href,Class<?> clazz, T deffault){
		if(StringUtils.isBlank(host)){
			throw new NullPointerException(" host is null");
		}
		headers = HttpUtil.getHeaders();
		headers.put("Host", host);
		try {
			return HttpUtil.getDetailPage(href, headers, clazz, deffault);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deffault;
	}
	public <T  extends SkynetEntity> T test(String href,Class<?  extends SkynetEntity> clazz, T deffault){
		if(href == null){
			return deffault;
		}
		href = HttpUtil.getParsedHref(href);
		String host = HttpUtil.getHost(href);
		headers = HttpUtil.getHeaders();
		if(host!=null)
			headers.put("Host", host);
		try {
			return HttpUtil.getDetailPage(href, headers, clazz, deffault);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		} 
	}
	
	/**
	 * 
	 * @param isMobile
	 * @param otherHeaders
	 * @param href
	 * @param clazz Xxx.class
	 * @param deffault xxx
	 * @return
	 */
	public <T extends SkynetEntity > T test(boolean isMobile,Map<String, String> otherHeaders,String href,Class<T> clazz, T deffault){
		return test(isMobile,otherHeaders, href, clazz, deffault, Config.BY_PROXY);
	}
	public <T  extends SkynetEntity> T test(Map<String, String> otherHeaders,String href,Class<T> clazz, T deffault){
		return test(false,otherHeaders, href, clazz, deffault, Config.BY_PROXY);
	}
	public <T  extends SkynetEntity> T test(Map<String, String> otherHeaders,String href,Class<T> clazz, T deffault,boolean proxy){
		return test(false,otherHeaders, href, clazz, deffault, proxy);
	}
	public <T  extends SkynetEntity> T test(boolean isMobile,Map<String, String> otherHeaders,String href,Class<T> clazz, T deffault,boolean proxy){
		headers = HttpUtil.getHeaders();
		if(otherHeaders!=null){
			Iterator<Entry<String, String>> itr = otherHeaders.entrySet().iterator();
			while (itr.hasNext()) {
				Entry<String, String> entry = itr.next();
				String key = entry.getKey();
				String value = entry.getValue();
				headers.put(key, value);
			}
		}
		try {
			return HttpUtil.getDetailPage(href, headers, clazz, deffault,proxy);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return deffault;
	}
	
	public <T  extends SkynetEntity> T test(HttpClient httpClient,Map<String, String> otherHeaders,String href,Class<T> clazz, T deffault){
		headers = HttpUtil.getHeaders();
		Iterator<Entry<String, String>> itr = otherHeaders.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, String> entry = itr.next();
			String key = entry.getKey();
			String value = entry.getValue();
			headers.put(key, value);
		}
		try {
			return HttpUtil.getDetailPage(href, headers, clazz, deffault , Config.BY_PROXY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deffault;
	}
	public <T  extends SkynetEntity> T testByHtml(String baseHref,String html,Class<T> clazz, T deffault){
		return HttpUtil.getDetailPage(baseHref, html, clazz, deffault);
	}
	public Map<String, String> getHeaders(boolean isMobile){
		return isMobile?HttpUtil.getMobileHeaders():HttpUtil.getHeaders();
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
}
