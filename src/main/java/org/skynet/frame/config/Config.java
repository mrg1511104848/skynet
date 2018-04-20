package org.skynet.frame.config;

import java.util.Arrays;
import java.util.List;

import org.skynet.frame.util.file.PropertiesHelper;

public class Config {
     
    public static String HOST = null;
 
    public static String DB = null;
    
    public static Integer PORT = null;
    
    public static String UERNAME = null;
    
    public static String PASSWORD = null;
     
    public static String ENTRYS ;
     
    public static String CHARSET = "UTF-8";
     
    public static String BLACK ;
     
    public static String WHITE ;

	public static boolean NEED_AUTH;
     
    public final static int THREAD_COUNT ;
    
    public final static int REQEUST_TIME;
    
    public final static int PROXY_HTTP_SLEEP;
    
    public final static int AUTO_CHANGE_IP_TIME_LIMIT;
    
    public final static int AUTO_CHANGE_IP;
    
    public final static String AUTO_CHANGE_IP_FLAG;
    
    public final static Integer SOCKET_TIMEOUT;
    public final static Integer CONNECT_TIMEOUT;
    
    public final static String PROXY_URL;
    public final static String PROXY_INIT;
    public final static Integer PROXY_INIT_COUNT;
    public final static Integer PROXY_MIN_COUNT;
    public final static Integer PROXY_INCREMENT_COUNT;

	public static final Integer PROXY_CONNECT_TIME_OUT_TO_REMOVE_TIME;
	public static final boolean PROXY_CONNECT_TIME_OUT_REMOVE;
	
	public static final boolean BY_PROXY;
    
	public static final String RUN_CLASS;
	
	public static final List<String> MECHINE_LIST;
	
	public static final String MQ_IP;
	public static final String MQ_USERNAME;
	public static final String MQ_PASSWORD;
	public static final Integer MQ_PORT;
    static{
    	ENTRYS = PropertiesHelper.getPropertyByName("config", "entrys");
    	BLACK = PropertiesHelper.getPropertyByName("config", "black");
    	WHITE = PropertiesHelper.getPropertyByName("config", "white");
    	
    	HOST = PropertiesHelper.getPropertyByName("config", "mongo_ip");
    	DB = PropertiesHelper.getPropertyByName("config", "mongo_db");
    	PORT = Integer.parseInt(PropertiesHelper.getPropertyByName("config", "mongo_port"));
    	UERNAME = PropertiesHelper.getPropertyByName("config", "mongo_userName");
    	PASSWORD = PropertiesHelper.getPropertyByName("config", "mongo_passWord");
    	
    	THREAD_COUNT = Integer.parseInt(PropertiesHelper.getPropertyByName("config", "threadCount"));
    	REQEUST_TIME = Integer.parseInt(PropertiesHelper.getPropertyByName("config", "requestTime"));
    	AUTO_CHANGE_IP_TIME_LIMIT=Integer.parseInt(PropertiesHelper.getPropertyByName("config", "autoChangeIpTimeLimit"));
    	AUTO_CHANGE_IP=Integer.parseInt(PropertiesHelper.getPropertyByName("config", "autoChangeIp"));
    	AUTO_CHANGE_IP_FLAG=PropertiesHelper.getPropertyByName("config", "autoChangeIpFlag");
    	SOCKET_TIMEOUT=Integer.parseInt(PropertiesHelper.getPropertyByName("config", "SocketTimeout"));
    	CONNECT_TIMEOUT=Integer.parseInt(PropertiesHelper.getPropertyByName("config", "ConnectTimeout"));
    	
    	PROXY_HTTP_SLEEP= Integer.parseInt(PropertiesHelper.getPropertyByName("config", "proxyHttpSleep"));
    	PROXY_URL=PropertiesHelper.getPropertyByName("config", "proxyUrl");
    	PROXY_INIT = PropertiesHelper.getPropertyByName("config", "proxyInit");
    	PROXY_INIT_COUNT = Integer.parseInt(PropertiesHelper.getPropertyByName("config", "proxyInitCount"));
    	PROXY_MIN_COUNT = Integer.parseInt(PropertiesHelper.getPropertyByName("config", "proxyMinCount"));
    	PROXY_INCREMENT_COUNT = Integer.parseInt(PropertiesHelper.getPropertyByName("config", "proxyIncrementCount"));
    	PROXY_CONNECT_TIME_OUT_TO_REMOVE_TIME = Integer.parseInt(PropertiesHelper.getPropertyByName("config", "proxyConnectTimeOutToRemoveTime"));
    	PROXY_CONNECT_TIME_OUT_REMOVE = PropertiesHelper.getPropertyByName("config", "proxyConnectTimeOutRemove")!=null&&PropertiesHelper.getPropertyByName("config", "proxyConnectTimeOutRemove").equals("true");
    	NEED_AUTH = PropertiesHelper.getPropertyByName("config", "mongo_need_auth")!=null&&PropertiesHelper.getPropertyByName("config", "mongo_need_auth").equals("true");
    	BY_PROXY = PropertiesHelper.getPropertyByName("config", "byProxy")!=null&&PropertiesHelper.getPropertyByName("config", "byProxy").equals("true");
    	MECHINE_LIST = Arrays.asList(PropertiesHelper.getPropertyByName("config", "mechine_list").split(","));
    	RUN_CLASS = PropertiesHelper.getPropertyByName("config", "runClass");
    	MQ_IP = PropertiesHelper.getPropertyByName("config", "mq_ip");
    	MQ_PORT = Integer.parseInt(PropertiesHelper.getPropertyByName("config", "mq_port"));
    	MQ_USERNAME = PropertiesHelper.getPropertyByName("config", "mq_username");
    	MQ_PASSWORD = PropertiesHelper.getPropertyByName("config", "mq_password");
    }
    
}