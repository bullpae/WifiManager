package com.kdn.wifimanager;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TabHost;

/**
 * 탭 메뉴 화면 구성
 * @author SG
 *
 */
public class MainActivity extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final TabHost tabHost = getTabHost();

		// 두개의 탭 메뉴로 구
		tabHost.addTab(tabHost.newTabSpec("tag1").setIndicator("WI-FI 장치제어")
				.setContent(new Intent(this, ControlActivity.class)));

		tabHost.addTab(tabHost.newTabSpec("tag2").setIndicator("AP 목록 관리")
				.setContent(new Intent(this, WifiScanActivity.class)));
	}

	// 왼쪽 하단의 옵션 메뉴 구성
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_layout, menu);
		return true;
	}

}
