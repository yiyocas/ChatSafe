package com.oropeza.chatsafe;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.Cursor;

import com.oropeza.codigo.ConexionLocal;

/**
 * Created by yiyo on 31/08/15.
 */
public class InfoDivice {
    private InfoDivice info;
    private String Id_mail;

    public String getId_mail(){return Id_mail;}

    public InfoDivice(Context context){
        ConexionLocal conn = new ConexionLocal(context);
        conn.abrir();
        Cursor c = conn.Consultar("select id_mail from usuario_divice");

        for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
         Id_mail = c.getString(c.getColumnIndex("id_mail"));
        }
        conn.cerrar();
    }
}
