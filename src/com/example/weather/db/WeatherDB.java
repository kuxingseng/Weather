package com.example.weather.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.weather.model.City;
import com.example.weather.model.County;
import com.example.weather.model.Province;
import com.example.weather.model.SelectedCity;
import com.example.weather.utils.LogUtil;

public class WeatherDB {

	/**
	* 数据库名
	*/
	public static final String DB_NAME = "my_weather";
	/**
	* 数据库版本
	*/
	public static final int VERSION = 1;
	
	private static WeatherDB weatherDB;
	private SQLiteDatabase db;
	
	private Context context;
	
	//私有构造函数
	private WeatherDB(Context context){
		this.context=context;
	}
	
	/**
	 * 获取数据库静态实例
	 * @param context
	 * @return
	 */
	public synchronized static WeatherDB getInstance(Context context){
		if(weatherDB==null)
			weatherDB=new WeatherDB(context);
		
		return weatherDB;
	}
	
	/**
	 * 初始化数据库
	 */
	public void initDB(){
		WeatherOpenHelper openHelper = new WeatherOpenHelper(context, DB_NAME, null, VERSION, null);
		db = openHelper.getWritableDatabase();
	}
	
	/**
	 * 插入选中城市
	 * @param selectedCity
	 */
	public void insertSelectedCity(SelectedCity selectedCity){
		if(selectedCity!=null){
			try{
				ContentValues values = new ContentValues();
				values.put("city_name", selectedCity.getCityName());
				values.put("city_code", selectedCity.getCityCode());
				values.put("city_weather_type", selectedCity.getCityWeatherType());
				values.put("city_temp", selectedCity.getCityTemp());
				db.insert("SelectedCity", null, values);
			}catch(Exception e){
				LogUtil.e("chenshuai", "insert error:"+e);
			}
		}
	}
	
	/**
	 * 删除选中城市
	 * @param cityCode
	 */
	public void deleteSelectedCity(String cityCode){
		try{
			db.delete("SelectedCity", "city_code=?", new String[]{cityCode});
		}catch(Exception e){
			LogUtil.e("chenshuai", "delete error:"+e);
		}
	}
	
	/**
	 * 更新选中城市当前天气状况
	 * @param cityCode
	 * @param weatherType
	 * @param cityTemp
	 */
	public void updateSelectedCity(String cityCode,int weatherType, String cityTemp){
		try{
			ContentValues values = new ContentValues();
			values.put("city_weather_type", weatherType);
			values.put("city_temp", cityTemp);
			db.update("SelectedCity", values, "city_code=?", new String[]{cityCode});
		}catch(Exception e){
			LogUtil.e("chenshuai", "update error:"+e);
		}
	}
	
	/**
	 * 读取所有选中城市列表
	 * @return
	 */
	public List<SelectedCity> loadAllSelectedCitys(){
		List<SelectedCity> list = new ArrayList<SelectedCity>();
		Cursor cursor = db.query("SelectedCity", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				SelectedCity selectedCity = new SelectedCity();
				selectedCity.setId(cursor.getInt(cursor.getColumnIndex("id")));
				selectedCity.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				selectedCity.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				selectedCity.setCityWeatherType(cursor.getInt(cursor.getColumnIndex("city_weather_type")));
				selectedCity.setCityTemp(cursor.getString(cursor.getColumnIndex("city_temp")));
				list.add(selectedCity);
			} while (cursor.moveToNext());
		}
		return list;
	}
	
	//-------------------------------------------------------------------------------------------------------------------------
	/**
	* 将Province实例存储到数据库。
	*/
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	
	/**
	* 从数据库读取全国所有的省份信息。
	*/
	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		return list;
	}
	
	/**
	* 将City实例存储到数据库。
	*/
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			db.insert("City", null, values);
		}
	}
	
	/**
	* 从数据库读取某省下所有的城市信息。
	*/
	public List<City> loadCities() {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, null,null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor	.getColumnIndex("city_code")));
				list.add(city);
			} while (cursor.moveToNext());
		}
		return list;
	}
	
	/**
	* 将County实例存储到数据库。
	*/
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			db.insert("County", null, values);
		}	
	}
	
	/**
	* 从数据库读取某城市下所有的县信息。
	*/
	public List<County> loadCounties(int cityId) {
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id = ?",
		new String[] { String.valueOf(cityId) }, null, null, null);
		if (cursor.moveToFirst()) {
		do {
			County county = new County();
			county.setId(cursor.getInt(cursor.getColumnIndex("id")));
			county.setCountyName(cursor.getString(cursor
			.getColumnIndex("county_name")));
			county.setCountyCode(cursor.getString(cursor
			.getColumnIndex("county_code")));
			list.add(county);
			} while (cursor.moveToNext());	
		}
		return list;
	}
}
