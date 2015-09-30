package com.oropeza.codigo;

import android.content.Context;

import com.oropeza.chatsafe.InfoDivice;

import java.math.BigInteger;

/**
 * Created by yiyo on 09/08/15.
 */
public class DatosPredeterminados {

        public String getUsuario_divice(Context c){
                return "insert into usuario_divice values('"+new InfoDivice(c).getId_mail()+"','ADMINISTRADOR','UNKNOWN','DEFAULT')";
        }

        public String getContactos(int i){
                return "insert into contactos values('prueba"+i+"@prueba.com','PRUEBA_"+i+"','UNKNOWN');";
        }
}
