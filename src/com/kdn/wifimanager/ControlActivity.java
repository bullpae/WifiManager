package com.kdn.wifimanager;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Wi-fi 장치제어 버튼, 토글버튼, 텍스트뷰, 토스트 사용
 */
public class ControlActivity extends Activity {
	
	private Button wifiOnBtn; 
	private Button wifiOffBtn; 
	private ToggleButton wifiTglBtn;
	
	// Wi-Fi 장치제어를 위한 wifiMgr 선언. 장치제어를 위하여 매니페스트 파일에 권한 추가해야함.
	private WifiManager wifiMgr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control_layout);
		
		wifiOnBtn = (Button)findViewById(R.id.wifiOnBtn);
		wifiOffBtn = (Button)findViewById(R.id.wifiOffBtn);
		wifiTglBtn = (ToggleButton)findViewById(R.id.wifiToggleBtn);
		
		wifiMgr = (WifiManager)getSystemService(Context.WIFI_SERVICE);

		wifiOnBtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				wifiOn(); // Wi-Fi 장치 활성화
				
				// 토글버튼이 Off 상태일 경우 On 상태로 변경
				if (!wifiTglBtn.isChecked()) {
					wifiTglBtn.setChecked(true);
				}

				Toast.makeText(ControlActivity.this, "Wi-Fi 장치가 활성화 되었습니다.", Toast.LENGTH_SHORT).show();
			}
		});

		wifiOffBtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				wifiOff(); // Wi-Fi 장치 비활성화

				// 토글버튼이 On 상태일 경우 Off 상태로 변경
				if (wifiTglBtn.isChecked()) {
					wifiTglBtn.setChecked(false);
				}

				Toast.makeText(ControlActivity.this, "Wi-Fi 장치가 비활성화 되었습니다.", Toast.LENGTH_SHORT).show();
			}
		});
		
        wifiTglBtn.setOnClickListener(new ToggleButton.OnClickListener() {
            public void onClick(View v) {
                if(wifiTglBtn.isChecked()) { //토클버튼이 ON인 상태인 경우
                	wifiOn();
                	Toast.makeText(ControlActivity.this, "[토글] Wi-Fi 장치가 활성화 되었습니다.", Toast.LENGTH_SHORT).show();
                }else{ //토글버튼이 OFF인 경우
                	wifiOff();
                	Toast.makeText(ControlActivity.this, "[토글] Wi-Fi 장치가 비활성화 되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
	}
	
	// Wi-Fi 장치 활성화 함수
	void wifiOn() {
		wifiMgr.setWifiEnabled(true);
	}

	// Wi-Fi 장치 비활성화 함수
	void wifiOff() {
		wifiMgr.setWifiEnabled(false);
	}
	
}
