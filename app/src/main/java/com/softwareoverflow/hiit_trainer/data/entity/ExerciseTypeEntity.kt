package com.softwareoverflow.hiit_trainer.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "ExerciseType")
class ExerciseTypeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var name: String,
    var icon: String,
    var color: String
)