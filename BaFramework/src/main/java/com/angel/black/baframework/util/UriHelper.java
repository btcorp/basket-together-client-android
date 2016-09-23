package com.angel.black.baframework.util;

import android.net.Uri;

import java.io.UnsupportedEncodingException;

public class UriHelper {

	private String mScheme;
	private String mUserInfo;
	private String mHost;
	private int mPort;
	private String mPath;
	private String mQuery;
	private String mFragment;
	public UriHelper(String url) {
		Uri u = Uri.parse(url);
		mScheme = u.getScheme();
		mUserInfo = u.getEncodedUserInfo();
		mHost = u.getHost();
		mPort = u.getPort();
		mPath = u.getEncodedPath();
		mQuery = u.getEncodedQuery();
		mFragment = u.getEncodedFragment();

		if (StringUtil.isEmptyString(mUserInfo))
			mUserInfo = "";
		if (StringUtil.isEmptyString(mQuery))
			mQuery = "";
		if (StringUtil.isEmptyString(mPath))
			mPath = "";
		if (StringUtil.isEmptyString(mFragment))
			mFragment = "";
	}

	public String toAuthority() {
		StringBuilder sb = new StringBuilder();
		sb.append(mScheme).append("://");
		if (!StringUtil.isEmptyString(mUserInfo))
			sb.append(mUserInfo).append('@');
		if (!StringUtil.isEmptyString(mHost))
			sb.append(mHost);
		if (mPort > 0)
			sb.append(':').append(mPort);
		return sb.toString();
	}
	public String toUrl() {
		StringBuilder sb = new StringBuilder(toAuthority());
		if (!StringUtil.isEmptyString(mPath))
			sb.append(mPath);
		if (!StringUtil.isEmptyString(mQuery))
			sb.append('?').append(mQuery);
		if (!StringUtil.isEmptyString(mFragment))
			sb.append('#').append(mFragment);
		return sb.toString();
	}
	public Uri toUri() {
		return Uri.parse(toUrl());
	}
	public void setEncodedPath(String path) {
		mPath = path;
	}
	public void setEncodedQuery(String query) {
		mQuery = query;
	}
	public void setEncodedFragment(String fragment) {
		mFragment = fragment;
	}
/////////////////////////////////////////
	public void addEncodedQuery(String query) {
		mQuery += (query + "&");
	}
	public void addQuery(String key, String value) {
		addEncodedQuery(Uri.encode(key) + "=" + Uri.encode(value));
	}
	public static String urlEncode(String url, String charset) throws UnsupportedEncodingException {
		byte[] bytes = url.getBytes(charset);
		StringBuilder sb = new StringBuilder(bytes.length);
		for (int i = 0; i < bytes.length; ++i) {
			int cp = bytes[i] < 0 ? bytes[i] + 256 : bytes[i];
			if (cp <= 0x20 || cp >= 0x7F || (cp == 0x22 || cp == 0x25 || cp == 0x3C || cp == 0x3E || cp == 0x20 || cp == 0x5B || cp == 0x5C || cp == 0x5D || cp == 0x5E || cp == 0x60 || cp == 0x7b || cp == 0x7c || cp == 0x7d)) {
				sb.append(String.format("%%%02X", cp));
			} else {
				sb.append((char) cp);
			}
		}
		return sb.toString();
	}
}
