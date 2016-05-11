package com.hewaiming.MyRecords.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.io.File;
import java.text.SimpleDateFormat;

import com.hewaiming.MyRecords.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import cn.bmob.v3.Bmob;

public class MainActivity extends FragmentActivity  {

//	private Button NewRecordBtn, RecordsBtn, StatsBtn;
	private NewRecordFragment mNewRecordFragment;
	private StatsFragment mStatsFragment;
	private RecordsFragment mRecordsFragment;
	private Button[] mTabs;
	private Fragment[] fragments;
	private ImageView iv_newRecord_tips;
	private ImageView iv_records_tips;
	private int index;
	private int currentTabIndex;
	private static final String BMOB_APP_KEY="83359a9b5e00afd280c3cf6659391812";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_main);	
		Bmob.initialize(this, BMOB_APP_KEY);
		initView();
		initTab();
		initImageLoader(getApplicationContext());

	}

	private void initView(){
		mTabs = new Button[3];
		mTabs[0] = (Button) findViewById(R.id.btn_new_record);
		mTabs[1] = (Button) findViewById(R.id.btn_records);
		mTabs[2] = (Button) findViewById(R.id.btn_stats);
		iv_newRecord_tips = (ImageView)findViewById(R.id.iv_newRecord_tips);
		iv_newRecord_tips.setVisibility(View.GONE);
		iv_records_tips = (ImageView)findViewById(R.id.iv_records_tips);
		//把第一个tab设为选中状态
		mTabs[0].setSelected(true);
	}
	
	private void initTab(){
		mNewRecordFragment = new NewRecordFragment();
		mRecordsFragment = new RecordsFragment();
		mStatsFragment = new StatsFragment();
		fragments = new Fragment[] {mNewRecordFragment, mRecordsFragment, mStatsFragment };
		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, mRecordsFragment)
			.add(R.id.fragmentContainer, mNewRecordFragment).hide(mRecordsFragment).show(mNewRecordFragment).commit();
	}
	
	public void onTabSelect(View view) {
		switch (view.getId()) {
		case R.id.btn_new_record:
			index = 0;
			break;
		case R.id.btn_records:
			index = 1;
			break;
		case R.id.btn_stats:
			index = 2;
			break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragmentContainer, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		//把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}	
	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				"bmobim/Cache");// 获取到缓存的目录地址
		// 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				// 线程池内加载的数量
				.threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(new WeakMemoryCache())
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
				// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);// 全局初始化此配置
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	
		return super.onCreateOptionsMenu(menu);
	}
}
