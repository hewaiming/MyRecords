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
	private String init_cameraPath="";//ȡ��ʼ���ݿ�ͼ���ַ

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
		date_btn.setOnClickListener(this);// ʱ��ѡ��
		time_btn.setOnClickListener(this);
		phote_btn.setOnClickListener(this);// ������ͼƬѡ��ť
		tv_camera.setOnClickListener(this);
		tv_picture.setOnClickListener(this);
		tv_location.setOnClickListener(this);
		iv_photo.setOnClickListener(this); // ͼƬ�Ŵ�
		eRecord updateRecord = (eRecord) getIntent().getSerializableExtra("updateRecord");// ȡ����
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
				Toast.makeText(getApplicationContext(), "��ͼƬ��ʾ", Toast.LENGTH_LONG).show();
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
				String strDate = year + "��";
				monthOfYear = monthOfYear + 1;
				if (monthOfYear < 10) {
					strDate = strDate + "0";
				}
				strDate = strDate + monthOfYear + "��";
				if (dayOfMonth < 10) {
					strDate = strDate + "0";
				}
				strDate = strDate + dayOfMonth + "��";
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
//			Toast.makeText(getApplicationContext(), "�޸ļ�����", Toast.LENGTH_LONG).show();
			updateRecord(imagePath);
			break;
		case R.id.iv_date_btn:// ѡ������
			dateDialog.show();
			break;
		case R.id.iv_time_btn:// ѡ��ʱ��
			timeDialog.show();
			break;
		case R.id.iv_phote_btn:// ѡ����ʾͼƬ�����ఴť
			if (layout_photo.getVisibility() == View.VISIBLE) {
				layout_photo.setVisibility(View.GONE);
			} else {
				layout_photo.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.tv_picture:// ͼƬ
			selectImageFromLocal();
			break;
		case R.id.tv_camera:// ����
			selectImageFromCamera();
			break;
		case R.id.tv_location:// ��λ
			Toast.makeText(this, "��λ", 0).show();
			break;
		case R.id.iv_image: // �Ŵ�ͼƬ
			if (iv_photo.getDrawable() != null) {
				if (showLargePath != "" ) {
					showLargeImage(showLargePath);
				} else {
					Toast.makeText(this, "��ͼƬ", 0).show();
				}
			} else {
				Toast.makeText(this, "û��ͼƬ��ʾ", 0).show();
			}
			break;
		}

	}

	private void updateRecord(String myImagePath) {
		if ((myImagePath!=null) && (myImagePath != "")) {
			if(myImagePath.equals(init_cameraPath)){
				UpLoadRecord(myImagePath);             //û�иĶ�ͼƬ
			}else{
				UploadImage(myImagePath); // �ϴ�ͼƬ�ļ�������������
			}
			
		} else {
			UpLoadRecord(myImagePath);// ��ͼ��
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
		progress.setMessage("�������������ύ��...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		mERecord.update(getApplicationContext(),myID,new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				progress.dismiss();				
				Toast.makeText(getApplicationContext(), "���¼������ɹ��� " , 1).show();
				Intent intent=getIntent();
				intent.putExtra("updateAction", "success");//���سɹ�����
				setResult(1,intent);
				finish();            
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				progress.dismiss();
				Toast.makeText(getApplicationContext(), "��������ʧ�ܣ�code=" + arg0 + "������������" + arg1, 0).show();
				Log.i("bmob ����������code=", arg1);
//				Intent intent=getIntent();
//				intent.putExtra("updateAction", "failure");//���سɹ�����
//				setResult(2,intent);
//				finish();     
			}
		});	
		showLargePath=mERecord.getImage();
	}

	private void UploadImage(String ImagePath) {
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("�����ϴ��޸ĺ��ͼƬ�ļ�...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		final BmobFile bmobFile = new BmobFile(new File(ImagePath));
		bmobFile.uploadblock(this, new UploadFileListener() {

			@Override
			public void onSuccess() {
				// bmobFile.getUrl()---���ص��ϴ��ļ��ĵ�ַ������������
				// bmobFile.getFileUrl(context)--���ص��ϴ��ļ���������ַ����������
				progress.dismiss();
				UpLoadRecord(bmobFile.getFileUrl(getApplicationContext()));
				Toast.makeText(getApplicationContext(), "����ͼƬ---�ϴ��ɹ�:" + bmobFile.getFileUrl(getApplicationContext()), 0).show();
			}

			@Override
			public void onProgress(Integer value) {
				// ���ص��ϴ����ȣ��ٷֱȣ�
			}

			@Override
			public void onFailure(int code, String msg) {
				progress.dismiss();
				Toast.makeText(getApplicationContext(), "����ͼƬ--�ϴ�ʧ�ܣ�" + msg, 0).show();
			}
		});
		
	}

	/**
	 * ����������� startCamera
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
	 * ѡ��ͼƬ @Title: selectImage @Description: TODO @param @return void @throws
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
			Toast.makeText(this, "��ͼƬ", 0).show();
		}
	}

	/**
	 * Ĭ�����ϴ�����ͼƬ��֮�����ʾ���� sendImageMessage @Title: sendImageMessage @Description:
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
			case MyRecordsConst.REQUESTCODE_TAKE_CAMERA:// ��ȡ��ֵ��ʱ����ϴ�path·���µ�ͼƬ��������
				// ShowLog("����ͼƬ�ĵ�ַ��" + localCameraPath);
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
							Toast.makeText(this, "�Ҳ�������Ҫ��ͼƬ", 0).show();
							;
							return;
						}
						showImage(localSelectPath);
					}
				}
				break;
			case MyRecordsConst.REQUESTCODE_TAKE_LOCATION:// ����λ��
				// double latitude = data.getDoubleExtra("x", 0);// ά��
				// double longtitude = data.getDoubleExtra("y", 0);// ����
				// String address = data.getStringExtra("address");
				// if (address != null && !address.equals("")) {
				// sendLocationMessage(address, latitude, longtitude);
				// } else {
				// Toast.makeText(getContext(), "�޷���ȡ������λ����Ϣ!",0).show();;
				// }

				break;
			}
		}

	}
}
