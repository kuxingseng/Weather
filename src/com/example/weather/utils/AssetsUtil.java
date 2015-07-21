package com.example.weather.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.example.weather.MyApplication;

public class AssetsUtil {

	public static String getAssetsConfig(){
		InputStreamReader inputReader;
		try {
			inputReader = new InputStreamReader(MyApplication.getContext().getResources().getAssets().open("localcity_new.txt"));
			BufferedReader bufReader = new BufferedReader(inputReader);
			
			StringBuilder result = new StringBuilder();
			String line;
			while((line=bufReader.readLine())!=null){
				result.append(line);
			}
			return result.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}
}
