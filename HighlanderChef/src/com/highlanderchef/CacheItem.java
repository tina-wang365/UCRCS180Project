package com.highlanderchef;

public class CacheItem {
	long accessTime;
	int numAccess;
	byte[] bytes;

	public CacheItem() {
		accessTime = 0;
		numAccess = 0;
		bytes = new byte[0];
	}
}

