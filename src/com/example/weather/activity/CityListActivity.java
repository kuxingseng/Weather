package com.example.weather.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.weather.R;
import com.example.weather.db.WeatherDB;
import com.example.weather.model.SelectedCity;
import com.example.weather.utils.AssetsUtil;

public class CityListActivity extends Activity implements OnClickListener{

	private Button searchButton;
	private EditText searchText;
	private ScrollView hotCityList;
	private LinearLayout hotCityListContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.city_list);		
		
		initView();
	}
	
	private void initView(){
		searchButton = (Button)findViewById(R.id.city_list_search_button);
		searchText = (EditText)findViewById(R.id.city_list_search_text);
		hotCityList = (ScrollView)findViewById(R.id.city_list_scrollview);
		hotCityListContent=(LinearLayout)findViewById(R.id.city_list_scrollview_content);
		
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(CityListActivity.this, searchText.getText(), Toast.LENGTH_SHORT).show();
			}
		});
		
		//动态生成热门城市列表
		//使用自定义界面方式循环加载 类.获取其中的子控件
		
		String hotCityList = AssetsUtil.getAssetsConfig();
		List<SelectedCity> hotCitys=new ArrayList<SelectedCity>();
		//LogUtil.e("chenshuai", "hotCityList:"+hotCityList);
		try {
			JSONObject jsonObject = new JSONObject(hotCityList);
			JSONArray list = jsonObject.getJSONArray("citylist");
			SelectedCity selectedCity;
			for(int i=0;i<list.length();i++){
				selectedCity=new SelectedCity();
				JSONObject city=list.getJSONObject(i);
				selectedCity.setCityName(city.getString("cityName"));
				selectedCity.setCityCode(city.getString("cityCode"));
				selectedCity.setCityWeatherType(city.getInt("cityWeatherType"));
				selectedCity.setCityTemp(city.getString("cityTemp"));
				
				hotCitys.add(selectedCity);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HotCityItemLayout hotCityItem;
		for(int i=0;i<hotCitys.size()/3;i++){
		
			hotCityItem = new HotCityItemLayout(this, null, new String[]{hotCitys.get(i*3).getCityName(),hotCitys.get(i*3+1).getCityName(),hotCitys.get(i*3+2).getCityName()});
			hotCityListContent.addView(hotCityItem);
			
			hotCityItem.firstBtn.setTag(hotCitys.get(i*3));
			hotCityItem.secendBtn.setTag(hotCitys.get(i*3+1));
			hotCityItem.thirdBtn.setTag(hotCitys.get(i*3+2));
			hotCityItem.firstBtn.setOnClickListener(this);
			hotCityItem.secendBtn.setOnClickListener(this);
			hotCityItem.thirdBtn.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		Toast.makeText(this, ((SelectedCity)v.getTag()).getCityName(), Toast.LENGTH_SHORT).show();
		//把选中城市添加至数据库并跳转到天气预报界面
		//Todo:设置已选择城市标志，并在再次选择时提示不可重复选择
		
		WeatherDB.getInstance(this).insertSelectedCity((SelectedCity)v.getTag());
		
		Intent intent = new Intent(this,MainActivity.class);
		intent.putExtra("fromAddCity", true);
		this.startActivity(intent);
		
		finish();
	}

}
