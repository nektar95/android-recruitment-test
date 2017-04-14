package dog.snow.androidrecruittest.database;

/**
 * Created by pc on 14.04.2017.
 */

public class ItemDbSchema {
    public static final class ItemTable{
        public static final String NAME = "items";

        public static final class Cols {
            public static final String ID = "id";
            public static final String NAME = "name";
            public static final String DESCRIPTION = "description";
            public static final String ICON = "icon";
            public static final String TIMESTAMP = "timestamp";
            public static final String URL = "url";
        }
    }
}
