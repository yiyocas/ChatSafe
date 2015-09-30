package com.oropeza.codigo;

import android.content.Context;

import com.oropeza.chatsafe.InfoDivice;

/**
 * Created by yiyo on 29/08/15.
 */
public class ItemConversacion {
    private String Id_conversacion;
    private String Amigo;
    private String Tu;
    private String Mensaje;
    private String Hora;
    private boolean QuienEnvia;
    private int IMG;
    private long cns;

    public String getId_conversacion() {
        return Id_conversacion;
    }

    public void setId_conversacion(String id_conversacion) {
        Id_conversacion = id_conversacion;
    }

    public String getAmigo() {
        return Amigo;
    }

    public void setAmigo(String amigo) {
        Amigo = amigo;
    }

    public String getTu() {
        return Tu;
    }

    public void setTu(String tu) {
        Tu = tu;
    }

    public String getMensaje() {
        return Mensaje;
    }

    public void setMensaje(String mensaje) {
        Mensaje = mensaje;
    }

    public String getHora() {
        return Hora;
    }

    public void setHora(String hora) {
        Hora = hora;
    }

    public boolean getQuienEnvia() {
        return QuienEnvia;
    }

    public void setQuienEnvia(String quienEnvia,Context c) {
        if(quienEnvia.equals(new InfoDivice(c).getId_mail())){
            this.QuienEnvia = false;
        }else{
            this.QuienEnvia = true;
        }
    }

    public int getIMG() {
        return IMG;
    }

    public void setIMG(int IMG) {
        this.IMG = IMG;
    }

    public long getCns() {
        return cns;
    }

    public void setCns(long cns) {
        this.cns = cns;
    }
}
