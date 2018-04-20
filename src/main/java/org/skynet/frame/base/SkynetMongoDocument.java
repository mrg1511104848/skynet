package org.skynet.frame.base;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.Document;

public class SkynetMongoDocument{
	
    public static List<Map<String, Object>> getArray(final Document doc,final Object key) {
        return (List<Map<String, Object>>) doc.get(key);
    }
}
