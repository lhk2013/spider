package com.spider.test;

import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MyThread implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(MyThread.class);
	private boolean flag;
	@Override
	public void run() {
		logger.info("线程- "+Thread.currentThread().getName()+" 进入"+" flag:"+flag);
		
//		if(!Thread.currentThread().getName().equals("main")) {
//			try {
//				logger.info("线程："+Thread.currentThread().getName()+" 休息1s "+flag);
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		
		
		if(flag) {
			logger.info("线程-"+Thread.currentThread().getName());
			try {
				logger.info("线程-"+Thread.currentThread().getName()+" 休息5秒");
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.info(UrlQueue.isEmpty()?"列队为空":"列队不为空 "+UrlQueue.getElement());
		while(!UrlQueue.isEmpty()) {
			flag = true;
			
			String url = UrlQueue.outElement();
			logger.info("线程-" + Thread.currentThread().getName() + " 开始处理URL:"+url+" | 全局flag设置为:"+flag);
			int i = Integer.parseInt(String.valueOf(url.charAt(5)));
			if(i%2==0) {
				int j = new Random().nextInt(100);
				UrlQueue.addElement("wURL-"+j);
				logger.info("w添加 URL-"+j+" 到列队");
			}
		}
		logger.info("线程-" + Thread.currentThread().getName() + ": 结束...");
	}
}

public class TestThread {
	private static final Logger logger = LoggerFactory.getLogger(TestThread.class);
	public static void main(String[] args) {
		initThread();
		initQueue();
	}
	
	private static void initThread() {
		for(int i=0;i<3;i++) {
			new Thread(new MyThread()).start();
		}
	}
	
	private static void initQueue() {
//		try {
			for(int i=0;i<=10; i++) {
				UrlQueue.addElement("fURL-"+i);
				logger.info("f添加 URL-"+i+" 到列队");
//				TimeUnit.SECONDS.sleep(1);
			}
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
}

class UrlQueue {
	private static BlockingDeque<String> urlDeques = new LinkedBlockingDeque<>();
	
	public static void addElement(String url) {
		try {
			urlDeques.putLast(url);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static String outElement() {
		try {
			return urlDeques.takeFirst();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void addFirstElement(String url){
		try {
			urlDeques.putFirst(url);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static String getElement() {
		return urlDeques.peek();
	}
	public static boolean isEmpty() {
		return urlDeques.isEmpty();
	}
	public static int size() {
		return urlDeques.size();
	}
	public static boolean isContains(String url) {
		return urlDeques.contains(url);
	}
}




/*

Random random = new Random();
if(random.nextBoolean()) {
	int i = random.nextInt();
	UrlQueue.addElement("RandomURL-"+i);
	logger.info("Random添加 URL-"+i+" 到列队");
	
	try {
		TimeUnit.SECONDS.sleep(2);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
}

*/