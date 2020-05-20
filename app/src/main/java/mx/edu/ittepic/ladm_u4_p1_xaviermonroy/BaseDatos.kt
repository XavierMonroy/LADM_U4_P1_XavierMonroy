package mx.edu.ittepic.ladm_u4_p1_xaviermonroy

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(context: Context, nombreBD: String, cursorFactory: SQLiteDatabase.CursorFactory?,
                numVersion: Int): SQLiteOpenHelper(context, nombreBD, cursorFactory, numVersion) {

    override fun onCreate(db: SQLiteDatabase?) {
        try {
            db?.execSQL("CREATE TABLE MENSAJE(ID INTEGER PRIMARY KEY AUTOINCREMENT, MENSAJE VARCHAR(300))")
            db?.execSQL("CREATE TABLE TELEFONO(ID INTEGER PRIMARY KEY AUTOINCREMENT, TELEFONO VARCHAR(15), NOMBRE VARCHAR (30), TIPO VARCHAR(1))")
            db?.execSQL("CREATE TABLE LLAMADAS_PERDIDAS(ID INTEGER PRIMARY KEY AUTOINCREMENT, TELEFONO VARCHAR(15), ESTADO BOOLEAN)")
        } catch (error : SQLiteException){

        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}