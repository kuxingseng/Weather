package com.example.weather;

import com.example.weather.db.WeatherDB;
import com.example.weather.utils.LogUtil;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class MyApplication extends Application {

	private static Context context;
	@Override
	public void onCreate() {
		super.onCreate();
		//获得应用程序上下文
		context=this.getApplicationContext();
		//初始化数据库
		WeatherDB.getInstance(context).initDB();
	}
	
	/**
	 * 获取应用程序上下文
	 * @return
	 */
	public static Context getContext(){
		return context;
	}

}
