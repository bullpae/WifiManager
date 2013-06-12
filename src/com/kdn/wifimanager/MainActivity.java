package com.kdn.wifimanager;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TabHost;

/**
 * �� �޴� ȭ�� ����
 * @author SG
 *
 */
public class MainActivity extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final TabHost tabHost = getTabHost();

		// �ΰ��� �� �޴��� ��
		tabHost.addTab(tabHost.newTabSpec("tag1").setIndicator("WI-FI ��ġ����")
				.setContent(new Intent(this, ControlActivity.class)));

		tabHost.addTab(tabHost.newTabSpec("tag2").setIndicator("AP ��� ����")
				.setContent(new Intent(this, WifiScanActivity.class)));
	}

	// ���� �ϴ��� �ɼ� �޴� ����
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_layout, menu);
		return true;
	}

}
