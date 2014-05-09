package FeevasDbLite;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import br.com.rlsystem.dao.DBHelper;
import br.com.rlsystem.dao.Filmes;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DAO {

	/**
	 * @author ahron
	 * 
	 */
	/**
	 * @param args
	 */

	private final Object tbl;
	private Context ctx;

	public DAO(Object tbl, Context cnx) {
		this.tbl = tbl;
		this.ctx = cnx;
	}

	public void Insere() throws FevasDbLiteException {
		TableName tn = tbl.getClass().getAnnotation(TableName.class);

		String table = tbl.getClass().getSimpleName();

		if (tn != null) {
			table = tn.toString();
		}

		ContentValues values = new ContentValues();

		Field[] campos = tbl.getClass().getDeclaredFields();
		Log.d("logs - ", table);
		for (Field campo : campos) {
			
				Object vlr = getValorCampo(campo.getName());

				values.put(campo.getName(), vlr.toString());
				
			

		}
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		System.out.println(values);

		try {
			db.insertOrThrow(table, null, values);
		} catch (Exception e) {
			Log.d("motivo erro - ", e.getMessage());

		}

	}

	public void altera() throws FevasDbLiteException {

		String table = tbl.getClass().getSimpleName();

		TableName tn = tbl.getClass().getAnnotation(TableName.class);

		if (tn != null) {
			table = tn.toString();
		}
		ContentValues values = new ContentValues();
		ContentValues primaryKey = new ContentValues();
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();

		Field[] campos = tbl.getClass().getDeclaredFields();
		Log.d("logs - ", table);
		for (Field campo : campos) {
			if (!campo.isAnnotationPresent(PrimaryKey.class)) {
				Object vlr = getValorCampo(campo.getName());

				values.put(campo.getName(), vlr.toString());
				Log.d("logs - ", campo.getName());
				Log.d("logs - ", vlr.toString());
			}else{
				Object vlr = getValorCampo(campo.getName());
				primaryKey.put(campo.getName(), vlr.toString());
				
			}

		}

		try {
			db.update(table, values,primaryKey.toString(), null);
		} catch (Exception e) {
			Log.d("motivo erro - ", e.getMessage());

		}
	}

	
	public void deleta() throws FevasDbLiteException{
		String table = tbl.getClass().getSimpleName();
		TableName tn = tbl.getClass().getAnnotation(TableName.class);

		if (tn != null) {
			table = tn.toString();
		}
	
		Field[] campos = tbl.getClass().getDeclaredFields();
		ContentValues values = new ContentValues();
		for (Field campo : campos) {
			if (campo.isAnnotationPresent(PrimaryKey.class)) {
				Object vlr = getValorCampo(campo.getName());
				values.put(campo.getName(), vlr.toString());
				
			}

		}
		
			SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
			db.delete(table,values.toString(), null);

	}
	
	private Object getValorCampo(String nomeCampo) throws FevasDbLiteException {

		String nomeMetodoGet = "get"
				+ Character.toUpperCase(nomeCampo.charAt(0))
				+ nomeCampo.substring(1);

		try {
			Method m = tbl.getClass().getDeclaredMethod(nomeMetodoGet, null);
			Object vl = m.invoke(tbl, null);

			return vl;

		} catch (Exception e) {
			throw new FevasDbLiteException(e);
		}
	}

}
