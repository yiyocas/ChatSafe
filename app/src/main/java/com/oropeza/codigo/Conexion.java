package com.oropeza.codigo;

import android.os.StrictMode;
import android.util.Log;

import com.oropeza.chatsafe.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Conexion {
	private MainActivity uno;
	HttpClient cliente;
	HttpPost post;	
	HttpEntity entidad;
	String[] respuestas;
	String aux;
	int r;
	
	public Conexion(String url){
		 HttpParams httpParameters = new BasicHttpParams();
		  HttpConnectionParams.setConnectionTimeout(httpParameters, 2000);
		  HttpConnectionParams.setSoTimeout(httpParameters, 2000);
		  ConnManagerParams.setTimeout(httpParameters, 2000);
		cliente = new DefaultHttpClient(httpParameters);
		post = new HttpPost(url);

		Log.i("New Conexion",url);
	}
	
	public String destroy(String arg){
		String[] aux = arg.split(" ");
		return aux[2];
	}
	
	public JSONArray leer(List<NameValuePair> Parametros){
		
		try {
			//soap para android
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

			StrictMode.setThreadPolicy(policy);

			post.setEntity(new UrlEncodedFormEntity(Parametros));
			HttpResponse resp = (HttpResponse) cliente.execute(post);
			String respstring = EntityUtils.toString(resp.getEntity());
			JSONObject json = new JSONObject(respstring);
			JSONArray respjson = json.getJSONArray("informacion");

		return respjson;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
//select sum(precio) from detalle_compra,platillos where detalle_compra.id_platillo=platillos.id_platillo and detalle_compra.id_compra=3;
	public void Escribri(List<NameValuePair> Parametros){
		

		
		try {

			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

			StrictMode.setThreadPolicy(policy);

			post.setEntity(new UrlEncodedFormEntity(Parametros));
			
			
			HttpResponse resp = (HttpResponse) cliente.execute(post);
			
			r = resp.getStatusLine().getStatusCode();
			System.out.println(r+" ResultadoS");
					
		}catch(Exception e){			
			System.out.println(r+" ResultadoR");
		}
			
		}
	
	
}
