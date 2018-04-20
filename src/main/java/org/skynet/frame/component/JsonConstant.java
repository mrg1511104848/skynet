package org.skynet.frame.component;

public class JsonConstant {
	public static final String JSON_OBJECT = "jobj";
	public static final String JSON_ARRAY = "jarr";
	public static final String JSON_SPLIT = "=GGGG=";
	public static final String JSON_CENGJI_SPLIT = ">>>";
	public static String JSON_OBJECT_ALL = JsonConstant.JSON_OBJECT+JsonConstant.JSON_SPLIT+"%s"+JsonConstant.JSON_CENGJI_SPLIT;
	public static String JSON_ARRAY_ALL = JsonConstant.JSON_ARRAY+JsonConstant.JSON_SPLIT+"%s"+JsonConstant.JSON_CENGJI_SPLIT;
	public static final String TEXT = "text";
	public static String getJsonObjectSplit(String key){
		return String.format(JsonConstant.JSON_OBJECT_ALL, key);
	}
	public static String getJsonArraySplit(String key){
		return String.format(JsonConstant.JSON_ARRAY_ALL, key);
	}
}
