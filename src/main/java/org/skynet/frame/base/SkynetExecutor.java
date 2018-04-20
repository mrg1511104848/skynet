package org.skynet.frame.base;

import org.skynet.frame.config.Config;
import org.skynet.frame.execute.ExecutorItrface;

public class SkynetExecutor<T extends Thread> implements ExecutorItrface{
	public void execute(T deffault) throws InterruptedException{
		Thread[] threads = new Thread[Config.THREAD_COUNT];
        for (int x = 0; x < threads.length; x++) {
            threads[x] = new Thread(deffault);
            threads[x].start();
        }
        for (int x = 0; x < threads.length; x++) {
            threads[x].join();
        }
        //System.out.println("end");
	}

	@Override
	public void execute() {
		
	}
	public static void main(String[] args) {
		
	}
}
