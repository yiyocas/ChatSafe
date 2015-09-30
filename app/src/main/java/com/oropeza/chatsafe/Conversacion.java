package com.oropeza.chatsafe;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.oropeza.codigo.ChatAdapter;
import com.oropeza.codigo.Conexion;
import com.oropeza.codigo.ConexionLocal;
import com.oropeza.codigo.ItemConversacion;
import com.oropeza.utilidades.Ipconfig;
import com.oropeza.utilidades.Post2Gcm;
import com.oropeza.utilidades.Preferencias;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by yiyo on 29/08/15.
 */
public class Conversacion extends AppCompatActivity {
    private ArrayList<ItemConversacion> listMensajes;
    private ListView listView;
    private EditText msnasend;
    private at.markushi.ui.CircleButton bn;
    private int opmenulistview;
    private ChatAdapter adapter;

    private String Id_mail_amigo;
    private String Alias_amigo;
    private String Id_Conversacion;
    private String key;
    private int IMG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Init();
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));
        //PruebaConver();
        setMensajesActivity();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setTitle(Alias_amigo);

        registerForContextMenu(listView);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.messagesContainer) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("OPCIONES");
            String[] menuItems = getResources().getStringArray(R.array.menulistview);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.menulistview);
        String menuItemName = menuItems[menuItemIndex];

        switch (menuItemName){
            case "Copiar Mensaje":
                copyTextToClipboard(listMensajes.get(opmenulistview).getMensaje());
                Toast.makeText(Conversacion.this, "Copiado", Toast.LENGTH_SHORT).show();
             break;
            case "Eliminar Mensaje":
                DeleteMensaje(listMensajes.get(opmenulistview).getCns());
             break;
            default:
             break;
        }

        return true;
    }

    private void Init(){
        listMensajes = new ArrayList<ItemConversacion>();
        listView = (ListView) findViewById(R.id.messagesContainer);
        msnasend = (EditText) findViewById(R.id.textmsn);
        bn = (at.markushi.ui.CircleButton) findViewById(R.id.bnsendmsn);

        bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msnasend.getText().toString().equals("")) {
                    Toast.makeText(Conversacion.this, "Campo Vavio", Toast.LENGTH_SHORT).show();
                } else {
                    //InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                    //inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    setMsnasend();

                    listMensajes = null;

                    listMensajes = new ArrayList<ItemConversacion>();

                    setMensajesActivity();

                    msnasend.setText("");

                }
            }
        });



        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

               opmenulistview = position;
                return false;
            }
        });

        getBundle();
    }

    private void DeleteMensaje(long cns){

       ConexionLocal conn = new ConexionLocal(this);
        conn.abrir();

        conn.Ejecutar("delete from mensajes where cns=" + cns);
        Toast.makeText(Conversacion.this, "Eliminado", Toast.LENGTH_SHORT).show();
        conn.cerrar();

        listMensajes = null;

        listMensajes = new ArrayList<ItemConversacion>();
        setMensajesActivity();


    }

    private void copyTextToClipboard(String txtView){
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(txtView.toString());
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("text label",txtView.toString());
            clipboard.setPrimaryClip(clip);
        }
    }

    private void getBundle( ){
        Bundle bun = getIntent().getExtras();
        Id_mail_amigo = bun.getString("Amigo");
        Id_Conversacion = bun.getString("Id_Conversacion");
        Alias_amigo = bun.getString("Alias");
        IMG = bun.getInt("IMG");
        key = bun.getString("key");

        if(Id_Conversacion==null){
            Log.i("insert,","NULL.........");
            bun = getIntent().getBundleExtra("INFO");
            Id_Conversacion = bun.getString("Id_Conversacion");
            Id_mail_amigo = bun.getString("Amigo");
        }

        //String query = "INSERT OR IGNORE INTO r_usr_conver VALUES ('"+Id_Conversacion+"','"+new InfoDivice(this).getId_mail()+"','"+Id_mail_amigo+"')";

        ConexionLocal conn = new ConexionLocal(this);

        conn.abrir();

        SQLiteStatement stm = conn.getSQLiteDatabase().compileStatement("INSERT OR IGNORE INTO r_usr_conver VALUES (?,?,?)");

        stm.bindString(1,Id_Conversacion);
        stm.bindString(2, new InfoDivice(this).getId_mail());
        stm.bindString(3,Id_mail_amigo);

        //conn.Ejecutar(query);

        stm.execute();
        conn.cerrar();
    }


    private void setMensajesActivity( ){
        ConexionLocal conn = new ConexionLocal(this);
        conn.abrir();

        Cursor c = conn.Consultar("SELECT * FROM mensajes where id_conversacion='"+Id_Conversacion+"'");

        for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            ItemConversacion item = new ItemConversacion();
            item.setAmigo(Id_mail_amigo);
            item.setMensaje(c.getString(c.getColumnIndex("mensaje")));
            item.setHora(c.getString(c.getColumnIndex("hora")));
            item.setQuienEnvia(c.getString(c.getColumnIndex("quien")), this);
            item.setCns(c.getLong(c.getColumnIndex("cns")));
            listMensajes.add(item);
        }

        adapter = new ChatAdapter(this, listMensajes);

        listView.setAdapter(adapter);

        listView.setSelection(listView.getCount() - 1);

        conn.cerrar();

    }

    private void PruebaConver(){

        Calendar c = Calendar.getInstance();


        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String fecha = dateFormat.format(date);

        dateFormat = new SimpleDateFormat("HH:mm a");
        String hora = dateFormat.format(date);

        //Log.i("Fecha",fecha +" "+ hora);

       ConexionLocal conn = new ConexionLocal(this);

        conn.abrir();

        SQLiteDatabase db = conn.getSQLiteDatabase();

        conn.Ejecutar("INSERT INTO mensajes (id_conversacion,quien,mensaje,fecha,hora) VALUES ('" + Id_Conversacion + "','" + Id_mail_amigo + "','Que accion, como estas master','" + fecha + "','" + hora + "')");
        conn.Ejecutar("INSERT INTO mensajes (id_conversacion,quien,mensaje,fecha,hora) VALUES ('" + Id_Conversacion + "','" + Id_mail_amigo + "','Que me cuentas','"+fecha+"','"+hora+"')");
        conn.Ejecutar("INSERT INTO mensajes (id_conversacion,quien,mensaje,fecha,hora) VALUES ('" + Id_Conversacion + "','" + new InfoDivice(this).getId_mail() + "','Que onda man, nada trabajando','" + fecha + "','" + hora + "')");
        conn.Ejecutar("INSERT INTO mensajes (id_conversacion,quien,mensaje,fecha,hora) VALUES ('" + Id_Conversacion + "','" + new InfoDivice(this).getId_mail() + "','Y ese milagraso que te acuerdas de mi','"+fecha+"','"+hora+"')");
        conn.Ejecutar("INSERT INTO mensajes (id_conversacion,quien,mensaje,fecha,hora) VALUES ('" + Id_Conversacion + "','" + Id_mail_amigo + "','Mames siempre me acuerdo','"+fecha+"','"+hora+"')");
        conn.Ejecutar("INSERT INTO mensajes (id_conversacion,quien,mensaje,fecha,hora) VALUES ('" + Id_Conversacion + "','" + Id_mail_amigo + "','Tas quedando mal master','" + fecha + "','" + hora + "')");
        conn.Ejecutar("INSERT INTO mensajes (id_conversacion,quien,mensaje,fecha,hora) VALUES ('" + Id_Conversacion + "','" + new InfoDivice(this).getId_mail() + "','" + getString(R.string.MsnPrueba) + "','" + fecha + "','" + hora + "')");

        conn.cerrar();
    }


    private void setMsnasend( ){
        Calendar c = Calendar.getInstance();


        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String fecha = dateFormat.format(date);

        dateFormat = new SimpleDateFormat("HH:mm a");
        String hora = dateFormat.format(date);
        ConexionLocal conn = new ConexionLocal(this);

        conn.abrir();
        //conn.Ejecutar("INSERT INTO mensajes (id_conversacion,quien,mensaje,fecha,hora) VALUES ('" + Id_Conversacion + "','" + new InfoDivice(this).getId_mail() + "','" + msnasend.getText().toString() + "','" + fecha + "','" + hora + "')");
        SQLiteStatement stm = conn.getSQLiteDatabase().compileStatement("INSERT INTO mensajes (id_conversacion,quien,mensaje,fecha,hora) VALUES (?,?,?,?,?)");
        stm.bindString(1,Id_Conversacion);
        stm.bindString(2,new InfoDivice(this).getId_mail());
        stm.bindString(3,msnasend.getText().toString());
        stm.bindString(4,fecha);
        stm.bindString(5,hora);
        stm.execute();

        conn.cerrar();

        /*Conexion internet = new Conexion(Ipconfig.getInstance("GCM.php"));

        List<NameValuePair> Parametros = new ArrayList<NameValuePair>( );
        Parametros.add(new BasicNameValuePair("reg_id",key));
        Parametros.add(new BasicNameValuePair("id_con",Id_Conversacion));
        Parametros.add(new BasicNameValuePair("quien",new InfoDivice(this).getId_mail()));
        Parametros.add(new BasicNameValuePair("mensaje",msnasend.getText().toString()));
        Parametros.add(new BasicNameValuePair("fecha",fecha));
        Parametros.add(new BasicNameValuePair("hora",hora));
        Parametros.add(new BasicNameValuePair("newuser","default"));
        Parametros.add(new BasicNameValuePair("acti", "default"));

        internet.Escribri(Parametros);*/


        new Send().execute(key, Id_Conversacion, new InfoDivice(getApplicationContext()).getId_mail(), msnasend.getText().toString(), fecha, hora, "uno");

    }

    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent i) {
            Log.i("OnReceive","Dentro.......");
          ConexionLocal conn = new ConexionLocal(context);

            conn.abrir();

            SQLiteDatabase db = conn.getSQLiteDatabase();

            //conn.Ejecutar("INSERT INTO mensajes (id_conversacion,quien,mensaje,fecha,hora) VALUES ('" + i.getStringExtra("data1") + "','" + i.getStringExtra("data2") + "','"+ i.getStringExtra("data3") +"','" + i.getStringExtra("data4") + "','" + i.getStringExtra("data5") + "')");

            SQLiteStatement stm = conn.getSQLiteDatabase().compileStatement("INSERT INTO mensajes (id_conversacion,quien,mensaje,fecha,hora) VALUES (?,?,?,?,?)");
            stm.bindString(1,i.getStringExtra("data1"));
            stm.bindString(2,i.getStringExtra("data2"));
            stm.bindString(3,i.getStringExtra("data3"));
            stm.bindString(4,i.getStringExtra("data4"));
            stm.bindString(5,i.getStringExtra("data5"));
            stm.execute();

            conn.cerrar();

            listMensajes = null;

            listMensajes = new ArrayList<ItemConversacion>();

            setMensajesActivity();

        }
    };

    private class Send extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {

            return Post2Gcm.post(args[0],args[1],args[2],args[3],args[4],args[5],args[6]);
        }

        @Override
        protected void onPostExecute(String respuesta) {

            Log.i("HTTPRESPONCE",respuesta);

        }
    }
}
