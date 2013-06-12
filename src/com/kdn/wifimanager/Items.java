package com.kdn.wifimanager;

import android.content.Context;

public class Items {

	// 리스트뷰의 각 항목에 표시할 데이터 정의

	private Context mContext;
	private String mSsid;
	private String mRssi;

	public Items(Context context, String ssid, String rssi) {
		mContext = context;
		mSsid = ssid;
		mRssi = rssi;
	}

	public String getSsid() {
		return mSsid;
	}

	public void setSsid(String ssid) {
		this.mSsid = ssid;
	}

	public String getRssi() {
		return mRssi;
	}

	public void setRssi(String rssi) {
		this.mRssi = rssi;
	}
}
