package org.skynet.frame.base;

public class SkynetProxy {
	private String ip;
	private String port;

	public SkynetProxy(String ip, String port) {
		super();
		this.ip = ip;
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	@Override
	public boolean equals(Object obj) {
		// 判断要判断的对象是不是由SkynetProxy创建的
		if (!(obj instanceof SkynetProxy)) {
			return false;
		}
		SkynetProxy sp = (SkynetProxy) obj;// 强制转换
		return sp.getIp().equals(this.ip) && sp.getPort().equals(this.port);
	}

	@Override
	public String toString() {
		return "SkynetProxy [ip=" + ip + ", port=" + port + "]";
	}

}
