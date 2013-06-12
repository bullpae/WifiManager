package com.kdn.wifimanager;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WiFiScanDataAdapter extends ArrayAdapter<Items> {

	// ���̾ƿ� XML�� �о���̱� ���� ��ü
	private LayoutInflater mInflater;

	public WiFiScanDataAdapter(Context context, ArrayList<Items> object) {

		// ���� Ŭ������ �ʱ�ȭ ����
		// context, 0, �ڷᱸ��
		super(context, 0, object);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	// �������� ��Ÿ���� �ڽ��� ���� xml�� ���̱� ���� ����
	@Override
	public View getView(int position, View v, ViewGroup parent) {
		View view = null;

		// ���� ����Ʈ�� �ϳ��� �׸� ���� ��Ʈ�� ���

		if (v == null) {
			// XML ���̾ƿ��� ���� �о ����Ʈ�信 ����
			view = mInflater.inflate(R.layout.item_layout, null);
		} else {
			view = v;
		}

		// �ڷḦ �޴´�.
		final Items item = this.getItem(position);

		if (item != null) {
			// ȭ�� ���
			TextView tv = (TextView) view.findViewById(R.id.textView1);
			TextView tv2 = (TextView) view.findViewById(R.id.textView2);
			// �ؽ�Ʈ��1�� getLabel()�� ��� �� ù��° �μ���
			tv.setText(item.getSsid());
//			tv.setTextColor(Color.WHITE);
			// �ؽ�Ʈ��2�� getData()�� ��� �� �ι�° �μ���
			tv2.setText(item.getRssi());
//			tv.setTextColor(Color.WHITE);
		}

		return view;
	}

}
