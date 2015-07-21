package com.example.weather.activity;

import com.example.weather.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

public class HotCityItemLayout extends LinearLayout {

	public Button firstBtn;
	public Button secendBtn;
	public Button thirdBtn;
	
	public HotCityItemLayout(Context context, AttributeSet attrs,String[] names) {
		super(context, attrs);

		//引入布局文件
		LayoutInflater.from(context).inflate(R.layout.hot_city_list_item, this);
		
		//初始界面元素
		firstBtn = (Button)findViewById(R.id.hot_city_list_item_button1);
		secendBtn = (Button)findViewById(R.id.hot_city_list_item_button2);
		thirdBtn = (Button)findViewById(R.id.hot_city_list_item_button3);
		
		firstBtn.setText(names[0]);
		secendBtn.setText(names[1]);
		thirdBtn.setText(names[2]);
	}

}
