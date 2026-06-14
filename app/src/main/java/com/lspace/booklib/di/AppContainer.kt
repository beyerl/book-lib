package com.lspace.booklib.di

import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.lspace.booklib.data.local.BookDatabase
import com.lspace.booklib.data.remote.OpenLibraryApi
import com.lspace.booklib.data.repository.BookRepository
import com.lspace.booklib.data.repository.ReadingGoalRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/** Manual dependency container, built once and held by the Application. */
class AppContainer(context: Context) {

    private val database: BookDatabase = Room.databaseBuilder(
        context.applicationContext,
        BookDatabase::class.java,
        BookDatabase.NAME,
    ).fallbackToDestructiveMigration().build()

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val okHttpClient = OkHttpClient.Builder().build()

    private val openLibraryApi: OpenLibraryApi = Retrofit.Builder()
        .baseUrl(OpenLibraryApi.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(OpenLibraryApi::class.java)

    val bookRepository: BookRepository =
        BookRepository(database.bookDao(), openLibraryApi)

    val readingGoalRepository: ReadingGoalRepository =
        ReadingGoalRepository(database.readingGoalDao())
}
