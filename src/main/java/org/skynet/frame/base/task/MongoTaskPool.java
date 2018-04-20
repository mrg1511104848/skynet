package org.skynet.frame.base.task;

import java.util.List;

import org.skynet.frame.base.task.itr.TaskPool;


public class MongoTaskPool extends TaskPool{

	@Override
	public synchronized void put(Object obj) {
	}

	@Override
	public synchronized Object get() {
		return null;
	}

	@Override
	public synchronized void init(List<Object> dataList) {
		
	}
}
