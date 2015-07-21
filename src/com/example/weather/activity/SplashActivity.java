package com.example.weather.activity;

import com.example.weather.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.splash);
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				//两秒后打开主界面
				Intent intent = new Intent(SplashActivity.this,MainActivity.class);
				SplashActivity.this.startActivity(intent);
				
				//关闭自身
				SplashActivity.this.finish();
			}
		}, 500);
	}

}
