package com.oropeza.utilidades;

import android.content.Context;
import android.database.Cursor;

import com.oropeza.codigo.ConexionLocal;

/**
 * Created by yiyo on 03/09/15.
 */
public class Ipconfig {
    private static Ipconfig ourInstance = new Ipconfig();

    public static String getInstance(String filephp) {
        return "http://192.168.1.204/chatsafe/"+filephp;

    }

    public static String getIam(Context context) {

        ConexionLocal conn = new ConexionLocal(context);
        conn.abrir();

        Cursor c = conn.Consultar("select * from usuario_divice");

        String Iam=null;

        for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            Iam = c.getString(c.getColumnIndex("id_mail"));
        }

        conn.cerrar();

        if(Iam==null){
         Iam = "b";
        }
        return Iam;
    }

    private Ipconfig( ) {
    }
}
