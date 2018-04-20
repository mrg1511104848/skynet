package org.skynet.frame.entity;

import java.util.HashMap;

@SuppressWarnings({ "hiding", "serial" })
public class AppendMap<String,Object> extends HashMap<String,Object>{

	public AppendMap<String, Object> appendMap(String key, Object value) {
		this.put(key, value);
		return this;
	}
}
