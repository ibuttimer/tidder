/*
 * Copyright (C) 2018  Ian Buttimer
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.ianbuttimer.tidderish.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.IfNotExists;
import net.simonvt.schematic.annotation.OnConfigure;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

import timber.log.Timber;

/**
 * Database class
 */

@Database(version = TidderDatabase.VERSION,
        packageName = "com.ianbuttimer.tidderish.data.db.gen")
public final class TidderDatabase extends AbstractDatabase {


    private TidderDatabase() {
    }

    public static final int VERSION = 1;

    private static final String RECREATE_MIGRATION;

    static {
        RECREATE_MIGRATION = "recreate";
    }

    private static final String[] MIGRATIONS = {
        // Put DDL/DML commands here, one string per VERSION increment
        /* ver 1 - 2 */ RECREATE_MIGRATION,
        /* ver 2 - 3 */ RECREATE_MIGRATION,
        /* ver 3 - 4 */ RECREATE_MIGRATION,
        /* ver 4 - 5 */ RECREATE_MIGRATION,
        /* ver 5 - 6 */ RECREATE_MIGRATION,
    };

    public static class Tables {
        @Table(FollowColumns.class) @IfNotExists public static final String FOLLOW = TableNames.FOLLOW;
        @Table(PinnedColumns.class) @IfNotExists public static final String PINNED = TableNames.PINNED;
    }

    @OnCreate
    public static void onCreate(Context context, SQLiteDatabase db) {
        // no op
    }

    @OnUpgrade
    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
                                            int newVersion) {
        boolean recreated = false;
        for (int i = oldVersion; i < newVersion; i++) {
            String migration = MIGRATIONS[i - 2];   // array 0-based, version started at 1, so 1st upgrade is 2
            if (RECREATE_MIGRATION.equals(migration)) {
                if (!recreated) {
                    recreateDatabase(context, db);
                    recreated = true;
                }
            } else {
                db.beginTransaction();
                try {
                    db.execSQL(migration);
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    Timber.e(e, "Error executing database migration: %s", migration);
                    break;
                } finally {
                    db.endTransaction();
                }
            }
        }
    }

    private static void recreateDatabase(Context context, SQLiteDatabase db) {
        for (String table : TABLE_NAMES) {
            String ddlCmd = "DROP TABLE IF EXISTS " + table + ";";
            db.beginTransaction();
            try {
                db.execSQL(ddlCmd);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Timber.e(e, "Error executing database migration: %s", ddlCmd);
                break;
            } finally {
                db.endTransaction();
            }
        }
        com.ianbuttimer.tidderish.data.db.gen.TidderDatabase.getInstance(context).onCreate(db);
    }


    @OnConfigure
    public static void onConfigure(SQLiteDatabase db) {
    }
}
