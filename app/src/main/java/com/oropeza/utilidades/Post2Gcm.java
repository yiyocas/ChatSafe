package com.oropeza.utilidades;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.StrictMode;
import android.util.Log;

public class Post2Gcm {

	private static Post2Gcm post2Gcm = new Post2Gcm( );
	private static String apiKey = "AIzaSyALG91RSYzb_nuBKfDwn32SOsbGbktDyfU";

	private Post2Gcm( ){

	}

	 public static String post(String Id_divice,String IDCONVERSACION, String QUIEN, String MENSAJE, String FECHA, String HORA, String ACTIVIDAD){
	 
		 StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();  
		 StrictMode.setThreadPolicy(policy);
		 
		 HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(
					"https://android.googleapis.com/gcm/send");			
			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("registration_id",Id_divice));

				nameValuePairs.add(new BasicNameValuePair("data1",IDCONVERSACION));
				nameValuePairs.add(new BasicNameValuePair("data2",QUIEN));
				nameValuePairs.add(new BasicNameValuePair("data3",MENSAJE));
				nameValuePairs.add(new BasicNameValuePair("data4", FECHA));
				nameValuePairs.add(new BasicNameValuePair("data5",HORA));
				nameValuePairs.add(new BasicNameValuePair("data6",ACTIVIDAD));


				post.setHeader("Authorization","key="+apiKey);
				post.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
				
				
				

				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = client.execute(post);
				InputStreamReader inputst = new InputStreamReader(response.getEntity().getContent());
				BufferedReader rd = new BufferedReader(inputst);
					

				String line = "";
				String s ="";
				while ((line = rd.readLine()) != null) {
					Log.e("HttpResponse", line);
					
						
						s = line.substring(0);
						Log.i("GCM response",s);

				}

				return s;
			
			} catch (IOException e) {
				e.printStackTrace();
				return e.getMessage();
			}
		}
	}
