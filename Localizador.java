/**
 * 
 */
package FeevasDbLite;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Debug;
import android.util.Log;
import br.com.rlsystem.dao.DBHelper;
import br.com.rlsystem.dao.Filmes;

/**
 * @author ahron
 * @param <T>
 * 
 */
@SuppressLint("NewApi")
public class Localizador<T extends Object> {

	private T registro;
	private boolean found;
	private Class<T> c;
	private Context ctx;

	public Localizador(Class<T> c, Context ctx) {
		this.c = c;
		found = false;
		this.ctx = ctx;
	}

	public List<T> localiza(String selection, String[] selectionArgs,
			String groupBy, String having, String orderBy)
			throws InstantiationException, IllegalAccessException {

		String table = c.getSimpleName();
		TableName tn = c.getAnnotation(TableName.class);

		if (tn != null) {
			table = tn.toString();
		}
		List<T> result = new ArrayList<T>();

		StringBuilder cmd = new StringBuilder();

		Field[] campos = c.getDeclaredFields();

		for (Field campo : campos) {
			cmd.append(campo.getName());
			cmd.append(",");
		}
		cmd.delete(cmd.length() - 1, cmd.length());

		String[] colunas = new String[] { cmd.toString() };

		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		Cursor cursor = db.query(table, colunas, selection, selectionArgs,
				groupBy, having, orderBy);

		while (cursor.moveToNext()) {
			T registro = c.newInstance();
			preencheRegistro(registro, cursor);

			result.add(registro);

		}

		return result;

	}

	private void preencheRegistro(T registro, Cursor cursor) {

		Field[] fields = c.getDeclaredFields();

		for (Field f : fields) {

			String nomeMetodoSet = "set"
					+ Character.toUpperCase(f.getName().charAt(0))
					+ f.getName().substring(1);

			try {
				Method mtd = registro.getClass().getDeclaredMethod(
						nomeMetodoSet, f.getType());

				if (f.getType().getSimpleName().equals("Integer")) {
					mtd.invoke(registro,
							cursor.getInt(cursor.getColumnIndex(f.getName())));
					Log.d("debig", f.getName());

				} else if (f.getType().getSimpleName().equals("String")) {
					mtd.invoke(registro, cursor.getString(cursor
							.getColumnIndex(f.getName())));
				}

			} catch (Exception e) {

			}
		}
	}

}
