package com.jwzt.download.application;

import android.app.Application;
import android.content.Context;

public class JXJYApplication extends Application{


	private static Context mContext;//全局上下文对象






	@Override
	public void onCreate() {
		super.onCreate();
		mContext=this;

	}




	/*应用的上下文对象*/
	public static Context getContext(){
		return mContext;
	}








}
