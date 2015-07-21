package com.example.weather.model;

public class SelectedCity {
	private int id;
	private String cityName;
	private String cityCode;
	private int cityWeatherType;
	private String cityTemp;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public int getCityWeatherType() {
		return cityWeatherType;
	}
	public void setCityWeatherType(int cityWeatherType) {
		this.cityWeatherType = cityWeatherType;
	}
	public String getCityTemp() {
		return cityTemp;
	}
	public void setCityTemp(String cityTemp) {
		this.cityTemp = cityTemp;
	}	
}
