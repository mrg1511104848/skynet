package org.skynet.frame.base;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.skynet.frame.annotation.Path;
import org.skynet.frame.entity.Method;
import org.skynet.frame.iter.HttpTestInterface;
import org.skynet.frame.persistent.itrface.SkynetPersistent;
import org.skynet.frame.util.date.DateUtil;
import org.skynet.frame.util.http.HttpUtil;
import org.skynet.frame.util.http.IpUtil;


public class SkynetEntity extends HttpTestInterface {
	@Path(needSave = false)
	private Logger log = Logger.getLogger(SkynetEntity.class.getName());
	@Path(value = "html", useText = false, needZLIB = true)
	public String html;

	@Path(isDefault = true)
	public String currHref;

	private boolean err;

	@Path(isDefault = true)
	private static String innerIp;

	@Path(isDefault = true)
	private static String outterIp;

	@Path(isDefault = true)
	private String createTime;

	@Path(needSave = false)
	public List<SkynetPersistent> skynetPersistents = new ArrayList<SkynetPersistent>();

	@Path(needSave = false)
	private long startTime;

	@Path(needSave = false)
	private long endTime;

	@Path(isDefault = true)
	private long timeConsuming;

	private String method = Method.GET;

	private String statusCode;

	private boolean proxy;

	private String redirectLocation;

	@Path(needSave = false)
	private String host;

	@Path(needSave = false)
	private Map<String, Object> params;
	
	@Path(needSave = false)
	private Boolean useProxy;
	
	@Path(needSave = false)
	private String userEncode;
	
	static{
		if (outterIp == null)
			outterIp = IpUtil.getOutterIp();
	}
	public SkynetEntity() {
	}

	public SkynetEntity(String href) {
		this.currHref = href;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getCurrHref() {
		return currHref;
	}

	public void setCurrHref(String currHref) {
		this.currHref = currHref;
	}
	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public SkynetEntity setSaveEngine(SkynetPersistent skynetPersistent) {
		if (!skynetPersistents.contains(skynetPersistent)) {
			this.skynetPersistents.add(skynetPersistent);
		}
		return this;
	}

	public SkynetEntity setSaveEngine(List<SkynetPersistent> skynetPersistents) {
		this.skynetPersistents = skynetPersistents;
		return this;
	}

	public void save() {
		for (SkynetPersistent skynetPersistent : skynetPersistents) {
			skynetPersistent.save(this);
		}
	}

	public boolean getErr() {
		return err;
	}

	public void setErr(boolean err) {
		this.err = err;
	}

	public String getInnerIp() {
		return innerIp;
	}

	public synchronized void setInnerIp() {
		// this.innerIp = innerIp;
		if (innerIp == null)
			innerIp = IpUtil.getLocalAddress();
	}

	public String getOutterIp() {
		return outterIp;
	}

	public void setOutterIp() {
		// this.outterIp = outterIp;
		synchronized (SkynetEntity.class) {
			if (outterIp == null)
				outterIp = IpUtil.getOutterIp();
		}
	}

	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime() {
		// this.createTime = createTime;
		this.createTime = new SimpleDateFormat(
				DateUtil.DATE_FORMAT_01)
				.format(new Date());
	}

	public long getTimeConsuming() {
		return timeConsuming;
	}

	public void setTimeConsuming() {
		this.timeConsuming = this.endTime - this.startTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public boolean isProxy() {
		return proxy;
	}

	public void setProxy(boolean proxy) {
		this.proxy = proxy;
	}

	public String getRedirectLocation() {
		return redirectLocation;
	}

	public void setRedirectLocation(String redirectLocation) {
		this.redirectLocation = redirectLocation;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Boolean getUseProxy() {
		return useProxy;
	}

	public void setUseProxy(Boolean useProxy) {
		this.useProxy = useProxy;
	}
	
	public String getUserEncode() {
		return userEncode;
	}

	public void setUserEncode(String userEncode) {
		this.userEncode = userEncode;
	}

	public void test() {
		if (StringUtils.isBlank(currHref)) {
			throw new NullPointerException(" currHref is null");
		}
		test(currHref, getClass(), this);
	}

	public void test(String host) {
		if (StringUtils.isBlank(currHref)) {
			throw new NullPointerException(" currHref is null");
		}
		test(host, currHref, getClass(), this);
	}

	public SkynetEntity testByHtml(String html) {
		/*if (StringUtils.isBlank(currHref)) {
			throw new NullPointerException(" currHref is null");
		}*/
		return HttpUtil.getDetailPage(this.getCurrHref(), html, getClass(),
				this);
	}
	public void saveHtmlToFile(String filePath) throws IOException{
		FileUtils.writeStringToFile(new File(filePath), this.html , "utf-8");
	}
}
