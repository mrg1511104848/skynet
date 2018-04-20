package org.skynet.frame.util.http;

import java.util.ArrayList;
import java.util.List;

import org.skynet.frame.annotation.Path;
import org.skynet.frame.base.SkynetEntity;
import org.skynet.frame.base.SkynetProxy;

public class TestProxyParser2 extends SkynetEntity implements ProxyParser{
	@Path(value="#list tbody tr td:eq(0)")
	private List<String> ipList ;
	@Path(value="#list tbody tr td:eq(1)")
	private List<String> portList ;
	
	public List<String> getIpList() {
		return ipList;
	}

	public void setIpList(List<String> ipList) {
		this.ipList = ipList;
	}

	public List<String> getPortList() {
		return portList;
	}

	public void setPortList(List<String> portList) {
		this.portList = portList;
	}

	@Override
	public List<SkynetProxy> parseProxy() {
		TestProxyParser2 testProxyParser = new TestProxyParser2();
		testProxyParser.setCurrHref("http://www.httpsdaili.com/?stype=3");
		testProxyParser.setUseProxy(false);
		testProxyParser.setUserEncode("gbk");
		testProxyParser.test();
		List<String> ipList = testProxyParser.getIpList();
		List<String> portList = testProxyParser.getPortList();
		List<SkynetProxy> skynetProxies = new ArrayList<SkynetProxy>(); 
		for (int i = 0; i < ipList.size(); i++) {
			skynetProxies.add(new SkynetProxy(ipList.get(i),portList.get(i)));
		}
		return skynetProxies;
	}
	public static void main(String[] args) {
		new TestProxyParser2().parseProxy();
	}

}
