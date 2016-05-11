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
				menu.add(0, 0, 0, "ɾ��");
				menu.add(0, 1, 0, "�޸�");

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

	// ��������
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
			query.order("-updatedAt");// ����ʱ�併��
			query.setLimit(10); // ����50�����ݣ����������������䣬Ĭ�Ϸ���10������
			query.findObjects(getContext(), new FindListener<eRecord>() {
				@Override
				public void onSuccess(List<eRecord> mList) {
					if (mList.size() > 0) {
						updateTime = mList.get(0).getUpdatedAt(); // ��¼��ǰˢ��ʱ��
						lastTime = mList.get(mList.size() - 1).getUpdatedAt();// ��¼���ˢ��ʱ��
						Log.i("updateTime_init", updateTime);
						Log.i("lastTime_init", lastTime);
					} else {
						Toast.makeText(getContext(), "û�����ݣ�", Toast.LENGTH_LONG).show();
					}
					showList(mView, mList);
				}

				@Override
				public void onError(int code, String msg) {
					Log.i("bmob��ѯ����ʧ��", msg);
					Toast.makeText(getContext(), "��ѯʧ�ܣ�" + msg, Toast.LENGTH_LONG).show();
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
						updateTime = myRecord.getUpdatedAt();// ��¼��ǰˢ��ʱ��
					}
					if (format_date.parse(myRecord.getUpdatedAt()).getTime() < format_date.parse(lastTime).getTime()) {
						lastTime = myRecord.getUpdatedAt();// ��¼���ˢ��ʱ��
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
		menu.add(0, 0, 0, "��������");
		menu.add(0, 1, 0, "ȡ��֪ͨ");
		inflater.inflate(R.menu.main, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			if (SelectedItem != -1) {
				delCrime(SelectedItem);// ɾ������
			}
			break;
		case 1:
			if (SelectedItem != -1) {
				updateCrime(SelectedItem);// �޸�����
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
			addCrime();// ����
		}
		if (id == 1) {// ȡ��֪ͨ

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
					onReflash(); // �޸ĳɹ��󣬸�������
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
					Toast.makeText(getContext(), "����ɾ���ɹ�", Toast.LENGTH_LONG).show();
					Log.i("bmob", "ɾ���ɹ�");
					mLists.remove(SelItem - 1);
					showList(mView, mLists);
					Log.i("list size:", mLists.size() + "");
				}

				@Override
				public void onFailure(int code, String msg) {
					Toast.makeText(getContext(), "����ɾ��ʧ�ܣ�" + msg, Toast.LENGTH_LONG).show();
					Log.i("bmob", "ɾ��ʧ�ܣ�" + msg);
				}
			});

		}

	}

	// ������������
	@Override
	public void onReflash() {
		Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// ��ȡ��������
				try {
					setReflashData();
					mListView.reflashComplete(); // ֪ͨlistview ˢ���������
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 2000);

	}

	private void setReflashData() throws Exception {
		BmobQuery<eRecord> query = new BmobQuery<eRecord>();
		query.order("-updatedAt");// ����ʱ�併��
		Date mDate = format_date.parse(updateTime);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		calendar.add(Calendar.SECOND, 100); // ʱ���һ��
		Date myDate = calendar.getTime();
		Log.i("updateTime+1", format_date.format(myDate));
		query.addWhereGreaterThan("updatedAt", new BmobDate(myDate));
		query.setLimit(10);// ����50�����ݣ����������������䣬Ĭ�Ϸ���10������
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
					Toast.makeText(getContext(), "���� " + mList.size() + "�����ݡ�", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getContext(), "��ͷ����", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onError(int code, String msg) {
				Log.i("bmob�������ݸ��²�ѯʧ��", msg);
				Toast.makeText(getContext(), "�������ݸ���ʧ�ܣ�" + msg, Toast.LENGTH_LONG).show();
			}
		});
	}

	// ���ط�ҳ����
	@Override
	public void onLoad() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					getLoadData();// ȡ��������
					mListView.loadComplete(); // ֪ͨlistview�������
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 2000);

	}

	protected void getLoadData() throws Exception {
		BmobQuery<eRecord> query = new BmobQuery<eRecord>();
		query.order("-updatedAt");// ����ʱ�併��
		Date mDate = format_date.parse(lastTime);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		// calendar.add(Calendar.SECOND, 1); // ʱ���һ��
		Date myDate = calendar.getTime();
		Log.i("updateTime+1", format_date.format(myDate));
		query.addWhereLessThan("updatedAt", new BmobDate(myDate));
		query.setLimit(10);// ����50�����ݣ����������������䣬Ĭ�Ϸ���10������
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
					Toast.makeText(getContext(), "���� " + mList.size() + "�����ݡ�", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getContext(), "��������", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onError(int code, String msg) {
				Log.i("bmob��ѯ����ʧ��", msg);
				Toast.makeText(getContext(), "���ظ�������ʧ�ܣ�" + msg, Toast.LENGTH_LONG).show();
			}
		});

	}

}
