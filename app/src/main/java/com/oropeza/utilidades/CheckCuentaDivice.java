package com.oropeza.utilidades;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.oropeza.codigo.ConexionLocal;

import java.io.IOException;

/**
 * Created by yiyo on 09/09/15.
 */
public class CheckCuentaDivice {


    final static String PROYECT_NUMBER="687524776273";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static CheckCuentaDivice ourInstance = new CheckCuentaDivice();
    static final String TAG = "CHATSAFE";

    public static CheckCuentaDivice getInstance() {
        return ourInstance;
    }

    private CheckCuentaDivice() {
    }

    public static boolean IsCuenta(Context context) {

        boolean resultSet = false;
        ConexionLocal local = new ConexionLocal(context);
        local.abrir();

        Cursor c = local.Consultar("SELECT * FROM usuario_divice");

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            resultSet = true;
        }

        local.cerrar();

        return resultSet;
    }

    public static boolean checkPlayServices(Context context, Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");

            }
            return false;
        }
        return true;
    }


    public static void RegistarDivice(final Context context){

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String ID_Divice="";
                GoogleCloudMessaging gcm = null;
                try {
                    if(gcm==null){
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                    ID_Divice = gcm.register(PROYECT_NUMBER);


                } catch (IOException e) {
                    ID_Divice = "Error: "+e.getMessage();
                }
                return ID_Divice;
            }

            @Override
            protected void onPostExecute(String result) {

                Preferencias prefs = new Preferencias(context);
                boolean okay = result.equals("Error: SERVICE_NOT_AVAILABLE") ? false:true;
                prefs.setID_DIVICE_Google(okay);
                if(!okay){
                    prefs.setID_DIVICE("Error: SERVICE_NOT_AVAILABLE");
                }else{
                    prefs.setID_DIVICE(result);
                }

                Log.i("ID_Divice", prefs.getID_DIVICE());
            }

        }.execute(null,null,null);
    }

    public static boolean getId_Divice_Correct(Context context ){
        Preferencias prefs = new Preferencias(context);

        return prefs.getID_DIVICE_Google();
    }

}
