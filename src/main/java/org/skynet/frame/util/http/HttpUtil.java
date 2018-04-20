package org.skynet.frame.util.http;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.skynet.frame.annotation.Path;
import org.skynet.frame.annotation.util.AnnotationUtil;
import org.skynet.frame.annotation.util.ReflectionUtils;
import org.skynet.frame.base.SkynetEntity;
import org.skynet.frame.base.SkynetProxy;
import org.skynet.frame.component.JsonConstant;
import org.skynet.frame.config.Config;
import org.skynet.frame.entity.SkynetHttpEntity;

public class HttpUtil {
	static Log log = LogFactory.getLog(HttpUtil.class.getName());
	private static final Map<String, String> DEFAULT_HEADERS = new HashMap<String, String>();
	// private static Pattern charsetPattern = Pattern.compile(
	// "charset=(.*)>|encoding=(.*)>", Pattern.CASE_INSENSITIVE);
	// private static final String DEFAULT = "GB2312";
	private static final Map<String, String> MOBILE_HEADERS = new HashMap<String, String>();
	static {
		DEFAULT_HEADERS
				.put("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		DEFAULT_HEADERS
				.put("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:44.0) Gecko/20100101 Firefox/44.0");
		MOBILE_HEADERS
				.put("User-Agent",
						"Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_3 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Mobile/11B508_eb64__Ctrip_CtripWireless_7.6.3_CtripAPP_iOS_soa");
	}

	public static byte[] getImg(CloseableHttpClient httpclient, String imgUrl,
			Map<String, String> headers) throws IOException {
		return getFile(httpclient, imgUrl, headers);
	}

	public static byte[] getImgNoClose(CloseableHttpClient httpclient,
			String imgUrl) throws IOException {
		return getFileNoClose(httpclient, imgUrl, null).getBytes();
	}

	public static byte[] getImgNoClose(CloseableHttpClient httpclient,
			String imgUrl, Map<String, String> headers) throws IOException {
		return getFileNoClose(httpclient, imgUrl, headers).getBytes();
	}

	public static void byte2File(byte[] buf, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			file = new File(filePath + File.separator + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(buf);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static URI getURI(String href) {
		URL url;
		URI uri = null;
		try {
			url = new URL(href); // 把你需要的地址放在这里，此段代码会帮你编码
			uri = new URI(url.getProtocol(), url.getHost(), url.getPath(),
					url.getQuery(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uri;
	}

	public static byte[] getFile(CloseableHttpClient httpclient,
			String fileHref, Map<String, String> headers) throws IOException {
		byte[] bytes = null;
		try {
			bytes = getFileNoClose(httpclient, fileHref, headers).getBytes();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpclient != null)
				httpclient.close();
		}
		return bytes;
	}

	public static SkynetHttpEntity getFileNoClose(
			CloseableHttpClient httpclient, String fileHref,
			Map<String, String> headers) throws IOException {
		byte[] data = null;
		try {
			HttpGet getMethod = new HttpGet(getURI(fileHref));
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(Config.SOCKET_TIMEOUT)
					.setConnectionRequestTimeout(Config.CONNECT_TIMEOUT)
					.setConnectTimeout(Config.CONNECT_TIMEOUT).build();// 设置请求和传输超时时间
			getMethod.setConfig(requestConfig);
			if (headers != null && headers.keySet().size() > 0) {
				for (String key : headers.keySet()) {
					getMethod.addHeader(key, headers.get(key));
				}
			}
			HttpResponse response = httpclient.execute(getMethod);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				data = EntityUtils.toByteArray(response.getEntity());
			}
		} finally {
		}
		return new SkynetHttpEntity(data, httpclient);
	}

	public static String getHost(String href) {
		try {
			java.net.URL parsedURL;
			parsedURL = new java.net.URL(href);
			String host = parsedURL.getHost();// 获取主机名
			return host;
		} catch (MalformedURLException e) {
			System.out.println("-- err href >> " + e.getMessage());
		}
		return null;
	}

	public static byte[] getImg(CloseableHttpClient httpclient, String imgUrl)
			throws IOException {
		return getImg(httpclient, imgUrl, null);
	}

	/*
	 * @SuppressWarnings("deprecation") public static String uploadFile(String
	 * uploadUrl ,String filepath,String filename){ HttpClient client = new
	 * DefaultHttpClient(); HttpPost post = new HttpPost(uploadUrl);
	 * MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	 * FileBody fileBody = new FileBody(new File(filepath + File.separator +
	 * filename)); // StringBody stringBody = new StringBody("Message 2",
	 * ContentType.MULTIPART_FORM_DATA); builder.addPart("upfile", fileBody); //
	 * builder.addPart("text1", stringBody); HttpEntity entity =
	 * builder.build(); post.setEntity(entity); try { HttpResponse response =
	 * client.execute(post); int statusCode =
	 * response.getStatusLine().getStatusCode(); if (statusCode == 200) { String
	 * data = EntityUtils.toString(response.getEntity()); return data; } } catch
	 * (ClientProtocolException e) { e.printStackTrace(); } catch (IOException
	 * e) { e.printStackTrace(); } return null; }
	 */
	public static void saveImgToLocal(HttpClient httpclient, String imgUrl,
			String path, String imgName) throws IOException {
		File filePath = new File(path);
		if (!filePath.exists())
			filePath.mkdir();
		HttpGet getMethod = new HttpGet(imgUrl);
		HttpResponse response = httpclient.execute(getMethod);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == 200) {
			byte[] data = EntityUtils.toByteArray(response.getEntity());
			// new一个文件对象用来保存图片，默认保存当前工程根目录
			File imageFile = new File(path + imgName);
			// 创建输出流
			FileOutputStream outStream = new FileOutputStream(imageFile);
			// 写入数据
			outStream.write(data);
			// 关闭输出流
			outStream.close();
		}
		return;
	}

	public static void saveImgToLocal(String imgUrl, String targetPath)
			throws Exception {
		// new一个URL对象
		URL url = new URL(imgUrl);
		// 打开链接
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// 设置请求方式为"GET"
		conn.setRequestMethod("GET");
		// 超时响应时间为5秒
		conn.setConnectTimeout(5 * 1000);
		// 通过输入流获取图片数据
		InputStream inStream = conn.getInputStream();
		byte[] bytes = new byte[inStream.available()];
		inStream.read(bytes);
		saveBytes2File(bytes, targetPath);
	}

	private static void saveBytes2File(byte[] bytes, String filePath) throws IOException {
		// 创建输出流
		FileOutputStream outStream = null;
		try {
			// new一个文件对象用来保存图片，默认保存当前工程根目录
			File imageFile = new File(filePath);
			outStream = new FileOutputStream(imageFile);
			// 写入数据
			outStream.write(bytes);
		} finally{
			try {
				// 关闭输出流
				if(outStream!=null)
					outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// 创建一个Buffer字符串
		byte[] buffer = new byte[1024];
		// 每次读取的字符串长度，如果为-1，代表全部读取完毕
		int len = 0;
		// 使用一个输入流从buffer里把数据读取出来
		while ((len = inStream.read(buffer)) != -1) {
			// 用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
			outStream.write(buffer, 0, len);
		}
		// 关闭输入流
		inStream.close();
		// 把outStream里的数据写入内存
		return outStream.toByteArray();
	}

	/**
	 * 自定义注解 @Path(isDefault = true) ， 会走这个方法。主要是初始化默认值用的
	 * 
	 * @param href
	 * @param field
	 * @param clazz
	 * @param deffault
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	private static <T> T getDetailPageByDefault(String href, Field field,
			Class<?> clazz, T deffault) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		String fieldName = field.getName();
		Class<?> type = field.getType();
		String fieldNameFirst = fieldName.substring(0, 1).toUpperCase();
		String fieldNameOther = fieldName.substring(1);
		// Method method = clazz.getDeclaredMethod("set" +
		// fieldNameFirst+fieldNameOther, type);
		String methodName = "set" + fieldNameFirst + fieldNameOther;
		Method method = null;

		if (fieldName.equals("currHref")) {
			method = AnnotationUtil.getDeclaredMethod(deffault, methodName,
					type);
			method.invoke(deffault, href);
		} else {
			method = AnnotationUtil.getDeclaredMethod(deffault, methodName);
			method.invoke(deffault);
		}

		return deffault;
	}

	private static <T> T getDetailPageByJson(Field field, String href,
			String html, Class<?> clazz, T deffault) throws Exception {
		String fieldName = field.getName();
		Class<?> type = field.getType();
		String fieldNameFirst = fieldName.substring(0, 1).toUpperCase();
		String fieldNameOther = fieldName.substring(1);
		// Method method = clazz.getDeclaredMethod("set" +
		// fieldNameFirst+fieldNameOther, type);
		String methodName = "set" + fieldNameFirst + fieldNameOther;
		Method method = AnnotationUtil.getDeclaredMethod(deffault, methodName,
				type);
		if (method == null) {
			throw new Exception("method " + methodName + " not found!");
		}
		Object fieldValue = null;
		if (field.isAnnotationPresent(Path.class) == true) {
			Path pathInfo = field.getAnnotation(Path.class);
			// String fieldAttr = pathInfo.attr();
			String fieldPath = pathInfo.value();
			// boolean useText = pathInfo.useText();
			// boolean needAbsolutePath = pathInfo.needAbsolutePath();
			// boolean isList = pathInfo.isList();
			html = html.replaceAll(":null", ":''");
			Map<String, Object> valueMap = new HashMap<String, Object>();
			try {
				if (html.startsWith("[")) {
//					JSONArray jsonArray = JSONArray.fromObject(html);
				}
				JSONObject jsonObject = JSONObject.fromObject(html);
				if (StringUtils.isBlank(fieldPath)) {
					throw new Exception("FieldPath is blank!");
				}

				if (!fieldPath.contains(JsonConstant.JSON_SPLIT)) {
					return deffault;
				}

				String[] cengjiArr = fieldPath
						.split(JsonConstant.JSON_CENGJI_SPLIT);
				Object currJsonCursor = jsonObject;

				for (int i = 0; i < cengjiArr.length; i++) {
					String eachCeng = cengjiArr[i];
					String[] eachCengArr = eachCeng
							.split(JsonConstant.JSON_SPLIT);
					String jsonKeyType = eachCengArr[0];
					String jsonKeyName = eachCengArr[1];

					JSONObject currJsonObject = ((JSONObject) currJsonCursor);
					if (jsonKeyType.equals(JsonConstant.JSON_ARRAY)) {
						JSONArray jsonValueArray = currJsonObject
								.getJSONArray(jsonKeyName);
						currJsonCursor = jsonValueArray;
					} else if (jsonKeyType.equals(JsonConstant.JSON_OBJECT)) {
						JSONObject jsonValueObject = currJsonObject
								.getJSONObject(jsonKeyName);
						currJsonCursor = jsonValueObject;
					} else if (jsonKeyType.equals(JsonConstant.TEXT)) {
						currJsonCursor = currJsonObject.get(jsonKeyName);
					}
					if (i == cengjiArr.length - 1)
						valueMap.put("value", currJsonCursor);
				}
			} catch (Exception e) {
				log.warn("getDetailPageByJson 异常" + e.getMessage());
				// e.printStackTrace();
			}
			fieldValue = valueMap;
		} else {
			if (methodName.equals("setHref")) {
				fieldValue = href;
			}
		}
		if (fieldValue instanceof Integer) {
			int value = ((Integer) fieldValue).intValue();
			method.invoke(deffault, value);
		} else if (fieldValue instanceof String) {
			String value = (String) fieldValue;
			method.invoke(deffault, value);
		} else if (fieldValue instanceof Double) {
			double value = ((Double) fieldValue).doubleValue();
			method.invoke(deffault, value);
		} else if (fieldValue instanceof Float) {
			float value = ((Float) fieldValue).floatValue();
			method.invoke(deffault, value);
		} else if (fieldValue instanceof Long) {
			long value = ((Long) fieldValue).longValue();
			method.invoke(deffault, value);
		} else if (fieldValue instanceof Boolean) {
			boolean value = ((Boolean) fieldValue).booleanValue();
			method.invoke(deffault, value);
		} else if (fieldValue instanceof Date) {
			Date value = (Date) fieldValue;
			method.invoke(deffault, value);
		} else {
			method.invoke(deffault, fieldValue);
		}
		return deffault;
	}

	/*private <T> T parseJsonArray(Map<String, Object> valueMap, String html,
			String fieldPath, T deffault) {
		if (html.startsWith("[")) {
			JSONArray jsonArray = JSONArray.fromObject(html);
			for (Object object : jsonArray) {
				JSONObject jsonObject = (JSONObject) object;
			}
		}
		return null;
	}*/

	/*private <T> T parseJsonObject(Map<String, Object> valueMap, String html,
			String fieldPath, T deffault) {
		JSONObject jsonObject = JSONObject.fromObject(html);
		if (StringUtils.isBlank(fieldPath)) {
			throw new RuntimeException("FieldPath is blank!");
		}

		if (!fieldPath.contains(JsonConstant.JSON_SPLIT)) {
			return deffault;
		}

		String[] cengjiArr = fieldPath.split(JsonConstant.JSON_CENGJI_SPLIT);
		Object currJsonCursor = jsonObject;

		for (int i = 0; i < cengjiArr.length; i++) {
			String eachCeng = cengjiArr[i];
			String[] eachCengArr = eachCeng.split(JsonConstant.JSON_SPLIT);
			String jsonKeyType = eachCengArr[0];
			String jsonKeyName = eachCengArr[1];

			JSONObject currJsonObject = ((JSONObject) currJsonCursor);
			if (jsonKeyType.equals(JsonConstant.JSON_ARRAY)) {
				JSONArray jsonValueArray = currJsonObject
						.getJSONArray(jsonKeyName);
				currJsonCursor = jsonValueArray;
			} else if (jsonKeyType.equals(JsonConstant.JSON_OBJECT)) {
				JSONObject jsonValueObject = currJsonObject
						.getJSONObject(jsonKeyName);
				currJsonCursor = jsonValueObject;
			} else if (jsonKeyType.equals(JsonConstant.TEXT)) {
				currJsonCursor = currJsonObject.get(jsonKeyName);
			}
			if (i == cengjiArr.length - 1)
				valueMap.put("value", currJsonCursor);
		}
		return deffault;
	}*/

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	private static <T> T getDetailPageByHtml(Field field, String href,
			org.jsoup.nodes.Document doc, Class<?> clazz, T deffault)
			throws Exception {

		String fieldName = field.getName();
		Class<?> type = field.getType();
		String fieldNameFirst = fieldName.substring(0, 1).toUpperCase();
		String fieldNameOther = fieldName.substring(1);
		String methodName = "set" + fieldNameFirst + fieldNameOther;

		Method method = AnnotationUtil.getDeclaredMethod(deffault, methodName,
				type);
		if (method == null) {
			// throw new Exception("method " + methodName + " not found!");
			return deffault;
		}
		Object fieldValue = null;
		if (field.isAnnotationPresent(Path.class) == true) {
			Path pathInfo = field.getAnnotation(Path.class);
			String fieldAttr = pathInfo.attr();
			String fieldPath = pathInfo.value();
			boolean useText = pathInfo.useText();
			boolean useOwnText = pathInfo.useOwnText();
			boolean needAbsolutePath = pathInfo.needAbsolutePath();
			boolean isList = pathInfo.isList();
			boolean useFirst = pathInfo.useFirst();
			if (StringUtils.isNotBlank(fieldPath)) {
				Elements fieldMatchPathEles = doc.select(fieldPath);
				if (fieldMatchPathEles != null && fieldMatchPathEles.size() > 0) {
					// if (isList) {
					Class fieldClazz = field.getType();
					if (fieldClazz.isAssignableFrom(List.class)) {
						List<Object> list = new ArrayList<Object>();
						for (Element ele : fieldMatchPathEles) {
							Object eleFieldValue = null;
							if (StringUtils.isNotBlank(fieldAttr)) {
								if (needAbsolutePath) {
									eleFieldValue = HttpUtil.getAbsolutePath(
											href, ele.attr(fieldAttr));
								} else {
									eleFieldValue = ele.attr(fieldAttr);
								}
							} else {
								if (useOwnText) {
									eleFieldValue = ele.ownText();
								} else if (useText) {
									eleFieldValue = ele.text();
								} else {
									eleFieldValue = ele.html();
								}
							}
							list.add(eleFieldValue);
						}
						fieldValue = list;
					} else {
						if (StringUtils.isNotBlank(fieldAttr)) {
							if (needAbsolutePath) {
								if (useFirst) {
									fieldValue = HttpUtil.getAbsolutePath(
											href,
											fieldMatchPathEles.first().attr(
													fieldAttr));
								} else {
									fieldValue = HttpUtil.getAbsolutePath(href,
											fieldMatchPathEles.attr(fieldAttr));
								}
							} else {
								if (useFirst) {
									fieldValue = fieldMatchPathEles.first()
											.attr(fieldAttr);
								} else {
									fieldValue = fieldMatchPathEles
											.attr(fieldAttr);
								}
							}
						} else {
							if (useOwnText) {
								if (useFirst) {
									fieldValue = fieldMatchPathEles.first()
											.ownText();
								} else {
									fieldValue = fieldMatchPathEles.text();
								}
							} else if (useText) {
								if (useFirst) {
									fieldValue = fieldMatchPathEles.first()
											.text();
								} else {
									fieldValue = fieldMatchPathEles.text();
								}
							} else {
								if (useFirst) {
									fieldValue = fieldMatchPathEles.first()
											.html();
								} else {
									fieldValue = fieldMatchPathEles.html();
								}
							}
						}
					}
				}
			}
		} else {
			if (methodName.equals("setCurrHref")) {
				fieldValue = href;
			}
		}
		method.invoke(deffault, fieldValue);
		return deffault;
	}

	public static <T extends SkynetEntity> T getDetailPage(String href,
			Map<String, String> headers, Class<?> clazz, T deffault) {
		Object useProxy = ReflectionUtils.getFieldValue(deffault, "useProxy");
		return getDetailPage(href, headers, clazz, deffault, null, null,
				Config.REQEUST_TIME, useProxy == null ? Config.BY_PROXY
						: (Boolean) useProxy);
	}

	public static <T extends SkynetEntity> T getDetailPage(String href,
			Map<String, String> headers, Class<T> clazz, T deffault,
			boolean proxy) {
		return getDetailPage(href, headers, clazz, deffault, null, null,
				Config.REQEUST_TIME, proxy);
	}

	public static <T extends SkynetEntity> T getDetailPage(String href,
			Map<String, String> headers, Class<?> clazz, T deffault, String ip,
			Integer port, int requestTime, boolean proxy) {
		Object methodName = ReflectionUtils.getFieldValue(deffault, "method");
		return getDetailPage(methodName.toString(), href, headers, clazz,
				deffault, ip, port, requestTime, proxy);
	}

	public static <T extends SkynetEntity> T getDetailPage(String baseHref,
			String html, Class<?> clazz, T deffault) {
		org.jsoup.nodes.Document doc = Jsoup.parse(html);
		Field[] fields = AnnotationUtil.getFieldsInfo(clazz);
		for (Field field : fields) {
			try {
				String fieldName = field.getName();
				Class<?> type = field.getType();
				String fieldNameFirst = fieldName.substring(0, 1).toUpperCase();
				String fieldNameOther = fieldName.substring(1);
				// Method method = clazz.getDeclaredMethod("set" +
				// fieldNameFirst+fieldNameOther, type);
				String setMethodName = "set" + fieldNameFirst + fieldNameOther;
				// String getMethodName = "get" + fieldNameFirst +
				// fieldNameOther;
				Method setMethod = AnnotationUtil.getDeclaredMethod(deffault,
						setMethodName, type);

				// Method getMethod = AnnotationUtil.getDeclaredMethod(deffault,
				// getMethodName);
				if (field.isAnnotationPresent(Path.class) == true) {
					Path pathInfo = field.getAnnotation(Path.class);
					boolean isJson = pathInfo.isJson();
					boolean isDefault = pathInfo.isDefault();
					String value = pathInfo.value();
					if (isDefault) {
						getDetailPageByDefault(baseHref, field, clazz, deffault);
						continue;
					}
					if (StringUtils.isBlank(value)) {// 如果是注解模式，并且path的value为null
														// ， 则跳过
						continue;
					}
					if (isJson) {
						getDetailPageByJson(field, baseHref, html, clazz,
								deffault);
					} else {
						getDetailPageByHtml(field, baseHref, doc, clazz,
								deffault);
					}
				}
				if (setMethod == null) {
					// throw new Exception("method " + setMethodName
					// + " not found!");
					log.warn("method " + setMethodName + " not found!");
					continue;
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		return deffault;
	}

	public static <T extends SkynetEntity> T getDetailPage(String methodName,
			String href, Map<String, String> headers, Class<?> clazz,
			T deffault, String ip, Integer port, int requestTime, boolean proxy) {
		String html = null;
		List<String> requestResult = null;
		String redirectLocation = null;
		try {
			ReflectionUtils.setFieldValue(deffault, "startTime",
					System.currentTimeMillis());
			ReflectionUtils.setFieldValue(deffault, "method", methodName);
			Object userEncode = ReflectionUtils.getFieldValue(deffault,
					"userEncode");

			String statusCode = null;
			if (methodName.equals(org.skynet.frame.entity.Method.POST)) {
				Object params = ReflectionUtils.getFieldValue(deffault,
						"params");
				if (params instanceof Map) {
					@SuppressWarnings("unchecked")
					Map<String, Object> mapParams = (Map<String, Object>) params;
					if (mapParams != null) {
						html = HttpUtil.doHttpPost(href, mapParams, headers);
					}
				}
			} else {
				if (proxy) {
					requestResult = HttpUtil.getByProxySuper(href, headers);
					html = requestResult.get(2);
					statusCode = requestResult.get(3);
					ReflectionUtils.setFieldValue(deffault, "proxy", true);
				} else if (ip == null && port == null) {
					List<String> returnList = HttpUtil.get(href, headers,
							requestTime,
							userEncode == null ? null : userEncode.toString());
					statusCode = returnList.get(0);
					html = returnList.get(1);
					if (returnList.size() > 2) {
						redirectLocation = returnList.get(2);
					}
				} else if (ip != null && port != null) {
					List<String> returnList = HttpUtil.getByProxy(href,
							headers, ip, port, requestTime);
					statusCode = returnList.get(0);
					html = returnList.get(1);
					proxy = true;
				}
			}
			String getMethodName = "getRemoveProxyCondition";
			Method getMethod = AnnotationUtil.getDeclaredMethod(deffault,
					getMethodName);
			if (getMethod != null) {
				if (proxy && html != null) {
					Object proxyCondition = getMethod.invoke(deffault);
					if (StringUtils.isBlank(html)) {
						ip = requestResult.get(0);
						port = Integer.parseInt(requestResult.get(1));
						removeProxy(ip, port);
					} else if (proxyCondition != null
							&& html.contains(proxyCondition.toString())) {
						if (requestResult != null && requestResult.size() == 3) {
							ip = requestResult.get(0);
							port = Integer.parseInt(requestResult.get(1));
							removeProxy(ip, port);
						}
					}
				}
			}
			if (StringUtils.isNotBlank(ip) && port != null) {
				ReflectionUtils.setFieldValue(deffault, "proxyInfo", ip + "_"
						+ port);
			}
			ReflectionUtils.setFieldValue(deffault, "proxy", true);
			ReflectionUtils.setFieldValue(deffault, "statusCode", statusCode);
			ReflectionUtils.setFieldValue(deffault, "endTime",
					System.currentTimeMillis());
			ReflectionUtils.setFieldValue(deffault, "redirectLocation",
					redirectLocation);
			return getDetailPage(href, html, clazz, deffault);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return deffault;
	}

	public static String doHttpPost(String url, String json,
			Map<String, String> headers) throws ParseException, IOException {
		// return doHttpPost(connManager.getHttpClient(), url, json, headers);
		CloseableHttpClient client = HttpClients.createDefault();
		return doHttpPost(client, url, json, headers);
	}

	public static String doHttpOptions(String url, Map<String, String> headers)
			throws ParseException, IOException {
		// return doHttpPost(connManager.getHttpClient(), url, json, headers);
		CloseableHttpClient client = HttpClients.createDefault();
		return doHttpOptions(client, url, headers);
	}

	private static String doHttpOptions(CloseableHttpClient client, String url,
			Map<String, String> headers) {
		HttpHost proxy = null;
		SkynetProxy proxyInfo = null;

		Builder builder = RequestConfig.custom()
				.setSocketTimeout(Config.SOCKET_TIMEOUT)
				.setConnectionRequestTimeout(Config.CONNECT_TIMEOUT)
				.setConnectTimeout(Config.CONNECT_TIMEOUT);// 设置请求和传输超时时间

		for (int i = 0; i < 1; i++) {
			if (Config.BY_PROXY) {
				try {
					proxyInfo = ProxyIPUtil.getProxyIpFromWeb();
					proxy = new HttpHost(proxyInfo.getIp(),
							Integer.parseInt(proxyInfo.getPort()));
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
				}
			}
			if (proxy != null) {
				builder.setProxy(proxy);
			}
			RequestConfig requestConfig = builder.build();
			CloseableHttpResponse response = null;
			String html = null;
			try {
				HttpOptions optionsMethod = new HttpOptions(url);
				optionsMethod.setConfig(requestConfig);
				if (headers != null) {
					Iterator<Entry<String, String>> headerItr = headers
							.entrySet().iterator();
					while (headerItr.hasNext()) {
						Entry<String, String> headerEntry = headerItr.next();
						String key = headerEntry.getKey();
						String value = headerEntry.getValue();
						optionsMethod.addHeader(key, value);
					}
				}
				response = client.execute(optionsMethod);
				HttpEntity entity2 = null;
				entity2 = response.getEntity();
				html = EntityUtils.toString(entity2, "UTF-8");
				if (response.getStatusLine().getStatusCode() == 200
						&& StringUtils.isNoneBlank(html)) {
					return html;
				}
			} catch (Exception e) {
				// e.printStackTrace();
				log.error(e.getMessage());
				if (proxyInfo != null)
					ProxyIPUtil.remove(proxyInfo);
			} finally {
				try {
					if (response != null)
						response.close();
					// if(httpClient!=null)
					// httpClient.close();
					// httpClient = connManager.getHttpClient();
					// httpClient = HttpClients.createDefault();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	final static String CONTENT_TYPE_TEXT_JSON = "text/json";

	public static String doHttpPost(CloseableHttpClient httpClient, String url,
			String json, Map<String, String> headers) {
		HttpHost proxy = null;
		SkynetProxy proxyInfo = null;

		Builder builder = RequestConfig.custom()
				.setSocketTimeout(Config.SOCKET_TIMEOUT)
				.setConnectionRequestTimeout(Config.CONNECT_TIMEOUT)
				.setConnectTimeout(Config.CONNECT_TIMEOUT);// 设置请求和传输超时时间

		for (int i = 0; i < Config.REQEUST_TIME; i++) {
			if (Config.BY_PROXY) {
				try {
					proxyInfo = ProxyIPUtil.getProxyIpFromWeb();
					proxy = new HttpHost(proxyInfo.getIp(),
							Integer.parseInt(proxyInfo.getPort()));
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
				}
			}
			if (proxy != null) {
				builder.setProxy(proxy);
			}
			RequestConfig requestConfig = builder.build();
			CloseableHttpResponse response = null;
			String html = null;
			try {
				StringEntity se = new StringEntity(json);
				// se.setContentType(CONTENT_TYPE_TEXT_JSON);
				HttpPost postMethod = new HttpPost(url);
				postMethod.setConfig(requestConfig);
				postMethod.setEntity(se);
				if (headers != null) {
					Iterator<Entry<String, String>> headerItr = headers
							.entrySet().iterator();
					while (headerItr.hasNext()) {
						Entry<String, String> headerEntry = headerItr.next();
						String key = headerEntry.getKey();
						String value = headerEntry.getValue();
						postMethod.addHeader(key, value);
					}
				}
				response = httpClient.execute(postMethod);
				HttpEntity entity2 = null;
				entity2 = response.getEntity();
				html = EntityUtils.toString(entity2, "UTF-8");
				if (response.getStatusLine().getStatusCode() == 200
						&& StringUtils.isNoneBlank(html)) {
					return html;
				}
			} catch (Exception e) {
				// e.printStackTrace();
				log.error(e.getMessage());
				if (proxyInfo != null)
					ProxyIPUtil.remove(proxyInfo);
			} finally {
				try {
					if (response != null)
						response.close();
					// if(httpClient!=null)
					// httpClient.close();
					// httpClient = connManager.getHttpClient();
					// httpClient = HttpClients.createDefault();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static String doHttpPost(String url, Map<String, Object> params,
			Map<String, String> headers) throws ParseException, IOException {
		return doHttpPost(connManager.getHttpClient(), url, params, headers);
	}

	public static String doHttpPost(String url, Map<String, Object> params)
			throws ParseException, IOException {
		return doHttpPost(connManager.getHttpClient(), url, params, null);
	}

	/*
	 * public static <T> T getDetailPage(String href,String
	 * html,Map<String,String> headers,Class<T> clazz, T deffault) { try {
	 * if(StringUtils.isBlank(html)){ html = HttpUtil.get(href, headers); return
	 * getDetailPage(href, html, headers, clazz, deffault,1); } } catch
	 * (Exception e) { e.printStackTrace(); } return deffault; }
	 */
	public static String doHttpPost(CloseableHttpClient httpclient, String url,
			Map<String, Object> params) throws ParseException, IOException {
		return doHttpPost(httpclient, url, params, null);
	}

	public static String doHttpPost(CloseableHttpClient httpclient, String url,
			Map<String, Object> params, Map<String, String> headers)
			throws ParseException, IOException {
		HttpPost postMethod = new HttpPost(url);
		return doHttpPost(httpclient, postMethod, url, params, headers, true);
	}

	/*
	 * public static SkynetHttpEntity doHttpPostV2(CloseableHttpClient
	 * httpclient, String url, Map<String, Object> params, Map<String, String>
	 * headers) throws ParseException, IOException { HttpPost postMethod = new
	 * HttpPost(url); return doHttpPost(httpclient, postMethod, url, params,
	 * headers, true); }
	 */

	public static String doHttpPost(CloseableHttpClient httpclient, String url,
			Map<String, Object> params, Map<String, String> headers,
			boolean redirect) throws ParseException, IOException {
		HttpPost postMethod = new HttpPost(url);
		return doHttpPost(httpclient, postMethod, url, params, headers, true);
	}

	// public static String doHttpPost(CloseableHttpClient httpclient, String
	// url,
	// Map<String, Object> params, Map<String, String> headers
	// ) throws ParseException, IOException {
	// HttpPost postMethod = new HttpPost(url);
	//
	// return doHttpPost(httpclient, postMethod, url, params,true);
	// }
	public static SkynetHttpEntity doHttpPostV2(CloseableHttpClient httpclient,
			String url, Map<String, Object> params,
			Map<String, String> headers, boolean redirect)
			throws ParseException, IOException {
		HttpPost postMethod = new HttpPost(url);
		return doHttpPostV2(httpclient, postMethod, url, params, headers,
				redirect);
	}

	public static SkynetHttpEntity doHttpPostV2NoClose(
			CloseableHttpClient httpclient, String url,
			Map<String, Object> params, Map<String, String> headers,
			boolean redirect) throws ParseException, IOException {
		HttpPost postMethod = new HttpPost(url);
		return doHttpPostV2NoClose(httpclient, postMethod, url, params,
				headers, redirect);
	}

	private static SkynetHttpEntity doHttpPostV2NoClose(
			CloseableHttpClient httpclient, HttpPost postMethod, String url,
			Map<String, Object> params, Map<String, String> headers,
			boolean redirect) {
		SkynetHttpEntity skynetHttpEntity = null;
		try {
			skynetHttpEntity = doHttpPostNoClose(httpclient, postMethod, url,
					params, headers, redirect);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return skynetHttpEntity;
	}

	public static SkynetHttpEntity doHttpPostV2(CloseableHttpClient httpclient,
			HttpPost postMethod, String url, Map<String, Object> params,
			Map<String, String> headers, boolean redirect)
			throws ParseException, IOException {
		SkynetHttpEntity skynetHttpEntity = null;
		try {
			skynetHttpEntity = doHttpPostNoClose(httpclient, postMethod, url,
					params, headers, redirect);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpclient != null)
				httpclient.close();
		}
		return skynetHttpEntity;
	}

	public static String doHttpPost(CloseableHttpClient httpclient,
			HttpPost postMethod, String url, Map<String, Object> params,
			Map<String, String> headers, boolean redirect)
			throws ParseException, IOException {
		return doHttpPostV2(httpclient, postMethod, url, params, headers, true)
				.getHtml();
	}

	public static SkynetHttpEntity doHttpPostNoClose(
			CloseableHttpClient httpclient, HttpPost postMethod, String url,
			Map<String, Object> params, Map<String, String> headers,
			boolean redirect) throws ParseException, IOException {
		if (headers != null) {
			Iterator<Entry<String, String>> headerItr = headers.entrySet()
					.iterator();
			while (headerItr.hasNext()) {
				Entry<String, String> headerEntry = headerItr.next();
				String key = headerEntry.getKey();
				String value = headerEntry.getValue();
				postMethod.addHeader(key, value);
			}
		}
		String redirectLocation = null;
		HttpHost proxy = null;
		SkynetProxy proxyInfo = null;
		Builder builder = RequestConfig.custom()
				.setSocketTimeout(Config.SOCKET_TIMEOUT)
				.setConnectionRequestTimeout(Config.CONNECT_TIMEOUT)
				.setRedirectsEnabled(redirect)
				.setConnectTimeout(Config.CONNECT_TIMEOUT);// 设置请求和传输超时时间
		String res = null;
		for (int i = 0; i < Config.REQEUST_TIME; i++) {
			if (Config.BY_PROXY) {
				try {
					proxyInfo = ProxyIPUtil.getProxyIpFromWeb();
					proxy = new HttpHost(proxyInfo.getIp(),
							Integer.parseInt(proxyInfo.getPort()));
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
				}
			}
			if (proxy != null) {
				builder.setProxy(proxy);
			}
			RequestConfig requestConfig = builder.build();
			CloseableHttpResponse response = null;
			try {
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				Iterator<Entry<String, Object>> iter = params.entrySet()
						.iterator();
				while (iter.hasNext()) {
					Entry<String, Object> entry = iter.next();
					if (entry.getValue() == null) {
						entry.setValue("");
					}
					nvps.add(new BasicNameValuePair(entry.getKey(), entry
							.getValue().toString()));
				}

				UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(
						nvps, "utf-8");
				postMethod.setConfig(requestConfig);
				postMethod.setEntity(encodedFormEntity);
				response = httpclient.execute(postMethod);

				res = EntityUtils.toString(response.getEntity(), "UTF-8");
				if (response.getLastHeader("Location") != null) {
					redirectLocation = response.getLastHeader("Location")
							.getValue();
				}
				// response.getStatusLine().getStatusCode() == 200
				/*
				 * if (StringUtils.isNoneBlank(res)) { return res; }
				 */
				if (response.getStatusLine().getStatusCode() == 302)
					break;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (response != null)
					response.close();

				/*
				 * if (httpclient != null) // 20180210 临时修改 httpclient.close();
				 */
			}
		}
		return new SkynetHttpEntity(res, httpclient, redirectLocation);
	}

	private static String getLocation(CloseableHttpResponse response) {
		String redirectLocation = null;
		if (response.getLastHeader("Location") != null) {
			redirectLocation = response.getLastHeader("Location").getValue();
		}
		return redirectLocation;
	}

	public static String get(CloseableHttpClient closeableHttpClient,
			String pageHref, Map<String, String> headers) throws IOException,
			Exception {
		String html = null;
		try {
			SkynetHttpEntity skynetHttpEntity = getNoClose(closeableHttpClient,
					pageHref, headers);
			html = skynetHttpEntity.getHtml();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 临时修改20180126
			if (closeableHttpClient != null)
				closeableHttpClient.close();
		}
		return html;
	}

	public static SkynetHttpEntity getNoClose(
			CloseableHttpClient closeableHttpClient, String pageHref,
			Map<String, String> headers) throws IOException, Exception {
		if (headers == null) {
			headers = DEFAULT_HEADERS;
		}
		String html = null;
		try {
			List<Object> result = HttpUtil.doGet(closeableHttpClient, pageHref,
					headers);
			return HttpUtil.getStringFromResponse(
					(CloseableHttpResponse) result.get(0),
					(CloseableHttpClient) result.get(1), null);
		} catch (Exception e) {
			log.info(e.getMessage());
			List<Object> result = HttpUtil.doGet(closeableHttpClient, pageHref,
					headers);
			return HttpUtil.getStringFromResponse(
					(CloseableHttpResponse) result.get(0),
					(CloseableHttpClient) result.get(1), null);
		}
	}

	public static String get(String pageHref, Map<String, String> headers)
			throws IOException, Exception {
		if (headers == null) {
			headers = DEFAULT_HEADERS;
		}
		String html = null;
		try {
			List<Object> result = HttpUtil.doGet(pageHref, headers);
			html = HttpUtil.getStringFromResponse(
					(CloseableHttpResponse) result.get(0),
					(CloseableHttpClient) result.get(1), null).getHtml();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("err pageHref>>>>>>>>>>>" + pageHref);
			log.info(e.getMessage());
			List<Object> result = HttpUtil.doGet(pageHref, headers);
			html = HttpUtil.getStringFromResponse(
					(CloseableHttpResponse) result.get(0),
					(CloseableHttpClient) result.get(1), null).getHtml();
		}
		return html;
	}

	public static List<String> get(String pageHref,
			Map<String, String> headers, String userEncode) throws IOException,
			Exception {
		List<String> returnList = new ArrayList<String>();
		if (headers == null) {
			headers = DEFAULT_HEADERS;
		}
		pageHref = getParsedHref(pageHref);
		String host = getHost(pageHref);
		if (host != null)
			headers.put("Host", host);

		String html = null;
		try {
			List<Object> result = HttpUtil.doGet(pageHref, headers);
			CloseableHttpResponse response = (CloseableHttpResponse) result
					.get(0);
			// Header contentType = response.getEntity().getContentType();
			html = HttpUtil.getStringFromResponse(response,
					(CloseableHttpClient) result.get(1), userEncode).getHtml();
			returnList.add(result.get(2) + "");
			returnList.add(html);
			if (result.size() > 3) {
				returnList.add(result.get(3) + "");
			} else {
				String location = getLocation(response);
				returnList.add(location);
			}
		} catch (Exception e) {
			String errorMessage = String.format(
					"Error while reading information of url [%s]", pageHref);
			log.error(errorMessage, e);
			throw e;
		}
		return returnList;
	}

	public static List<String> get(String pageHref,
			Map<String, String> headers, int rTime, String userEncode)
			throws IOException, Exception {
		List<String> returnList = null;
		for (int i = 1; i <= rTime; i++) {
			try {
				returnList = HttpUtil.get(pageHref, headers, userEncode);
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
			if (returnList != null && returnList.get(0).equals("404")) {
				log.info(" the pageHref " + pageHref + " is not found! break");
				break;
			}
			if (returnList != null && StringUtils.isNotBlank(returnList.get(1))
					&& !returnList.get(1).equals("null")) {
				break;
			}
			log.info(String.format("--not proxy 为您 第 %s次请求 %s", i, pageHref));
		}
		return returnList;
	}

	public static List<String> get(String pageHref,
			Map<String, String> headers, int rTime) throws IOException,
			Exception {
		List<String> returnList = null;
		for (int i = 1; i <= rTime; i++) {
			try {
				returnList = HttpUtil.get(pageHref, headers, null);
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
			if (returnList != null && StringUtils.isNotBlank(returnList.get(1))
					&& !returnList.get(1).equals("null")) {
				break;
			}
			log.info(String.format("--not proxy 为您 第 %s次请求 %s", i, pageHref));
		}
		return returnList;
	}

	public static synchronized void removeProxy(String ip, String port)
			throws Exception {
		log.info(String.format(" remove the proxy ip %s:%s", ip, port));
		SkynetProxy skynetProxy = new SkynetProxy(ip, port + "");
		ProxyIPUtil.ipList.remove(skynetProxy);
		// initProxy(2);
	}

	public static synchronized void removeProxy(String ip, Integer port)
			throws Exception {
		SkynetProxy skynetProxy = new SkynetProxy(ip, port + "");
		ProxyIPUtil.ipList.remove(skynetProxy);
		// initProxy(2);
	}

	/*
	 * public static String getByProxy(String pageHref, Map<String, String>
	 * headers, int rTime) throws IOException, Exception { String html = null;
	 * String proxyIp = ProxyIPUtil.getProxyIpFromWeb(); String[] ipArr =
	 * proxyIp.split("_"); String ip = ipArr[0]; Integer port =
	 * Integer.parseInt(ipArr[1]); for (int i = 1; i <= rTime; i++) { try { html
	 * = HttpUtil.getByProxy(pageHref, headers, ip, port); } catch (Exception
	 * e1) { // e1.printStackTrace(); } if (StringUtils.isNotBlank(html)) {
	 * break; } log.info(String.format("--proxy %s:%s 为您 第 %s次请求 %s", ip,port,i,
	 * pageHref)); } if (StringUtils.isBlank(html) ) {
	 * ProxyIPUtil.remove(proxyIp); } return html; }
	 */
	public static List<String> getByProxySuper(String pageHref,
			Map<String, String> headers) throws IOException, Exception {
		List<String> resultList = new ArrayList<String>();
		SkynetProxy ipInfo = ProxyIPUtil.getProxyIpFromWeb();
		String html = null;
		String ip = ipInfo.getIp();
		Integer port = Integer.parseInt(ipInfo.getPort());
		resultList.add(ip);
		resultList.add(port + "");
		List<String> returnList = null;
		try {
			returnList = HttpUtil.getByProxy(pageHref, headers, ip, port);
			html = returnList.get(1);
		} catch (Exception e1) {
			// e1.printStackTrace();
		}
		if (StringUtils.isNotBlank(html)) {
			resultList.add(html);
		}
		if (StringUtils.isBlank(html))
			resultList.add("");
		/*
		 * if(StringUtils.isBlank(html)) { ipList.remove(r); portList.remove(r);
		 * }
		 */
		String statusCode = returnList.get(0);
		resultList.add(statusCode);
		return resultList;
	}

	public static List<String> getByProxy(String pageHref,
			Map<String, String> headers, String ip, Integer port)
			throws IOException, Exception {
		return getByProxy(pageHref, headers, ip, port, Config.REQEUST_TIME);
	}

	/**
	 * 代理访问
	 * 
	 * @param pageHref
	 * @param headers
	 * @param rTime
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static List<String> getByProxy(String pageHref,
			Map<String, String> headers, String ip, Integer port,
			Integer requestTime) throws IOException, Exception {
		List<String> returnInfoList = new ArrayList<String>();
		HttpHost proxy = new HttpHost(ip, port);
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(Config.SOCKET_TIMEOUT)
				.setConnectionRequestTimeout(Config.CONNECT_TIMEOUT)
				.setConnectTimeout(Config.CONNECT_TIMEOUT).setProxy(proxy)
				.build();// 设置请求和传输超时时间
		String html = null;
		String statusCode = null;
		for (int i = 0; i < requestTime; i++) {
			log.info(String.format("-- proxy %s:%s 为您 第 %s次请求 %s", ip, port, i,
					pageHref));
			CloseableHttpClient client = null;
			CloseableHttpResponse response = null;
			long startTime = System.currentTimeMillis();
			String[] newProxyInfo = null;
			try {
				Thread.sleep(Config.PROXY_HTTP_SLEEP);
				client = connManager.getHttpClient();
				HttpGet httpGet = new HttpGet(pageHref);
				httpGet.setConfig(requestConfig);
				if (headers == null) {
					headers = DEFAULT_HEADERS;
				}
				if (headers != null && headers.keySet().size() > 0) {
					for (String key : headers.keySet()) {
						httpGet.addHeader(key, headers.get(key));
					}
				}
				response = client.execute(httpGet);
				HttpEntity entity = response.getEntity();
				html = EntityUtils.toString(entity, "utf-8");
			} catch (HttpHostConnectException e1) {
				log.error(e1.getMessage());
				// newProxyInfo = removeProxyAndGet(i, ip, port);
			} catch (ConnectTimeoutException e2) {
				log.error(e2.getMessage());
				// newProxyInfo = removeProxyAndGet(i, ip, port);
			} catch (ConnectException e3) {
				log.error(e3.getMessage());
				// newProxyInfo = removeProxyAndGet(i, ip, port);
			} catch (Exception e) {
				// e.printStackTrace();
				log.error(e.getMessage());
			} finally {
				long endTime = System.currentTimeMillis();
				ProxyIPUtil.saveIpUseInf(ip + "_" + port, pageHref, html,
						(endTime - startTime));
				if (newProxyInfo != null && newProxyInfo.length == 2) {
					ip = newProxyInfo[0];
					port = Integer.parseInt(newProxyInfo[1]);
				}
				if (response != null) {
					statusCode = response.getStatusLine().getStatusCode() + "";
					response.close();
				} else {
					statusCode = "ERR";
				}
				if (client != null)
					client.close();
				if (StringUtils.isNotBlank(html))
					break;
			}
		}
		returnInfoList.add(statusCode);
		returnInfoList.add(html);
		return returnInfoList;
	}

	/*
	 * private static String[] removeProxyAndGet(int currTime, String ip, int
	 * port) { if (currTime >= Config.PROXY_CONNECT_TIME_OUT_TO_REMOVE_TIME) {
	 * if (Config.PROXY_CONNECT_TIME_OUT_REMOVE) { SkynetProxy skynetProxy = new
	 * SkynetProxy(ip, port + ""); ProxyIPUtil.remove(skynetProxy); SkynetProxy
	 * proxyInfo = ProxyIPUtil.getProxyIpFromWeb(); if (proxyInfo != null) {
	 * String new_ip = proxyInfo.getIp(); Integer new_port =
	 * Integer.parseInt(proxyInfo.getPort()); return new String[] { new_ip,
	 * new_port + "" }; } } } return null; }
	 */

	/**
	 * 阿布云代理
	 * 
	 * @param pageHref
	 * @param headers
	 * @param rTime
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static String getByABY(String pageHref, Map<String, String> headers,
			int rTime) throws IOException, Exception {
		String html = null;
		for (int i = 1; i <= rTime; i++) {
			try {
				html = ProxyDemo.getHtml(pageHref, headers);
				// html = HttpUtil.get(pageHref, headers,null);
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
			if (StringUtils.isNotBlank(html)) {
				break;
			}
			log.info(String.format("--not proxy 为您 第 %s次请求 %s", i, pageHref));
		}
		return html;
	}

	public static String get(String pageHref, Map<String, String> headers,
			String userEncode, int rTime) throws IOException, Exception {
		String html = null;
		for (int i = 1; i <= rTime; i++) {
			try {
				List<String> returnList = HttpUtil.get(pageHref, headers,
						userEncode);
				if (returnList != null) {
					html = returnList.get(1);
				}
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
			if (StringUtils.isNotBlank(html)) {
				break;
			}
			log.info(String.format("--not proxy 为您 第 %s次请求 %s", i, pageHref));
		}
		return html;
	}

	static HttpConnectionManager connManager;
	static {
		connManager = new HttpConnectionManager();
	}

	/*
	 * params : url: 地址 headers请求头部信息 return : httpresponse响应
	 */
	public static List<Object> doGet(String url, Map<String, String> headers)
			throws Exception {
		return doGet(null, url, headers, 1);
	}

	/*
	 * params : url: 地址 headers请求头部信息 return : httpresponse响应
	 */
	public static List<Object> doGet(CloseableHttpClient client, String url,
			Map<String, String> headers) throws Exception {
		return doGet(client, url, headers, Config.REQEUST_TIME);
	}

	public static String getParsedHref(String url) {
		if (url.startsWith("//")) {
			url = url.replaceFirst("//", "");
		}
		if (!(url.startsWith("http://") || url.startsWith("https://"))) {
			url = "http://" + url;
		}
		url = url.replaceAll(" ", "%20");
		return url;
	}

	/*
	 * params : url: 地址 headers请求头部信息 return : httpresponse响应
	 */
	public static List<Object> doGet(CloseableHttpClient client, String url,
			Map<String, String> headers, int rTime) throws Exception {
		url = getParsedHref(url);
		List<Object> result = null;

		URL url_;
		URI uri = null;
		try {
			url_ = new URL(url);
			uri = new URI(url_.getProtocol(), url_.getAuthority(),
					url_.getPath(), url_.getQuery(), null);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		CloseableHttpClient hclient = null;
		if (client != null) {
			hclient = client;
		} else {
			hclient = connManager.getHttpClient();
		}
		Builder builder = RequestConfig.custom()
				.setSocketTimeout(Config.SOCKET_TIMEOUT)
				.setConnectionRequestTimeout(Config.CONNECT_TIMEOUT)
				.setRedirectsEnabled(true)
				.setConnectTimeout(Config.CONNECT_TIMEOUT);

		// 设置请求和传输超时时间
		/*
		 * HttpHost proxy = null; if (Config.BY_PROXY) { try { SkynetProxy
		 * proxyInfo = ProxyIPUtil.getProxyIpFromWeb(); proxy = new
		 * HttpHost(proxyInfo.getIp(), Integer.parseInt(proxyInfo.getPort())); }
		 * catch (NumberFormatException e1) { e1.printStackTrace(); } } if
		 * (proxy != null) { builder.setProxy(proxy); }
		 */
		RequestConfig requestConfig = builder.build();

		HttpContext httpContext = new BasicHttpContext();
		HttpGet getMethod = new HttpGet(uri);
		getMethod.setConfig(requestConfig);
		CloseableHttpResponse response = null;
		try {
			if (headers != null && headers.keySet().size() > 0) {
				for (String key : headers.keySet()) {
					getMethod.addHeader(key, headers.get(key));
				}
			}
			response = hclient.execute(getMethod, httpContext);
		} finally {
			//
		}
		HttpHost targetHost = (HttpHost) httpContext
				.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
		HttpUriRequest realRequest = (HttpUriRequest) httpContext
				.getAttribute(ExecutionContext.HTTP_REQUEST);
		result = new ArrayList<Object>();
		result.add(response);
		result.add(hclient);
		result.add(response.getStatusLine().getStatusCode() + "");
		result.add(targetHost + realRequest.getURI().toString());
		// //System.out.println(response);
		return result;
	}

	/*
	 * 从响应中得到字符串
	 */
	public static SkynetHttpEntity getStringFromResponse(
			CloseableHttpResponse response, CloseableHttpClient client,
			String userEncode) throws Exception {
		String content = null;
		int statusCode = 200;
		String redirectLocation = null;
		try {
			if (response == null) {
				return null;
			}
			statusCode = response.getStatusLine().getStatusCode();
			redirectLocation = getLocation(response);
			if (response.getStatusLine().getStatusCode() == 200) {
				if (StringUtils.isNotBlank(userEncode)) {
					content = EntityUtils.toString(response.getEntity(),
							userEncode);
				} else {
					content = EntityUtils.toString(response.getEntity(),
							"utf-8");
				}
			}
		} finally {
			if (response != null)
				response.close();
		}
		return new SkynetHttpEntity(content, client, redirectLocation,
				statusCode);
	}

	/**
	 * 创建支持多线程并发连接的HTTPCLIENT
	 * 
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	/*
	 * private final static HttpClient createHttpClient(boolean bySSL) throws
	 * KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
	 * String proxyHost = "web-proxy-sha.chn.hp.com"; int proxyPort = 8080;
	 * HttpHost proxy = new HttpHost(proxyHost, proxyPort); HttpParams params =
	 * new BasicHttpParams(); if (Constant.enableProxy) {
	 * params.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy); }
	 * HttpConnectionParams.setConnectionTimeout(params, 5 * 1000);
	 * HttpConnectionParams.setSoTimeout(params, 5 * 1000);
	 * HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	 * HttpProtocolParams.setContentCharset(params, "UTF-8");
	 * 
	 * ThreadSafeClientConnManager clientmanager = new
	 * ThreadSafeClientConnManager(); clientmanager.setMaxTotal(20); HttpClient
	 * client = null; if(bySSL){ client = SSLClient.createSSLClient(); }else{
	 * client = new DefaultHttpClient(clientmanager, params); //
	 * 定义了环形重定向,定向到相同的路径是否被允许. client.getParams().setParameter(
	 * "http.protocol.allow-circular-redirects", true); // 定义了重定向的最大数量
	 * client.getParams().setParameter("http.protocol.max-redirects", 50); //
	 * 定义了重定向是否应该自动处理 client.getParams()
	 * .setParameter("http.protocol.handle-redirects", false); }
	 * 
	 * return client; }
	 */

	/*
	 * private static String sniffCharacterEncoding(byte[] str, Header
	 * contentType) { String encoding = null; if (contentType != null &&
	 * contentType.toString().split("charset=").length > 1) { return
	 * contentType.toString().split("charset=")[1]; } try { Matcher
	 * charsetMatcher = charsetPattern.matcher(new String(str)); if
	 * (charsetMatcher.find()) { encoding = charsetMatcher.group(); if
	 * (encoding.toLowerCase().contains("utf")) return "UTF-8"; else { if
	 * (encoding.toLowerCase().contains("gbk")) return "gbk"; if
	 * (encoding.toLowerCase().contains("2312")) return "gb2312"; } } encoding =
	 * getEncoding(str); if (encoding != null && !encoding.equals("")) { return
	 * encoding; } if (encoding == null || encoding.equals("")) { encoding =
	 * DEFAULT; } } catch (Exception e) { return DEFAULT; } return encoding; }
	 */

	/*
	 * private static String getEncoding(byte[] content) { nsDetector det = new
	 * nsDetector(nsPSMDetector.ALL); nsICharsetDetectionObserverImp nsIC = new
	 * nsICharsetDetectionObserverImp(); det.Init(nsIC); det.DoIt(content,
	 * content.length, false); det.DataEnd(); String encode =
	 * nsIC.getEncoding(); // System.err.println(encode); //
	 * if(encode.equals("Big5")) // return "Big5"; if (encode != null &&
	 * !encode.equals("") && !encode.equalsIgnoreCase("utf-8")) { encode =
	 * "GBK"; } return encode; }
	 */

	/*
	 * private static class nsICharsetDetectionObserverImp implements
	 * nsICharsetDetectionObserver { String encod = "";
	 * 
	 * public void Notify(String charset) { HtmlCharsetDetector.found = true;
	 * encod = charset; }
	 * 
	 * public String getEncoding() { return encod; } }
	 */

	public static String getAbsolutePath(String url, String toBeParsedUrl) {
		try {
			url = getParsedUrl(url);
			toBeParsedUrl = getParsedUrl(toBeParsedUrl);
			URI base = new URI(url);// 基本网页URI
			if (StringUtils.isBlank(toBeParsedUrl))
				return null;
			if (!toBeParsedUrl.matches("^(https?|ftp):(\\\\|//).*$")) {
				URI abs = base.resolve(toBeParsedUrl);// 解析相对URL，得到绝对URI
				return abs.toURL().toString();
			}
		} catch (Exception e) {
			// e.printStackTrace();
			// log.info(url +"\t" +toBeParsedUrl+"###err###" + e.getMessage());
		}
		return toBeParsedUrl;
	}

	public static void addDefaultHeader(Map<String, String> headers) {
		Iterator<Entry<String, String>> itr = DEFAULT_HEADERS.entrySet()
				.iterator();
		while (itr.hasNext()) {
			Entry<String, String> entry = itr.next();
			headers.put(entry.getKey(), entry.getValue());
		}
	}

	public static void addMobileHeader(Map<String, String> headers) {
		Iterator<Entry<String, String>> itr = MOBILE_HEADERS.entrySet()
				.iterator();
		while (itr.hasNext()) {
			Entry<String, String> entry = itr.next();
			headers.put(entry.getKey(), entry.getValue());
		}
	}

	public static Map<String, String> getHeaders() {
		Map<String, String> headers = new HashMap<String, String>();
		addDefaultHeader(headers);
		return headers;
	}

	public static Map<String, String> getMobileHeaders() {
		Map<String, String> headers = new HashMap<String, String>();
		addMobileHeader(headers);
		return headers;
	}

	private static String getParsedUrl(String href) {
		href = href.replaceAll(" ", "%20");
		return href;
	}

	public static void main(String[] args) {
		/*
		 * Map<String, String> otherHeaders = new HashMap<String, String>();
		 * otherHeaders.put("Host", "www.tripadvisor.cn");
		 * otherHeaders.put("Origin", "https://www.tripadvisor.cn");
		 * otherHeaders .put("Cookie",
		 * "TAUnique=%1%enc%3Aw15YmyxtSRYZ8FEsuWWLXswJA8MG3YTM9OsUkbr9oBE1jFGw1G8Jhw%3D%3D; ServerPool=C; TASSK=enc%3AABeWa2ljtkrVX%2FDzYR6yWpYntuE5VfZGCc3NtaH04p0GnGBsYi5aEGQIIdzL3rmLtBRRgzWxCNHBFUo2ZSxh9sBcztq%2F2WvlNdAQAmMfi2dk0LuU8gC%2FWwXdQhM8RTO0Qg%3D%3D; VRMCID=%1%V1*id.16631*llp.%2F-a_ttcampaign%5C.MTYpc-a_ttgroup%5C.title-m16631*e.1505965512310; _smt_uid=59b9fb44.45b3df58; __gads=ID=9738e353ec99854d:T=1505360726:S=ALNI_MbTi1VS9VMpkVlZio8P1KtrSX2bBA; _jzqy=1.1505360710.1505360710.1.jzqsr=baidu|jzqct=%E5%88%B0%E5%88%B0%E7%BD%91.-; _jzqckmp=1; CommercePopunder=SuppressAll*1505360757733; BEPIN=%1%15e7e7eb346%3Bbak09c.daodao.com%3A10023%3B; _jzqx=1.1505366157.1505366157.1.jzqsr=tripadvisor%2Ecn|jzqct=/hotels-g294212-beijing-hotels%2Ehtml.-; CM=%1%PremiumMobSess%2C%2C-1%7Ct4b-pc%2C%2C-1%7CRCPers%2C%2C-1%7CHomeAPers%2C%2C-1%7CPremiumMobPers%2C%2C-1%7CWShadeSeen%2C%2C-1%7CRCSess%2C%2C-1%7CTheForkMCCPers%2C%2C-1%7CHomeASess%2C1%2C-1%7CLaFourchette+MC+Banners%2C%2C-1%7CPremiumSURPers%2C%2C-1%7CPremiumMCSess%2C%2C-1%7Csh%2C%2C-1%7Cpssamex%2C%2C-1%7CTheForkMCCSess%2C%2C-1%7CCCPers%2C%2C-1%7CCCSess%2C%2C-1%7CPremRetPers%2C%2C-1%7CViatorMCPers%2C%2C-1%7Cb2bmcsess%2C%2C-1%7Csesssticker%2C%2C-1%7CPremiumORSess%2C%2C-1%7Ct4b-sc%2C%2C-1%7CPremRetSess%2C%2C-1%7CViatorMCSess%2C%2C-1%7CMC_IB_UPSELL_IB_LOGOS2%2C%2C-1%7Cb2bmcpers%2C%2C-1%7CPremiumMCPers%2C%2C-1%7CMC_IB_UPSELL_IB_LOGOS%2C%2C-1%7CPremMCBtmSess%2C%2C-1%7CPremiumRRPers%2C%2C-1%7CPremiumSURSess%2C%2C-1%7CLaFourchette+Banners%2C%2C-1%7Csess_rev%2C%2C-1%7Csessamex%2C%2C-1%7CPremiumRRSess%2C%2C-1%7CTheForkORPers%2C%2C-1%7CPremMCBtmPers%2C%2C-1%7CTheForkRRPers%2C%2C-1%7CTheForkORSess%2C%2C-1%7CPremiumORPers%2C%2C-1%7CTheForkRRSess%2C%2C-1%7Cpers_rev%2C%2C-1%7CRBASess%2C%2C-1%7Cperssticker%2C%2C-1%7Cmds%2C1505368611813%2C1505455011%7CRBAPers%2C%2C-1%7C; _ga=GA1.2.536752204.1505360696; _gid=GA1.2.271873785.1505360696; ki_t=1505360711795%3B1505360711795%3B1505368784030%3B1%3B23; ki_r=; Hm_lvt_2947ca2c006be346c7a024ce1ad9c24a=1505360709; Hm_lpvt_2947ca2c006be346c7a024ce1ad9c24a=1505368784; _qzja=1.1306400318.1505360711080.1505360711081.1505366157953.1505368781640.1505368784098..0.0.23.2; _qzjc=1; _qzjto=23.2.0; _jzqa=1.3843546897605326000.1505360710.1505360710.1505366157.2; _jzqc=1; TAReturnTo=%1%%2FHotels-g294212-Beijing-Hotels.html; roybatty=TNI1625!AGGQ9Z2XImpUWwqJkcioLd9tE42C95xqyNeqOsyPOZoE8w6PT4ggFgSt4RFqtpDiUBX8epgBDu1RBGOVgrAz7NXb%2FmIQF40SnNV3X033hCu5%2F1v5BHR%2BvRyCMDYDrjPNsAa%2Bd06SOEXDHTLMWGU96i4TFurodPa6LDH1%2F9Y4MHDd%2C1; TASession=%1%V2ID.3DC533E66AD8D30B74F2EF19B77A530D*SQ.191*MC.16631*LR.http%3A%2F%2Fbzclk%5C.baidu%5C.com%2Fadrc%5C.php%3Foq%3D%25E5%2588%25B0%25E5%2588%25B0%25E7%25BD%2591%26rqlang%3Dcn%26usm%3D1%26cq%3D%25E7%25A9%25B7%25E6%25B8%25B8%25E7%25BD%2591%26srcid%3D28310%26rt%3D%25E6%2597%2585%25E6%25B8%25B8%25E6%2588%25B7%25E5%25A4%2596%25E7%25B1%25BB%25E7%25BD%2591%25E7%25AB%2599%26recid%3D21102%26euri%3D4db49cf898f9447987b262a2e2e813e5*LP.%2F-a_ttcampaign%5C.MTYpc-a_ttgroup%5C.title-m16631*PR.39538%7C*LS.MetaPlacementAjax*PD13481.2*GR.24*TCPAR.62*TBR.27*EXEX.64*ABTR.10*PHTB.52*FS.76*CPU.94*HS.availability*ES.popularity*AS.popularity*DS.5*SAS.popularity*FPS.oldFirst*FA.2*DF.0*MS.-1*RMS.-1*TRA.false*LD.1469049*BG.294211; TATravelInfo=V2*AY.2017*AM.9*AD.24*DY.2017*DM.9*DD.25*A.2*MG.294211*HP.2*FL.3*RHS.47d43_2017-09-24_1la102b_2017-09-24_1l2_2017-09-24_1*RVL.293916_256l2318761_256l2_256l659499_256l294211_256l1469049_257l302144_257l12133922_257*DSM.1505370788754*RS.1; TAUD=RDD-1505360712293-2017_09_13*HDD-228955-2017_09_24.2017_09_25*HC-253247*LA-494426-2*LD-10076405-2017.9.24.2017.9.25*LG-10076410-2.1.F."
		 * );
		 * 
		 * try { List<Object> r = HttpUtil.doGet(
		 * "http://ypk.39.net/search/肠道病毒Cox A16型手足口-p1/", null);
		 * System.out.println(r); } catch (Exception e) { e.printStackTrace(); }
		 */
		try {
			byte[] filebytes = getFile(new DefaultHttpClient(),
					"http://zy.yaozh.com/instruct/20140214/12.doc", null);
			byte2File(filebytes, "D://", "temp_xxxx.doc");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String parseCookieToStr(BasicCookieStore bcs) {
		List<Cookie> cookies = bcs.getCookies();
		ArrayList<Map<String, Object>> diyCookies = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < cookies.size(); i++) {
			Map<String, Object> diyCookieMap = new HashMap<String, Object>();
			diyCookieMap.put(cookies.get(i).getName(), cookies.get(i)
					.getValue());
			Iterator<Map<String, Object>> iterator = diyCookies.iterator();// 做sesion驅蟲處理
			while (iterator.hasNext()) {
				Map<String, Object> next = iterator.next();
				if (next.get(cookies.get(i).getName()) != null) {
					iterator.remove();
					break;
				}
			}
			diyCookies.add(diyCookieMap);
		}

		StringBuffer cookieStr = new StringBuffer();
		for (Map<String, Object> cResult : diyCookies) {
			Iterator<Entry<String, Object>> iterator = cResult.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> entry = iterator.next();
				cookieStr.append(entry.getKey());
				cookieStr.append("=");
				cookieStr.append(entry.getValue());
				cookieStr.append(";");
			}
		}
		return cookieStr.toString();
	}
}
