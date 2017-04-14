package dog.snow.androidrecruittest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import dog.snow.androidrecruittest.database.ItemBaseHelper;
import dog.snow.androidrecruittest.database.ItemCursorWraper;
import dog.snow.androidrecruittest.database.ItemDbSchema;
import dog.snow.androidrecruittest.model.Item;

/**
 * Created by pc on 14.04.2017.
 */

public class AppContainer {
    private static AppContainer sAppContainer;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static AppContainer get(Context context) {
        if(sAppContainer == null)
        {
            sAppContainer = new AppContainer(context);

        }
        return sAppContainer;
    }
    public AppContainer(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ItemBaseHelper(mContext).getWritableDatabase();

    }

    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();

        Cursor c = mDatabase.query(ItemDbSchema.ItemTable.NAME,null,null,null,null,null,null);

        ItemCursorWraper cursor = new ItemCursorWraper(c);

        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                items.add(cursor.getItem());
                cursor.moveToNext();
            }

        }finally{
            cursor.close();
        }
        return items;
    }

    public Item getItem(Integer id) {
        ItemCursorWraper cursor = queryItems(ItemDbSchema.ItemTable.Cols.ID+"=?",new String[]{id.toString()});

        try{
            if(cursor.getCount()==0)
            {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getItem();

        }finally{
            cursor.close();
        }
    }

    private ItemCursorWraper queryItems(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(ItemDbSchema.ItemTable.NAME,null,whereClause,whereArgs,null,null,null);
        return new ItemCursorWraper(cursor);
    }

    public void clearDatabase(){
        mDatabase.execSQL("DELETE FROM "+ItemDbSchema.ItemTable.NAME);
    }

    public void addItem(Item item){
        ContentValues values=getContentValues(item);
        mDatabase.insert(ItemDbSchema.ItemTable.NAME,null,values);
    }

    private static ContentValues getContentValues(Item item) {
        ContentValues values = new ContentValues();
        values.put(ItemDbSchema.ItemTable.Cols.ID,item.getId().toString());
        values.put(ItemDbSchema.ItemTable.Cols.NAME,item.getName());
        values.put(ItemDbSchema.ItemTable.Cols.DESCRIPTION,item.getDescription());
        values.put(ItemDbSchema.ItemTable.Cols.ICON,item.getIcon());
        values.put(ItemDbSchema.ItemTable.Cols.TIMESTAMP,item.getTimestamp());
        values.put(ItemDbSchema.ItemTable.Cols.URL,item.getUrl());

        return values;
    }
}
