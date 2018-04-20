package org.skynet.frame.entity;

import org.apache.http.impl.client.CloseableHttpClient;

public class SkynetHttpEntity {
	private String html;
	private byte[] bytes;
	private CloseableHttpClient client;
	private String redirectLocation;
	private int statusCode;
	public SkynetHttpEntity() {
		super();
	}
	public SkynetHttpEntity(byte[] bytes, CloseableHttpClient client) {
		super();
		this.bytes = bytes;
		this.client = client;
	}
	public SkynetHttpEntity(String html, CloseableHttpClient client) {
		super();
		this.html = html;
		this.client = client;
	}
	public SkynetHttpEntity(String res, CloseableHttpClient httpclient,
			String redirectLocation) {
		this.html = res;
		this.client = httpclient;
		this.redirectLocation = redirectLocation;
	}
	
	public SkynetHttpEntity(String html, CloseableHttpClient client,
			String redirectLocation, int statusCode) {
		super();
		this.html = html;
		this.client = client;
		this.redirectLocation = redirectLocation;
		this.statusCode = statusCode;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	public CloseableHttpClient getClient() {
		return client;
	}
	public void setClient(CloseableHttpClient client) {
		this.client = client;
	}
	public byte[] getBytes() {
		return bytes;
	}
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	public String getRedirectLocation() {
		return redirectLocation;
	}
	public void setRedirectLocation(String redirectLocation) {
		this.redirectLocation = redirectLocation;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
}
