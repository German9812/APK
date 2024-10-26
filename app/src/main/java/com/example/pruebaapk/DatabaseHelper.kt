package com.example.pruebaapk

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    companion object {
        const val DATABASE_NAME = "crudApp.db"
        const val TABLE_USERS = "users"
        const val TABLE_PRODUCTS = "productos"

        // Columnas para la tabla de usuarios
        const val COL_USER_ID = "user_id"
        const val COL_USERNAME = "username"
        const val COL_PASSWORD = "password"

        // Columnas para la tabla de productos
        const val COL_PRODUCT_ID = "product_id"
        const val COL_PRODUCT_NAME = "name"
        const val COL_PRODUCT_DESC = "description"
    }


    override fun onCreate(db: SQLiteDatabase) {


        // Crear tabla de usuarios
        db.execSQL("CREATE TABLE $TABLE_USERS ($COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_USERNAME TEXT, $COL_PASSWORD TEXT)")

        // Crear tabla de productos
        db.execSQL("CREATE TABLE $TABLE_PRODUCTS ($COL_PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_PRODUCT_NAME TEXT, $COL_PRODUCT_DESC TEXT)")

        // Insertar un usuario de prueba
        insertUser(db, "admin", "0000")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS productos")
        onCreate(db) // Vuelve a crear las tablas
    }


    private fun insertUser(db: SQLiteDatabase, username: String, password: String): Boolean {
        val contentValues = ContentValues().apply {
            put(COL_USERNAME, username)
            put(COL_PASSWORD, password)
        }
        val result = db.insert(TABLE_USERS, null, contentValues)
        return result != -1L
    }

    fun insertUser(username: String, password: String): Boolean {
        val db = this.writableDatabase
        val success = insertUser(db, username, password)
        db.close()
        return success
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COL_USERNAME = ? AND $COL_PASSWORD = ?", arrayOf(username, password))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    // Métodos CRUD para productos

    fun insertProduct(name: String, description: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_PRODUCT_NAME, name)
            put(COL_PRODUCT_DESC, description)
        }
        val result = db.insert(TABLE_PRODUCTS, null, contentValues)
        db.close()
        return result != -1L
    }

    fun getAllProducts(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_PRODUCTS", null)
    }

    fun updateProduct(id: Int, name: String, description: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_PRODUCT_NAME, name)
            put(COL_PRODUCT_DESC, description)
        }
        val updatedRows = db.update(TABLE_PRODUCTS, contentValues, "$COL_PRODUCT_ID = ?", arrayOf(id.toString()))
        db.close()
        return updatedRows > 0
    }

    fun deleteProduct(id: Int): Boolean {
        val db = this.writableDatabase
        val deletedRows = db.delete(TABLE_PRODUCTS, "$COL_PRODUCT_ID = ?", arrayOf(id.toString()))
        db.close()
        return deletedRows > 0
    }
}
