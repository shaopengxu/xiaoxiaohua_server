package com.u.bops.biz.vo;

import org.apache.commons.lang.StringUtils;

public class Channel {

	/*
	 * android-old android-default ios-iphone-default ios-ipad-default
	 * ios-iphone-hd ios-ipad-hd ios-iphone-default-version
	 */
	private int channelId;
	private String channelName;
	private String iconUrl;
	private String androidTags = "[]";
	private String iosTags = "[]";
	private double androidRating;
	private double iosRating;
	private String androidStreamUrl;
	private String iosStreamUrl;
	private String path;
	private String android;
	private String ios;
	private String extraConfig1Key;
	private String extraConfig1Value;
	private String extraConfig2Key;
	private String extraConfig2Value;
	private String extraConfig3Key;
	private String extraConfig3Value;
	private String minVersionAndroid;
	private String minVersionIphone;
	private String minVersionIpad;
	private String androidBackUrl;
	private String iosBackUrl;
	private String title;

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getAndroidTags() {
		return androidTags;
	}

	public void setAndroidTags(String androidTags) {
		if (!StringUtils.isBlank(androidTags)) {
			this.androidTags = androidTags;
		}
	}

	public String getIosTags() {
		return iosTags;
	}

	public void setIosTags(String iosTags) {
		if (!StringUtils.isBlank(iosTags)) {
			this.iosTags = iosTags;
		}
	}

	public double getAndroidRating() {
		return androidRating;
	}

	public void setAndroidRating(double androidRating) {
		this.androidRating = androidRating;
	}

	public double getIosRating() {
		return iosRating;
	}

	public void setIosRating(double iosRating) {
		this.iosRating = iosRating;
	}

	public String getAndroidStreamUrl() {
		return androidStreamUrl;
	}

	public void setAndroidStreamUrl(String androidStreamUrl) {
		this.androidStreamUrl = androidStreamUrl;
	}

	public String getIosStreamUrl() {
		return iosStreamUrl;
	}

	public void setIosStreamUrl(String iosStreamUrl) {
		this.iosStreamUrl = iosStreamUrl;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getAndroid() {
		return android;
	}

	public void setAndroid(String android) {
		this.android = android;
	}

	public String getIos() {
		return ios;
	}

	public void setIos(String ios) {
		this.ios = ios;
	}

	public String getExtraConfig1Key() {
		return extraConfig1Key;
	}

	public void setExtraConfig1Key(String extraConfig1Key) {
		this.extraConfig1Key = extraConfig1Key;
	}

	public String getExtraConfig1Value() {
		return extraConfig1Value;
	}

	public void setExtraConfig1Value(String extraConfig1Value) {
		this.extraConfig1Value = extraConfig1Value;
	}

	public String getExtraConfig2Key() {
		return extraConfig2Key;
	}

	public void setExtraConfig2Key(String extraConfig2Key) {
		this.extraConfig2Key = extraConfig2Key;
	}

	public String getExtraConfig2Value() {
		return extraConfig2Value;
	}

	public void setExtraConfig2Value(String extraConfig2Value) {
		this.extraConfig2Value = extraConfig2Value;
	}

	public String getExtraConfig3Key() {
		return extraConfig3Key;
	}

	public void setExtraConfig3Key(String extraConfig3Key) {
		this.extraConfig3Key = extraConfig3Key;
	}

	public String getExtraConfig3Value() {
		return extraConfig3Value;
	}

	public void setExtraConfig3Value(String extraConfig3Value) {
		this.extraConfig3Value = extraConfig3Value;
	}

	public String getAndroidBackUrl() {
		return androidBackUrl;
	}

	public void setAndroidBackUrl(String androidBackUrl) {
		this.androidBackUrl = androidBackUrl;
	}

	public String getIosBackUrl() {
		return iosBackUrl;
	}

	public void setIosBackUrl(String iosBackUrl) {
		this.iosBackUrl = iosBackUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMinVersionAndroid() {
		return minVersionAndroid;
	}

	public void setMinVersionAndroid(String minVersionAndroid) {
		this.minVersionAndroid = minVersionAndroid;
	}

	public String getMinVersionIpad() {
		return minVersionIpad;
	}

	public void setMinVersionIpad(String minVersionIpad) {
		this.minVersionIpad = minVersionIpad;
	}

	public String getMinVersionIphone() {
		return minVersionIphone;
	}

	public void setMinVersionIphone(String minVersionIphone) {
		this.minVersionIphone = minVersionIphone;
	}
}
