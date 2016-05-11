package com.hewaiming.MyRecords.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import com.hewaiming.MyRecords.R;
import com.hewaiming.MyRecords.R.id;
import com.hewaiming.MyRecords.R.layout;
import com.hewaiming.MyRecords.R.menu;
import com.hewaiming.MyRecords.bean.eRecord;
import com.hewaiming.MyRecords.config.MyRecordsConst;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class UpdateActivity extends Activity implements OnClickListener {

	private Button submit_btn, btnBack, btnOk;
	private TextView tv_date, tv_time, tv_name, tv_address, tv_content;
	private ImageView iv_photo, date_btn, time_btn, phote_btn;
	private DatePickerDialog dateDialog;
	private TimePickerDialog timeDialog;
	private int year, monthOfYear, dayOfMonth, hourOfDay, minute;
	private LinearLayout layout_photo;
	private String myID;
	private TextView tv_camera, tv_picture, tv_location;
	private String showLargePath = "";
	private String localCameraPath = "";
	private String imagePath = "";
	private String init_cameraPath="";//取初始数据库图像地址

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		init_view();
		init_DateTime();
	}

	private void init_view() {
		submit_btn = (Button) findViewById(R.id.btn_newrecord_ok);
		submit_btn.setVisibility(View.GONE);
		tv_date = (TextView) findViewById(R.id.et_date);
		tv_time = (TextView) findViewById(R.id.et_time);
		tv_address = (TextView) findViewById(R.id.et_address);
		tv_content = (TextView) findViewById(R.id.et_content);
		tv_name = (TextView) findViewById(R.id.et_name);
		iv_photo = (ImageView) findViewById(R.id.iv_image);
		btnBack = (Button) findViewById(R.id.btn_back);
		btnOk = (Button) findViewById(R.id.btn_true);
		date_btn = (ImageView) findViewById(R.id.iv_date_btn);
		time_btn = (ImageView) findViewById(R.id.iv_time_btn);
		phote_btn = (ImageView) findViewById(R.id.iv_phote_btn);
		layout_photo = (LinearLayout) findViewById(R.id.layout_add);
		tv_camera = (TextView) findViewById(R.id.tv_camera);
		tv_picture = (TextView) findViewById(R.id.tv_picture);
		tv_location = (TextView) findViewById(R.id.tv_location);
		btnBack.setOnClickListener(this);
		btnOk.setOnClickListener(this);
		date_btn.setOnClickListener(this);// 时间选择
		time_btn.setOnClickListener(this);
		phote_btn.setOnClickListener(this);// 以下是图片选择按钮
		tv_camera.setOnClickListener(this);
		tv_picture.setOnClickListener(this);
		tv_location.setOnClickListener(this);
		iv_photo.setOnClickListener(this); // 图片放大
		eRecord updateRecord = (eRecord) getIntent().getSerializableExtra("updateRecord");// 取数据
		if (!updateRecord.equals(null)) {
			myID = updateRecord.getObjectId();
			tv_date.setText(updateRecord.getRecordDate());
			tv_time.setText(updateRecord.getRecordTime());
			tv_name.setText(updateRecord.getName());
			tv_address.setText(updateRecord.getAddress());
			tv_content.setText(updateRecord.getContent());
			if (updateRecord.getImage() != null) {
				String cameraPath = updateRecord.getImage();
				if (cameraPath != "" && cameraPath != null) {
					showLargePath=cameraPath;
					init_cameraPath=cameraPath;
					ImageLoader.getInstance().displayImage(cameraPath, iv_photo);
				}
			} else {
				Toast.makeText(getApplicationContext(), "无图片显示", Toast.LENGTH_LONG).show();
			}

		}

	}

	private void init_DateTime() {
		Calendar mCalendar = Calendar.getInstance();
		year = mCalendar.get(Calendar.YEAR);
		monthOfYear = mCalendar.get(Calendar.MONTH);
		dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
		dateDialog = new DatePickerDialog(this, new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				String strDate = year + "年";
				monthOfYear = monthOfYear + 1;
				if (monthOfYear < 10) {
					strDate = strDate + "0";
				}
				strDate = strDate + monthOfYear + "月";
				if (dayOfMonth < 10) {
					strDate = strDate + "0";
				}
				strDate = strDate + dayOfMonth + "日";
				tv_date.setText(strDate);
			}
		}, year, monthOfYear, dayOfMonth);

		hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
		minute = mCalendar.get(Calendar.MINUTE);
		timeDialog = new TimePickerDialog(this, new OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				String strTime = "";
				if (hourOfDay < 10) {
					strTime = strTime + "0";
				}
				strTime = strTime + hourOfDay + ":";
				if (minute < 10) {
					strTime = strTime + "0";
				}
				strTime = strTime + minute;
				tv_time.setText(strTime);

			}
		}, hourOfDay, minute, true);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_true:
//			Toast.makeText(getApplicationContext(), "修改记事条", Toast.LENGTH_LONG).show();
			updateRecord(imagePath);
			break;
		case R.id.iv_date_btn:// 选择日期
			dateDialog.show();
			break;
		case R.id.iv_time_btn:// 选择时间
			timeDialog.show();
			break;
		case R.id.iv_phote_btn:// 选择显示图片和照相按钮
			if (layout_photo.getVisibility() == View.VISIBLE) {
				layout_photo.setVisibility(View.GONE);
			} else {
				layout_photo.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.tv_picture:// 图片
			selectImageFromLocal();
			break;
		case R.id.tv_camera:// 照相
			selectImageFromCamera();
			break;
		case R.id.tv_location:// 定位
			Toast.makeText(this, "定位", 0).show();
			break;
		case R.id.iv_image: // 放大图片
			if (iv_photo.getDrawable() != null) {
				if (showLargePath != "" ) {
					showLargeImage(showLargePath);
				} else {
					Toast.makeText(this, "无图片", 0).show();
				}
			} else {
				Toast.makeText(this, "没有图片显示", 0).show();
			}
			break;
		}

	}

	private void updateRecord(String myImagePath) {
		if ((myImagePath!=null) && (myImagePath != "")) {
			if(myImagePath.equals(init_cameraPath)){
				UpLoadRecord(myImagePath);             //没有改动图片
			}else{
				UploadImage(myImagePath); // 上传图片文件到服务器保存
			}
			
		} else {
			UpLoadRecord(myImagePath);// 无图像
		}
		
	}

	private void UpLoadRecord(String fileUrl2) {		
		String strDate = tv_date.getText().toString();
		String strTime = tv_time.getText().toString();
		String strName = tv_name.getText().toString();
		String strAddress = tv_address.getText().toString();
		String strContent = tv_content.getText().toString();
		eRecord mERecord = new eRecord();
		mERecord.setValue("recordDate", strDate);
		mERecord.setValue("recordTime", strTime);
		mERecord.setValue("name", strName);
		mERecord.setValue("address", strAddress);
		mERecord.setValue("content", strContent);		
		if (!(fileUrl2.equals(null)) && fileUrl2 != "") {			
			mERecord.setValue("image", fileUrl2);
		}
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("更新数据正在提交中...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		mERecord.update(getApplicationContext(),myID,new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				progress.dismiss();				
				Toast.makeText(getApplicationContext(), "更新记事条成功！ " , 1).show();
				Intent intent=getIntent();
				intent.putExtra("updateAction", "success");//返回成功参数
				setResult(1,intent);
				finish();            
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				progress.dismiss();
				Toast.makeText(getApplicationContext(), "更新数据失败：code=" + arg0 + "，错误描述：" + arg1, 0).show();
				Log.i("bmob 错误描述：code=", arg1);
//				Intent intent=getIntent();
//				intent.putExtra("updateAction", "failure");//返回成功参数
//				setResult(2,intent);
//				finish();     
			}
		});	
		showLargePath=mERecord.getImage();
	}

	private void UploadImage(String ImagePath) {
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("正在上传修改后的图片文件...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		final BmobFile bmobFile = new BmobFile(new File(ImagePath));
		bmobFile.uploadblock(this, new UploadFileListener() {

			@Override
			public void onSuccess() {
				// bmobFile.getUrl()---返回的上传文件的地址（不带域名）
				// bmobFile.getFileUrl(context)--返回的上传文件的完整地址（带域名）
				progress.dismiss();
				UpLoadRecord(bmobFile.getFileUrl(getApplicationContext()));
				Toast.makeText(getApplicationContext(), "更新图片---上传成功:" + bmobFile.getFileUrl(getApplicationContext()), 0).show();
			}

			@Override
			public void onProgress(Integer value) {
				// 返回的上传进度（百分比）
			}

			@Override
			public void onFailure(int code, String msg) {
				progress.dismiss();
				Toast.makeText(getApplicationContext(), "更新图片--上传失败：" + msg, 0).show();
			}
		});
		
	}

	/**
	 * 启动相机拍照 startCamera
	 * 
	 * @Title: startCamera @throws
	 */
	public void selectImageFromCamera() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File dir = new File(MyRecordsConst.MyRecords_PICTURE_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, String.valueOf(System.currentTimeMillis()) + ".jpg");
		localCameraPath = file.getPath();
		Uri imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, MyRecordsConst.REQUESTCODE_TAKE_CAMERA);
	}

	/**
	 * 选择图片 @Title: selectImage @Description: TODO @param @return void @throws
	 */
	public void selectImageFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, MyRecordsConst.REQUESTCODE_TAKE_LOCAL);
	}

	private void showLargeImage(String PhotoPath) {
		if (PhotoPath != "") {
			Intent intent = new Intent(this, ImageBrowserActivity.class);
			ArrayList<String> photos = new ArrayList<String>();
			photos.add(PhotoPath);
			intent.putStringArrayListExtra("photos", photos);
			intent.putExtra("position", 0);
			startActivity(intent);
		} else {
			Toast.makeText(this, "无图片", 0).show();
		}
	}

	/**
	 * 默认先上传本地图片，之后才显示出来 sendImageMessage @Title: sendImageMessage @Description:
	 * TODO @param @param localPath @return void @throws
	 */

	private void showImage(String localCameraPath) {
		if (layout_photo.getVisibility() == View.VISIBLE) {
			layout_photo.setVisibility(View.GONE);
		}
		Log.i("localcameraPath", localCameraPath);
		imagePath = localCameraPath;
		int isHave = localCameraPath.indexOf("http");	
		if (isHave >= 0) {
			showLargePath = localCameraPath;
			if (localCameraPath != "" && !localCameraPath.equals(null)) {
				ImageLoader.getInstance().displayImage(localCameraPath, iv_photo);
			}
		} else {
			showLargePath = "file://" + localCameraPath;
			if (localCameraPath != "" && !localCameraPath.equals(null)) {
				ImageLoader.getInstance().displayImage("file:///" + localCameraPath, iv_photo);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// getActivity();
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case MyRecordsConst.REQUESTCODE_TAKE_CAMERA:// 当取到值的时候才上传path路径下的图片到服务器
				// ShowLog("本地图片的地址：" + localCameraPath);
				showImage(localCameraPath);
				break;
			case MyRecordsConst.REQUESTCODE_TAKE_LOCAL:
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
						cursor.moveToFirst();
						int columnIndex = cursor.getColumnIndex("_data");
						String localSelectPath = cursor.getString(columnIndex);
						cursor.close();
						if (localSelectPath == null || localSelectPath.equals("null")) {
							Toast.makeText(this, "找不到您想要的图片", 0).show();
							;
							return;
						}
						showImage(localSelectPath);
					}
				}
				break;
			case MyRecordsConst.REQUESTCODE_TAKE_LOCATION:// 地理位置
				// double latitude = data.getDoubleExtra("x", 0);// 维度
				// double longtitude = data.getDoubleExtra("y", 0);// 经度
				// String address = data.getStringExtra("address");
				// if (address != null && !address.equals("")) {
				// sendLocationMessage(address, latitude, longtitude);
				// } else {
				// Toast.makeText(getContext(), "无法获取到您的位置信息!",0).show();;
				// }

				break;
			}
		}

	}
}
