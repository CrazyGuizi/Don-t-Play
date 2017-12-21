package com.example.dontplay.database;

/**
 * Created by Crazy贵子 on 2017/12/13.
 */

public class AppDbSchema {
    public static final class AppTable {
        public static final String NAME = "apps";

        public static final class Cols {
            public static final String NAME = "name";
            public static final String PACKAGE_NAME = "packageName";
            public static final String ICON = "icon";
            public static final String ISTABOO = "isTaboo";
        }
    }
}
