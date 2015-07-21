package com.example.weather.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	public static final String HTTP_GET = "GET";
	public static final String HTTP_POST = "POST";
	
	public static void sendHttpRequset(final String address, final String requestMethod,final HttpCallbackListener listener ){
		sendHttpRequset(address,requestMethod,listener,8000,8000);
	}
	
	public static void sendHttpRequset(final String address, final String requestMethod ,final HttpCallbackListener listener,final int connectTimeout, final int readTimeout){
		//LogUtil.e("chenshuai", "address:"+address);
		new Thread(new Runnable() {			
			@Override
			public void run() {
				HttpURLConnection connection=null;
				try{
					URL url = new URL(address);
					connection = (HttpURLConnection)url.openConnection();
					connection.setRequestMethod(requestMethod);
					connection.setConnectTimeout(connectTimeout);
					connection.setReadTimeout(readTimeout);
					connection.setDoInput(true);
					//connection.setDoOutput(true);
					//在请求消息头指定返回语言
					connection.addRequestProperty("Accept-Language", "zh-CN");
					
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					
					StringBuilder response = new StringBuilder();
					String line;
					while((line=reader.readLine())!=null){
						response.append(line);
					}
					
					if(listener!=null)
						listener.onFinish(response.toString());
				}catch(Exception e){
					if(listener!=null){
						listener.onError(e);
					}
				}finally{
					connection.disconnect();
				}
			}
		}).start();
		
	}
}
