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
		// 从List中将参数转回 List<Map<String, Object>>
		List<Map<String, Object>> lists = (List<Map<String, Object>>) list.get(0);

		ArrayList<String> xValues = new ArrayList<String>();
		ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();

		switch (which) {
		case 1:   //取按日期统计数据
			for (int i = 0; i < lists.size(); i++) {
				xValues.add(lists.get(i).get("recordDate").toString());
				float value = Float.parseFloat(lists.get(i).get("total").toString());
				yValues.add(new BarEntry(value, i));
			}	
			title.setText("按【日期】统计记录本总件数");
			break;
		case 2:  //取按地点统计数据
			for (int i = 0; i < lists.size(); i++) {
				xValues.add(lists.get(i).get("address").toString());
				float value = Float.parseFloat(lists.get(i).get("total").toString());
				yValues.add(new BarEntry(value, i));
			}	
			title.setText("按【地点】统计记录本总件数");
			break;
		case 3: //取按当事人统计数据
			for (int i = 0; i < lists.size(); i++) {
				xValues.add(lists.get(i).get("name").toString());
				float value = Float.parseFloat(lists.get(i).get("total").toString());
				yValues.add(new BarEntry(value, i));
			}	
			title.setText("按【当事人】统计记录本总件数");
			break;

		}		
      
		BarDataSet barDataSet = new BarDataSet(yValues, "多媒体记录本总件数统计图");

		barDataSet.setColor(Color.rgb(114, 188, 223));

		List<IBarDataSet> barDataSets = new ArrayList<IBarDataSet>();
		barDataSets.add(barDataSet); // add the datasets

		BarData barData = new BarData(xValues, barDataSets);

		return barData;

	}

	private void showBarChart(BarChart barChart, BarData barData) {
		barChart.setDrawBorders(false); //// 是否在折线图上添加边框

		barChart.setDescription("");// 数据描述

		// 如果没有数据的时候，会显示这个，类似ListView的EmptyView
		barChart.setNoDataTextDescription("统计图没有数据！");

		barChart.setDrawGridBackground(false); // 是否显示表格颜色
		barChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度

		barChart.setTouchEnabled(true); // 设置是否可以触摸

		barChart.setDragEnabled(true);// 是否可以拖拽
		barChart.setScaleEnabled(true);// 是否可以缩放

		barChart.setPinchZoom(false);//

		// barChart.setBackgroundColor();// 设置背景

		barChart.setDrawBarShadow(false);

		barChart.setData(barData); // 设置数据

		Legend mLegend = barChart.getLegend(); // 设置比例图标示

		mLegend.setForm(LegendForm.CIRCLE);// 样式
		mLegend.setFormSize(4f);// 字体
		mLegend.setTextColor(Color.BLACK);// 颜色

//		 X轴设定
		 XAxis xAxis = barChart.getXAxis();
		 xAxis.setPosition(XAxisPosition.TOP);
		 xAxis.setTextSize(6f);
		 xAxis.setTextColor(Color.BLUE);
		 xAxis.setLabelRotationAngle(90f);;

		barChart.animateX(2500); // 立即执行的动画,x轴
	}

	private BarData getBarData(int count, float range) {
		ArrayList<String> xValues = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			xValues.add("第" + (i + 1) + "季度");
		}

		ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();

		for (int i = 0; i < count; i++) {
			float value = (float) (Math.random() * range/* 100以内的随机数 */) + 3;
			yValues.add(new BarEntry(value, i));
		}

		// y轴的数据集合
		BarDataSet barDataSet = new BarDataSet(yValues, "测试饼状图");

		barDataSet.setColor(Color.rgb(114, 188, 223));

		List<IBarDataSet> barDataSets = new ArrayList<IBarDataSet>();
		barDataSets.add(barDataSet); // add the datasets

		BarData barData = new BarData(xValues, barDataSets);

		return barData;
	}

}
