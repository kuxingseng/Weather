package com.example.weather.db;

import java.util.List;

import android.text.TextUtils;

import com.example.weather.MyApplication;
import com.example.weather.http.HttpCallbackListener;
import com.example.weather.http.HttpUtil;
import com.example.weather.model.City;
import com.example.weather.model.County;
import com.example.weather.model.Province;
import com.example.weather.utils.LogUtil;

public class InitDBDataUtil {
	
	public synchronized static void queryFromServer(final String code, final String type){
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
//			address = "http://maps.googleapis.com/maps/api/geocode/json?latlng=31.235707,121.417489&sensor=false";
		}
		LogUtil.e("chenshuai", "address:"+address);
		HttpUtil.sendHttpRequset(address, HttpUtil.HTTP_GET, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				if ("province".equals(type)) {
					handleProvincesResponse(response);
					//循环省列表加载城市
					List<Province> provinceList = WeatherDB.getInstance(MyApplication.getContext()).loadProvinces();
					if (provinceList.size() > 0) {
						for (Province province : provinceList) {
							LogUtil.e("chenshuai", "provinceName:"+province.getProvinceName()+"provinceCode:"+province.getProvinceCode());
							queryFromServer(province.getProvinceCode(),"city");
						}
					}
				} else if ("city".equals(type)) {
					//循环加载城市列表
					handleCitiesResponse(response);
					//循环省列表加载城市
					List<City> cityList = WeatherDB.getInstance(MyApplication.getContext()).loadCities();
					if (cityList.size() > 0) {
						for (City city : cityList) {
							LogUtil.e("chenshuai", "cityName:"+city.getCityName()+"cityCode:"+city.getCityCode());
						}
					}
					
				} else if ("county".equals(type)) {
					//循环加载县城列表
				}				
			}
			
			@Override
			public void onError(Exception e) {
				LogUtil.e("chenshuai", "get province error"+e);
			}
		});
	}

	/**
	* 解析和处理服务器返回的省级数据
	*/
	public synchronized static boolean handleProvincesResponse(String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					// 将解析出来的数据存储到Province表
					WeatherDB.getInstance(MyApplication.getContext()).saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	* 解析和处理服务器返回的市级数据
	*/
	public static boolean handleCitiesResponse(String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String c : allCities) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					// 将解析出来的数据存储到City表
					WeatherDB.getInstance(MyApplication.getContext()).saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	* 解析和处理服务器返回的县级数据
	*/
	public static boolean handleCountiesResponse(String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String c : allCounties) {
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					// 将解析出来的数据存储到County表
					WeatherDB.getInstance(MyApplication.getContext()).saveCounty(county);	
				}
				return true;
			}
		}
		return false;
	}
}
