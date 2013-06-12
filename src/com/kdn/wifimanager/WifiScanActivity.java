package com.kdn.wifimanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * ��ĵ�� �������� ������ ����Ʈ �並 �̿��Ͽ� ǥ���ϴ� ��Ƽ��Ƽ 
 */
public class WifiScanActivity extends Activity implements OnClickListener {

	private static final String TAG = "WIFIScanner";

	// WifiManager variable
	WifiManager wifimanager;

	// UI variable
	//TextView textStatus;
	Button btnScanStart;
	Button btnScanStop;
	TextView apInfoTxt;

	private int scanCount = 0;
	String text = "";
	String result = "";

	private List<ScanResult> mScanResult; // ScanResult List

	// ����Ʈ�� ����
	private ListView mListview;

	// �����͸� ������ Adapter
	WiFiScanDataAdapter mWifiScanDataAdapter;

	// �����͸� ���� �ڷᱸ��
	ArrayList<Items> mItemList;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifiscan_layout);

		// Setup UI
		//textStatus = (TextView) findViewById(R.id.textStatus);
		btnScanStart = (Button) findViewById(R.id.btnScanStart);
		btnScanStop = (Button) findViewById(R.id.btnScanStop);
		apInfoTxt = (TextView)findViewById(R.id.apInfoTxtView);
		
		// Setup OnClickListener
		btnScanStart.setOnClickListener(this);
		btnScanStop.setOnClickListener(this);

		// Setup WIFI Manager
		wifimanager = (WifiManager) getSystemService(WIFI_SERVICE);
		Log.d(TAG, "Setup WIfiManager getSystemService");

		// if WIFIEnabled
		if (wifimanager.isWifiEnabled() == false)
			wifimanager.setWifiEnabled(true);
		
		// ���� ���ӵǾ� �ִ� AP ���� ����
		WifiInfo wifiInfo = wifimanager.getConnectionInfo();
		apInfoTxt.setText("�������� AP : " + wifiInfo.getSSID());

		// ������ ����Ʈ�信 ����� �����俬��
		mListview = (ListView) findViewById(R.id.listView);

		// ��ĵ�� �������� ����� ���� ����Ʈ ��ü�� �����մϴ�
		mItemList = new ArrayList<Items>();

		// �����͸� �ޱ����� ������ ����� ��ü ����
		mWifiScanDataAdapter = new WiFiScanDataAdapter(this, mItemList);

		// ����Ʈ�信 ����� ����
		mListview.setAdapter(mWifiScanDataAdapter);
		// ����Ʈ �� �׸� ���� �� ����ó��
		mListview.setOnItemClickListener(mItemClickListener); 
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				Log.d(TAG,
						"onReceive - WifiManager.SCAN_RESULTS_AVAILABLE_ACTION");
				getWIFIScanResult(); // get WIFISCanResult
				wifimanager.startScan(); // for refresh
			} else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				Log.d(TAG,
						"onReceive - WifiManager.NETWORK_STATE_CHANGED_ACTION");
				sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
			}
		}
	};

	public void getWIFIScanResult() {
		mScanResult = wifimanager.getScanResults(); // ScanResult
		
		// Scan count ��� ��� ǥ�� 
//		textStatus.setText("Scan is counting\t" + ++scanCount + " times \n");

		// �������� ��ĵ����� ȭ�鿡 ǥ��
//		textStatus.append("=======================================\n");
//		for (int i = 0; i < mScanResult.size(); i++) {
//			ScanResult result = mScanResult.get(i);
//			textStatus.append((i + 1) + ". SSID : " + result.SSID.toString()
//					+ "\t\t RSSI : " + result.level + " dBm\n");
//		}
//		textStatus.append("=======================================\n");
		
		// �������� ��ĵ����� ����Ʈ�信 ǥ��
		mWifiScanDataAdapter.clear();
		for (int i = 0; i < mScanResult.size(); i++) {
			ScanResult result = mScanResult.get(i);
			mWifiScanDataAdapter.add(new Items(getApplicationContext(), "SSID : " + result.SSID.toString(), "RSSI : " + result.level + " dBm\n"));
		}
		
		/*
		 * SSID : Service Set IDentifier RSSI : Received Signal Strength
		 * Indication RSSI�� AP�� ���� ��ȣ ����, 0�� ����� ���� ��ȣ�� ���� -100�� ����� ���� ������.
		 */
		
		// ���� ���ӵǾ� �ִ� AP ���� ����
		WifiInfo wifiInfo = wifimanager.getConnectionInfo();
		apInfoTxt.setText("�������� AP : " + wifiInfo.getSSID());
	}

	public void initWIFIScan() {
		// init WIFISCAN
		scanCount = 0;
		text = "";
		final IntentFilter filter = new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		/*
		 * BR�� ���� �޴��佺Ʈ�� ����� �θ� �� ��� BR�� ��ġ ���ĺ��� �׻� ��� ������ ����Ѵ�. ��Ƽ��Ƽ�� ���� ���� ���ȸ�
		 * ����� �����Ϸ��� �ڵ忡�� BR�� �Ͻ������� ����� �� �ִ�. �ʿ��� ���� ����� �����Ϸ��� �Ŵ��佺Ʈ ��� ���� ����
		 * �޼ҵ�� ��� �� �����Ѵ�. registerReceiver, unregisterReceiver
		 * 
		 * BR�� ����ϴ� ������ ������ onResume�̸� ����� ������ ������ ������ onPause�̴�. ��Ƽ��Ƽ�� �ü���� ����
		 * ���� �� ������ �� �����Ƿ� ���� ���ĺ��� ����ϰ� �ʹٰ��ؼ� onCreate���� ����ϴ� ���� �ƴ��� ��������.
		 */
		registerReceiver(mReceiver, filter);
		wifimanager.startScan();
		Log.d(TAG, "initWIFIScan()");
	}

	public void printToast(String messageToast) {
		Toast.makeText(this, messageToast, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.btnScanStart) {
			Log.d(TAG, "OnClick() btnScanStart()");
			printToast("WIFI SCAN !!!");
			initWIFIScan(); // start WIFIScan
		}
		if (v.getId() == R.id.btnScanStop) {
			Log.d(TAG, "OnClick() btnScanStop()");
			printToast("WIFI STOP !!!");
			unregisterReceiver(mReceiver); // stop WIFISCan
		}
	}

	// ����Ʈ �׸� ���ý� �߻��ϴ� �̺�Ʈ �ۼ�
	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> p, View view, int position, long id) {
			// ���õ� �׸�(��������)�� ���������� Alert ���̾�α׸� �̿��Ͽ� �����ش�.
			ScanResult result = mScanResult.get(position);
			String msg = "SSID(AP) : " + result.SSID.toString() + "\nRSSI : " + result.level + " dBm\n" +
			             "BSSID(mac) : " + result.BSSID + "\nCapabilities(����) : \n" + result.capabilities + "\nFrequency : " + result.frequency + " MHz\n";
			
			// �����ϰ����ϴ� SSID ȹ��
			final String ssid = result.SSID.toString();
			
			new AlertDialog.Builder(WifiScanActivity.this)
			.setTitle(result.SSID + " ��������")
			.setMessage(msg)
			.setPositiveButton("����", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try{
						// ���õ� AP ����
						WifiConfiguration wfc = new WifiConfiguration();

						// TODO capabilities �� Open ���°� �ƴҰ�� ��й�ȣ �Է�â�� �����Ͽ� �Է¹��� �� �־���Ѵ�.
						String password = "BeMyGyum";
						
						wfc.SSID = "\"".concat(ssid).concat("\"");
						wfc.status = WifiConfiguration.Status.DISABLED;
						wfc.priority = 40;
	
						// capabilities �� Open �϶� ���� (Ű�� ���� �ٷ� ����)
						wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
						wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
						wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
						wfc.allowedAuthAlgorithms.clear();
						wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
	
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
	
						// WEP ��� ����
						wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
						wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
						wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
						wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
						wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
						wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
	
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
						wfc.wepKeys[0] = "\"".concat(password).concat("\"");
						wfc.wepTxKeyIndex = 0;
	
						// WPA, WPA2 ��� ����
					    wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
						wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
						wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
						wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
						wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
						wfc.preSharedKey = "\"".concat(password).concat("\"");
						
						// ���� ������� WifiConfiguration �� �����ϰ�, �Ʒ��� ������� ���� �õ�
						int networkId = wifimanager.addNetwork(wfc);
						if (networkId != -1) {
							// success, can call
							wifimanager.enableNetwork(networkId, true);
							// to connect
						}
					}catch(Exception e){
						Log.e(TAG, e.getMessage(), e);
					}
				}
			})
			.setNegativeButton("Ȯ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//finish();
				}
			})
			.show();
			
		}
	};
}

	      