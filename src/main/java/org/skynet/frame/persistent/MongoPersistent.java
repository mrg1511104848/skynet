package org.skynet.frame.persistent;

import java.util.List;

import org.skynet.frame.persistent.itrface.SkynetPersistent;
import org.skynet.frame.util.mongo.MongoUtil;

public class MongoPersistent implements SkynetPersistent {
	private String collectionName;
	public MongoPersistent(String collectionName) {
		this.collectionName = collectionName;
	}
	public MongoPersistent(String collectionName,List<MongoIndex> idxList) {
		this.collectionName = collectionName;
		if(idxList!=null){
			for (MongoIndex mongoIndex : idxList) {
				MongoUtil.addIndex(collectionName, mongoIndex.getIndexName(), mongoIndex.getUnique());
			}
		}
	}
	@Override
	public boolean save(Object obj) {
		return MongoUtil.saveBean(collectionName, obj);
	}
	@Override
	public void addIndex(MongoIndex... indexs) {
		MongoUtil.addIndexs(collectionName, indexs);
	}

}
