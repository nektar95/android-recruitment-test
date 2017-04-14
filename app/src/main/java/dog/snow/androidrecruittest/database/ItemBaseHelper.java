package dog.snow.androidrecruittest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pc on 14.04.2017.
 */

public class ItemBaseHelper  extends SQLiteOpenHelper{
    private static final int VERSION = 1;
    private static final String DATABASE_NAME="itemBase.db";

    public ItemBaseHelper(Context context){super(context,DATABASE_NAME,null,VERSION);}
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ ItemDbSchema.ItemTable.NAME + "("+" _id integer primary key autoincrement, "+ItemDbSchema.ItemTable.Cols.ID+
                ", "+ItemDbSchema.ItemTable.Cols.NAME+
                ", "+ItemDbSchema.ItemTable.Cols.DESCRIPTION+
                ", "+ItemDbSchema.ItemTable.Cols.ICON+
                ", "+ItemDbSchema.ItemTable.Cols.TIMESTAMP+
                ", "+ItemDbSchema.ItemTable.Cols.URL+
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
