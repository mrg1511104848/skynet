package org.skynet.frame.persistent;

public class MongoIndex {
	private String indexName;
	private boolean unique;
	
	public MongoIndex() {
		super();
	}
	public MongoIndex(String indexName, boolean unique) {
		super();
		this.indexName = indexName;
		this.unique = unique;
	}
	public MongoIndex(String indexName) {
		super();
		this.indexName = indexName;
	}
	public String getIndexName() {
		return indexName;
	}
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	public boolean getUnique() {
		return unique;
	}
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	
}
