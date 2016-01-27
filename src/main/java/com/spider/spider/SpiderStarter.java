package com.spider.spider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spider.down.DownImg;
import com.spider.model.Data;
import com.spider.model.SpiderParams;
import com.spider.queue.UrlQueue;
import com.spider.utils.DBConn;
import com.spider.worker.SpiderWorker;

public class SpiderStarter {
	private static final Logger logger = LoggerFactory.getLogger(SpiderStarter.class);
	public static void main(String[] args) throws InterruptedException{
		// 初始化配置参数
		initializeParams();
		// 初始化爬取队列
		initializeQueue();
		// 创建worker线程并启动
		for(int i = 1; i <= SpiderParams.WORKER_NUM; i++){
			new Thread(new SpiderWorker(i)).start();
		}
		
//		TimeUnit.SECONDS.sleep(10);
		logger.info("开始下载图片任务！");
		DownImg.DowImgTask();
	}
	/**
	 * 准备初始的爬取链接
	 */
	private static void initializeQueue(){
		// Mzitu
		for(int i = 1; i <= 1; i++){
			String url = "http://www.mzitu.com/page/" + i;
			UrlQueue.addElement(new Data(null,url));
			logger.info("当前页=="+url);
		}
	}
	
	/**
	 * 初始化配置文件参数
	 */
	private static void initializeParams(){
		InputStream in;
		try {
//			in = new BufferedInputStream(new FileInputStream("spider.properties"));
			in =SpiderStarter.class.getResourceAsStream("spider.properties");
			Properties properties = new Properties();
			properties.load(in);
			
			// 从配置文件中读取数据库连接参数
			DBConn.CONN_URL = properties.getProperty("DB.connUrl");
			DBConn.USERNAME = properties.getProperty("DB.username");
			DBConn.PASSWORD = properties.getProperty("DB.password");
			
			// 从配置文件中读取参数
			SpiderParams.WORKER_NUM = Integer.parseInt(properties.getProperty("spider.threadNum"));
			SpiderParams.DEYLAY_TIME = Integer.parseInt(properties.getProperty("spider.fetchDelay"));
			SpiderParams.FILEPATH = properties.getProperty("spider.filepath");
			SpiderParams.SITECHARSET = properties.getProperty("spider.siteCharset");
			SpiderParams.URL = properties.getProperty("spider.url");

			in.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
