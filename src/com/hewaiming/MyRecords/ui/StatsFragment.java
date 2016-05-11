package com.hewaiming.MyRecords.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hewaiming.MyRecords.R;
import com.hewaiming.MyRecords.bean.eRecord;
import com.hewaiming.MyRecords.charts.BarChartActivity;
import com.hewaiming.MyRecords.view.FooterListView;
import com.hewaiming.MyRecords.view.HeaderListView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.camera2.CameraCaptureSession;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindStatisticsListener;

public class StatsFragment extends Fragment implements OnItemClickListener {
	private GridView gridView;
	private View statsView;
	private SimpleAdapter adapter;
	private ListView mListView;
	private List<Map<String, Object>> dataList_grid;
	List<Map<String, Object>> data;
	private HeaderListView headerView;
	private FooterListView footView;
	private String[] iconName = { "������ͳ��", "���ص�ͳ��", "��������ͳ��" };
	private int tab; // �����ַ�ʽͳ��
	private Button btn_show_charts;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		statsView = inflater.inflate(R.layout.stats_fragment, container, false);
		return statsView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
		insert_foot();
		btn_show_charts=(Button) statsView.findViewById(R.id.btn_stats_footer);
		btn_show_charts.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent barIntent = new Intent();
				barIntent.setClass(getActivity(), BarChartActivity.class);
				Bundle mBundle = new Bundle();
				ArrayList bundlelist = new ArrayList();
				bundlelist.add(data);
				mBundle.putParcelableArrayList("list", bundlelist);
				mBundle.putInt("statsWhich", tab);					
				barIntent.putExtras(mBundle);
				startActivity(barIntent);
				
			}
		});
	}

	private void insert_foot() {
		footView = new FooterListView(getContext());// ��ӱ�end			
		mListView.addFooterView(footView);		
	}

	private void init() {
		gridView = (GridView) statsView.findViewById(R.id.gridView);
		dataList_grid = new ArrayList<Map<String, Object>>();
		adapter = new SimpleAdapter(this.getContext(), getData(), R.layout.stats_item, new String[] { "pic", "name" },
				new int[] { R.id.pic, R.id.name });
		gridView.setAdapter(adapter);
		mListView = (ListView) statsView.findViewById(R.id.stats_lv);
		gridView.setOnItemClickListener(this);

	}

	// gridview ����
	private List<Map<String, Object>> getData() {

		int[] drawable = { R.drawable.stats_date, R.drawable.stats_address, R.drawable.stats_people };
		for (int i = 0; i < drawable.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pic", drawable[i]);
			map.put("name", iconName[i]);
			dataList_grid.add(map);
		}
		Log.i("Main", "size=" + dataList_grid.size());
		return dataList_grid;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
		case 0:
			statsByDate();
			break;
		case 1:
			statsByAddress();
			break;
		case 2:
			statsByPeople();
			break;

		}

	}

	private void statsByPeople() {
		BmobQuery<eRecord> query = new BmobQuery<eRecord>();
		query.sum(new String[] { "name" }); // ͳ���ܵ÷�
		query.groupby(new String[] { "name" });// ������������
		query.order("-recordDate"); // ��������
		query.setHasGroupCount(true); // ͳ��ÿһ���ж��ٸ���ҵĵ÷ּ�¼��Ĭ�ϲ����ط������
		final ProgressDialog progress = new ProgressDialog(this.getContext());
		progress.setMessage("����ͳ�����ݣ��������ˣ�...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		query.findStatistics(this.getActivity(), eRecord.class, new FindStatisticsListener() {

			@Override
			public void onSuccess(Object object) {
				progress.dismiss();
				data = new ArrayList<Map<String, Object>>();
				data.clear();
				JSONArray ary = (JSONArray) object;
				if (ary != null) {
					int length = ary.length();
					try {
						for (int i = 0; i < length; i++) { // �����ʾ����
							JSONObject obj = ary.getJSONObject(i);
							// int mTotal = obj.getInt("_sumrecordDate");
							String mName = obj.getString("name");
							int count = obj.getInt("_count");// setHasGroupCount����Ϊtrueʱ�����صĽ���к���"_count"�ֶ�
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("name", mName);
							map.put("total", count);
							data.add(map);
						}
						tab = 3;
						// ���ݰ���ʱ������
						Collections.sort(data, new Comparator<Object>() {
							@Override
							public int compare(Object lhs, Object rhs) {
								Map<String, Object> obj1 = (Map<String, Object>) lhs;
								Map<String, Object> obj2 = (Map<String, Object>) rhs;
								String str1 = obj1.get("name").toString();
								String str2 = obj2.get("name").toString();
								return str1.compareTo(str2);

							}

						});
						if (mListView.getHeaderViewsCount() > 0) {
							mListView.removeHeaderView(headerView);
						}
						headerView = new HeaderListView(getContext());// ��ӱ�ͷ
						headerView.setTvHeadId("������");
						headerView.setTvHeadTotal("��  ��");
						mListView.addHeaderView(headerView);
						
						mListView.setAdapter(new SimpleAdapter(getContext(), data, R.layout.stats_date_item,
								new String[] { "name", "total" },
								new int[] { R.id.tv_stats_date, R.id.tv_date_total }));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(getContext(), "��������ͳ�Ƴɹ���������", 1).show();
				}
			}

			@Override
			public void onFailure(int code, String msg) {
				progress.dismiss();
				Toast.makeText(getContext(), "��������ͳ�Ƴɹ�����code =" + ",msg = " + msg, 1).show();
			}
		});

	}

	private void statsByAddress() {
		BmobQuery<eRecord> query = new BmobQuery<eRecord>();
		query.sum(new String[] { "address" }); // ͳ���ܵ÷�
		query.groupby(new String[] { "address" });// ����address����
		query.order("-recordDate"); // ��������
		query.setHasGroupCount(true); // ͳ�Ƽ�¼��Ĭ�ϲ����ط������
		final ProgressDialog progress = new ProgressDialog(this.getContext());
		progress.setMessage("����ͳ�����ݣ����ص㣩...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		query.findStatistics(this.getActivity(), eRecord.class, new FindStatisticsListener() {

			@Override
			public void onSuccess(Object object) {
				progress.dismiss();
				data = new ArrayList<Map<String, Object>>();
				data.clear();
				JSONArray ary = (JSONArray) object;
				if (ary != null) {
					int length = ary.length();
					try {
						for (int i = 0; i < length; i++) {
							JSONObject obj = ary.getJSONObject(i);
							// int mTotal = obj.getInt("_sumrecordDate");
							String mAddress = obj.getString("address");
							int count = obj.getInt("_count");// setHasGroupCount����Ϊtrueʱ�����صĽ���к���"_count"�ֶ�
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("address", mAddress);
							map.put("total", count);
							data.add(map);
						}
						tab = 2;
						// ���ݰ��ص�������
						Collections.sort(data, new Comparator<Object>() {
							@Override
							public int compare(Object lhs, Object rhs) {
								Map<String, Object> obj1 = (Map<String, Object>) lhs;
								Map<String, Object> obj2 = (Map<String, Object>) rhs;
								String str1 = obj1.get("address").toString();
								String str2 = obj2.get("address").toString();
								return str2.compareTo(str1);

							}

						});
						if (mListView.getHeaderViewsCount() > 0) {
							mListView.removeHeaderView(headerView);
						}
						headerView = new HeaderListView(getContext());// ��ӱ�ͷ
						headerView.setTvHeadId("��  ��");
						headerView.setTvHeadTotal("��  ��");
						mListView.addHeaderView(headerView);//

						mListView.setAdapter(new SimpleAdapter(getContext(), data, R.layout.stats_date_item,
								new String[] { "address", "total" },
								new int[] { R.id.tv_stats_date, R.id.tv_date_total }));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(getContext(), "���ص�ͳ�Ƴɹ���������", 1).show();
				}
			}

			@Override
			public void onFailure(int code, String msg) {
				progress.dismiss();
				Toast.makeText(getContext(), "���ص�ͳ�Ƴɹ�����code =" + ",msg = " + msg, 1).show();
			}
		});
	}

	private void statsByDate() {
		BmobQuery<eRecord> query = new BmobQuery<eRecord>();
		query.sum(new String[] { "recordDate" }); // ͳ���ܵ÷�
		query.groupby(new String[] { "recordDate" });// �������ڷ���
		query.order("-recordDate"); // ��������
		query.setHasGroupCount(true); // ͳ�Ƽ�¼��Ĭ�ϲ����ط������
		final ProgressDialog progress = new ProgressDialog(this.getContext());
		progress.setMessage("����ͳ�����ݣ������ڣ�...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		query.findStatistics(this.getActivity(), eRecord.class, new FindStatisticsListener() {

			@Override
			public void onSuccess(Object object) {
				progress.dismiss();
				data = new ArrayList<Map<String, Object>>();
				data.clear();
				JSONArray ary = (JSONArray) object;
				if (ary != null) {
					int length = ary.length();
					try {
						for (int i = 0; i < length; i++) {
							JSONObject obj = ary.getJSONObject(i);
							// int mTotal = obj.getInt("_sumrecordDate");
							String mDate = obj.getString("recordDate");
							int count = obj.getInt("_count");// setHasGroupCount����Ϊtrueʱ�����صĽ���к���"_count"�ֶ�
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("recordDate", mDate);
							map.put("total", count);
							data.add(map);
						}
						tab = 1;
						// ���ݰ���������
						Collections.sort(data, new Comparator<Object>() {
							@Override
							public int compare(Object lhs, Object rhs) {
								Map<String, Object> obj1 = (Map<String, Object>) lhs;
								Map<String, Object> obj2 = (Map<String, Object>) rhs;
								String str1 = obj1.get("recordDate").toString();
								String str2 = obj2.get("recordDate").toString();
								return str2.compareTo(str1);

							}

						});
						if (mListView.getHeaderViewsCount() > 0) {
							mListView.removeHeaderView(headerView);
						}

						headerView = new HeaderListView(getContext());// ��ӱ�ͷ
						headerView.setTvHeadId("��  ��");
						headerView.setTvHeadTotal("��  ��");
						mListView.addHeaderView(headerView);						

						mListView.setAdapter(new SimpleAdapter(getContext(), data, R.layout.stats_date_item,
								new String[] { "recordDate", "total" },
								new int[] { R.id.tv_stats_date, R.id.tv_date_total }));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(getContext(), "������ͳ�Ƴɹ���������", 1).show();
				}
			}

			@Override
			public void onFailure(int code, String msg) {
				progress.dismiss();
				Toast.makeText(getContext(), "������ͳ�Ƴɹ�����code =" + ",msg = " + msg, 1).show();
			}
		});
	}
	
}