package com.hewaiming.MyRecords.view;

import com.hewaiming.MyRecords.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

public class FooterListView extends RelativeLayout {
	private Context context;


	public FooterListView(Context context) {
		super(context);
		this.context = context;
		View view = LayoutInflater.from(this.context).inflate(R.layout.footer_stats, null);
		// ���������˳���ܵ�����Ҫ��addView��Ȼ�����ͨ��findViewById�ҵ���TextView
		addView(view);		
	}
}