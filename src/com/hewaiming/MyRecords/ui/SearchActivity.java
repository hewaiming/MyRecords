package com.hewaiming.MyRecords.ui;

import java.util.ArrayList;
import java.util.List;

import com.hewaiming.MyRecords.R;
import com.hewaiming.MyRecords.R.id;
import com.hewaiming.MyRecords.R.layout;
import com.hewaiming.MyRecords.R.menu;
import com.hewaiming.MyRecords.adapter.RecordAdapter;
import com.hewaiming.MyRecords.bean.eRecord;
import com.hewaiming.MyRecords.view.xListView;
import com.hewaiming.MyRecords.view.xListView.ILoadListener;
import com.hewaiming.MyRecords.view.xListView.IReflashListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class SearchActivity extends Activity implements IReflashListener, ILoadListener, OnClickListener {
	private Button btn_search, btn_back;
	private xListView findListView;
	private EditText etInput;
	private RecordAdapter findAdapter = null;
	private List<eRecord> findList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		init_view();
	}

	private void init_view() {
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_search = (Button) findViewById(R.id.btn_search);
		etInput = (EditText) findViewById(R.id.et_find);
		btn_back.setOnClickListener(this);
		btn_search.setOnClickListener(this);
		findList = new ArrayList<eRecord>();
		findListView = (xListView) findViewById(R.id.lv_search);
		findListView.setInterface(this, this);
		findListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selRecordShow(position - 1); // 点击显示详情
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;

		case R.id.btn_search:
			findItems();
			break;
		}

	}

	private void findItems() {

		String findvalue = etInput.getText().toString();
		if (findvalue.equals("") || findvalue == null) {
			Toast.makeText(getApplicationContext(), "模糊查询内容不能为空，请输入需查询内容！", Toast.LENGTH_LONG).show();
			return;
		}
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("正在模糊查询数据...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		BmobQuery<eRecord> eq1 = new BmobQuery<eRecord>();
		eq1.addWhereContains("content", findvalue); // 内容查询
		BmobQuery<eRecord> eq2 = new BmobQuery<eRecord>();
		eq2.addWhereContains("address", findvalue);// 地址查询
		BmobQuery<eRecord> eq3 = new BmobQuery<eRecord>();
		eq3.addWhereContains("name", findvalue); // 名字查询
		BmobQuery<eRecord> eq4 = new BmobQuery<eRecord>();
		eq4.addWhereContains("recordDate", findvalue);// 日期查询

		List<BmobQuery<eRecord>> queries = new ArrayList<BmobQuery<eRecord>>();
		queries.add(eq1);
		queries.add(eq2);
		queries.add(eq3);
		queries.add(eq4);// 组合查询
		BmobQuery<eRecord> mainQuery = new BmobQuery<eRecord>();
		mainQuery.or(queries);
		mainQuery.findObjects(this, new FindListener<eRecord>() {

			@Override
			public void onSuccess(List<eRecord> mList) {
				progress.dismiss();
				if (mList.size() > 0) {
					findList = mList;
					if (findAdapter == null) {
						findAdapter = new RecordAdapter(getApplicationContext(), mList);
					} else {
						findAdapter.onDateChange(mList);
					}
					findListView.setAdapter(findAdapter);
				} else {
					Toast.makeText(getApplicationContext(), "模糊查询无数据！", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onError(int code, String msg) {
				Log.i("bmob模糊组合查询数据失败", msg);
				progress.dismiss();
				Toast.makeText(getApplicationContext(), "模糊查询失败：" + msg, Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onLoad() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					// getLoadData();// 取更多数据
					Toast.makeText(getApplicationContext(), "到底啦", 0).show();
					findListView.loadComplete(); // 通知listview加载完毕
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 2000);
	}

	@Override
	public void onReflash() {
		Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// 获取最新数据
				try {
					// setReflashData();
					Toast.makeText(getApplicationContext(), "到头啦", 0).show();
					findListView.reflashComplete(); // 通知listview 刷新数据完毕
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 2000);
	}

	public void selRecordShow(int i) {
		// Toast.makeText(getContext(), "click " + i, Toast.LENGTH_LONG).show();
		if (i >= 0) {
			eRecord selRecord = findList.get(i);
			Intent showRecordIntent = new Intent();
			showRecordIntent.setClass(getApplicationContext(), ShowRecordActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putSerializable("ShowRecord", selRecord);
			showRecordIntent.putExtras(mBundle);
			startActivity(showRecordIntent);
		}

	}
}
