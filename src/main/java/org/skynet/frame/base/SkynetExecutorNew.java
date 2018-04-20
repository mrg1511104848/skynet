package org.skynet.frame.base;

import org.apache.commons.lang3.StringUtils;
import org.skynet.frame.config.Config;
import org.skynet.frame.execute.ExecutorItrface;

public class SkynetExecutorNew implements ExecutorItrface {
	public void execute() throws Exception{
		Thread[] threads = new Thread[Config.THREAD_COUNT];
		if(StringUtils.isBlank(Config.RUN_CLASS)){
			return;
		}
		@SuppressWarnings("rawtypes")
		Class onwClass = Class.forName(Config.RUN_CLASS);
    	Object o = onwClass.newInstance();
    	if(o == null || !(o instanceof Thread)){
    		throw new Exception("Run Class is not invalid !");
    	}
        for (int x = 0; x < threads.length; x++) {
            threads[x] = new Thread((Thread)o);
            threads[x].start();
        }
        for (int x = 0; x < threads.length; x++) {
            try {
				threads[x].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
         
        //System.out.println("end");
	}
}
