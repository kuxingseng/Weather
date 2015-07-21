package com.example.weather.db;

import com.example.weather.utils.LogUtil;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherOpenHelper extends SQLiteOpenHelper {

	/**
	* Province表建表语句
	*/
	public static final String CREATE_PROVINCE = "create table Province ("
			+ "id integer primary key autoincrement, "
			+ "province_name text, "
			+ "province_code text)";
	
	/**
	* City表建表语句
	*/
	public static final String CREATE_CITY = "create table City ("
			+ "id integer primary key autoincrement, "
			+ "city_name text, "
			+ "city_code text)";
	
	/**
	* County表建表语句
	*/
	public static final String CREATE_COUNTY = "create table County ("
			+ "id integer primary key autoincrement, "
			+ "county_name text, "
			+ "county_code text)";
	
	/**
	 * 创建当前选中城市列表表语句
	 */
	public static final String CREATE_SELECTED_CITY="create table SelectedCity("
			+"id integer primary key autoincrement,"
			+"city_name text,"
			+"city_code text,"
			+"city_weather_type integer,"
			+"city_temp text)";
	
	public WeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version,
			DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
//		db.execSQL(CREATE_PROVINCE);
//		db.execSQL(CREATE_CITY);
//		db.execSQL(CREATE_COUNTY);
		db.execSQL(CREATE_SELECTED_CITY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 用于升级数据库升级时操作，可通过版本号来判断如何操作

	}
	

}
