package org.skynet.frame.util.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MapRemoveNullUtil {
	/**
	* 递归删除map中的null值
	* @param map
	* @return
	*/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> removeNullEntry(Map<String, Object> map){
		Set<Entry<String, Object>> set = map.entrySet();
		Iterator<Entry<String, Object>> it = set.iterator();
		Map map2 = new HashMap();
		while(it.hasNext()){
			Entry<String, Object> en = it.next();
			if(!(en.getValue() instanceof Map)){
				if(null == en.getValue() || "".equals(en.getValue())){
				it.remove();
				}
			}else{
				map2 = (Map) en.getValue();
				removeNullEntry(map2);
			}
		}
		return map;
	}
      
      
    public static void main(String[] args) {  
    }  
}
