package com.hewaiming.MyRecords.ui;

import com.hewaiming.MyRecords.R;
import com.hewaiming.MyRecords.R.id;
import com.hewaiming.MyRecords.R.layout;
import com.hewaiming.MyRecords.R.menu;
import com.hewaiming.MyRecords.bean.eRecord;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.listener.InitListener;

public class ShowRecordActivity extends Activity implements OnClickListener {
	private TextView tvDateTime, tvAddress, tvName, tvContent, tvTitle;
	private ImageView ivPhoto;
	private Button btnBack, btnOK;
	// private String localCameraPath = "";
	// private String imagePath = "";
	// private String init_cameraPath="";//取初始数据库图像地址

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_record);
		init_view();
		init_data();
	}

	private void init_data() {
		eRecord mRecord = (eRecord) getIntent().getSerializableExtra("ShowRecord");// 取数据
		if (mRecord != null) {
			// myID = mRecord.getObjectId();
			tvDateTime.setText(mRecord.getRecordDate() + " " + mRecord.getRecordTime());
			tvName.setText("  源自:"+mRecord.getName());
			tvAddress.setText("地址："+mRecord.getAddress());
			tvContent.setText(mRecord.getContent());
			if (mRecord.getImage() != null) {
				String cameraPath = mRecord.getImage();
				if (cameraPath != "" && cameraPath != null) {
					// showLargePath=cameraPath;
					// init_cameraPath=cameraPath;
					ivPhoto.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(cameraPath, ivPhoto);
				}
			} else {
				ivPhoto.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "无图片显示", Toast.LENGTH_LONG).show();
			}
		}

	}

	private void init_view() {
		btnBack = (Button) findViewById(R.id.btn_back);
		btnOK = (Button) findViewById(R.id.btn_true);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvDateTime = (TextView) findViewById(R.id.tv_datetime);
		tvAddress = (TextView) findViewById(R.id.tv_address);
		tvContent = (TextView) findViewById(R.id.tv_content);
		tvName = (TextView) findViewById(R.id.tv_name);
		ivPhoto = (ImageView) findViewById(R.id.iv_photo);
		btnOK.setVisibility(View.GONE);
		tvTitle.setVisibility(View.GONE);
		btnBack.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		}
	}

}
