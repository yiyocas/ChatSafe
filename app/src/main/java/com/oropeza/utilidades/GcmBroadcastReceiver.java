/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oropeza.utilidades;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {


		Bundle bun = intent.getExtras();
		Intent msgrcv = new Intent("Msg");
		Log.i("HHHHHHHHH",bun.getString("data1"));
		msgrcv.putExtra("data1",bun.getString("data1"));
		msgrcv.putExtra("data2",bun.getString("data2"));
		msgrcv.putExtra("data3",bun.getString("data3"));
		msgrcv.putExtra("data4",bun.getString("data4"));
		msgrcv.putExtra("data5",bun.getString("data5"));
		msgrcv.putExtra("data6",bun.getString("data6"));

		LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
		ComponentName comp = new ComponentName(context.getPackageName(),GcmIntentService.class.getName());
		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
	}

}