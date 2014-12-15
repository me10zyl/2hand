package com.bingo.hand.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bingo.hand.R;
import com.bingo.hand.adapter.GridViewUploadPictureAdapter;
import com.bingo.hand.util.AsyncHttpUtil;
import com.bingo.hand.util.AsyncHttpUtil.OnRecieveListener;

public class PublishFragment extends Fragment implements OnClickListener {
	protected static final int RESPONSE = 0;
	protected static final int ERROR = 1;
	protected static final int UPLOAD = 2;
	protected static final int START_RECORD = 3;
	protected static final int STOP_RECORD = 4;
	private static final int CONDITION_DISCONTENT = 5;
	GridView gv_picture;
	Button btn_publish;
	EditText et_goodsname, et_goodsdescription, et_goodsprice;
	LinearLayout ll_old_publish, ll_new_publish;
	private GridViewUploadPictureAdapter adapter;
	private ArrayList<Bitmap> arr;// GridView adpter的数组(UI相关)
	private ArrayList<Uri> arr_pic_uri;// 真正要上传的图片的URI地址数组
	private Bitmap bitmap_add;
	// private final String ip = "121.40.80.205:8899";
	private String ip;
	private ImageView iv_old_publish;// 二手单选按钮
	private ImageView iv_new_publish;// 最新单选按钮
	private String url_pic, url_audio;
	private boolean isUploadFinished;// 全部上传完成
	private boolean isPicUploadFnishedAndNeedToAddAudioUrl;// 图片上传完成，需要添加音频url到url_audio
	private boolean isPressRecord;// 用户点击过录制音频
	private int picture_upload_count;
	// -----------------------------音频相关-----------------------------------
	FrameLayout fl_audio;
	TextView tv_audio;
	boolean isRecording;// 是否正在录音
	private File mRecAudioFile; // 录制的音频文件
	private MediaPlayer mp;
	private MediaRecorder mMediaRecorder;// MediaRecorder对象
	private String mRecordFilePath;
	private Chronometer mTimer;
	private String strTempFile = "test";// 零时文件的前缀
	private boolean isNew;// 是否是新的商品(供Radio按钮使用)
	private AsyncHttpUtil util;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		ip = getResources().getString(R.string.ip);
		View view = inflater.inflate(R.layout.fragment_publish, null);
		gv_picture = (GridView) view.findViewById(R.id.gv_picture);
		btn_publish = (Button) view.findViewById(R.id.btn_publish);
		fl_audio = (FrameLayout) view.findViewById(R.id.fl_audio);
		iv_old_publish = (ImageView) view.findViewById(R.id.iv_old_publish);
		iv_new_publish = (ImageView) view.findViewById(R.id.iv_new_publish);
		ll_old_publish = (LinearLayout) view.findViewById(R.id.ll_old_publish);
		ll_new_publish = (LinearLayout) view.findViewById(R.id.ll_new_publish);
		tv_audio = (TextView) view.findViewById(R.id.tv_audio);
		et_goodsdescription = (EditText) view
				.findViewById(R.id.et_goodsdecription);
		et_goodsname = (EditText) view.findViewById(R.id.et_goodsname);
		et_goodsprice = (EditText) view.findViewById(R.id.et_goodsprice);
		btn_publish.setOnClickListener(this);
		arr = new ArrayList<Bitmap>();
		arr_pic_uri = new ArrayList<Uri>();
		Drawable drawable = getActivity().getResources().getDrawable(
				R.drawable._2);
		bitmap_add = ((BitmapDrawable) drawable).getBitmap();
		bitmap_add = zoomImg(bitmap_add, 215, 215);
		bitmap_add = toRoundCorner(bitmap_add, 20);
		arr.add(bitmap_add);
		adapter = new GridViewUploadPictureAdapter(getActivity(), arr);
		gv_picture.setAdapter(adapter);
		gv_picture.setBackgroundResource(R.drawable.tuceng_4);
		setRadioButtonUI();
		ll_old_publish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isNew = false;
				setRadioButtonUI();
			}

		});
		ll_new_publish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isNew = true;
				setRadioButtonUI();
			}

		});
		fl_audio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				// Toast.makeText(getBaseContext(), "Bingo!",
				// Toast.LENGTH_LONG).show();
				if (!isRecording) {
					try {
						/* ①Initial：实例化MediaRecorder对象 */
						mMediaRecorder = new MediaRecorder();
						/* ②setAudioSource/setVedioSource */
						// mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置麦克风
						mMediaRecorder
								.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
						/*
						 * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default
						 * THREE_GPP(
						 * 3gp格式，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB
						 * )
						 */
						mMediaRecorder
								.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
						/* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default */
						mMediaRecorder
								.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
						/* ②设置输出文件的路径 */
						try {
							mRecAudioFile = File.createTempFile(strTempFile,
									".amr", getActivity().getCacheDir());
							// mRecAudioFile = File.createTempFile(null,
							// ".amr");

						} catch (Exception e) {
							e.printStackTrace();
						}
						// mRecordFilePath = mRecAudioFile.getAbsolutePath();
						mRecordFilePath = "/sdcard/test.amr";

						mMediaRecorder.setOutputFile(mRecordFilePath);

						/* ③准备 */
						mMediaRecorder.prepare();
						/* ④开始 */
						mMediaRecorder.start();
						/* 按钮状态 */
						handler.sendEmptyMessage(START_RECORD);
					} catch (IOException e) {
						e.printStackTrace();
					}
					isRecording = true;
				} else {
					if (mRecAudioFile != null) {
						isPressRecord = true; // 用户已完成了录制音频
						/* ⑤停止录音 */
						mMediaRecorder.stop();
						/* 将录音文件添加到List中 */
						// mMusicList.add(mRecAudioFile.getName());
						// ArrayAdapter<String> musicList = new
						// ArrayAdapter<String>(mediarecorder1.this,
						// R.layout.list, mMusicList);
						// setListAdapter(musicList);
						/* ⑥释放MediaRecorder */
						mMediaRecorder.release();
						mMediaRecorder = null;

						/* 按钮状态 */
						// MainActivity.this.mAudioStartBtn.setEnabled(true);
						// MainActivity.this.mAudioStopBtn.setEnabled(false);
						// MainActivity.this.mTimer.stop();
						// MainActivity.this.DisplayText("录音结束");
						// MainActivity.this.DisplayTip(mRecordFilePath);
						handler.sendEmptyMessage(STOP_RECORD);
					}
					isRecording = false;
				}
				// MainActivity.this.mTimer.setBase(SystemClock.elapsedRealtime());
				// MainActivity.this.mTimer.start();
				// MainActivity.this.mTimer.clearComposingText();
				// MainActivity.this.DisplayText("开始录音");

			}
		});
		gv_picture.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position == arr.size() - 1
						&& arr.get(arr.size() - 1) == bitmap_add) {
					Intent intent = new Intent(Intent.ACTION_PICK);
					intent.setType("image/*");
					// intent.putExtra("crop", true);
					// intent.putExtra("return-data", true);
					startActivityForResult(intent, 2);
				} else {
					arr_pic_uri.remove(position);
					arr.remove(position);
					if (arr.size() == 3
							&& arr.get(arr.size() - 1) != bitmap_add) {
						arr.add(bitmap_add);
					}
					adapter.notifyDataSetChanged();
				}
			}
		});
		return view;
	}

	private void setRadioButtonUI() {
		if (isNew) {
			iv_old_publish.setImageResource(R.drawable.old);
			iv_new_publish.setImageResource(R.drawable.new_);
		} else {
			iv_old_publish.setImageResource(R.drawable.new_);
			iv_new_publish.setImageResource(R.drawable.old);
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// 缩略图
		// Uri uri = data.getParcelableExtra("data");
		if (resultCode == 0) {
			return;
		}
		Uri uri = data.getData();
		arr_pic_uri.add(uri);
		try {
			InputStream is = getActivity().getContentResolver()
					.openInputStream(uri);
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			bitmap = zoomImg(bitmap, 215, 215);
			bitmap = toRoundCorner(bitmap, 20);
			arr.add(arr.size() - 1, bitmap);
			if (arr.size() == 5) {
				arr.remove(arr.size() - 1);
			}
			adapter.notifyDataSetChanged();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// iv_selectPic.setImageURI(uri);
		// Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

	}

	public void uploadPic() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(UPLOAD);
				for (int i = arr_pic_uri.size() - 1; i >= 0; i--) {
					Uri uri = arr_pic_uri.get(i);
					Map<String,String> post_param = new HashMap<String,String>();
					// BASE64Encoder encoder = new BASE64Encoder();
					StringBuilder pictureBuffer = null;
					try {
						pictureBuffer = new StringBuilder();
						String[] proj = { MediaColumns.DATA };
						Cursor actualimagecursor = getActivity().managedQuery(
								uri, proj, null, null, null);
						int actual_image_column_index = actualimagecursor
								.getColumnIndexOrThrow(MediaColumns.DATA);
						actualimagecursor.moveToFirst();
						String img_path = actualimagecursor
								.getString(actual_image_column_index);
						File file = new File(img_path);
						post_param.put("resource_name",
								"picture_" + System.currentTimeMillis());
						Bitmap bitmap = BitmapFactory.decodeFile(file
								.toString());
						InputStream input = new FileInputStream(file);
						ByteArrayOutputStream out = (ByteArrayOutputStream) compressImage(bitmap);
						// byte[] temp = new byte[1024];
						// for (int len = input.read(temp); len != -1; len =
						// input
						// .read(temp)) {
						// out.write(temp, 0, len);
						// pictureBuffer.append(new String(Base64.encode(out
						// .toByteArray(),Base64.DEFAULT)));
						// // out(pictureBuffer.toString());
						// out.reset();
						// }
						pictureBuffer.append(new String(Base64.encode(
								out.toByteArray(), Base64.DEFAULT)));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// System.out.println(pictureBuffer.toString());
					post_param.put("resource_data",
							pictureBuffer.toString());
					System.out.println("正在上传...");
					util.doPost("http://" + ip + "/resources/upload", post_param, new OnRecieveListener() {
						
						@Override
						public void onRecieveMessage(int state, String response) {
							// TODO Auto-generated method stub
							switch (state) {
							case AsyncHttpUtil.POST_RESPONSE:
								System.out.println(response);
								break;
							case AsyncHttpUtil.POST_ERROR:
								System.out.println(response);
								break;
							default:
								System.out.println(response);
								break;
							}

						}
					});
				}
			}
		}).start();
	}

	public void uploadAudio() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				ArrayList<BasicNameValuePair> arr_post = new ArrayList<BasicNameValuePair>();
				File file = new File("/sdcard/test.amr");
				InputStream input = null;
				try {
					input = new FileInputStream(file);
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					for (int len = input.read(buffer); len != -1; len = input
							.read(buffer)) {
						output.write(buffer, 0, len);
					}
					arr_post.add(new BasicNameValuePair("resource_name",
							"audio_" + System.currentTimeMillis()));
					arr_post.add(new BasicNameValuePair("resource_data",
							new String(Base64.encode(output.toByteArray(),
									Base64.DEFAULT))));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				HttpUtil.doPost("http://" + ip + "/resources/upload", arr_post,
//						handler);
			}
		}).start();
	}

	private OutputStream compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.out.println("正在压缩...");
		image.compress(Bitmap.CompressFormat.JPEG, 1, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) {
			// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
			if (options <= 0) {
				options = 10;
				break;
			}
		}
		return baos;
	}

	/**
	 * 获取圆角位图的方法
	 * 
	 * @param bitmap
	 *            需要转化成圆角的位图
	 * @param pixels
	 *            圆角的度数，数值越大，圆角越大
	 * @return 处理后的圆角位图
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 处理图片
	 * 
	 * @param bm
	 *            所要转换的bitmap
	 * @param newWidth新的宽
	 * @param newHeight新的高
	 * @return 指定宽高的bitmap
	 */
	public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片 www.2cto.com
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);
		return newbm;
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == AsyncHttpUtil.POST_RESPONSE) {
				String str = msg.getData().getString("response");
				parseJSON(str);
			} else if (msg.what == AsyncHttpUtil.POST_ERROR) {
				uploadFailed();
			} else if (msg.what == UPLOAD) {
				// Toast.makeText(getActivity(), "正在上传...请稍后",
				// Toast.LENGTH_LONG).show();
				btn_publish.setEnabled(false);
				btn_publish.setText("上传中...");
			} else if (msg.what == START_RECORD) {
				tv_audio.setText("停止录音");
			} else if (msg.what == STOP_RECORD) {
				tv_audio.setText("开始录音");
			} else if (msg.what == CONDITION_DISCONTENT) {
				Toast.makeText(getActivity(), "没有添加图片或音频", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	private void uploadFailed() {
		initVariable();
		Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_SHORT).show();
		btn_publish.setText("上传失败...");
		Timer timer = new Timer();
		// TimerTask timerTask = new TimerTask() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		//
		// }
		// };
		// timer.schedule(timerTask,2000);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		btn_publish.setEnabled(true);
		btn_publish.setText("发布");
	}

	private void initVariable() {
		picture_upload_count = 0;
		isPressRecord = false;
		isUploadFinished = false;
		isUploadFinished = false;
	}

	private void parseJSON(String str) {
		System.out.println("PublishFragment.parseJSON()" + str);
		try {
			JSONTokener jsonTokener = new JSONTokener(str);
			JSONObject jsonObj = (JSONObject) jsonTokener.nextValue();
			String api_name = jsonObj.getString("api_name");
			String status = jsonObj.getString("status");
			if ("/resources/upload".equals(api_name)) {
				if (status.equals("ok")) {
					if (isPicUploadFnishedAndNeedToAddAudioUrl) // 图片上传完成.语音上传也完成,待添加语音url
					{
						url_audio = jsonObj.getString("url");
						if (url_audio == null) {
							uploadFailed();
							return;
						}
						isUploadFinished = true;
					} else {
						url_pic = jsonObj.getString("url");
						if (url_pic == null) {
							uploadFailed();
							return;
						}
						picture_upload_count++;
						if (picture_upload_count == arr_pic_uri.size()) {
							uploadAudio();
							isPicUploadFnishedAndNeedToAddAudioUrl = true;
						}
					}
					if (isUploadFinished) {
						ArrayList<BasicNameValuePair> arr = new ArrayList<BasicNameValuePair>();
						arr.add(new BasicNameValuePair("goods_name",
								et_goodsname.getText().toString()));
						arr.add(new BasicNameValuePair("goods_price",
								et_goodsprice.getText().toString()));
						arr.add(new BasicNameValuePair("goods_description",
								new String(et_goodsdescription.getText()
										.toString().getBytes(), "UTF-8")));
						arr.add(new BasicNameValuePair("goods_type", "null"));
						arr.add(new BasicNameValuePair("goods_isnew", isNew
								+ ""));
						arr.add(new BasicNameValuePair("goods_image", url_pic));
						arr.add(new BasicNameValuePair("goods_record",
								url_audio));
						arr.add(new BasicNameValuePair("goods_owner",
								getActivity().getIntent().getStringExtra(
										"username")));
						arr.add(new BasicNameValuePair("goods_user_uid", "null"));
//						HttpUtil.doPost("http://" + ip + "/goods/publish", arr,
//								handler);
					}
				} else {
					uploadFailed();
				}
			} else if ("/goods/publish".equals(api_name)) {

				if (status.equals("ok")) {
					uploadSuccess();
//					MainActivity mainActivity = (MainActivity) getActivity();
//					mainActivity.switchContent(new MarketFragment());
//					String id = jsonObj.getString("id");
//					Bundle bundle_ = getActivity().getIntent().getExtras();
//					Bundle bundle = new Bundle();
//					bundle.putString("id", id);
//					bundle.putString("username", bundle_.getString("username"));
//					bundle.putString("uid", bundle_.getString("uid"));
//					Intent intent = new Intent(getActivity(),
//							GoodsDetailActivity.class);
//					intent.putExtras(bundle);
//					startActivity(intent);
				} else {
					uploadFailed();
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			uploadFailed();
			System.out.println("JSON失败");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			uploadFailed();
			System.out.println("编码64失败");
			e.printStackTrace();
		}
	}

	private void uploadSuccess() {
		initVariable();
		Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_LONG).show();
		btn_publish.setEnabled(true);
		btn_publish.setText("发布");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btn_publish) {
			// 图片数量大于一
			if (arr_pic_uri.size() >= 1 && isPressRecord) {
				uploadPic();
			} else {
				handler.sendEmptyMessage(CONDITION_DISCONTENT);
			}
		}
	}
}