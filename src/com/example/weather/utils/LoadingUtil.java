package com.example.weather.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.weather.R;

public class LoadingUtil {
	
	private static ProgressDialog dialog;
	private static TextView textDesc=null;
	/**
	 * 显示不含进度的加载操作页面
	 * @param context
	 * @param desc
	 */
	public static void showLoading(Context context, String desc){
		 showLoading(context,desc,0,0);
	 }
	
	/**
	 * 显示包含进度的加载操作页面
	 * @param context
	 * @param desc
	 * @param progress
	 * @param total
	 */
	public static void showLoading(final Context context, final String desc, final int progress,final int total){
		
		if(dialog==null||!dialog.isShowing()){
			dialog = new ProgressDialog(context);  
			View v = LayoutInflater.from(context).inflate(R.layout.loading, null);  
	        textDesc = (TextView) v.findViewById(R.id.loading_desc);  
	        dialog.show();  
	        dialog.setCancelable(false);
	        dialog.setContentView(v);  
		}
		if (dialog.isShowing() && desc != null && !desc.equals(""))  {
        	if(progress==0&&total==0)
        		textDesc.setText(desc);  
        	else
        		textDesc.setText(desc+" "+progress+"/"+total);  
        }	
	} 
	
	/**
	 * 隐藏进度条
	 */
	public static void hideLoading(){
		if(dialog!=null && dialog.isShowing()){  
			dialog.dismiss();  
			dialog=null;
			textDesc=null;
        }  
	}
}

	