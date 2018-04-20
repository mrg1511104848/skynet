package org.skynet.frame.util.http;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.skynet.frame.base.SkynetProxy;
import org.skynet.frame.config.Config;

public class DefaultProxyParser implements ProxyParser{

	@Override
	public List<SkynetProxy> parseProxy() {
		List<SkynetProxy> skynetProxies = new ArrayList<SkynetProxy>();
		try {
			String html = HttpUtil.get(String.format(Config.PROXY_URL,Config.PROXY_INIT_COUNT), null);
			JSONObject json = null;
			try {
				json = JSONObject.fromObject(html);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
//			//System.out.println(json);
			JSONArray dataArray = json.getJSONArray("data");
			for (Object d : dataArray) {
				JSONObject dJsonObject = (JSONObject)d;
				String ip = dJsonObject.getString("ip");
				Integer port = Integer.parseInt(dJsonObject.getString("port"));
				skynetProxies.add(new SkynetProxy(ip, port+""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return skynetProxies;
	}
	
}
