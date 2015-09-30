package com.oropeza.codigo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ConexionLocal extends SQLiteOpenHelper {

	private static String UserDivce = "create table usuario_divice(\n" +
									  "id_mail text primary key, \n" +
			 						  "alias text, \n" +
									  "key text, \n" +
									  "img text, \n" +
									  "passwd text\n" +
									  ");";

	private static String Contactos = 	"create table contactos(\n" +
										"id_mail text unique primary key, \n" +
										"alias text, \n" +
										"key text,\n" +
										"img text\n" +
										");";

	private static String r_usr_conver = 	"create table r_usr_conver(\n" +
											"id_conversacion text primary key,\n" +
											"tu text default 'Tu',\n" +
											"amigo text references contactos not  null\n" +
											");";

	private static String Mensajes = 	"create table mensajes(\n" +
										"cns INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
										"id_conversacion text references r_usr_conver,\n" +
										"quien text not null,\n" +
										"mensaje text,\n" +
										"fecha date,\n" +
										"hora time\n" +
										");   ";

	private Context context;
	
	public ConexionLocal (Context ctx){
				  //Base datos
		super(ctx, "chatsafeprueba", null,14);
		context = ctx;
		}
		
	@Override
	public void onCreate(SQLiteDatabase db){
			//Crear Tablas		
		db.execSQL(UserDivce);
		db.execSQL(Contactos);
		db.execSQL(r_usr_conver);
		db.execSQL(Mensajes);
			//Precargar Tablas
		//DatosPredeterminados datos = new DatosPredeterminados( );

		//db.execSQL(datos.getUsuario_divice(this.context));

		}
		
		@Override
	public void onUpgrade(SQLiteDatabase db, int version_ant, int version_nue)
		{
			db.execSQL("DROP TABLE IF EXISTS usuario_divice");
			db.execSQL("DROP TABLE IF EXISTS contactos");
			db.execSQL("DROP TABLE IF EXISTS r_usr_conver");
			db.execSQL("DROP TABLE IF EXISTS mensajes");

			db.execSQL(UserDivce);
			db.execSQL(Contactos);
			db.execSQL(r_usr_conver);
			db.execSQL(Mensajes);

		}
		
		public void Ejecutar(String sql){
			Log.i("insert,",sql);	
				this.getWritableDatabase().execSQL(sql);
			}
		
	public Cursor Consultar(String sql){
			Log.i("Select",sql);
			Cursor c = this.getReadableDatabase().rawQuery(sql,null);
		return c;		
		}
		
	public void abrir(){
			this.getWritableDatabase();		
		}
		
	public SQLiteDatabase getSQLiteDatabase( ){
		return this.getWritableDatabase();
	}
	public void cerrar(){
			this.close();
			}
}