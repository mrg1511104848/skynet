package org.skynet.frame.util.mongo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class BeanFromDBObject {
	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("u", 1);
		map.put("p", 2);
		System.out.println(getDBObject(map));;
	}
	 /**
     * @Description:bean-->DBObject
     * @param  bean
     * @return DBObject 返回类型
     */
    public static <T> DBObject getDBObject(T bean) {
        if (bean == null) {
            return null;
        }
        DBObject obj = new BasicDBObject();
        Field[] field = bean.getClass().getDeclaredFields();
        for (Field f : field) {
            String name = f.getName();
            if (!f.isAccessible()) {
                f.setAccessible(true);
            }
            try {
                Object oj = f.get(bean);
                if (oj == null) {
                    obj.put(name, "");
                } else if (oj instanceof Integer) {
                    int value = ((Integer) oj).intValue();
                    obj.put(name, value);
                } else if (oj instanceof Double) {
                    Double value = ((Double) oj).doubleValue();
                    obj.put(name, value);
                } else if (oj instanceof Float) {
                    Float value = ((Float) oj).floatValue();
                    obj.put(name, value);
                } else if (oj instanceof Boolean) {
                    Boolean value = ((Boolean) oj).booleanValue();
                    obj.put(name, value);
                } else if (oj instanceof Long) {
                    Long value = ((Long) oj).longValue();
                    obj.put(name, value);
                } else {
                    obj.put(name, oj);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return obj;

    }
}
