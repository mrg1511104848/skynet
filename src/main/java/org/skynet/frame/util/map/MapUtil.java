package org.skynet.frame.util.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONNull;

public class MapUtil {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void cpAndRemoveNullValue(Map map,Map newMap){
		if(map==null) throw new NullPointerException("Map must not be null!");
		Iterator<Entry> itr = map.entrySet().iterator();
		while (itr.hasNext()) {
			Entry itrNext = itr.next();
			Object value = itrNext.getValue();
			if(value instanceof Map){
				newMap.put(itrNext.getKey(), (Map)value);
				cpAndRemoveNullValue((Map)value, (Map)value);
			}else if(!(value==null||value instanceof JSONNull)){
				newMap.put(itrNext.getKey(), value);
			}
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void removeNullValue(Map map){
		if(map==null) throw new NullPointerException("Map must not be null!");
		Iterator<Entry> itr = map.entrySet().iterator();
		while (itr.hasNext()) {
			Entry itrNext = itr.next();
			Object value = itrNext.getValue();
			if(value==null){
				itr.remove();
			}else if(value instanceof JSONNull){
				itr.remove();
			}else if(value instanceof Map){
				removeNullValue((Map)value);
			}
		}
	}
	
	/**
     * 使用 Map按value进行排序
     * @param map
     * @return
     */
    public static Map<String, Integer> sortMapByValue(Map<String, Integer> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String, Integer>>(
                oriMap.entrySet());
        Collections.sort(entryList, new MapValueComparator());

        Iterator<Map.Entry<String, Integer>> iter = entryList.iterator();
        Map.Entry<String, Integer> tmpEntry = null;
        while (iter.hasNext()) {
            tmpEntry = iter.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
    }
    static class MapValueComparator implements Comparator<Map.Entry<String, Integer>> {

        @Override
        public int compare(Entry<String, Integer> me1, Entry<String, Integer> me2) {

            return me1.getValue().compareTo(me2.getValue());
        }
    }
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws Exception {
		Collection<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a", null);
		map.put("ewq", 2222);
		Map<String, Object> subMap = new HashMap<String, Object>();
		subMap.put("d", 1);
		subMap.put("c", 2);
		subMap.put("e", null);
		Map<String, Object> subsubMap = new HashMap<String, Object>();
		subsubMap.put("gh", 1);
		subsubMap.put("e", null);
		subMap.put("b", subsubMap);
		map.put("b", subMap);
		list.add(map);
		Collection<Map<String, Object>> collection = Collections.unmodifiableCollection(list);  ;
		Map newMap = new HashMap();
		newMap.put("a", collection);
//		cpAndRemoveNullValue(map,newMap);
//		System.out.println(newMap);
		removeNullValue(newMap);
		System.out.println(newMap);
		
		
	}
}
