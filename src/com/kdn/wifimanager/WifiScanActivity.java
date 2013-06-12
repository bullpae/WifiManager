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
 * 스캔된 와이파이 정보를 리스트 뷰를 이용하여 표시하는 액티비티 
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

	// 리스트뷰 선언
	private ListView mListview;

	// 데이터를 연결할 Adapter
	WiFiScanDataAdapter mWifiScanDataAdapter;

	// 데이터를 담을 자료구조
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
		
		// 현재 접속되어 있는 AP 정보 추출
		WifiInfo wifiInfo = wifimanager.getConnectionInfo();
		apInfoTxt.setText("접속중인 AP : " + wifiInfo.getSSID());

		// 선언한 리스트뷰에 사용할 리스뷰연결
		mListview = (ListView) findViewById(R.id.listView);

		// 스캔된 와이파이 목록을 담을 리스트 객체를 생성합니다
		mItemList = new ArrayList<Items>();

		// 데이터를 받기위해 데이터 어댑터 객체 선언
		mWifiScanDataAdapter = new WiFiScanDataAdapter(this, mItemList);

		// 리스트뷰에 어댑터 연결
		mListview.setAdapter(mWifiScanDataAdapter);
		// 리스트 뷰 항목 선택 시 동작처리
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
		
		// Scan count 결과 출력 표시 
//		textStatus.setText("Scan is counting\t" + ++scanCount + " times \n");

		// 와이파이 스캔결과를 화면에 표시
//		textStatus.append("=======================================\n");
//		for (int i = 0; i < mScanResult.size(); i++) {
//			ScanResult result = mScanResult.get(i);
//			textStatus.append((i + 1) + ". SSID : " + result.SSID.toString()
//					+ "\t\t RSSI : " + result.level + " dBm\n");
//		}
//		textStatus.append("=======================================\n");
		
		// 와이파이 스캔결과를 리스트뷰에 표시
		mWifiScanDataAdapter.clear();
		for (int i = 0; i < mScanResult.size(); i++) {
			ScanResult result = mScanResult.get(i);
			mWifiScanDataAdapter.add(new Items(getApplicationContext(), "SSID : " + result.SSID.toString(), "RSSI : " + result.level + " dBm\n"));
		}
		
		/*
		 * SSID : Service Set IDentifier RSSI : Received Signal Strength
		 * Indication RSSI는 AP의 수신 신호 강도, 0에 가까울 수록 신호가 좋고 -100에 가까울 수록 안좋다.
		 */
		
		// 현재 접속되어 있는 AP 정보 추출
		WifiInfo wifiInfo = wifimanager.getConnectionInfo();
		apInfoTxt.setText("접속중인 AP : " + wifiInfo.getSSID());
	}

	public void initWIFIScan() {
		// init WIFISCAN
		scanCount = 0;
		text = "";
		final IntentFilter filter = new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		/*
		 * BR은 보통 메니페스트에 등록해 두며 이 경우 BR은 설치 직후부터 항상 방송 수신을 대기한다. 액티비티가 실행 중인 동안만
		 * 방송을 수신하려면 코드에서 BR을 일시적으로 등록할 수 있다. 필요할 때만 방송을 수신하려면 매니페스트 등록 없이 다음
		 * 메소드로 등록 및 해제한다. registerReceiver, unregisterReceiver
		 * 
		 * BR을 등록하는 최적의 시점은 onResume이며 등록을 해제할 최적의 시점은 onPause이다. 액티비티는 운영체제에 의해
		 * 종료 및 재실행될 수 있으므로 실행 직후부터 대기하고 싶다고해서 onCreate에서 등록하는 것이 아님을 주의하자.
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

	// 리스트 항목 선택시 발생하는 이벤트 작성
	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> p, View view, int position, long id) {
			// 선택된 항목(와이파이)의 세부정보를 Alert 다이얼로그를 이용하여 보여준다.
			ScanResult result = mScanResult.get(position);
			String msg = "SSID(AP) : " + result.SSID.toString() + "\nRSSI : " + result.level + " dBm\n" +
			             "BSSID(mac) : " + result.BSSID + "\nCapabilities(인증) : \n" + result.capabilities + "\nFrequency : " + result.frequency + " MHz\n";
			
			// 접속하고자하는 SSID 획득
			final String ssid = result.SSID.toString();
			
			new AlertDialog.Builder(WifiScanActivity.this)
			.setTitle(result.SSID + " 세부정보")
			.setMessage(msg)
			.setPositiveButton("연결", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try{
						// 선택된 AP 접속
						WifiConfiguration wfc = new WifiConfiguration();

						// TODO capabilities 가 Open 상태가 아닐경우 비밀번호 입력창을 생성하여 입력받을 수 있어야한다.
						String password = "BeMyGyum";
						
						wfc.SSID = "\"".concat(ssid).concat("\"");
						wfc.status = WifiConfiguration.Status.DISABLED;
						wfc.priority = 40;
	
						// capabilities 가 Open 일때 설정 (키값 없이 바로 접속)
						wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
						wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
						wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
						wfc.allowedAuthAlgorithms.clear();
						wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
	
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
	
						// WEP 방식 설정
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
	
						// WPA, WPA2 방식 설정
					    wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
						wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
						wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
						wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
						wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
						wfc.preSharedKey = "\"".concat(password).concat("\"");
						
						// 위의 방식으로 WifiConfiguration 을 설정하고, 아래의 방법으로 접속 시도
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
			.setNegativeButton("확인", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//finish();
				}
			})
			.show();
			
		}
	};
}

	      