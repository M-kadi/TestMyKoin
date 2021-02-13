package com.example.testmykoin

import android.content.Context
import android.content.SharedPreferences
import com.example.testmykoin.room.UserRepository
import com.example.testmykoin.room.UserRoomDatabase
import com.example.testmykoin.sqlite.DbHelper
import com.example.testmykoin.userList.UsersViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val MyModule = module {

    single { Car3() }
    single { Car2() }

    single { provideSharedPreferences(androidContext()) }

    single{ MySharedPreferences(get()) }

    single { DbHelper(androidContext(),"demo-koin.db",2) }

    // room
    factory { UserRoomDatabase.getDatabase(androidApplication()).userDao() }
    factory { UserRepository(get()) }

    viewModel {
        UsersViewModel(get())
    }
}

private fun provideSharedPreferences(context: Context) : SharedPreferences =
    context.getSharedPreferences("PrefName", Context.MODE_PRIVATE)



