package com.example.weather.http;

public interface HttpCallbackListener {
	void onFinish(String response);
	void onError(Exception e);
}
