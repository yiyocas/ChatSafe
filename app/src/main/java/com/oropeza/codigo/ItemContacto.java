package com.oropeza.codigo;

/**
 * Created by yiyo on 09/08/15.
 */
public class ItemContacto {
    private String Nombre;
    private String Mail;
    private int ImagenContacto;
    private String ImagenString;
    private String Passwd;
    private String key;

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getMail() {
        return Mail;
    }

    public void setMail(String mail) {
        Mail = mail;
    }

    public int getImagenContacto() {
        return ImagenContacto;
    }

    public void setImagenContacto(int imagenContacto) {
        ImagenContacto = imagenContacto;
    }

    public String getImagenString() {
        return ImagenString;
    }

    public void setImagenContacto(String imagenContacto) {
        ImagenString = imagenContacto;
    }

    public String getPasswd() {
        return Passwd;
    }

    public void setPasswd(String passwd) {
        Passwd = passwd;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
