package com.example.laboratorio08

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Task::class], version = 2) // Cambia esto a 2 o un número más alto
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Instrucciones para actualizar la base de datos sin perder datos
        database.execSQL("ALTER TABLE tasks ADD COLUMN priority TEXT DEFAULT 'baja'")
        database.execSQL("ALTER TABLE tasks ADD COLUMN category TEXT DEFAULT 'general'")
        database.execSQL("ALTER TABLE tasks ADD COLUMN recurrence TEXT DEFAULT 'diaria'")
    }
}