package org.skynet.frame.util.mongo;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.Binary;
import org.skynet.frame.config.Config;
import org.skynet.frame.persistent.MongoIndex;
import org.skynet.frame.util.zlib.ZLib;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.UpdateOptions;

/**
 * Mongo Utils
 * 
 * @author 2bu
 *
 */
public class MongoUtil {
	private static Logger log = Logger.getLogger(MongoUtil.class.getName());
	private static String ip = Config.HOST; // server ip
	private static Integer port = Config.PORT; // server port
	private static MongoClient mongoClient;// mongoClient
	private static List<MongoDatabase> mongoDatabases = new ArrayList<MongoDatabase>();// mongoDatabases
	private static Map<String, MongoCollection<Document>> mongoCollectionMap = new HashMap<String, MongoCollection<Document>>();// mongoCollection
	static MongoClientOptions myOptions;
	static {

		MongoClientOptions.Builder build = new MongoClientOptions.Builder();
		build.connectionsPerHost(50); // 与目标数据库能够建立的最大connection数量为50
		build.threadsAllowedToBlockForConnectionMultiplier(50); // 如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待
		/*
		 * 一个线程访问数据库的时候，在成功获取到一个可用数据库连接之前的最长等待时间为2分钟
		 * 这里比较危险，如果超过maxWaitTime都没有获取到这个连接的话，该线程就会抛出Exception
		 * 故这里设置的maxWaitTime应该足够大，以免由于排队线程过多造成的数据库访问失败
		 */
		build.maxWaitTime(1000 * 60 * 2);
		build.connectTimeout(1000 * 60 * 1); // 与数据库建立连接的timeout设置为1分钟

		myOptions = build.build();
		if (Config.NEED_AUTH) {
			MongoUtil
					.authentication(Config.UERNAME, Config.PASSWORD, Config.DB);
		}
	}

	/**
	 * Authentication user permissions
	 * 
	 * @param userName
	 *            auth userName
	 * @param password
	 *            auth password
	 * @param dbName
	 *            auth dbName
	 * @return
	 */
	public static boolean authentication(String userName, String password,
			String dbName) {
		ServerAddress serverAddress = new ServerAddress(ip, port);
		MongoCredential credential = MongoCredential.createScramSha1Credential(
				userName, dbName, password.toCharArray());
		List<MongoCredential> credentials = new ArrayList<MongoCredential>();
		credentials.add(credential);
		List<ServerAddress> addrs = new ArrayList<ServerAddress>();
		addrs.add(serverAddress);
		// mongoClient = new MongoClient(addrs, credentials,myOptions);
		mongoClient = new MongoClient(addrs, credentials);
		return true;
	}

	/**
	 * Authentication user permissions
	 * 
	 * @param ip
	 * @param port
	 * @param userName
	 * @param password
	 * @param dbName
	 * @return
	 */
	public static MongoClient authenticationAndGetClient(String ip,
			Integer port, String userName, String password, String dbName) {
		ServerAddress serverAddress = new ServerAddress(ip, port);
		List<ServerAddress> addrs = new ArrayList<ServerAddress>();
		addrs.add(serverAddress);
		if (StringUtils.isBlank(userName) && StringUtils.isBlank(password)) {
			MongoClient mongoClient = new MongoClient(addrs);
			return mongoClient;
		}
		MongoCredential credential = MongoCredential.createScramSha1Credential(
				userName, dbName, password.toCharArray());
		List<MongoCredential> credentials = new ArrayList<MongoCredential>();
		credentials.add(credential);
		// mongoClient = new MongoClient(addrs, credentials,myOptions);
		MongoClient mongoClient = new MongoClient(addrs, credentials);
		return mongoClient;
	}

	/**
	 * Add an index to collection
	 * 
	 * @param collection
	 *            collection
	 * @param field
	 *            collection field to be index
	 * @param isUnique
	 *            unique flag
	 */
	public static void addIndex(MongoCollection<org.bson.Document> collection,
			String field, boolean isUnique) {
		BasicDBObject fieldDBObj = new BasicDBObject();
		fieldDBObj.put(field, 1);
		IndexOptions indexOptions = new IndexOptions();
		indexOptions.unique(isUnique);
		collection.createIndex(fieldDBObj, indexOptions);
	}

	/**
	 * Add an index to collection
	 * 
	 * @param collection
	 *            collection
	 * @param field
	 *            collection field to be index
	 * @param isUnique
	 *            unique flag
	 */
	public static void addIndexs(MongoCollection<org.bson.Document> collection,
			MongoIndex... mongoIndexs) {
		List<IndexModel> models = new ArrayList<IndexModel>();
		for (MongoIndex mongoIndex : mongoIndexs) {
			BasicDBObject fieldDBObj = new BasicDBObject();
			fieldDBObj.put(mongoIndex.getIndexName(), 1);
			IndexOptions indexOptions = new IndexOptions();
			indexOptions.unique(mongoIndex.getUnique());
			IndexModel indexModel = new IndexModel(fieldDBObj, indexOptions);
			models.add(indexModel);
		}
		collection.createIndexes(models);
	}

	/**
	 * Add an index to collection
	 * 
	 * @param collection
	 *            collection
	 * @param field
	 *            collection field to be index
	 * @param isUnique
	 *            unique flag
	 */
	public static void addIndexs(String collectionName,
			MongoIndex... mongoIndexs) {
		MongoCollection<Document> collection = MongoUtil
				.getCollection(collectionName);
		addIndexs(collection, mongoIndexs);
	}

	/**
	 * Add an index to collection
	 * 
	 * @param collection
	 *            collection
	 * @param field
	 *            collection field to be index
	 * @param isUnique
	 *            unique flag
	 */
	public static void addCombinationIndex(
			MongoCollection<org.bson.Document> collection,
			List<MongoIndex> mongoIndexs, boolean isUnique) {
		BasicDBObject fieldDBObjs = new BasicDBObject();
		for (MongoIndex mongoIndex : mongoIndexs) {
			fieldDBObjs.put(mongoIndex.getIndexName(), 1);
		}
		IndexOptions indexOptions = new IndexOptions();
		indexOptions.unique(isUnique);
		collection.createIndex(fieldDBObjs, indexOptions);
	}
	

	/**
	 * Add an index to collection
	 * 
	 * @param collection
	 *            collection
	 * @param field
	 *            collection field to be index
	 * @param isUnique
	 *            unique flag
	 */
	public static void addCombinationIndex(
			String collectionName,
			List<MongoIndex> mongoIndexs, boolean isUnique) {
		addCombinationIndex(getCollection(collectionName), mongoIndexs, isUnique);
	}

	/**
	 * Add an index to collection
	 * 
	 * @param collection
	 *            collection
	 * @param field
	 *            collection field to be index
	 * @param isUnique
	 *            unique flag
	 */
	public static void addCombinationIndex(
			MongoCollection<org.bson.Document> collection, boolean isUnique,
			MongoIndex... mongoIndexs) {
		BasicDBObject fieldDBObjs = new BasicDBObject();
		for (MongoIndex mongoIndex : mongoIndexs) {
			fieldDBObjs.put(mongoIndex.getIndexName(), 1);
		}
		IndexOptions indexOptions = new IndexOptions();
		indexOptions.unique(isUnique);
		collection.createIndex(fieldDBObjs, indexOptions);
	}

	/**
	 * Add an index to collection
	 * 
	 * @param collection
	 *            collection
	 * @param field
	 *            collection field to be index
	 * @param isUnique
	 *            unique flag
	 */
	public static void addCombinationIndex(String collectionName,
			boolean isUnique, MongoIndex... mongoIndexs) {
		addCombinationIndex(getCollection(collectionName), isUnique,
				mongoIndexs);
	}

	/**
	 * Add an index to collections
	 * 
	 * @param collections
	 *            collections
	 * @param field
	 *            collection field to be index
	 * @param isUnique
	 *            unique flag
	 */
	public static void addIndex(
			List<MongoCollection<org.bson.Document>> collections, String field,
			boolean isUnique) {
		for (MongoCollection<Document> mongoCollection : collections) {
			addIndex(mongoCollection, field, isUnique);
		}
	}

	/**
	 * Add an index to collection
	 * 
	 * @param collectionName
	 *            collectionName
	 * @param field
	 *            collection field to be index
	 * @param isUnique
	 *            unique flag
	 */
	public static void addIndex(String collectionName, String field,
			boolean isUnique) {
		MongoCollection<Document> mongoCollection = MongoUtil.getCollection(
				Config.DB, collectionName);
		addIndex(mongoCollection, field, isUnique);
	}

	/**
	 * Connect server noAuthentication
	 */
	public static void noAuthentication() {
		ServerAddress serverAddress = new ServerAddress(ip, port);
		List<ServerAddress> addrs = new ArrayList<ServerAddress>();
		addrs.add(serverAddress);
		// mongoClient = new MongoClient(addrs,myOptions);
		mongoClient = new MongoClient(addrs);
	}

	/**
	 * Connect server noAuthentication
	 */
	public static MongoClient noAuthentication(String ip, Integer port) {
		ServerAddress serverAddress = new ServerAddress(ip, port);
		List<ServerAddress> addrs = new ArrayList<ServerAddress>();
		addrs.add(serverAddress);
		// mongoClient = new MongoClient(addrs,myOptions);
		MongoClient mongoClient = new MongoClient(addrs);
		return mongoClient;
	}

	/**
	 * Get a db of server
	 * 
	 * @param databaseName
	 *            databaseName
	 * @return
	 */
	public static MongoDatabase getDB(String databaseName) {
		if (mongoClient == null) {
			noAuthentication();
		}
		MongoDatabase returnMongoDatabase = null;
		for (MongoDatabase mongoDatabase : mongoDatabases) {
			if (mongoDatabase.getName().equals(databaseName)) {
				returnMongoDatabase = mongoDatabase;
				break;
			}
		}
		if (returnMongoDatabase == null) {
			returnMongoDatabase = mongoClient.getDatabase(databaseName);
			mongoDatabases.add(returnMongoDatabase);
		}
		return returnMongoDatabase;
	}

	/**
	 * Get a collection of db
	 * 
	 * @param dbName
	 * @param collectionName
	 * @return
	 */
	public static MongoCollection<org.bson.Document> getCollection(
			String collectionName) {
		return getCollection(Config.DB, collectionName);
	}

	/**
	 * Get a collection of db
	 * 
	 * @param dbName
	 * @param collectionName
	 * @return
	 */
	public static void dropCollection(String collectionName) {
		getCollection(collectionName).drop();
	}

	public static Pattern like(String str) {
		Pattern ptn = Pattern.compile("^.*" + str + ".*$",
				Pattern.CASE_INSENSITIVE);
		return ptn;
	}

	public static Pattern pattern(String patternStr) {
		Pattern ptn = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
		return ptn;
	}

	/**
	 * Get a collection of db
	 * 
	 * @param dbName
	 * @param collectionName
	 * @return
	 */
	public static MongoCollection<org.bson.Document> getCollection(
			String dbName, String collectionName) {
		MongoCollection<Document> cacheCollection = mongoCollectionMap
				.get(dbName + "_" + collectionName);
		if (cacheCollection != null) {
			return cacheCollection;
		}
		cacheCollection = getDB(dbName).getCollection(collectionName);
		if (cacheCollection != null) {
			mongoCollectionMap.put(dbName + "_" + collectionName,
					cacheCollection);
		}
		return cacheCollection;
	}

	/**
	 * Get collections of db
	 * 
	 * @param dbName
	 *            The dbName
	 * @param collectionNames
	 *            The collectionNames
	 * @return
	 */
	public static List<MongoCollection<org.bson.Document>> getCollections(
			String dbName, String... collectionNames) {
		List<MongoCollection<org.bson.Document>> collections = new ArrayList<MongoCollection<Document>>();
		for (String cName : collectionNames) {
			MongoCollection<Document> collection = getDB(dbName).getCollection(
					cName);
			collections.add(collection);
		}
		return collections;
	}

	/**
	 * Get collections of db
	 * 
	 * @param dbName
	 *            The dbName
	 * @param collectionNames
	 *            The collectionNames
	 * @return
	 */
	public static List<MongoCollection<org.bson.Document>> getCollectionsFromConfigDB(
			String... collectionNames) {
		List<MongoCollection<org.bson.Document>> collections = new ArrayList<MongoCollection<Document>>();
		for (String cName : collectionNames) {
			MongoCollection<Document> collection = getDB(Config.DB)
					.getCollection(cName);
			collections.add(collection);
		}
		return collections;
	}

	/**
	 * Save the doc
	 * 
	 * @param collection
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean saveDoc(String collectionName, Map<String, Object> map) {
		if (map instanceof JSONObject) {
			String json = map.toString().replaceAll(":null", ":''");
			map = JSONObject.fromObject(json);
		}
		MongoCollection<Document> collection = MongoUtil.getCollection(
				Config.DB, collectionName);
		return saveDoc(collection, map);
	}
	/**
	 * Save the doc
	 * 
	 * @param collection
	 * @param map
	 * @return
	 */
	public static boolean saveDoc(
			MongoCollection<org.bson.Document> collection,
			Map<String, Object> map) {
		if (map == null)
			throw new NullPointerException("Map can't be null");
		Document document = new Document(map);
		try {
			collection.insertOne(document);
		} catch (Exception e) {
			// e.printStackTrace();
			// System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Save the bean
	 * 
	 * @param collection
	 * @param map
	 * @return
	 */
	public static boolean saveBean(
			MongoCollection<org.bson.Document> collection, Object bean) {
		if (bean == null)
			throw new NullPointerException("Map can't be null");
		Document document = new Document(BeanUtil.transBean2Map(bean));
		try {
			collection.insertOne(document);
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Save the bean
	 * 
	 * @param collection
	 * @param map
	 * @return
	 */
	public static boolean saveBean(String collectionName, Object bean) {
		if (bean == null)
			throw new NullPointerException("Map can't be null");
		Document document = new Document(BeanUtil.transBean2Map(bean));
		MongoCollection<Document> collection = MongoUtil.getCollection(
				Config.DB, collectionName);
		try {
			collection.insertOne(document);
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			log.error(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get the collection count by conditionMap
	 * 
	 * @param collection
	 *            The collection
	 * @param conditionMap
	 *            The condition map
	 * @return
	 */
	public static int getCount(String collectionName,
			Map<String, Object> conditionMap) {
		MongoCollection<Document> collection = MongoUtil.getCollection(
				Config.DB, collectionName);
		return getCount(collection, conditionMap);
	}
	/**
	 * Get the collection count by conditionMap
	 * 
	 * @param collection
	 *            The collection
	 * @param conditionMap
	 *            The condition map
	 * @return
	 */
	public static int getCount(String collectionName) {
		MongoCollection<Document> collection = MongoUtil.getCollection(
				Config.DB, collectionName);
		return getCount(collection, null);
	}

	/**
	 * Get the collection count by conditionMap
	 * 
	 * @param collection
	 *            The collection
	 * @param conditionMap
	 *            The condition map
	 * @return
	 */
	public static int getCount(MongoCollection<org.bson.Document> collection,
			Map<String, Object> conditionMap) {
		BasicDBObject basicDBObject = null;
		if(conditionMap == null){
			basicDBObject = new BasicDBObject();
		}else{
			basicDBObject = new BasicDBObject(conditionMap);
		}
		try {
			return Integer.parseInt(String.valueOf(collection
					.count(basicDBObject)));
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			return 0;
		}
	}

	/**
	 * Get the collection count by conditionFieldName and conditionFieldValue
	 * 
	 * @param conditionFieldName
	 *            The conditionFieldName
	 * @param conditionFieldValue
	 *            The conditionFieldValue
	 * @return
	 */
	public static int getCount(MongoCollection<org.bson.Document> collection,
			String conditionFieldName, Object conditionFieldValue) {
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put(conditionFieldName, conditionFieldValue);
		BasicDBObject basicDBObject = new BasicDBObject(conditionMap);
		try {
			return Integer.parseInt(String.valueOf(collection
					.count(basicDBObject)));
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			return 0;
		}
	}

	public static boolean has(MongoCollection<org.bson.Document> collection,
			String conditionFieldName, Object conditionFieldValue) {
		return getCount(collection, conditionFieldName, conditionFieldValue) > 0;
	}

	public static boolean has(String collectionName, String conditionFieldName,
			Object conditionFieldValue) {
		return getCount(getCollection(collectionName), conditionFieldName,
				conditionFieldValue) > 0;
	}

	public static boolean has(String collectionName, Map<String, Object> query) {
		return getCount(getCollection(collectionName), query) > 0;
	}

	/**
	 * Update collection
	 * 
	 * @param collection
	 *            The collection
	 * @param queryMap
	 *            The queryMap
	 * @param updateMap
	 *            The updateMap
	 * @return
	 */
	public static boolean updateDoc(String collection,
			Map<String, Object> queryMap, Map<String, Object> updateMap) {
		return updateDoc(getCollection(collection), queryMap, updateMap);
	}

	/**
	 * Update collection
	 * 
	 * @param collection
	 *            The collection
	 * @param queryMap
	 *            The queryMap
	 * @param updateMap
	 *            The updateMap
	 * @return
	 */
	public static boolean updateDoc(
			MongoCollection<org.bson.Document> collection,
			Map<String, Object> queryMap, Map<String, Object> updateMap) {
		BasicDBObject queryDBObject = new BasicDBObject(queryMap);
		BasicDBObject updateDBObject = new BasicDBObject(updateMap);
		BasicDBObject updateSetValue = new BasicDBObject("$set", updateDBObject);
		UpdateOptions options = new UpdateOptions();
		// options.upsert(true);
		try {
			collection.updateOne(queryDBObject, updateSetValue, options);
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Update one collection
	 * 
	 * @param collection
	 *            The collection
	 * @param queryMap
	 *            The queryMap
	 * @param updateMap
	 *            The updateMap
	 * @return
	 */
	public static Document findOneAndUpdate(String collectionName,
			Map<String, Object> queryMap, Map<String, Object> updateMap) {
		return findOneAndUpdate(getCollection(collectionName), queryMap,
				updateMap);
	}

	/**
	 * Update one collection
	 * 
	 * @param collection
	 *            The collection
	 * @param queryMap
	 *            The queryMap
	 * @param updateMap
	 *            The updateMap
	 * @return
	 */
	public static Document findOneAndUpdate(
			MongoCollection<org.bson.Document> collection,
			Map<String, Object> queryMap, Map<String, Object> updateMap) {
		return findOneAndUpdate(collection, queryMap, updateMap, null);
	}

	/**
	 * Update one collection
	 * 
	 * @param collection
	 *            The collection
	 * @param queryMap
	 *            The queryMap
	 * @param updateMap
	 *            The updateMap
	 * @return
	 */
	public static Document findOneAndUpdate(
			MongoCollection<org.bson.Document> collection,
			Map<String, Object> queryMap, Map<String, Object> updateMap,
			FindOneAndUpdateOptions findOneAndUpdateOptions) {
		BasicDBObject queryDBObject = new BasicDBObject(queryMap);
		BasicDBObject updateDBObject = new BasicDBObject(updateMap);
		BasicDBObject updateSetValue = new BasicDBObject("$set", updateDBObject);
		Document document = null;
		try {
			if (findOneAndUpdateOptions != null) {
				document = collection.findOneAndUpdate(queryDBObject,
						updateSetValue, findOneAndUpdateOptions);
			} else {
				document = collection.findOneAndUpdate(queryDBObject,
						updateSetValue);
			}
			return document;
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			return null;
		}
	}

	public static String getStringParsedValue(org.bson.Document doc,
			String fieldName) {
		Object obj = doc.get(fieldName);
		String objStr = "";
		if (obj instanceof Binary) {
			try {
				byte[] bytes = ZLib.decompress(((Binary) obj).getData());
				objStr = new String((bytes), guessEncoding(bytes));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				objStr = doc.getString(fieldName);
			}
		} else {
			objStr = doc.getString(fieldName);
		}
		return objStr;
	}

	public static String guessEncoding(byte[] bytes) {
		String DEFAULT_ENCODING = "UTF-8";
		org.mozilla.universalchardet.UniversalDetector detector = new org.mozilla.universalchardet.UniversalDetector(
				null);
		detector.handleData(bytes, 0, bytes.length);
		detector.dataEnd();
		String encoding = detector.getDetectedCharset();
		detector.reset();
		if (encoding == null) {
			encoding = DEFAULT_ENCODING;
		}
		return encoding;
	}

	public static boolean cpCollection(String srcCollectionName,
			String tarCollectionName) {
		return cpCollection(srcCollectionName, tarCollectionName, Config.HOST,
				Config.HOST, Integer.toString(Config.PORT), Config.UERNAME,
				Config.PASSWORD, Integer.toString(Config.PORT), Config.UERNAME,
				Config.PASSWORD, Config.DB, Config.DB);
	}

	public static boolean cpCollection(String srcCollectionName,
			String tarCollectionName, String srcIp, String tarIp,
			String srcPort, String srcUserName, String srcPassword,
			String tarPort, String tarUserName, String tarPassword,
			String srcDBName, String tarDBName) {
		MongoClient srcClient = null;
		MongoClient tarClient = null;
		try {
			srcClient = authenticationAndGetClient(srcIp,
					Integer.parseInt(srcPort), srcUserName, srcPassword,
					srcDBName);
			tarClient = authenticationAndGetClient(tarIp,
					Integer.parseInt(tarPort), tarUserName, tarPassword,
					tarDBName);
			MongoDatabase srcDatabase = srcClient.getDatabase(srcDBName);
			MongoDatabase tarDatabase = tarClient.getDatabase(tarDBName);
			MongoCollection<Document> srcCollection = srcDatabase
					.getCollection(srcCollectionName);
			MongoCollection<Document> tarCollection = tarDatabase
					.getCollection(tarCollectionName);
			int currCp = 0;
			FindIterable<Document> srcItr = srcCollection.find();
			MongoCursor<Document> srcCursor = srcItr.iterator();
			while (srcCursor.hasNext()) {
				Document doc = srcCursor.next();
				MongoUtil.saveDoc(tarCollection, doc);
				System.out.println(currCp++);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (srcClient != null)
				srcClient.close();
			if (tarClient != null)
				tarClient.close();
		}
		return true;
	}

	/**
	 * 获取索引列表，只支持单索引
	 * 
	 * @param collectionName
	 * @return
	 */
	public static List<MongoIndex> getIndexList(String collectionName) {
		List<MongoIndex> mongoIndexs = new ArrayList<MongoIndex>();
		ListIndexesIterable<Document> listIndexs = getCollection(collectionName)
				.listIndexes();
		MongoCursor<Document> itr = listIndexs.iterator();
		while (itr.hasNext()) {
			Document doc = itr.next();
			Object key = doc.get("key");
			Boolean unique = doc.getBoolean("unique");
			if (key instanceof Document) {
				Document keyDocument = (Document) key;
				Iterator<String> keysItr = keyDocument.keySet().iterator();
				if (keysItr.hasNext()) {// 只支持单索引
					String k = keysItr.next();
					if (unique == null) {
						unique = false;
					}
					MongoIndex mongoIndex = new MongoIndex(k, unique);
					mongoIndexs.add(mongoIndex);
				}
			}
		}
		return mongoIndexs;
	}

	public static void addIndexs(String collectionName,
			List<MongoIndex> mongoIndexs) {
		List<MongoIndex> midxs = getIndexList(collectionName);
		List<String> existsIdxNames = new ArrayList<String>();
		for (MongoIndex midx : midxs) {
			existsIdxNames.add(midx.getIndexName());
		}
		List<IndexModel> models = new ArrayList<IndexModel>();
		for (MongoIndex mongoIndex : mongoIndexs) {
			if (existsIdxNames.contains(mongoIndex.getIndexName())) {
				continue;
			}
			BasicDBObject fieldDBObj = new BasicDBObject();
			fieldDBObj.put(mongoIndex.getIndexName(), 1);
			IndexOptions indexOptions = new IndexOptions();
			indexOptions.unique(mongoIndex.getUnique());
			IndexModel indexModel = new IndexModel(fieldDBObj, indexOptions);
			models.add(indexModel);
		}
		getCollection(collectionName).createIndexes(models);
	}

	public static FindIterable<Document> find(String collectionName) {
		return find(collectionName, null, null, false,null);
	}
	public static FindIterable<Document> find(String collectionName,Map<String, Object> projection) {
		return find(collectionName, null, null, false,projection);
	}
	public static FindIterable<Document> find(String collectionName,
			Integer limit) {
		return find(collectionName, null, limit, false,null);
	}

	public static FindIterable<Document> find(String collectionName,
			Map<String, Object> filter, Integer limit, boolean noCursorTimeout,Map<String, Object> projection) {
		BasicDBObject find = null;
		BasicDBObject projectionBson = null;
		if (filter != null) {
			find = new BasicDBObject(filter);
		} else {
			find = new BasicDBObject();
		}
		if(projection!=null){
			projectionBson = new BasicDBObject(projection);
		}else{
			projectionBson = new BasicDBObject();
		}
		FindIterable<Document> findIterable = getCollection(collectionName)
				.find(find).projection(projectionBson);
		if (limit != null) {
			if (limit <= 0) {
				throw new RuntimeException("Find limit must > 0");
			}
			findIterable = findIterable.limit(limit);
		}
		return findIterable.noCursorTimeout(noCursorTimeout);
	}

	public static MongoCursor<Document> iterator(String collectionName,
			Map<String, Object> filter,Integer limit, boolean noCursorTimeout) {
		return find(collectionName, filter,limit, noCursorTimeout,null).iterator();
	}
	/**
	 * 
	 * @param collectionName
	 * @return
	 */
	public static MongoCursor<Document> iterator(String collectionName) {
		return iterator(collectionName, null,null, false);
	}
	/**
	 * 获取集合的cursor
	 * @param collectionName 集合名称
	 * @param limit 限制返回的数量
	 * @return
	 */
	public static MongoCursor<Document> iterator(String collectionName,Integer limit) {
		return iterator(collectionName, null,limit, false);
	}
	/**
	 * 获取集合的cursor
	 * @param collectionName 集合名称
	 * @param noCursorTimeout 游标不过期 true（不过期） false（自动过期）
	 * @return 游标
	 */
	public static MongoCursor<Document> iterator(String collectionName,
			boolean noCursorTimeout) {
		return iterator(collectionName, null,null, noCursorTimeout);
	}
	/**
	 * Find one from collectionName
	 * @param collectionName 集合名
	 * @param queryMap 查询条件
	 * @return
	 */
	public static Document findOne(String collectionName,
			Map<String, Object> queryMap) {
		Document resultDoc = null;
		MongoCursor<Document> findResult = iterator(collectionName,queryMap,1,false);
		boolean hasNext = findResult.hasNext();
		if (hasNext) {
			resultDoc = findResult.next();
		}
		return resultDoc;
	}
	/**
	 * Find one from collectionName
	 * @param collectionName 集合名
	 * @param queryMap 查询条件
	 * @return
	 */
	public static Document findOne(String collectionName,
			String key,String value) {
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put(key, value);
		return findOne(collectionName, queryMap);
	}

	public static MongoCursor<Document> iterator(String collectionName,
			Map<String, Object> projection) {
		return find(collectionName, projection).iterator();
	}

	public static void delete(String collectionName,Map<String, Object> filter) {
		if(filter == null || filter.size() == 0){
			throw new RuntimeException("Filter can't be empty!");
		}
		BasicDBObject find = new BasicDBObject(filter);
		getCollection(collectionName).deleteMany(find);
	}
}
