package org.skynet.frame.util.mongo;

import java.util.Date;

import org.bson.BsonDateTime;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;

public class BsonTool {
	/** 
     * Java对象转BsonValue对象 
     * @param obj 
     * @return 
     */  
    public static BsonValue objectToBsonValue(Object obj){  
        if (obj instanceof Integer){  
            return new BsonInt32((Integer) obj);  
        }  
  
        if (obj instanceof String){  
            return new BsonString((String) obj);  
        }  
  
        if (obj instanceof Long){  
            return new BsonInt64((Long) obj);  
        }  
  
        if (obj instanceof Date){  
            return new BsonDateTime(((Date) obj).getTime());  
        }  
        return new BsonNull();  
    }  
}
