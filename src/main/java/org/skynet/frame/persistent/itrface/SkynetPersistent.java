package org.skynet.frame.persistent.itrface;

import org.skynet.frame.persistent.MongoIndex;

/**
 * 保存接口类
 * @author 2bu
 *
 */
public interface SkynetPersistent {
	/**
	 * 保存
	 * @param obj
	 * @return
	 */
	public boolean save(Object obj);
	
	public void addIndex(MongoIndex... indexs);
}
