package com.hewaiming.MyRecords.ui;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.hewaiming.MyRecords.R;
import com.hewaiming.MyRecords.bean.eRecord;
import com.hewaiming.MyRecords.config.MyRecordsConst;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.R.layout;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Notification.Action.Builder;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.InitListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.bmob.v3.listener.UploadFileListener;

public class NewRecordFragment extends Fragment implements OnClickListener {

	private static final String TAG = "CrimeCameraFragment";
	private Camera mCamera;
	private EditText mDate, mTime, mName, mAddress, mContent;
	private ImageView mPhote, date_btn, time_btn, phote_btn;
	private Button mButton;
	private View view;
	private DatePickerDialog dateDialog;
	private TimePickerDialog timeDialog;
	private int year, monthOfYear, dayOfMonth, hourOfDay, minute;
	private LinearLayout layout_photo,mLayoutImage;
	private TextView tv_picture, tv_camera, tv_location;
	private String localCameraPath = "";// ���պ�õ���ͼƬ��ַ
	public String FileUrl = "";
	private String imagePath = "";
	private String showLargePath = "";
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.newrecord_fragment, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init_view();
		init_DateTime();
	}

	private void init_DateTime() {
		Calendar mCalendar = Calendar.getInstance();
		year = mCalendar.get(Calendar.YEAR);
		monthOfYear = mCalendar.get(Calendar.MONTH);
		dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
		dateDialog = new DatePickerDialog(getContext(), new OnDateSetListener() {

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
				mDate.setText(strDate);
			}
		}, year, monthOfYear, dayOfMonth);

		hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
		minute = mCalendar.get(Calendar.MINUTE);
		timeDialog = new TimePickerDialog(getContext(), new OnTimeSetListener() {

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
				mTime.setText(strTime);

			}
		}, hourOfDay, minute, true);

	}

	private void init_view() {
		mDate = (EditText) view.findViewById(R.id.et_date);
		mTime = (EditText) view.findViewById(R.id.et_time);
		mName = (EditText) view.findViewById(R.id.et_name);
		mAddress = (EditText) view.findViewById(R.id.et_address);
		mContent = (EditText) view.findViewById(R.id.et_content);
		mPhote = (ImageView) view.findViewById(R.id.iv_image);
		mButton = (Button) view.findViewById(R.id.btn_newrecord_ok);
		date_btn = (ImageView) view.findViewById(R.id.iv_date_btn);
		time_btn = (ImageView) view.findViewById(R.id.iv_time_btn);
		phote_btn = (ImageView) view.findViewById(R.id.iv_phote_btn);
		layout_photo = (LinearLayout) view.findViewById(R.id.layout_add);
		mLayoutImage=(LinearLayout)view.findViewById(R.id.layout_image);		
		tv_camera = (TextView) view.findViewById(R.id.tv_camera);
		tv_picture = (TextView) view.findViewById(R.id.tv_picture);
		tv_location = (TextView) view.findViewById(R.id.tv_location);
		SimpleDateFormat format_date = new SimpleDateFormat("yyyy��MM��dd��");
		SimpleDateFormat format_time = new SimpleDateFormat("hh:mm");
		Date date = new Date();
		String now_date = format_date.format(date);
		String now_time = format_time.format(date);
		mDate.setText(now_date);
		mTime.setText(now_time);// ����ʱ��
		mButton.setOnClickListener(this);
		date_btn.setOnClickListener(this);
		time_btn.setOnClickListener(this);
		phote_btn.setOnClickListener(this); // ͼƬѡ��ť
		tv_camera.setOnClickListener(this);
		tv_picture.setOnClickListener(this);
		tv_location.setOnClickListener(this);
		mPhote.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_newrecord_ok:// ȷ���ύ
			Submit(imagePath);
			break;
		case R.id.iv_date_btn:// ѡ������
			dateDialog.show();
			break;
		case R.id.iv_time_btn:// ѡ��ʱ��
			timeDialog.show();
			break;
		case R.id.iv_phote_btn:// ѡ����ʾͼƬ�����ఴť
			if (layout_photo.getVisibility() == View.VISIBLE) {	
//				mLayoutImage.setLayoutParams(new LinearLayout.LayoutParams(mwidth, imageLayoutHeight));
				layout_photo.setVisibility(View.GONE);
			} else {
//				mLayoutImage.setLayoutParams(new LinearLayout.LayoutParams(mwidth, (int) (imageLayoutHeight*0.7)));
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
			Toast.makeText(getContext(), "��λ", 0).show();
			break;
		case R.id.iv_image: // �Ŵ�ͼƬ
			if (mPhote.getDrawable() != null) {
				if (showLargePath != "" && (!showLargePath.equals(null))) {
					showLargeImage(showLargePath);
				} else {
					Toast.makeText(getContext(), "��ͼƬ", 0).show();
				}
			} else {
				Toast.makeText(getContext(), "û��ͼƬ��ʾ", 0).show();
			}
			break;
		}
	}

	private void showLargeImage(String PhotoPath) {
		if ((!PhotoPath.equals(null)) && PhotoPath != "") {
			Intent intent = new Intent(getContext(), ImageBrowserActivity.class);
			ArrayList<String> photos = new ArrayList<String>();
			photos.add(PhotoPath);
			intent.putStringArrayListExtra("photos", photos);
			intent.putExtra("position", 0);
			getContext().startActivity(intent);
		} else {
			Toast.makeText(getContext(), "��ͼƬ", 0).show();
		}

	}

	private void Submit(final String imagePath) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		// builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("ȷ���ϴ����������������У�");
		Log.i("runButton", "running");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!(imagePath.equals(null)) && imagePath != "") {
					UploadImage(imagePath); // �ϴ�ͼƬ�ļ�������������
				} else {
					UpLoadRecord(imagePath);// ��ͼ��
				}
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// showDialog("��ѡ����ȡ��������");
			}
		});
		builder.create().show();
	}

	private void UploadImage(String ImagePath) {
		final ProgressDialog progress = new ProgressDialog(getContext());
		progress.setMessage("�����ϴ�ͼƬ�ļ�...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();		
		final BmobFile bmobFile = new BmobFile(new File(ImagePath));
		bmobFile.uploadblock(getContext(), new UploadFileListener() {

			@Override
			public void onSuccess() {
				// bmobFile.getUrl()---���ص��ϴ��ļ��ĵ�ַ������������
				// bmobFile.getFileUrl(context)--���ص��ϴ��ļ���������ַ����������
				progress.dismiss();
				UpLoadRecord(bmobFile.getFileUrl(getContext()));			
				Toast.makeText(getContext(), "�ϴ��ļ��ɹ�:" + bmobFile.getFileUrl(getContext()), 0).show();
			}

			@Override
			public void onProgress(Integer value) {
				// ���ص��ϴ����ȣ��ٷֱȣ�				
			}

			@Override
			public void onFailure(int code, String msg) {	
				progress.dismiss();
				Toast.makeText(getContext(), "�ϴ��ļ�ʧ�ܣ�" + msg, 0).show();
			}
		});
	}

	protected void UpLoadRecord(String fileUrl2) {
		String strDate = mDate.getText().toString();
		String strTime = mTime.getText().toString();
		String strName = mName.getText().toString();
		String strAddress = mAddress.getText().toString();
		String strContent = mContent.getText().toString();
		final eRecord mERecord = new eRecord();
		mERecord.setRecordDate(strDate);
		mERecord.setRecordTime(strTime);
		mERecord.setName(strName);
		mERecord.setAddress(strAddress);
		mERecord.setContent(strContent);
		if (!(fileUrl2.equals(null)) && fileUrl2 != "") {
			mERecord.setImage(fileUrl2);
		}
		final ProgressDialog progress = new ProgressDialog(getContext());
		progress.setMessage("�����ύ��...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		mERecord.save(this.getContext(), new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				progress.dismiss();
				Toast.makeText(getContext(), "�ύ���ݳɹ�: ������ " + mERecord.getName() + "�����ڣ�" + mERecord.getRecordDate(), 0)
						.show();

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				progress.dismiss();
				Toast.makeText(getContext(), "�ύ����ʧ�ܣ�code=" + arg0 + "������������" + arg1, 0).show();
				Log.i("bmob ����������code=", arg1);
			}
		});
		showLargePath = mERecord.getImage();
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getActivity();
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
						Cursor cursor = getActivity().getContentResolver().query(selectedImage, null, null, null, null);
						cursor.moveToFirst();
						int columnIndex = cursor.getColumnIndex("_data");
						String localSelectPath = cursor.getString(columnIndex);
						cursor.close();
						if (localSelectPath == null || localSelectPath.equals("null")) {
							Toast.makeText(getContext(), "�Ҳ�������Ҫ��ͼƬ", 0).show();
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
		showLargePath = "file://" + localCameraPath;
		if (localCameraPath != "" && !localCameraPath.equals(null)) {
			ImageLoader.getInstance().displayImage("file:///" + localCameraPath, mPhote);
		}

	}

}
