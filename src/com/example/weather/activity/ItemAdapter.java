package com.example.weather.activity;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.R;
import com.example.weather.db.WeatherDB;
import com.example.weather.model.SelectedCity;
import com.example.weather.utils.LogUtil;

public class ItemAdapter extends ArrayAdapter<SelectedCity>{

	private int resourceId;
	private ViewHolder viewHolder;
	private List<SelectedCity> viewItemList;
	private Context context;
	
	ImageView imageWeatherType;
	TextView textCityName;
	TextView textTemp;
	Button buttonDeleteFlag;
	Button buttonDelete;
	
	public ItemAdapter(Context context, int textViewResourceId,
			List<SelectedCity> lists) {
		super(context, textViewResourceId, lists);
		this.context=context;
		viewItemList=lists;
		resourceId=textViewResourceId;
	}
	
//	public void refresh(List<SelectedCity> lists){
//		viewItemList=lists;
//		notifyDataSetChanged();
//	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//获得当前item实例
		SelectedCity itemInfo = this.getItem(position);
		//LogUtil.e("chenshuai", "list item position:"+position+" "+itemInfo.getCityName()+" ");
		//获取自定义列表项 convertView是上一次显示的缓存，没必要每次都重新加载布局文件
		//同时将view中的组件存储起来，不要每次重新find
		View view;		
		if(convertView==null){
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);	
			viewHolder=new ViewHolder();
			viewHolder.imageWeatherType= (ImageView)view.findViewById(R.id.editor_city_weather_type);
			viewHolder.textCityName=(TextView)view.findViewById(R.id.editor_city_cityname);
			viewHolder.textTemp=(TextView)view.findViewById(R.id.editor_city_temp);
			viewHolder.buttonDeleteFlag=(Button)view.findViewById(R.id.editor_city_delete_flag);
			viewHolder.buttonDelete=(Button)view.findViewById(R.id.editor_city_delete_button);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder=(ViewHolder)view.getTag();
		}
				
		//填充内容
		int imageId;
		switch(itemInfo.getCityWeatherType()){
			case 1:
				imageId=R.drawable.w0;
				break;
			case 2:
				imageId=R.drawable.w1;
				break;
			case 3:
				imageId=R.drawable.w2;
				break;
			case 4:
				imageId=R.drawable.w3;
				break;
			case 5:
				imageId=R.drawable.w4;
				break;
			default:
				imageId=R.drawable.w5;
				break;
		}
		viewHolder.imageWeatherType.setImageResource(imageId);
		viewHolder.textCityName.setText(itemInfo.getCityName());
		viewHolder.textTemp.setText(itemInfo.getCityTemp());
		
		//按钮默认为不可见状态
		if(CityEditorActivity.isEditor){
			viewHolder.buttonDeleteFlag.setVisibility(View.VISIBLE);
			if((Boolean)viewHolder.buttonDeleteFlag.getTag()){
				
			}
				
		}else
			viewHolder.buttonDeleteFlag.setVisibility(View.GONE);
		viewHolder.buttonDelete.setVisibility(View.GONE);
		viewHolder.buttonDeleteFlag.setTag(R.id.btn_flag_first_tag,viewHolder);
		viewHolder.buttonDeleteFlag.setTag(R.id.btn_flag_secend_tag,false);
		viewHolder.buttonDelete.setTag(itemInfo.getCityCode());
		
		viewHolder.buttonDeleteFlag.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(((Boolean)v.getTag(R.id.btn_flag_secend_tag))==false){
					((ViewHolder)v.getTag(R.id.btn_flag_first_tag)).buttonDelete.setVisibility(View.VISIBLE);
					((ViewHolder)v.getTag(R.id.btn_flag_first_tag)).buttonDeleteFlag.setBackgroundResource(R.drawable.city_delete_rotate);
					((ViewHolder)v.getTag(R.id.btn_flag_first_tag)).buttonDeleteFlag.setTag(R.id.btn_flag_secend_tag,true);
				}else{
					((ViewHolder)v.getTag(R.id.btn_flag_first_tag)).buttonDelete.setVisibility(View.GONE);
					((ViewHolder)v.getTag(R.id.btn_flag_first_tag)).buttonDeleteFlag.setBackgroundResource(R.drawable.city_delete_normal);
					((ViewHolder)v.getTag(R.id.btn_flag_first_tag)).buttonDeleteFlag.setTag(R.id.btn_flag_secend_tag,false);
				}
			}
		});
		viewHolder.buttonDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(v.getContext(), "删除城市id："+v.getTag(), Toast.LENGTH_SHORT).show();
				WeatherDB.getInstance(context).deleteSelectedCity(v.getTag().toString());
				//LogUtil.e("chenshuai", v.getTag().toString());
				((CityEditorActivity)context).initData(true);
			}
		});
		
		return view;
	}
}
//public interface item
class ViewHolder{
	ImageView imageWeatherType;
	TextView textCityName;
	TextView textTemp;
	Button buttonDeleteFlag;
	Button buttonDelete;
}
