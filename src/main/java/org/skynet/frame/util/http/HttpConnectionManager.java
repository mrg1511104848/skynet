package org.skynet.frame.util.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.skynet.frame.config.Config;

public class HttpConnectionManager {
	private static final String HTTP = "http";
    private static final String HTTPS = "https";
	PoolingHttpClientConnectionManager cm = null;
	private static SSLContextBuilder builder = null;
	private  static LayeredConnectionSocketFactory sslsf = null;
	@PostConstruct
	public void init() {
		builder = new SSLContextBuilder();
        // 全部信任 不做身份鉴定
        try {
			builder.loadTrustMaterial(null, new TrustStrategy() {
			    @Override
			    public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
			        return true;
			    }
			});
			sslsf = new SSLConnectionSocketFactory(builder.build(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
	                .register(HTTP, new PlainConnectionSocketFactory())
	                .register(HTTPS, sslsf)
	                .build();
			cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			cm.setMaxTotal(200);
			cm.setDefaultMaxPerRoute(50);
		} catch (NoSuchAlgorithmException | KeyStoreException e1) {
			e1.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
//		LayeredConnectionSocketFactory sslsf = null;
		/*try {
			sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}*/
        
	}
	public CloseableHttpClient getHttpClient() {
		// 请求重试处理  
	     HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {  
	         public boolean retryRequest(IOException exception,  
	                 int executionCount, HttpContext context) {  
	             if (executionCount >= 1) {// 如果已经重试了5次，就放弃  
	                 return false;  
	             }  
	             if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试  
	                 return true;  
	             }  
	             if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常  
	                 return false;  
	             }  
	             if (exception instanceof InterruptedIOException) {// 超时  
	                 return false;  
	             }  
	             if (exception instanceof UnknownHostException) {// 目标服务器不可达  
	                 return false;  
	             }  
	             if (exception instanceof ConnectTimeoutException) {// 连接被拒绝  
	                 return false;  
	             }  
	             if (exception instanceof SSLException) {// SSL握手异常  
	                 return false;  
	             }  
	  
	             HttpClientContext clientContext = HttpClientContext  
	                     .adapt(context);  
	             HttpRequest request = clientContext.getRequest();  
	                // 如果请求是幂等的，就再次尝试  
	             if (!(request instanceof HttpEntityEnclosingRequest)) {  
	                 return true;  
	             }  
	             return false;  
	         }  
	     };  
		RequestConfig globalConfig = RequestConfig.custom().setSocketTimeout(Config.SOCKET_TIMEOUT)
			    .setConnectTimeout(Config.CONNECT_TIMEOUT)
			    .setConnectionRequestTimeout(Config.CONNECT_TIMEOUT).setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
		CloseableHttpClient httpClient = HttpClients.custom()
				.setConnectionManager(cm).setDefaultRequestConfig(globalConfig).build();
		
		
		
	     
		/*
		 * CloseableHttpClient httpClient =
		 * HttpClients.createDefault();//如果不采用连接池就是这种方式获取连接
		 */
		return httpClient;
	}
}
