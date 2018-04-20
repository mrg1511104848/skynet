package org.skynet.frame.util.http;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.skynet.frame.base.SkynetProxy;
import org.skynet.frame.config.Config;
import org.skynet.frame.util.date.DateUtil;
import org.skynet.frame.util.mongo.MongoUtil;
import org.skynet.frame.util.zlib.ZLib;

import com.mongodb.BasicDBObject;
import com.mongodb.QueryOperators;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

public class ProxyIPUtil {
	static Log log = LogFactory.getLog(ProxyIPUtil.class.getName());
	private static Lock lock = new ReentrantLock();
	public static Condition condition = lock.newCondition();
	static AtomicBoolean initProxyFlag =new AtomicBoolean(false);
	private static ProxyParser proxyParser;
	public static List<SkynetProxy> ipList = Collections.synchronizedList(new ArrayList<SkynetProxy>());
	public static MongoCollection<org.bson.Document> ipInitRecordCollection;
	public static MongoCollection<org.bson.Document> ipUseRecordCollection;
	static{
		List<MongoCollection<org.bson.Document>> collections = MongoUtil.getCollections("huilanData","ipUseRecord","ipInitRecord");
		ipUseRecordCollection = collections.get(0);
		ipInitRecordCollection = collections.get(1);
	}
	public static boolean testProxyIp(String ip , Integer port){
		try {
			List<String> result = HttpUtil.getByProxy("https://www.baidu.com/", null, ip, port,1);
			String statusCode = result.get(0);
			return statusCode.equals("200");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	public static void setProxyParser(ProxyParser proxyParser){
		ProxyIPUtil.proxyParser = proxyParser;
	}
	public static SkynetProxy getProxyIpFromWeb(){
		SkynetProxy ipInfo = null;
		try {
			lock.lock();
			/*for (int i = 0; i < 100; i++) {
				ProxyIPUtil.ipList.add("112.85.58.13_4357");
			}*/
			if(ProxyIPUtil.ipList.size()<=Config.PROXY_MIN_COUNT){
				ProxyIPUtil.initProxy(Config.PROXY_INCREMENT_COUNT);
			}
			int r = new Random().nextInt(ipList.size());
			ipInfo = ProxyIPUtil.ipList.get(r);
			lock.unlock();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ipInfo;
	}
	public static String getProxyIpFromDB() {
		SimpleDateFormat sdfDateFormat = new SimpleDateFormat(DateUtil.DATE_FORMAT_01);
		Date minius5 = DateUtils.addMinutes(new Date(), -20);
		BasicDBObject basicDBObject = new BasicDBObject().append(QueryOperators.GTE, sdfDateFormat.format(minius5));
		BasicDBObject queryObject = new BasicDBObject();
		queryObject.put("createTime", basicDBObject);
		
		FindIterable<Document> itr = ipInitRecordCollection.find(
				queryObject).limit(20);
		if (itr == null) {
			//System.out.println("itr is blank");
			return null;
		}
		if (!itr.iterator().hasNext()) {
			//System.out.println("No next!");
			return null;
		}
		Document document = itr.iterator().next();
		if (document == null) {
			//System.out.println("document is blank");
			return null;
		}
		String ipInf = document.getString("ipInf");
		return ipInf;
	}
	/*public static void initProxy(int initNum){
		if(!Config.PROXY_INIT.equals("true")){
			//System.out.println("Proxy init is false");
			return ;
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			String html = HttpUtil.get(String.format(Config.PROXY_URL,initNum), null);
			JSONObject json = null;
			try {
				json = JSONObject.fromObject(html);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(json==null) {
//				initProxy(initNum);
				return;
			}
			//System.out.println(json);
//			JSONArray dataArray = json.getJSONArray("data");
			JSONArray dataArray = null;
			try {
				dataArray = json.getJSONObject("data").getJSONObject("list").getJSONArray("ProxyIpInfoList");
			} catch (Exception e) {
				log.error("ip init err : "+e.getMessage());
				return;
			}
			for (Object d : dataArray) {
				JSONObject dJsonObject = (JSONObject)d;
//				String ip = dJsonObject.getString("ip");
//				Integer port = Integer.parseInt(dJsonObject.getString("port"));
				
				String ip = dJsonObject.getString("IP");
				Integer port = Integer.parseInt(dJsonObject.getString("Port"));
				ipList.add(ip+"_"+port);
				saveIpInitInf(ip+"_"+port);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	public static synchronized void initProxy(int initNum){
		if(!Config.PROXY_INIT.equals("true")){
			//System.out.println("Proxy init is false");
			return ;
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if(proxyParser == null){
			proxyParser = new DefaultProxyParser();
		}
		List<SkynetProxy> skynetProxies = proxyParser.parseProxy();
		if(skynetProxies == null || skynetProxies.size() == 0)
			throw new RuntimeException("Proxy init fail!");
		for (SkynetProxy skynetProxy : skynetProxies) {
			ipList.add(skynetProxy);
			//skynetProxy.getIp()+"_"+skynetProxy.getPort()
		}
		System.out.println(String.format("共初始化 : %s个代理",ipList.size()));
		final Iterator<SkynetProxy> ipListItrator = ipList.iterator();
		ExecutorService executorService = Executors.newCachedThreadPool();
		int runProxyIdx = 0;
		final int totalCount = ipList.size();
		final AtomicInteger checkProxyCount = new AtomicInteger(0);
		while (ipListItrator.hasNext()) {
			final SkynetProxy skynetProxy = ipListItrator.next();
			final int finalRunProxyIdx = runProxyIdx++;
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					System.out.println("-- startCheckProxy "+finalRunProxyIdx+" : "+skynetProxy);
					boolean proxyValid = false;
					try {
						proxyValid = testProxyIp(skynetProxy.getIp(),Integer.parseInt(skynetProxy.getPort()));
						if(!proxyValid){
//						remove(skynetProxy);
							ipListItrator.remove();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(finalRunProxyIdx+" : "+skynetProxy + " : "+proxyValid +" ("+checkProxyCount.incrementAndGet()+"/"+totalCount+")");
				}
			});
		}
		executorService.shutdown();
		try {
			executorService.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("所有的子线程都结束了！");  
	}
	
	public static void initProxy2(int initNum){
		/*try {
			String html = HttpUtil.get(Config.PROXY_URL, null);
			String bodyHtml = Jsoup.parse(html).select("body").text();
			bodyHtml = bodyHtml.replace("高效高匿名代理IP提取地址：http://www.ip3366.net/fetch/", "").replace("流年最新HTTP代理IP 24小时自助提取系统 高效稳定代理IP 一手高匿名代理IP 每日更新40万代理 价格优惠 无限制提取 API提取 点此购买 网上的花刺代理已失效请点击下载本站专用修改版花刺代理 站长QQ：1138026702 QQ交流群：317771073 本站IP资源免费如果你是买来的证明你已经被骗了 ", "");
			String[] ipinf = bodyHtml.split(" ");
			for (String ipI : ipinf) {
				String ip = ipI.split(":")[0];
				String port = ipI.split(":")[1];
				ipList.add(ip+"_"+port);
			}
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		/*try {
			for (int i = 1; i < 10; i++) {
				String html = HttpUtil.get("http://www.goubanjia.com/free/gngn/index"+i+".shtml", null);
				org.jsoup.nodes.Document doc = Jsoup.parse(html);
				Elements ipElements = doc.select("#list table tbody tr ");
				for (Element ipEle : ipElements) {
					if(ipEle.select("td:eq(1)").text().contains("高匿")){
						ipEle.select("[style*=none;]").remove();
						ipList.add(ipEle.select("td:eq(0)").text().replace(":", "_").replaceAll(" ", ""));
//						ipList.add("47.89.41.164_80");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
//		ipList.add("174.120.70.232_80");
//		ipList.add("125.122.170.90_808");
//		ipList.add("58.57.75.142_63000");
//		ipList.add("221.180.170.5_8080");
//		ipList.add("219.148.201.54_80");
//		ipList.add("58.251.225.114_8118");
		/*String html;
		try {
			html = HttpUtil.get(String.format(Config.PROXY_URL,initNum), null);
			JSONArray array = JSONArray.fromObject(html);
			for (Object arr : array) {
				JSONArray ipArray = (JSONArray)arr;
				String ip = ipArray.get(0)+"";
				int port = Integer.parseInt(ipArray.get(1)+"");
				ipList.add(ip+"_"+port);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		/*try {
			for (int i = 1; i < 10; i++) {
				String html = HttpUtil.get("http://www.kuaidaili.com/free/inha/"+i+"/", null);
				org.jsoup.nodes.Document doc = Jsoup.parse(html);
				Elements ipElements = doc.select("#list table tbody tr ");
				for (Element ipEle : ipElements) {
					ipList.add(ipEle.select("td:eq(0)").text()+"_"+ipEle.select("td:eq(1)").text());
//					ipList.add("47.89.41.164_80");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		/*try {
			String href = "";
			for (int i = 1; i < 10; i++) {
				if(i==1){
					href = "http://www.66ip.cn/areaindex_1/index.html";
				}else{
					href = "http://www.66ip.cn/areaindex_1/"+i+".html";
				}
				String html = HttpUtil.get(href, null);
				org.jsoup.nodes.Document doc = Jsoup.parse(html);
				Elements ipElements = doc.select("#footer table tr:gt(0)");
				for (Element ipEle : ipElements) {
					ipList.add(ipEle.select("td:eq(0)").text()+"_"+ipEle.select("td:eq(1)").text());
	//				ipList.add("47.89.41.164_80");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		
		/*try {
			String html = HttpUtil.get("http://www.66ip.cn/nmtq.php?getnum=30&isp=1&anonymoustype=3&start=&ports=&export=&ipaddress=&area=1&proxytype=0&api=66ip", null);
			String bodyHtml = Jsoup.parse(html).select("body").text();
//			bodyHtml = bodyHtml.replace("高效高匿名代理IP提取地址：http://www.ip3366.net/fetch/", "").replace("流年最新HTTP代理IP 24小时自助提取系统 高效稳定代理IP 一手高匿名代理IP 每日更新40万代理 价格优惠 无限制提取 API提取 点此购买 网上的花刺代理已失效请点击下载本站专用修改版花刺代理 站长QQ：1138026702 QQ交流群：317771073 本站IP资源免费如果你是买来的证明你已经被骗了 ", "");
			String[] ipinf = bodyHtml.split(" ");
			for (String ipI : ipinf) {
				String ip = ipI.split(":")[0];
				String port = ipI.split(":")[1];
				ipList.add(ip+"_"+port);
			}
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	public static void main(String[] args) {
		setProxyParser(new TestProxyParser());
		initProxy(1);
	}
	/*private static void saveIpInitInf(String ipInf) {
		Map<String,Object> ipInitRecordSaveMap = new HashMap<String,Object>();
		ipInitRecordSaveMap.put("ipInf", ipInf);
		ipInitRecordSaveMap.put("createTime", new SimpleDateFormat(DateUtil.DATE_FORMAT_01).format(new Date()));
		MongoUtil.saveDoc(ipInitRecordCollection, ipInitRecordSaveMap);
	}*/
	
	
	public static boolean remove(SkynetProxy ipInf){
		return ipList.remove(ipInf);
	}
	
	public static void saveIpUseInf(String ipInf,String href,String html,long useTime) {
		try {
//			log.info(String.format("save [proxy] %s [href] %s",ipInf,href));
			Map<String,Object> ipUseRecordSaveMap = new HashMap<String,Object>();
			ipUseRecordSaveMap.put("ipInf", ipInf);
			ipUseRecordSaveMap.put("href", href);
			ipUseRecordSaveMap.put("createTime", new SimpleDateFormat(DateUtil.DATE_FORMAT_01).format(new Date()));
			if(html==null) {
				html = "";
			}
			ipUseRecordSaveMap.put("resultHtml", ZLib.compress(html.getBytes()));
			ipUseRecordSaveMap.put("useTime", useTime);
			MongoUtil.saveDoc(ipUseRecordCollection, ipUseRecordSaveMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
