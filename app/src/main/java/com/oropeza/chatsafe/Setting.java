package com.oropeza.chatsafe;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.camera.CropImageIntentBuilder;
import com.oropeza.codigo.Conexion;
import com.oropeza.codigo.ConexionLocal;
import com.oropeza.utilidades.CheckCuentaDivice;
import com.oropeza.utilidades.Ipconfig;
import com.oropeza.codigo.ItemContacto;
import com.oropeza.utilidades.ImageUtil;
import com.oropeza.utilidades.MediaStoreUtils;
import com.oropeza.utilidades.Preferencias;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yiyo on 25/08/15.
 */
public class Setting extends ActionBarActivity {

    private static int REQUEST_PICTURE = 1;
    private static int REQUEST_CROP_PICTURE = 2;

    private String selectedImagePath;
    private String imageString;
    private boolean CuentaActiva;
    private boolean getIDGoogle;

    private ItemContacto User;
    private FloatingActionButton bnsafe;
    private EditText UserName;
    private EditText UserMail;
    private EditText UserPasswd;

    private ImageView img;

    private CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i("Tiene Cuenta", VerificarCuenta() + " " + User.getNombre());
        setContentView(R.layout.setting);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Init();

        if(CuentaActiva){
            VerificarCuenta();
            setTextEditText();
        }else{
            if(CheckCuentaDivice.checkPlayServices(this,this)){
                getID_Divice();
            }else{
                finish();
            }
        }
    }

    private void getID_Divice( ){
        CheckCuentaDivice.RegistarDivice(this);
    }

    private void Init( ){
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        img = (ImageView)findViewById(R.id.backdrop);
        UserMail = (EditText) findViewById(R.id.usermail);
        UserName = (EditText) findViewById(R.id.username);
        UserPasswd = (EditText) findViewById(R.id.userpass);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.primary));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(MediaStoreUtils.getPickImageIntent(getBaseContext()), REQUEST_PICTURE);
            }
        });

        bnsafe = (FloatingActionButton) findViewById(R.id.bnsafe);

        bnsafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserMail.getText().toString().equals("") ||
                        UserName.getText().toString().equals("") ||
                        UserPasswd.getText().toString().equals("")) {
                    Toast.makeText(Setting.this, "NINGUNO DE LOS CAMPO DEBE QUEDAR VACIO", Toast.LENGTH_SHORT).show();
                } else {

                    if (VerificarCuenta()) {
                        UpdateContact();
                    } else {
                        AddNewContact();
                    }

                }
            }
        });

        Bundle b = getIntent().getExtras();
        CuentaActiva = b.getBoolean("Cuenta");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Resume", "Return!!");
       // VerificarCuenta();
       // setTextEditText();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        File croppedImageFile = new File(getFilesDir(), "test.jpg");

        if ((requestCode == REQUEST_PICTURE) && (resultCode == RESULT_OK)) {
            // When the user is done picking a picture, let's start the CropImage Activity,
            // setting the output image file and size to 200x200 pixels square.
            Uri croppedImage = Uri.fromFile(croppedImageFile);

            CropImageIntentBuilder cropImage = new CropImageIntentBuilder(500, 350,500,390, croppedImage);
            cropImage.setOutlineColor(0xFF03A9F4);
            cropImage.setSourceImage(data.getData());
            cropImage.setScale(false);
            cropImage.setDoFaceDetection(true);

            startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_PICTURE);
        } else if ((requestCode == REQUEST_CROP_PICTURE) && (resultCode == RESULT_OK)) {
            // When we are done cropping, display it in the ImageView.
            Log.i("Captura","???");
            img.setImageBitmap(BitmapFactory.decodeFile(croppedImageFile.getAbsolutePath()));
        }
    }

    private void setImageString( ){
        View content = img.findViewById(R.id.backdrop);
        content.setDrawingCacheEnabled(true);
        Bitmap bitmap = content.getDrawingCache();

        ByteArrayOutputStream stream = new ByteArrayOutputStream( );

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArrayImage = stream.toByteArray();
        imageString = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

    }

    public String getPath(Uri uri) {

        Log.i("Uri","Uri..........");

        if( uri == null ) {
            return null;
        }

        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }

        return uri.getPath();
    }

    private boolean VerificarCuenta( ){

        Log.i("Verificar", "Verificar..........");
        ConexionLocal conn = new ConexionLocal(this);
        conn.abrir();

        Cursor c = conn.Consultar("select * from usuario_divice");


        for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            User = new ItemContacto();
            imageString = c.getString(c.getColumnIndex("img"));
            User.setImagenContacto(imageString);
            User.setMail(c.getString(c.getColumnIndex("id_mail")));
            User.setNombre(c.getString(c.getColumnIndex("alias")));
            User.setPasswd(c.getString(c.getColumnIndex("passwd")));

        }

        conn.cerrar();
        if(User!= null){
            return true;
        }
        return false;
    }

    private void setTextEditText( ){

        Log.i("Set","Text..........");
        if(User!= null){

            collapsingToolbar.setTitle(User.getNombre());

            UserMail.setText(User.getMail());
            UserMail.setEnabled(false);
            UserPasswd.setText(User.getPasswd());
            UserName.setText(User.getNombre());
            img.setImageBitmap(ImageUtil.getInstance().getBitmap(User.getImagenString()));
            bnsafe.setImageResource(R.drawable.button_reload);
            collapsingToolbar.setTitle(User.getNombre());
        }else{
            img.setImageResource(R.drawable.enso2_sk);
            collapsingToolbar.setTitle("NUEVO USUARIO");
        }
    }

    private void AddNewContact( ){
        Log.i("Add","ADD..........");

       if(CheckCuentaDivice.getId_Divice_Correct(this)){

        setImageString();
        Conexion internet = new Conexion(Ipconfig.getInstance("RegistroDivice.php"));

        List<NameValuePair> Parametros = new ArrayList<NameValuePair>( );
        Parametros.add(new BasicNameValuePair("id_mail", UserMail.getText().toString()));
        Parametros.add(new BasicNameValuePair("alias", UserName.getText().toString()));
        Parametros.add(new BasicNameValuePair("key", new Preferencias(this).getID_DIVICE()));
        Parametros.add(new BasicNameValuePair("img", imageString));

        internet.Escribri(Parametros);

        ConexionLocal conn = new ConexionLocal(getApplicationContext());

        conn.abrir();

        SQLiteDatabase sqLiteDatabase = conn.getSQLiteDatabase();

        SQLiteStatement stmt = sqLiteDatabase.compileStatement("INSERT INTO usuario_divice VALUES(?,?,?,?,?);");
        stmt.bindString(1,UserMail.getText().toString());
        stmt.bindString(2, UserName.getText().toString());
        stmt.bindString(3, new Preferencias(this).getID_DIVICE());
        stmt.bindString(4, imageString);
        stmt.bindString(5, UserPasswd.getText().toString());
        stmt.execute();

        stmt.close();

        conn.cerrar();

        UserMail.setEnabled(false);
           VerificarCuenta();
           setTextEditText();

        Toast.makeText(Setting.this, "Hecho", Toast.LENGTH_SHORT).show();
        }else{
            getID_Divice( );
            Toast.makeText(Setting.this, "Error Vuelva a Intentar", Toast.LENGTH_SHORT).show();
        }
    }

    private void UpdateContact( ){
        Log.i("update","update..........");

        setImageString();

        Conexion internet = new Conexion(Ipconfig.getInstance("RegistroDivice.php"));

        List<NameValuePair> Parametros = new ArrayList<NameValuePair>( );
        Parametros.add(new BasicNameValuePair("id_mail", UserMail.getText().toString()));
        Parametros.add(new BasicNameValuePair("alias", UserName.getText().toString()));
        Parametros.add(new BasicNameValuePair("key", new Preferencias(this).getID_DIVICE()));
        Parametros.add(new BasicNameValuePair("img", imageString));

        internet.Escribri(Parametros);

        ConexionLocal conn = new ConexionLocal(getApplicationContext());

        conn.abrir();

        SQLiteDatabase sqLiteDatabase = conn.getSQLiteDatabase();

        SQLiteStatement stmt = sqLiteDatabase.compileStatement("update usuario_divice set alias=?, key=?, img=?, passwd=? where id_mail=?;");
        stmt.bindString(1, UserName.getText().toString());
        stmt.bindString(2, new Preferencias(this).getID_DIVICE());
        stmt.bindString(3, imageString);
        stmt.bindString(4, UserPasswd.getText().toString());
        stmt.bindString(5, UserMail.getText().toString());
        stmt.execute();

        stmt.close();

        conn.cerrar();

        UserMail.setEnabled(false);
        VerificarCuenta();
        setTextEditText();
        Toast.makeText(Setting.this, "Actualizado", Toast.LENGTH_SHORT).show();
    }
}
