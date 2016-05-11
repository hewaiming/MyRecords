package com.hewaiming.MyRecords.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.bmob.btp.e.a.in;
import com.hewaiming.MyRecords.R;
import com.hewaiming.MyRecords.adapter.RecordAdapter;
import com.hewaiming.MyRecords.bean.eRecord;
import com.hewaiming.MyRecords.view.xListView;
import com.hewaiming.MyRecords.view.xListView.ILoadListener;
import com.hewaiming.MyRecords.view.xListView.IReflashListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

public class RecordsFragment extends Fragment implements IReflashListener, ILoadListener {

	private xListView mListView;
	private List<eRecord> mLists = null;
	private RecordAdapter mRecordAdapter = null;
	private int SelectedItem = -1;
	private View mView;
	private Button btnFind;
	String updateTime, lastTime;
	SimpleDateFormat format_date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	@Override
	public void onAttach(Activity activity) {
		// Toast.makeText(getContext(), "ATTACH", 0).show();
		Log.i("fragment", "ATTACH");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// setHasOptionsMenu(true);
		Log.i("fragment", "create");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.records_fragment, container, false);
		Log.i("fragment", "create view");
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i("fragment", "activityCreate");
		super.onActivityCreated(savedInstanceState);
		mListView = (xListView) mView.findViewById(R.id.crimeListView);
		init_data();
		init_search();
		mListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				menu.add(0, 0, 0, "删除");
				menu.add(0, 1, 0, "修改");

			}
		});
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				SelectedItem = position;
				return false;
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SelectedItem = position;
				selRecordShow(SelectedItem - 1);
			}
		});

	}

	// 查找内容
	private void init_search() {
		btnFind = (Button) mView.findViewById(R.id.btn_find);
		btnFind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent findIntent = new Intent();
				findIntent.setClass(getContext(), SearchActivity.class);
				startActivity(findIntent);
			}
		});

	}

	public void selRecordShow(int i) {
		// Toast.makeText(getContext(), "click " + i, Toast.LENGTH_LONG).show();
		eRecord selRecord = mLists.get(i);
		Intent showRecordIntent = new Intent();
		showRecordIntent.setClass(getContext(), ShowRecordActivity.class);
		Bundle mBundle = new Bundle();
		mBundle.putSerializable("ShowRecord", selRecord);
		showRecordIntent.putExtras(mBundle);
		startActivity(showRecordIntent);

	}

	@Override
	public void onStart() {
		Log.i("fragment", "Start");
		super.onStart();
	}

	public void init_data() {
		if (mLists == null || mLists.equals(null) || mLists.size() == 0) {
			mLists = new ArrayList<eRecord>();
			BmobQuery<eRecord> query = new BmobQuery<eRecord>();
			query.order("-updatedAt");// 按照时间降序
			query.setLimit(10); // 返回50条数据，如果不加上这条语句，默认返回10条数据
			query.findObjects(getContext(), new FindListener<eRecord>() {
				@Override
				public void onSuccess(List<eRecord> mList) {
					if (mList.size() > 0) {
						updateTime = mList.get(0).getUpdatedAt(); // 记录最前刷新时间
						lastTime = mList.get(mList.size() - 1).getUpdatedAt();// 记录最后刷新时间
						Log.i("updateTime_init", updateTime);
						Log.i("lastTime_init", lastTime);
					} else {
						Toast.makeText(getContext(), "没有数据！", Toast.LENGTH_LONG).show();
					}
					showList(mView, mList);
				}

				@Override
				public void onError(int code, String msg) {
					Log.i("bmob查询数据失败", msg);
					Toast.makeText(getContext(), "查询失败：" + msg, Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	public void showList(View mView, List<eRecord> mList) {
		mLists = mList;
		if (mLists.size() > 0 && !mLists.equals(null)) {
			updateTime = mLists.get(0).getUpdatedAt();
			lastTime = mLists.get(mLists.size() - 1).getUpdatedAt();
			for (eRecord myRecord : mList) {
				try {
					if (format_date.parse(myRecord.getUpdatedAt()).getTime() > format_date.parse(updateTime)
							.getTime()) {
						updateTime = myRecord.getUpdatedAt();// 记录最前刷新时间
					}
					if (format_date.parse(myRecord.getUpdatedAt()).getTime() < format_date.parse(lastTime).getTime()) {
						lastTime = myRecord.getUpdatedAt();// 记录最后刷新时间
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
			Log.i("updateTime_showlist", updateTime);
			Log.i("lastTime_showlist", lastTime);
		}
		if (mRecordAdapter == null) {
			mListView = (xListView) mView.findViewById(R.id.crimeListView);
			mListView.setInterface(this, this);
			mRecordAdapter = new RecordAdapter(this.getContext(), mLists);
			mListView.setAdapter(mRecordAdapter);
		} else {
			mRecordAdapter.onDateChange(mLists);
		}

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.add(0, 0, 0, "增加数据");
		menu.add(0, 1, 0, "取消通知");
		inflater.inflate(R.menu.main, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			if (SelectedItem != -1) {
				delCrime(SelectedItem);// 删除数据
			}
			break;
		case 1:
			if (SelectedItem != -1) {
				updateCrime(SelectedItem);// 修改数据
			}
			break;
		}
		return super.onContextItemSelected(item);
	}

	private void updateCrime(int selectedItem2) {
		String update_id = mLists.get(selectedItem2 - 1).getObjectId();
		String update_name = mLists.get(selectedItem2 - 1).getName();
		eRecord selRecord = mLists.get(selectedItem2 - 1);
		// Toast.makeText(getContext(), "name:"+update_name+" ObjectId:
		// "+update_id, Toast.LENGTH_LONG).show();
		// mList_adapter.remove(selectedItem2- 1);
		// mRecordAdapter.onDateChange(mList_adapter);
		Intent mIntent = new Intent();
		mIntent.setClass(getContext(), UpdateActivity.class);
		Bundle mBundle = new Bundle();
		mBundle.putSerializable("updateRecord", selRecord);
		mIntent.putExtras(mBundle);
		startActivityForResult(mIntent, 1);	

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == 0) {
			addCrime();// 增加
		}
		if (id == 1) {// 取消通知

		}
		if (id == R.id.action_settings) {

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (resultCode == 1) {
				if (data.getStringExtra("updateAction").equals("success")) {
					mLists.remove(SelectedItem - 1);
					mRecordAdapter.onDateChange(mLists);
					onReflash(); // 修改成功后，更新数据
				}
			}
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("fragment", "Resume");
	}

	private boolean addCrime() {
		SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd");
		eRecord mCrime = new eRecord();
		mCrime.setName("bmw");
		mCrime.setAddress("china shanghai");
		mCrime.setContent("34wrefasdfasdfadsf");
		mCrime.setRecordDate(myFmt.format(System.currentTimeMillis()));
		mLists.add(mCrime);
		mRecordAdapter.notifyDataSetChanged();
		Toast.makeText(getContext(), "new crime", Toast.LENGTH_SHORT).show();
		return true;
	}

	private boolean addCrime(eRecord newCrime) {
		mLists.add(newCrime);
		mRecordAdapter.notifyDataSetChanged();
		Toast.makeText(getContext(), "new crime", Toast.LENGTH_SHORT).show();
		return true;
	}

	private void delCrime(final int SelItem) {

		if (SelItem >= 0) {
			eRecord selected_record = mLists.get(SelItem - 1);
			String selected_id = selected_record.getObjectId();

			eRecord mERecord = new eRecord();
			mERecord.setObjectId(selected_id);
			mERecord.delete(getContext(), new DeleteListener() {

				@Override
				public void onSuccess() {
					Toast.makeText(getContext(), "数据删除成功", Toast.LENGTH_LONG).show();
					Log.i("bmob", "删除成功");
					mLists.remove(SelItem - 1);
					showList(mView, mLists);
					Log.i("list size:", mLists.size() + "");
				}

				@Override
				public void onFailure(int code, String msg) {
					Toast.makeText(getContext(), "数据删除失败！" + msg, Toast.LENGTH_LONG).show();
					Log.i("bmob", "删除失败：" + msg);
				}
			});

		}

	}

	// 加载最新数据
	@Override
	public void onReflash() {
		Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// 获取最新数据
				try {
					setReflashData();
					mListView.reflashComplete(); // 通知listview 刷新数据完毕
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 2000);

	}

	private void setReflashData() throws Exception {
		BmobQuery<eRecord> query = new BmobQuery<eRecord>();
		query.order("-updatedAt");// 按照时间降序
		Date mDate = format_date.parse(updateTime);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		calendar.add(Calendar.SECOND, 100); // 时间加一秒
		Date myDate = calendar.getTime();
		Log.i("updateTime+1", format_date.format(myDate));
		query.addWhereGreaterThan("updatedAt", new BmobDate(myDate));
		query.setLimit(10);// 返回50条数据，如果不加上这条语句，默认返回10条数据
		query.findObjects(getContext(), new FindListener<eRecord>() {
			@Override
			public void onSuccess(List<eRecord> mList) {
				if (mList.size() > 0) {
					for (eRecord myRecord : mList) {
						mLists.add(0, myRecord);
					}
//					updateTime = mLists.get(0).getUpdatedAt();
					Log.i("updateTime_reflash", updateTime);
					showList(mView, mLists);
					Toast.makeText(getContext(), "更新 " + mList.size() + "条数据。", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getContext(), "到头啦！", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onError(int code, String msg) {
				Log.i("bmob最新数据更新查询失败", msg);
				Toast.makeText(getContext(), "最新数据更新失败：" + msg, Toast.LENGTH_LONG).show();
			}
		});
	}

	// 加载分页数据
	@Override
	public void onLoad() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					getLoadData();// 取更多数据
					mListView.loadComplete(); // 通知listview加载完毕
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 2000);

	}

	protected void getLoadData() throws Exception {
		BmobQuery<eRecord> query = new BmobQuery<eRecord>();
		query.order("-updatedAt");// 按照时间降序
		Date mDate = format_date.parse(lastTime);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		// calendar.add(Calendar.SECOND, 1); // 时间加一秒
		Date myDate = calendar.getTime();
		Log.i("updateTime+1", format_date.format(myDate));
		query.addWhereLessThan("updatedAt", new BmobDate(myDate));
		query.setLimit(10);// 返回50条数据，如果不加上这条语句，默认返回10条数据
		query.findObjects(getContext(), new FindListener<eRecord>() {
			@Override
			public void onSuccess(List<eRecord> mList) {
				if (mList.size() > 0) {
					for (eRecord myRecord : mList) {
						
						mLists.add(0, myRecord);
					}
					lastTime = mList.get(0).getUpdatedAt();
					Log.i("lastTime_loading", lastTime);
					showList(mView, mLists);
					Toast.makeText(getContext(), "加载 " + mList.size() + "条数据。", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getContext(), "到底啦！", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onError(int code, String msg) {
				Log.i("bmob查询数据失败", msg);
				Toast.makeText(getContext(), "加载更多数据失败：" + msg, Toast.LENGTH_LONG).show();
			}
		});

	}

}
