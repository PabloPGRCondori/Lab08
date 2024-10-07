package com.example.laboratorio08

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false,
    @ColumnInfo(name = "recurrence") val recurrence: String = "diaria",
    @ColumnInfo(name = "priority") val priority: String = "baja",
    @ColumnInfo(name = "category") val category: String = "general"
)