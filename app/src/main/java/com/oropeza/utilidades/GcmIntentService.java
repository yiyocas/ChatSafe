/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oropeza.utilidades;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.oropeza.chatsafe.Conversacion;
import com.oropeza.chatsafe.MainActivity;
import com.oropeza.chatsafe.R;
import com.oropeza.chatsafe.Setting;
import com.oropeza.codigo.ConexionLocal;


import java.util.List;

public class GcmIntentService extends IntentService {

	private String IDCONVERSACION;
	private String QUIEN;
	private String MENSAJE;
	private String FECHA;
	private String HORA;
	private String ACTIVIDAD;

	private String ALIAS;
	private String KEY;


	public static final int NOTIFICATION_ID = 1;

	private Handler hdler;
	private static Class[ ] clase={MainActivity.class, Conversacion.class};

	NotificationCompat.Builder notification;
	NotificationManager manager;

	public GcmIntentService( ) {
		super("GcmIntentService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		hdler = new Handler( );
	}
	@Override
	protected void onHandleIntent(Intent intent) {

		Bundle bun = intent.getExtras( );

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);



		if (!bun.isEmpty()) {

			if (GoogleCloudMessaging.
					MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				Log.e("L2C", "Error");

			} else if (GoogleCloudMessaging.
					MESSAGE_TYPE_DELETED.equals(messageType)) {
				Log.e("L2C","Error");

			} else if (GoogleCloudMessaging.
					MESSAGE_TYPE_MESSAGE.equals(messageType)) {



			}
		}

		IDCONVERSACION 	= bun.getString("data1");
		QUIEN 			= bun.getString("data2");
		MENSAJE 		= bun.getString("data3");
		FECHA 			= bun.getString("data4");
		HORA 			= bun.getString("data5");
		ACTIVIDAD 		= bun.getString("data6");

		showToast();
		
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	
	public void showToast( ){
		hdler.post(new Runnable() {

			@Override
			public void run() {

				Toast.makeText(GcmIntentService.this, "Nuevo Mensaje ", Toast.LENGTH_LONG).show();
				if(!comprobarActivityALaVista(getApplicationContext(),Conversacion.class.getName())){
					if(ACTIVIDAD.equals("uno")){
						SetMensajeNuevo();
					}
					//sendNotification();
					setNotification(MENSAJE);
				}

			}
		});
	}

	
	private void sendNotification( ) {

		getInfoUserMSN();

		Bundle args = new Bundle();
		args.putString("Id_Conversacion", IDCONVERSACION);
		args.putString("quien", QUIEN);
		args.putString("Amigo", QUIEN);
		args.putString("Alias", ALIAS);
		args.putInt("IMG", R.drawable.camera_icon);
		args.putString("key", KEY);
		args.putString("mensaje", MENSAJE);
		args.putString("fecha", FECHA);
		args.putString("hora", HORA);
		Intent chat = new Intent(this, Conversacion.class);
		chat.putExtra("INFO", args);
		chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notification = new NotificationCompat.Builder(this);
		notification.setContentTitle("NUEVO MENSAJE");
		notification.setContentText(MENSAJE);
		notification.setTicker(MENSAJE);
		notification.setGroup(GROUP_KEY_EMAILS);
		notification.setGroupSummary(true);
		notification.setDefaults(
				Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND
						| Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_LIGHTS
		| Notification.FLAG_GROUP_SUMMARY | Notification.DEFAULT_VIBRATE);
		notification.setSmallIcon(R.drawable.ensroww);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 1000,
				chat, PendingIntent.FLAG_CANCEL_CURRENT);
		notification.setContentIntent(contentIntent);
		notification.setAutoCancel(true);
		manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(1, notification.build());
    }

	private boolean isOnline() {
	    ConnectivityManager cm = 
	         (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}

	private void getInfoUserMSN( ){
		ConexionLocal conn = new ConexionLocal(getApplicationContext());
		conn.abrir();
		Cursor c = conn.Consultar("SELECT * FROM contactos WHERE id_mail='" + QUIEN + "'");

		for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
			ALIAS = c.getString(c.getColumnIndex("alias"));
			KEY = c.getString(c.getColumnIndex("key"));
		}
		conn.cerrar();
	}

	public boolean comprobarActivityALaVista(
			Context context, String nombreClase){

		// Obtenemos nuestro manejador de activitys
		ActivityManager am = (ActivityManager)
				context.getSystemService(Context.ACTIVITY_SERVICE);
		// obtenemos la informacion de la tarea que se esta ejecutando
		// actualmente
		List< ActivityManager.RunningTaskInfo > taskInfo =
				am.getRunningTasks(1);
		// Creamos una variable donde vamos a almacenar
		// la activity que se encuentra a la vista
		String nombreClaseActual = null;

		try{
			// Creamos la variable donde vamos a guardar el objeto
			// del que vamos a tomar el nombre
			ComponentName componentName = null;
			// si pudimos obtener la tarea actual, vamos a intentar cargar
			// nuestro objeto
			if(taskInfo != null && taskInfo.get(0) != null){
				componentName = taskInfo.get(0).topActivity;
			}
			// Si pudimos cargar nuestro objeto, vamos a obtener
			// el nombre con el que vamos a comparar
			if(componentName != null){
				nombreClaseActual = componentName.getClassName();
			}

		}catch (NullPointerException e){

			Log.e("Error", "Error al tomar el nombre de la clase actual " + e);
			return false;
		}

		// devolvemos el resultado de la comparacion
		return nombreClase.equals(nombreClaseActual);
	}

	private void SetMensajeNuevo( ){

		ConexionLocal conn = new ConexionLocal(getApplicationContext());

		conn.abrir();

		SQLiteDatabase db = conn.getSQLiteDatabase();

		SQLiteStatement stm = conn.getSQLiteDatabase().compileStatement("INSERT INTO mensajes (id_conversacion,quien,mensaje,fecha,hora) VALUES (?,?,?,?,?)");
		stm.bindString(1,IDCONVERSACION);
		stm.bindString(2,QUIEN);
		stm.bindString(3,MENSAJE);
		stm.bindString(4,FECHA);
		stm.bindString(5,HORA);
		stm.execute();

		conn.cerrar();
	}
	int numMessages = 0;
	final static String GROUP_KEY_EMAILS = "group_key_emails";
	private void setNotification(String msg){

		manager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent myintent = new Intent(this, MainActivity.class);
		myintent.putExtra("message", msg);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				myintent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
						.setSmallIcon(R.drawable.camera_icon)
						.setContentTitle("GCM Notification")
						.setStyle(new NotificationCompat.BigTextStyle()
								.bigText(msg))
						.setContentText(msg);

		mBuilder.setContentIntent(contentIntent);
		manager.notify(NOTIFICATION_ID, mBuilder.build());
	}

}

/*
*
* int icon = R.drawable.camera_icon; // icon from resources
		CharSequence tickerText = "TickerText"; // ticker-text
		long when = System.currentTimeMillis(); // notification time
		Context context = getApplicationContext(); // application Context
		CharSequence contentTitle = "Titulo"; // expanded message title
		CharSequence contentText = msg; // expanded message text
		Intent notificationIntent = new Intent(this, MainActivity.class);

		Bundle xtra = new Bundle();
		xtra.putString("title", "Titulo");
		xtra.putString("message", msg);

		notificationIntent.putExtras(xtra);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_ONE_SHOT
						+ PendingIntent.FLAG_UPDATE_CURRENT);
		String ns = Context.NOTIFICATION_SERVICE;

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		Notification notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(context, contentTitle, contentText,   contentIntent);
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.FLAG_AUTO_CANCEL;
		notification.flags = Notification.DEFAULT_LIGHTS
				| Notification.FLAG_AUTO_CANCEL;
		final int HELLO_ID = 0;
		mNotificationManager.notify(HELLO_ID, notification);

* */
