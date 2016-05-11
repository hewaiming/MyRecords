package com.hewaiming.MyRecords.charts;

import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hewaiming.MyRecords.R;
import com.hewaiming.MyRecords.R.id;
import com.hewaiming.MyRecords.R.layout;
import com.hewaiming.MyRecords.R.menu;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class BarChartActivity extends Activity {

	private BarChart mBarChart;
	private BarData mBarData;
	private Button btnBack;
	private TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bar_chart);
		btnBack = (Button) findViewById(R.id.btn_back_bar_charts);
		title=(TextView) findViewById(R.id.bar_charts_title);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mBarChart = (BarChart) findViewById(R.id.bar_chart);
		// mBarData = getBarData(4, 100);
		mBarData = getBarDataFromIntent();
		showBarChart(mBarChart, mBarData);
	}

	private BarData getBarDataFromIntent() {
		
		Bundle bundle = getIntent().getExtras();
		ArrayList list = bundle.getParcelableArrayList("list");
		int which = bundle.getInt("statsWhich");
		// ��List�н�����ת�� List<Map<String, Object>>
		List<Map<String, Object>> lists = (List<Map<String, Object>>) list.get(0);

		ArrayList<String> xValues = new ArrayList<String>();
		ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();

		switch (which) {
		case 1:   //ȡ������ͳ������
			for (int i = 0; i < lists.size(); i++) {
				xValues.add(lists.get(i).get("recordDate").toString());
				float value = Float.parseFloat(lists.get(i).get("total").toString());
				yValues.add(new BarEntry(value, i));
			}	
			title.setText("�������ڡ�ͳ�Ƽ�¼���ܼ���");
			break;
		case 2:  //ȡ���ص�ͳ������
			for (int i = 0; i < lists.size(); i++) {
				xValues.add(lists.get(i).get("address").toString());
				float value = Float.parseFloat(lists.get(i).get("total").toString());
				yValues.add(new BarEntry(value, i));
			}	
			title.setText("�����ص㡿ͳ�Ƽ�¼���ܼ���");
			break;
		case 3: //ȡ��������ͳ������
			for (int i = 0; i < lists.size(); i++) {
				xValues.add(lists.get(i).get("name").toString());
				float value = Float.parseFloat(lists.get(i).get("total").toString());
				yValues.add(new BarEntry(value, i));
			}	
			title.setText("���������ˡ�ͳ�Ƽ�¼���ܼ���");
			break;

		}		
      
		BarDataSet barDataSet = new BarDataSet(yValues, "��ý���¼���ܼ���ͳ��ͼ");

		barDataSet.setColor(Color.rgb(114, 188, 223));

		List<IBarDataSet> barDataSets = new ArrayList<IBarDataSet>();
		barDataSets.add(barDataSet); // add the datasets

		BarData barData = new BarData(xValues, barDataSets);

		return barData;

	}

	private void showBarChart(BarChart barChart, BarData barData) {
		barChart.setDrawBorders(false); //// �Ƿ�������ͼ����ӱ߿�

		barChart.setDescription("");// ��������

		// ���û�����ݵ�ʱ�򣬻���ʾ���������ListView��EmptyView
		barChart.setNoDataTextDescription("ͳ��ͼû�����ݣ�");

		barChart.setDrawGridBackground(false); // �Ƿ���ʾ�����ɫ
		barChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); // ���ĵ���ɫ�����������Ǹ���ɫ����һ��͸����

		barChart.setTouchEnabled(true); // �����Ƿ���Դ���

		barChart.setDragEnabled(true);// �Ƿ������ק
		barChart.setScaleEnabled(true);// �Ƿ��������

		barChart.setPinchZoom(false);//

		// barChart.setBackgroundColor();// ���ñ���

		barChart.setDrawBarShadow(false);

		barChart.setData(barData); // ��������

		Legend mLegend = barChart.getLegend(); // ���ñ���ͼ��ʾ

		mLegend.setForm(LegendForm.CIRCLE);// ��ʽ
		mLegend.setFormSize(4f);// ����
		mLegend.setTextColor(Color.BLACK);// ��ɫ

//		 X���趨
		 XAxis xAxis = barChart.getXAxis();
		 xAxis.setPosition(XAxisPosition.TOP);
		 xAxis.setTextSize(6f);
		 xAxis.setTextColor(Color.BLUE);
		 xAxis.setLabelRotationAngle(90f);;

		barChart.animateX(2500); // ����ִ�еĶ���,x��
	}

	private BarData getBarData(int count, float range) {
		ArrayList<String> xValues = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			xValues.add("��" + (i + 1) + "����");
		}

		ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();

		for (int i = 0; i < count; i++) {
			float value = (float) (Math.random() * range/* 100���ڵ������ */) + 3;
			yValues.add(new BarEntry(value, i));
		}

		// y������ݼ���
		BarDataSet barDataSet = new BarDataSet(yValues, "���Ա�״ͼ");

		barDataSet.setColor(Color.rgb(114, 188, 223));

		List<IBarDataSet> barDataSets = new ArrayList<IBarDataSet>();
		barDataSets.add(barDataSet); // add the datasets

		BarData barData = new BarData(xValues, barDataSets);

		return barData;
	}

}
