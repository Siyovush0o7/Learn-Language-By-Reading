package uz.siyovush.learnlanguagebyreading.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.siyovush.learnlanguagebyreading.data.database.AppDatabase
import uz.siyovush.learnlanguagebyreading.data.database.DictDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "word.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideWordDao(database: AppDatabase) = database.getWordDao()

    @Provides
    @Singleton
    fun provideBookDao(database: AppDatabase) = database.getBookDao()

    @Provides
    fun provideDictDatabase(
        @ApplicationContext context: Context,
    ): DictDatabase = Room.databaseBuilder(context, DictDatabase::class.java, "dict.db")
        .createFromAsset("dict.db").fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideDictDao(database: DictDatabase) = database.dictDao()
}