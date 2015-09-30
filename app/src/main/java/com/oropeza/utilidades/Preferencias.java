package com.oropeza.utilidades;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by yiyo on 09/09/15.
 */
public class Preferencias {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context contex;

    public Preferencias(Context context){
        this.contex = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();
    }

    public String getID_DIVICE() {
        return prefs.getString("ID_DIVICE","");
    }

    public void setID_DIVICE(String  ID_DIVICE) {
        editor.putString("ID_DIVICE",ID_DIVICE);
        editor.commit();
    }

    public boolean getID_DIVICE_Google() {
        return prefs.getBoolean("ID_DIVICE_GOOGLE",false);
    }

    public void setID_DIVICE_Google(boolean  ID_DIVICE) {
        editor.putBoolean("ID_DIVICE_GOOGLE", ID_DIVICE);
        editor.commit();
    }
}
