package org.skynet.frame.util.mongo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.skynet.frame.annotation.Path;
import org.skynet.frame.annotation.util.AnnotationUtil;
import org.skynet.frame.util.encrypt.MD5Util;
import org.skynet.frame.util.zlib.ZLib;

public class BeanUtil {
//	private static Logger log = Logger.getLogger(BeanUtil.class);
	// Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map
	public static Map<String, Object> transBean2Map(Object obj) {
		if (obj == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Field[] fields = AnnotationUtil.getFieldsInfo(obj.getClass());
			for (Field field : fields) {
				String fieldName = field.getName();
				String fieldNameFirst = fieldName.substring(0, 1).toUpperCase();
				String fieldNameOther = fieldName.substring(1);
				String getMethodName = "get" + fieldNameFirst + fieldNameOther;
//				String setMethodName = "set" + fieldNameFirst + fieldNameOther;
				Method getMethod = AnnotationUtil.getDeclaredMethod(obj,
						getMethodName);
				if(getMethod==null) {
//					log.warn(String.format("the %s getMethod is blank",fieldName));
					continue;
				}
//				Method setMethod = AnnotationUtil.getDeclaredMethod(obj.getClass(),
//						setMethodName, type);
				Object value = getMethod.invoke(obj);
				if(value==null) continue;
				if (field.isAnnotationPresent(Path.class) == true) {
					Path pathInfo = field.getAnnotation(Path.class);
					boolean needSave = pathInfo.needSave();
					if(!needSave) continue;
					boolean needMd5 = pathInfo.needMD5();
					boolean needZLIB = pathInfo.needZLIB();
					
					if(needMd5){
						value = MD5Util.encode(value.toString());
					}
					if(needZLIB){
						value = ZLib.compress(value.toString().getBytes());
					}
				}
				map.put(fieldName, value);
			}
		} catch (Exception e) {
			System.out.println();
			System.out.println("transBean2Map Error " + e);
		}

		return map;

	}
}
