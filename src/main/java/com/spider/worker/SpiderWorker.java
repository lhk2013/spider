package com.spider.worker;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spider.fetcher.PageFetcher;
import com.spider.handler.ContentHandler;
import com.spider.model.FetchedPage;
import com.spider.model.SpiderParams;
import com.spider.parser.ContentParser;
import com.spider.queue.UrlQueue;
import com.spider.storage.DataStorage;

/**
 * 每一个worker就是一个爬虫线程，由主线程SpiderStarter创建
 * @author Administrator
 */
public class SpiderWorker implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(SpiderWorker.class);
	
//	private Boolean flag = false;
	
	private PageFetcher fetcher;
	private ContentHandler handler;
	private ContentParser parser;
	private DataStorage storage;
	
	private int threadIndex;
	
	
	public SpiderWorker(int threadIndex){
		this.threadIndex = threadIndex;
		this.fetcher = new PageFetcher();
		this.handler = new ContentHandler();
		this.parser = new ContentParser();
		this.storage = new DataStorage();
	}
	
	@Override
	public void run() {
		// 登录  如果有登录的情况下 这块应该比较麻烦
		
		// 当待抓取URL列队不为空时，执行爬去任务
		// 注： 当队列内容为空时，也不爬取任务已经结束了
		//     因为有可能是UrlQueue暂时空，其他worker线程还没有将新的URL放入队列
		//	        所以，这里可以做个等待时间，再进行抓取（二次机会）
		
		logger.info("线程-："+Thread.currentThread().getName()+"进入");
		if(!Thread.currentThread().getName().equals("Thread-0")) {
			try {
				logger.info("线程===="+Thread.currentThread().getName()+"休息2秒");
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		while (!UrlQueue.isEmpty()) {
			// 抓取URL指定的页面，并返回状态码和页面内容构成的FetchedPage对象
			FetchedPage fetchedPage = fetcher.getHtmlContentByUrl(UrlQueue.outElement());
			if(!handler.check(fetchedPage)) {
				// 切换IP等操作
				// TODO
				
				logger.info("Spider-" + threadIndex + ": switch IP to ");
				continue;
			}
			// 解析页面，获取目标数据
			Object targetData = parser.parse(fetchedPage);
			// 存储目标数据到数据存储（如DB）、存储已爬取的Url到VisitedUrlQueue
			storage.store(targetData);
			// delay
			try {
				Thread.sleep(SpiderParams.DEYLAY_TIME);
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		fetcher.close();
		logger.info("Spider-" + (--threadIndex) + ": stop..."+"size"+UrlQueue.size());
	}
	
}
