package org.skynet.frame.base.task.itr;

import java.util.List;


public abstract class TaskPool {
	public abstract void init(List<Object> dataList);
	public abstract void put(Object obj);
	public abstract Object get();
}
