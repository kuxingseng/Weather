package com.example.weather.activity;

import java.util.List;

import com.example.weather.R;
import com.example.weather.R.drawable;
import com.example.weather.R.id;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyPagerAdapter extends PagerAdapter {

	private List<View> viewList;  
	 private int mChildCount = 0;  
	
	public MyPagerAdapter(List<View> viewList){  
        this.viewList = viewList;  
    }
	
	@Override
	public int getCount() {
		return viewList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getItemPosition(Object object) {
		if ( mChildCount > 0) {  
	           mChildCount --;  
	           return POSITION_NONE;  
	    }  
		return super.getItemPosition(object);
	}
	
	 @Override  
     public void notifyDataSetChanged() {           
           mChildCount = getCount();  
           super.notifyDataSetChanged();  
     } 

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		if(viewList.size()>arg1)
			((ViewPager) arg0).removeView(viewList.get(arg1));
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(viewList.get(arg1));
		
//		Log.e("chenshuai", "instantiateItem"+arg1);
//		Toast.makeText(arg0.getContext(), "测试"+arg1, Toast.LENGTH_SHORT).show();
		
		
		//耗时且静态的元素在这里设置，系统会提前初始化下一个界面
//		TextView textCityName=(TextView)((View)viewList.get(arg1)).findViewById(R.id.page_name);
//		textCityName.setText("测试"+arg1);
//		ImageView pageBg=(ImageView)((View)viewList.get(arg1)).findViewById(R.id.page_bg);
//		if(arg1==0)		
//			pageBg.setBackgroundResource(R.drawable.bg_fog_day);
//		else if(arg1==1)		
//			pageBg.setBackgroundResource(R.drawable.bg_heavy_rain_night);
//		else if(arg1==2)		
//			pageBg.setBackgroundResource(R.drawable.bg_moderate_rain_day);
//		else
//			pageBg.setBackgroundResource(R.drawable.bg_na);
		
		return viewList.get(arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
//		Log.e("chenshuai", "startUpdate"+arg0.getId());
	}

	@Override
	public void finishUpdate(View arg0) {
//		Log.e("chenshuai", "finishUpdate"+arg0.getId());
	}

}
