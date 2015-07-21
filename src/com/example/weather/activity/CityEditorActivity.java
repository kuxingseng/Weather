package com.example.weather.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.inputmethodservice.Keyboard.Key;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.weather.R;
import com.example.weather.db.WeatherDB;
import com.example.weather.model.SelectedCity;

public class CityEditorActivity extends Activity implements OnClickListener {
	
	private List<SelectedCity> selectedCitys;
	private ListView listView;
	ItemAdapter adapter;
	
	public static boolean isEditor=false;	//是否处于编辑状态
	
	private boolean isEdited;	//是否被删除过
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.city_editor);		
				
		initView();
		initData(false);
	}
	
	public void initData(boolean isEdited){
		this.isEdited=isEdited;
		//LogUtil.e("chenshuai", "init list data");
		selectedCitys=WeatherDB.getInstance(this).loadAllSelectedCitys();		
		adapter = new ItemAdapter(this, R.layout.editor_citylist_item, selectedCitys);
		listView.setAdapter(adapter);
		
		if(selectedCitys.size()<=0){
			Toast.makeText(this, "暂时没有选定城市，请按‘+’添加城市", Toast.LENGTH_LONG).show();
		}
	}
	
	private void initView(){
		isEditor=false;
		
		listView=(ListView)findViewById(R.id.city_editor_listview);
		//相应点击事件-需要在item的relativelayout中添加属性：android:descendantFocusability="blocksDescendants"
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
			int position, long id) {
				SelectedCity item = selectedCitys.get(position);
				//Toast.makeText(CityEditorActivity.this, item.getCityName()+" "+position,Toast.LENGTH_SHORT).show();
				//点击打开指定城市天气预报页面
				Intent intent = new Intent(CityEditorActivity.this,MainActivity.class);
				intent.putExtra("fromEditCity", position);
				startActivity(intent);
				
				//finish();	
			}
		});
		
		Button btnEditor = (Button)findViewById(R.id.city_btn_editorcity);
		Button btnBack = (Button)findViewById(R.id.city_btn_back);
		Button btnAdd = (Button)findViewById(R.id.city_btn_addcity);
		btnEditor.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnAdd.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.city_btn_addcity:
				startActivity(new Intent(this,CityListActivity.class));
				break;
			case R.id.city_btn_editorcity:
				if(isEditor==false)
					isEditor=true;
				else
					isEditor=false;
				//获得列表项
				View itemView=null;
				for(int i=0;i<listView.getChildCount();i++){
					itemView=listView.getChildAt(i);
					if(itemView!=null){
						Button btnDeleteFlag = (Button)itemView.findViewById(R.id.editor_city_delete_flag);
						Button btnDelete = (Button)itemView.findViewById(R.id.editor_city_delete_button);
						if(isEditor){
							btnDeleteFlag.setVisibility(View.VISIBLE);
							btnDeleteFlag.setTag(R.id.btn_flag_secend_tag,false);
						}else{
							btnDeleteFlag.setVisibility(View.GONE);
							btnDeleteFlag.setBackgroundResource(R.drawable.city_delete_normal);
							btnDelete.setVisibility(View.GONE);
						}
					}
				}
				break;
			case R.id.city_btn_back:
				Intent intent = new Intent();
				intent.putExtra("isNeedFresh", isEdited);
				setResult(RESULT_OK, intent);
				finish();
				break;
			default:break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			Intent intent = new Intent();
			intent.putExtra("isNeedFresh", isEdited);
			setResult(RESULT_OK, intent);
		}
		return super.onKeyDown(keyCode, event);
	}
	
	

}
