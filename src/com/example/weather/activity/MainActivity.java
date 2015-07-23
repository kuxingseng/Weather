package com.example.weather.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.R;
import com.example.weather.db.WeatherDB;
import com.example.weather.http.HttpCallbackListener;
import com.example.weather.http.HttpUtil;
import com.example.weather.model.SelectedCity;
import com.example.weather.utils.LoadingUtil;
import com.example.weather.utils.LocationCallbackListener;
import com.example.weather.utils.LocationUtil;
import com.example.weather.utils.LogUtil;
import com.example.weather.utils.TimeUtil;

public class MainActivity extends Activity implements OnClickListener{

	//动态生成当前页面的标识点
	private ImageView[] imageViews;
	private ImageView imageView;
	
	//标志点容器
	private LinearLayout indicator_container;
	
	//页面容器
	private ViewPager viewPager;
	
	//页面列表
	private List<View> viewList = new ArrayList<View>();
	private LayoutInflater myInflater;
	private MyPagerAdapter pagerAdapter;
	
	//城市名称
	private TextView cityTitle;
	private TextView textDate;
	
	//按钮
	private Button btnCityList;
	private Button btnShare;
	
	//页面元素
	private TextView textPublishTime;
	private TextView textWeatherDesc;
	private TextView textTEMP;
	
	//用户选择的城市列表
	private int pageIndex;		//当前需要显示的城市页面
	private List<SelectedCity> selectedCitys;
	
	private boolean isFromAddCityActivity;	//是否是从添加城市页面来，若是定位到最后一个
	private boolean isNeedRefresh;		//是否需要重新初始化数据，若从城市编辑界面返回并且无删除操作，不需要刷新
	
	private long exitTime; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//要在setview之前设置
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_main);
		
		//模拟插入数据
//		SelectedCity selectedCity = new SelectedCity();
//		selectedCity.setCityName("上海");
//		selectedCity.setCityCode("101020100");
//		selectedCity.setCityWeatherType(1);
//		selectedCity.setCityTemp("24~28");
//		
//		SelectedCity selectedCity1 = new SelectedCity();
//		selectedCity1.setCityName("北京");
//		selectedCity1.setCityCode("101010100");
//		selectedCity1.setCityWeatherType(1);
//		selectedCity1.setCityTemp("24~28");
//		WeatherDB.getInstance(this).insertSelectedCity(selectedCity);
//		WeatherDB.getInstance(this).insertSelectedCity(selectedCity1);
//		
//		WeatherDB.getInstance(this).updateSelectedCity("101020100", 1, "10~12");
//		
//		WeatherDB.getInstance(this).deleteSelectedCity("1010101001");
		
		getExtraAndInit();
		//初始化界面
		initView();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		//当活动属性设置为singleTask时，再次启动页面并传递参数会调到这个方法，而不是onCreate
		setIntent(intent); 	//保存新的intent
		getExtraAndInit();
	}
	
	private void getExtraAndInit(){
		isNeedRefresh=true;
		//判断是否是从添加城市页面转过来，若是定位到列表的最后一页
		isFromAddCityActivity=getIntent().getBooleanExtra("fromAddCity", false);
		//如果是从编辑城市列表页面转过来，直接获得指定页数
		pageIndex=getIntent().getIntExtra("fromEditCity", 0);
	}

	private void initView(){
		viewPager=(ViewPager)findViewById(R.id.pages);
		myInflater = LayoutInflater.from(this);
		viewList=new ArrayList<View>();
		pagerAdapter= new MyPagerAdapter(viewList);
		viewPager.setAdapter(pagerAdapter);
		//设置当页面切换后的监听
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {				
			@Override
			public void onPageSelected(int arg0) {
				//设置当前选中页面标识
				pageIndex=arg0;
				 for (int i = 0; i < imageViews.length; i++) {
			         imageViews[arg0].setBackgroundResource(R.drawable.splash_indicator_focused);  
			         if (arg0 != i) {  
			             imageViews[i].setBackgroundResource(R.drawable.splash_indicator);  
			         }  
			     }
				 //设置城市气象详情			        
				setPageDetail(arg0);
			}
					
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
					
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
		
		indicator_container=(LinearLayout)findViewById(R.id.page_indicator_container);
		cityTitle=(TextView)findViewById(R.id.text_title);
		cityTitle.setText("N/A");
		btnCityList=(Button)findViewById(R.id.btn_city_list);
		btnShare=(Button)findViewById(R.id.btn_share);
		btnCityList.setOnClickListener(this);
		btnShare.setOnClickListener(this);
		
		textDate=(TextView)findViewById(R.id.text_date);
		textDate.setText(TimeUtil.getCurrentDate());
	}

	private void initData(){
		//首先读取本地数据库，判断当前是否有选中城市，
		//若没有自动定位城市，若有则直接读取当前城市列表以及上次选中城市
		//根据当前选中城市数量生成初始化viewpager
		if(selectedCitys!=null && selectedCitys.size()>0)
			selectedCitys.clear();
				
		selectedCitys = WeatherDB.getInstance(this).loadAllSelectedCitys();
		if(selectedCitys.size()>0){
//			for(int i=0;i<selectedCitys.size();i++){
//				LogUtil.e("chenshuai", selectedCitys.get(i).getCityName()+selectedCitys.get(i).getCityCode()+" "+selectedCitys.get(i).getCityTemp());
//			}
			initViewPager();
		}else{
			//没有默认选中城市，自动定位
			LogUtil.e("chenshuai", "没有默认选中城市，启动自动定位");
			LoadingUtil.showLoading(this, "定位中...");
			LocationUtil.getCurrentCity(new LocationCallbackListener() {			
				@Override
				public void onGetCityName(String cityName) {
					LoadingUtil.hideLoading();
					if(cityName!=null){
						//同时把定位的记录加入到当前选中城市表
						SelectedCity selectedCity = new SelectedCity();
						selectedCity.setCityName(cityName);
						selectedCity.setCityCode("CN101020100");
						selectedCity.setCityWeatherType(1);
						selectedCity.setCityTemp("24~28");
						selectedCitys.add(selectedCity);
							
						//插入数据库
						WeatherDB.getInstance(MainActivity.this).insertSelectedCity(selectedCity);
								
						initViewPager();
					}else{
						//自动打开选择城市界面
						Toast.makeText(MainActivity.this, "定位失败，请手动选择城市", Toast.LENGTH_LONG).show();
						MainActivity.this.startActivity(new Intent(MainActivity.this,CityEditorActivity.class));                                                                                                                                                                                                                                                                                                                                                                                                 
					}
				}
			});
		}
	}
	
	private void initViewPager(){
		int i=0;	
		
		//初始化viewPager
		View view;
		if(viewList!=null && viewList.size()>0)
			viewList.clear();
		for(i=0;i<selectedCitys.size();i++){
			view=myInflater.inflate(R.layout.page_main, null);			
			//加入视图列表
			viewList.add(view);
		}
		pagerAdapter.notifyDataSetChanged();	//通知数据变化		
		
		//初始化也没标识列表
		if(isFromAddCityActivity)
			pageIndex=selectedCitys.size()-1;
		
		imageViews=new ImageView[selectedCitys.size()];
		indicator_container.removeAllViews();	//清除之前的子视图
		for(i=0;i<selectedCitys.size();i++){
			imageView = new ImageView(this);
			LayoutParams params = new LayoutParams(10,10);
			params.setMargins(5, 0, 5, 0);
			imageView.setLayoutParams(params);
			imageViews[i] = imageView;  
			
			// 默认选中第一张图片  
			if (i == pageIndex) {                 
				imageViews[i].setBackgroundResource(R.drawable.splash_indicator_focused);  
		    } else {  
		        imageViews[i].setBackgroundResource(R.drawable.splash_indicator);  
		    }     
			indicator_container.addView(imageViews[i]); 
		}
		//指定页面
		viewPager.setCurrentItem(pageIndex);
		//设置城市气象详情			  
		setPageDetail(pageIndex);
	}
	
	private void setPageDetail(int index){
		//默认为第一个城市名称
		cityTitle.setText(selectedCitys.get(index).getCityName());
		
		//通过城市名称获取城市天气预报并设置到界面中
		getWeatherByCityCode(index);
	}
	
	private String lowTemp;
	private String heighTemp;
	private String desc;
	private String refreshTime;
	private void getWeatherByCityCode(int index){
		LoadingUtil.showLoading(MainActivity.this, "更新天气信息...");
		//接口以废弃
		//String address = "http://www.weather.com.cn/adat/cityinfo/"+selectedCitys.get(index)+".html";
		//和风天气
		String address = "http://api.heweather.com/x3/weather?cityid="+selectedCitys.get(index).getCityCode()+"&key=68f3abe177f7451b9ffd05e5ccaaf0de";
		//LogUtil.e("chenshuai", "address:"+address);
		HttpUtil.sendHttpRequset(address, HttpUtil.HTTP_POST, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				//LogUtil.e("chenshuai", response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					JSONArray array=jsonObject.getJSONArray("HeWeather data service 3.0");
					JSONObject obj1 = array.getJSONObject(0);
					JSONObject obj2 = obj1.getJSONObject("basic");
					JSONObject obj3=obj2.getJSONObject("update");
					refreshTime=obj3.getString("loc");
					
					JSONArray dailyArray=obj1.getJSONArray("daily_forecast");
					JSONObject firstDailyObj=dailyArray.getJSONObject(0);
					JSONObject tempObj = firstDailyObj.getJSONObject("tmp");
					lowTemp=tempObj.getString("min");
					heighTemp=tempObj.getString("max");
					
					JSONObject condObj=firstDailyObj.getJSONObject("cond");
					desc=condObj.getString("txt_n");
					
					runOnUiThread(new Runnable() {
						public void run() {
							View view = (View)viewList.get(pageIndex);
							textPublishTime=(TextView)view.findViewById(R.id.page_publish_time);
							textPublishTime.setText("更新时间 "+refreshTime);
							
							textWeatherDesc=(TextView)view.findViewById(R.id.page_weather_desc);
							textWeatherDesc.setText(desc);
							
							textTEMP=(TextView)view.findViewById(R.id.page_TEMP);
							textTEMP.setText(lowTemp+"°C~"+heighTemp+"°C");
							
							Toast.makeText(MainActivity.this, "更新天气成功", Toast.LENGTH_SHORT).show();
							LoadingUtil.hideLoading();
						}
					});
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(MainActivity.this, "更新天气失败", Toast.LENGTH_SHORT).show();
							LoadingUtil.hideLoading();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch(view.getId())
		{
			case R.id.btn_city_list:
				btnCityList.setBackgroundResource(R.drawable.city_btn_pressed);
				//this.startActivity(new Intent(this,CityEditorActivity.class));
				this.startActivityForResult(new Intent(this,CityEditorActivity.class), 1);
				break;
			case R.id.btn_share:
				btnShare.setBackgroundResource(R.drawable.common_title_share_pressed);
				LoadingUtil.showLoading(MainActivity.this, "定位中...",0,10);
				LoadingUtil.showLoading(MainActivity.this, "定位中...",10,10);
				new Handler().postDelayed(new Runnable() {
					public void run() {
						LoadingUtil.hideLoading();
					}
				}, 2000);
				//Toast.makeText(this, "share", Toast.LENGTH_SHORT).show();
				break;
			default:break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case 1:
				if (resultCode == RESULT_OK) {
					isNeedRefresh=data.getBooleanExtra("isNeedFresh", true);
					LogUtil.e("chenshuai", "editor activity back ,is need refresh:"+isNeedRefresh);
				}
			break;
			default:break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(isNeedRefresh){
			//刷新城市列表
			initData();
		}
		
		btnCityList.setBackgroundResource(R.drawable.city_btn);
		btnShare.setBackgroundResource(R.drawable.common_title_share);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			
			if(System.currentTimeMillis()-exitTime>2000){
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				exitTime=System.currentTimeMillis();
			}else{
				finish();
				System.exit(0);
			}
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
