package com.example.coffea.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase

@Database(entities = [Recipe::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "recipe_db")
                    .build()
                    .also {Instance = it}
            }
        }
    }
}