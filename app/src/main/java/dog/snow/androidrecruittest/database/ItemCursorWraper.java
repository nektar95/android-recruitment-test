package dog.snow.androidrecruittest.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import dog.snow.androidrecruittest.model.Item;

/**
 * Created by pc on 14.04.2017.
 */

public class ItemCursorWraper extends CursorWrapper{
    public ItemCursorWraper(Cursor cursor)
    {
        super(cursor);
    }
    public Item getItem()
    {
        int id = getInt(getColumnIndex(ItemDbSchema.ItemTable.Cols.ID));
        String name = getString(getColumnIndex(ItemDbSchema.ItemTable.Cols.NAME));
        String description = getString(getColumnIndex(ItemDbSchema.ItemTable.Cols.DESCRIPTION));
        String icon = getString(getColumnIndex(ItemDbSchema.ItemTable.Cols.ICON));
        long timestamp = getLong(getColumnIndex(ItemDbSchema.ItemTable.Cols.TIMESTAMP));
        String url = getString(getColumnIndex(ItemDbSchema.ItemTable.Cols.URL));

        Item item =new Item();

        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setIcon(icon);
        item.setTimestamp(timestamp);
        item.setUrl(url);

        return item;
    }
}
