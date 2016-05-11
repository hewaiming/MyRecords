package com.hewaiming.MyRecords.view;

import com.hewaiming.MyRecords.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HeaderListView extends RelativeLayout {
	private Context context;
	private TextView tvHeadId, tvHeadTotal;

	public void setTvHeadId(String mId) {
		this.tvHeadId.setText(mId);
	}

	public void setTvHeadTotal(String mTotal) {
		this.tvHeadTotal.setText(mTotal);
	}

	public HeaderListView(Context context) {
		super(context);
		this.context = context;
		View view = LayoutInflater.from(this.context).inflate(R.layout.header_stats, null);
		// 以下两句的顺序不能调换，要先addView，然后才能通过findViewById找到该TextView
		addView(view);
		tvHeadId = (TextView) view.findViewById(R.id.tv_header_id);
		tvHeadTotal = (TextView) view.findViewById(R.id.tv_header_total);
	}
}
