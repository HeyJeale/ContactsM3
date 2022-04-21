package edu.zjut.contactsMaterial3Experimental.Helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ContactDatabaseHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory, version: Int)
    : SQLiteOpenHelper(context, name, factory, version) {

    private val mContext:Context = context

    override fun onCreate(p0: SQLiteDatabase?) {
        val createSQLString = "create table Contacts(" +
                "id integer primary key autoincrement, " +
                "name text not null, " +
                "pinyin text not null," +
                "wired text, " +
                "mobile text, " +
                "mail text, " +
                "belonging text)"

        p0?.execSQL(createSQLString)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

}