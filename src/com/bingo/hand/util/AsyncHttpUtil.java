package com.bingo.hand.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class AsyncHttpUtil {
	public static final int POST_RESPONSE = 167162;
	public static final int POST_ERROR = 167163;
	public static final int GET_RESPONSE = 167164;
	public static final int GET_ERROR = 167165;
	private OnRecieveListener mListener;

	public void setOnRecieveListener(OnRecieveListener mListener) {
		this.mListener = mListener;
	}

	private Handler handler = new Handler();

	/**
	 * android Post请求,handler发送的Message格式:response[服务器回应内容]
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param postParam
	 *            POST请求的键值对
	 * @param  callback
	 *            回调函数,需重写onRecieveMessage
	 */
	public void doPost(final String url, final Map<String, String> postParam,
			final OnRecieveListener callback) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpPost httpRequest = new HttpPost(url);
				List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
				Set<String> keySet = postParam.keySet();
				Iterator<String> iterator = keySet.iterator();
				while (iterator.hasNext()) {
					String keyString = iterator.next();
					nvps.add(new BasicNameValuePair(keyString, postParam
							.get(keyString)));
				}
				try {
					// httpRequest.setHeader("Content-Type",
					// "application/x-www-form-urlencoded;charset=utf-8");
					httpRequest.setEntity(new UrlEncodedFormEntity(nvps,
							"UTF-8"));
					HttpClient httpClient = new DefaultHttpClient();
					httpClient.getParams().setParameter(
							CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
					HttpResponse httpResponse = httpClient.execute(httpRequest);
					if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						final String str = EntityUtils.toString(
								httpResponse.getEntity(), "UTF-8");
						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								callback.onRecieveMessage(POST_RESPONSE, str);
							}
						});
					}
				} catch (final ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							callback.onRecieveMessage(POST_ERROR,
									e.getMessage());
						}
					});
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							callback.onRecieveMessage(POST_ERROR,
									e.getMessage());
						}
					});
				}
			}
		};
		new Thread(runnable).start();
	}

	/**
	 * 
	 * @param url
	 *            统一资源定位符
	 * @param callback
	 *            回调函数,需重写onRecieveMessage
	 */
	public void doGet(final String url, final OnRecieveListener callback) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					URL url_ = new URL(url);
					HttpURLConnection conn = (HttpURLConnection) url_
							.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(5000);
					// 此InputStream不返回消息头
					InputStream is = conn.getInputStream();
					final ByteArrayOutputStream os = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int len = -1;
					while ((len = is.read(buffer)) != -1) {
						os.write(buffer, 0, len);
					}
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							callback.onRecieveMessage(GET_RESPONSE, new String(
									os.toByteArray()));
						}
					});
				} catch (final MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							callback.onRecieveMessage(GET_ERROR, e.getMessage());
						}
					});
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							callback.onRecieveMessage(GET_ERROR, e.getMessage());
						}
					});
				}
			}
		}).start();

	}

	private static String getFileNameByUrl(String url) {
		// TODO Auto-generated method stub
		Pattern p = Pattern.compile("\\w+");
		Matcher m = p.matcher(url);
		String filename = "";
		while (m.find()) {
			String str = m.group();
			filename = str;
		}
		return filename;
	}

	public interface OnRecieveListener {
		/**
		 * 当服务器接受到消息
		 * 
		 * @param state
		 *            服务器返回的状态.可以是4个字段<br>
		 *            HttpUtil.POST_RESPONSE<br>
		 *            HttpUtil.POST_ERROR<br>
		 *            HttpUtil.GET_RESPONSE<br>
		 *            HttpUtil.GET_ERROR
		 * @param response
		 *            服务器回复的数据
		 */
		public void onRecieveMessage(int state, String response);
	}
}
