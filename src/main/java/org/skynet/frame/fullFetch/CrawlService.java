package org.skynet.frame.fullFetch;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class CrawlService {
	@SuppressWarnings("unused")
	private List<Crawl> crawlerThreads = new CopyOnWriteArrayList<Crawl>();
	
	/**
     * 尝试终止
     */
    public void tryFinish(){
        /*boolean isRunning = false;
        for (Crawl crawlerThread: SkynetExecutor<Thread>) {
            if (crawlerThread.isRunning()) {
                isRunning = true;
                break;
            }
        }
        boolean isEnd = unVisitedUrlQueue.size()==0 && !isRunning;
        System.out.println(Thread.currentThread().getName()+" >> unVisitedUrlQueue.size() : "+unVisitedUrlQueue.size());
        if (isEnd) {
            logger.info(">>>>>>>>>>> xxl crawler is finished.");
            stop();
        }*/
    }
}
