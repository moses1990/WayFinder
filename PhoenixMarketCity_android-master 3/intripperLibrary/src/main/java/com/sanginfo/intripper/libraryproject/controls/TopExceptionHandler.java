package com.sanginfo.intripper.libraryproject.controls;

import android.content.Context;

public class TopExceptionHandler implements Thread.UncaughtExceptionHandler {

	private Context objApp;
	private static final String IMAGE_CACHE_DIR = "thumbs";

	public TopExceptionHandler(Context app) {
		objApp = app;
		Thread.getDefaultUncaughtExceptionHandler();
	}
	
	public TopExceptionHandler(){
		Thread.getDefaultUncaughtExceptionHandler();
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		try{
			StackTraceElement[] arr = ex.getStackTrace();
			String report = ex.toString();
			
			String strStack = "";
			strStack += "Stack trace: ";
			for (int i=0; i<arr.length; i++){
				strStack += "    "+arr[i].toString()+"\t";
			}

			// If the exception was thrown in a background thread inside
			// AsyncTask, then the actual exception can be found with getCause
			String strCause = "";
			strCause += "Cause: ";
			Throwable cause = ex.getCause();
			
			if (cause instanceof OutOfMemoryError){
				clearMemCache();
			}
		}
		catch(Exception ex1){
			System.err.println(ex1);
		}
	}
	private void clearMemCache(){
		try{
			System.gc();
		}
		catch(Exception ex){
			System.err.println(ex);
		}
	}

}
