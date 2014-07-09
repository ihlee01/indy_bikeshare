import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.misabelleeli.pacers_bikeshare.User;

import java.net.ContentHandler;

/**
 * Created by Lee on 2014-07-09.
 */
public class DBTools extends SQLiteOpenHelper{

    private final static int DB_VERSION = 10;

    public DBTools(Context context) {
        super(context, "myApp.db", null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "create table logins (userId Integer primary key autoincrement, "+
                " username text, password text)";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        try{
            System.out.println("UPGRADE DB oldVersion="+oldVersion+" - newVersion="+newVersion);

            if(oldVersion<10) {
                String query = "create table logins (userId Integer primary key autoincrement, "+
                        " username text, password text)";
                sqLiteDatabase.execSQL(query);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("DOWNGRADE DB oldVersion = " + oldVersion + " - newVersion = " + newVersion);
    }

    public User insertUser (User queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", queryValues.username);
        values.put("password", queryValues.password);
        queryValues.userId = database.insert("logins", null, values);
        database.close();
        return queryValues;
    }

    public int updateUserPassword (User queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", queryValues.username);
        values.put("password", queryValues.password);
        queryValues.userId = database.insert("logins", null, values);
        database.close();
        return database.update("logins", values, "userId = ?", new String[] {String.valueOf(queryValues.userId)});
    }

    public User getUser (String username) {
        String query = "Select userId, password from logins where username = '" + username+ "'";
        User myUser = new User(Long.parseLong("0"), username, "");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            do {
                myUser.userId = cursor.getLong(0);
                myUser.password = cursor.getString(1);
            } while (cursor.moveToNext());
        }
        return myUser;
    }

}
