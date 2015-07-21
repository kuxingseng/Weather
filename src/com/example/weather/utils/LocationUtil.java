package com.example.weather.utils;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;

import com.example.weather.MyApplication;
import com.example.weather.http.HttpCallbackListener;
import com.example.weather.http.HttpUtil;

public class LocationUtil {

	private static final int GET_CITYNAME=1;
	private static final int 	NOT_GET_CITYNAME=2;
	private static LocationCallbackListener myListener;
	
	public static void getCurrentCity(final LocationCallbackListener listener){
		myListener=listener;
		LocationManager locationManager = (LocationManager)MyApplication.getContext().getSystemService(Context.LOCATION_SERVICE);
		String provider;
		List<String> providerList = locationManager.getProviders(true);
		
		if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER;
		} else if (providerList.contains(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
		} else {
			// 当没有可用的位置提供器时，弹出Toast提示用户
			Message message = new Message();
			message.what=NOT_GET_CITYNAME;
			message.obj="定位功能未开启";
			handler.sendMessage(message);
			return;
		}
		
		Location location = locationManager.getLastKnownLocation(provider);
		if(location!=null){
			//LogUtil.e("chenshuai", "location is not null"+location.getLatitude()+" "+location.getLongitude());			
			
			//通过经纬度获取具体城市名称
			StringBuilder url = new StringBuilder();
			url.append("http://maps.googleapis.com/maps/api/geocode/json?latlng=");
			url.append(location.getLatitude()).append(",");
			url.append(location.getLongitude());
			url.append("&sensor=false");
			LogUtil.e("chenshuai", url.toString());
			
			//两种调用接口方式
			HttpUtil.sendHttpRequset(url.toString(), HttpUtil.HTTP_POST, new HttpCallbackListener() {
				
				@Override
				public void onFinish(String response) {
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONArray jsonArray = jsonObject.getJSONArray("results");
						
						if(jsonArray.length()>0){
							JSONObject jsonObject1 = jsonArray.getJSONObject(0);
							JSONArray jsonArray1 = jsonObject1.getJSONArray("address_components");
							
							//获取数组的第4个元素，其中为城市名称
							JSONObject jsonObject2 = jsonArray1.getJSONObject(4);
							Message message = new Message();
							message.what=GET_CITYNAME;
							message.obj=jsonObject2.getString("long_name");
							handler.sendMessage(message);
							//LogUtil.e("chenshuai", "cityName:"+jsonObject2.getString("long_name"));
						}else{
							Message message = new Message();
							message.what=NOT_GET_CITYNAME;
							message.obj="反向获取城市名称失败";
							handler.sendMessage(message);
						}
					} catch (JSONException e) {
						e.printStackTrace();
						Message message = new Message();
						message.what=NOT_GET_CITYNAME;
						message.obj="反向获取城市名称失败";
						handler.sendMessage(message);
					}				
				}
				
				@Override
				public void onError(Exception e) {
					Message message = new Message();
					message.what=NOT_GET_CITYNAME;
					message.obj="请求城市接口失败";
					handler.sendMessage(message);
				}
			});
		}else{
			Message message = new Message();
			message.what=NOT_GET_CITYNAME;
			message.obj="获取经纬度信息失败";
			handler.sendMessage(message);
		}
	}
	
	private static Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case GET_CITYNAME:
					LogUtil.e("chenshuai", "handler:"+(String)msg.obj);
					if(myListener!=null)
						myListener.onGetCityName((String)msg.obj);
					break;
				case NOT_GET_CITYNAME:
					LogUtil.e("chenshuai", "handler:"+(String)msg.obj);
					if(myListener!=null)
						myListener.onGetCityName(null);
					break;
				default:
					break;
				}
			}
	};
	
	/*
	new Thread(new Runnable() {
		
		@Override
		public void run() {
			
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url.toString());
			
			// 在请求消息头中指定语言，保证服务器会返回中文数据
			httpGet.addHeader("Accept-Language", "zh-CN");
			HttpResponse httpResponse;
			try {
				httpResponse = httpClient.execute(httpGet);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = httpResponse.getEntity();
					String response = EntityUtils.toString(entity, "utf-8");
					
					try {
						//LogUtil.e("chenshuai", response);
						JSONObject jsonObject = new JSONObject(response);
						JSONArray jsonArray = jsonObject.getJSONArray("results");
						
						if(jsonArray.length()>0){
							JSONObject jsonObject1 = jsonArray.getJSONObject(0);
							//LogUtil.e("chenshuai", jsonObject1.toString());
							JSONArray jsonArray1 = jsonObject1.getJSONArray("address_components");
							
							//获取数组的第5个元素，其中为城市名称
							JSONObject jsonObject2 = jsonArray1.getJSONObject(4);
							//LogUtil.e("chenshuai", jsonObject2.toString());
							cityName= jsonObject2.getString("long_name");
							LogUtil.e("chenshuai", jsonObject2.getString("long_name")+"   cityName:"+cityName);
							//Toast.makeText(MyApplication.getContext(), "当前所在城市:"+ jsonObject2.getString("long_name"), Toast.LENGTH_SHORT).show();
						}else{
							//Toast.makeText(MyApplication.getContext(), "获取定位信息失败，请手动选择城市", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
						//Toast.makeText(MyApplication.getContext(), "获取定位信息失败，请手动选择城市", Toast.LENGTH_SHORT).show();
					}
				}
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}				
			
		}
	}).start();
	*/	
}
