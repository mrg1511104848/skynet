package org.skynet.frame.annotation.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class AnnotationUtil {
	public static Field[] getFieldsInfo(Class<?> clazz) {
		List<Field> fields = ReflectionUtils.getDeclaredFields(clazz);
		Field[] fieldarray = new Field[fields.size()];
		Field[] fieldarrayParsed = fields.toArray(fieldarray);
		return fieldarrayParsed;
	}

	public static Method getDeclaredMethod(Object object, String methodName,
			Class<?>... parameterTypes) {
		Method method = null;
		for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz
				.getSuperclass()) {
			try {
				method = clazz.getDeclaredMethod(methodName, parameterTypes);
				return method;
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static void main(String[] args) {
	}
}
