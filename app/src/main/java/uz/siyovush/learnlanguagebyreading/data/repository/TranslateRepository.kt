package uz.siyovush.learnlanguagebyreading.data.repository

import com.google.cloud.translate.Translate.TranslateOption
import com.google.cloud.translate.TranslateOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranslateRepository @Inject constructor() {
    suspend fun translate(text: String, to: String): String {
        return withContext(Dispatchers.IO) {
            val translate = TranslateOptions.newBuilder().setApiKey("AIzaSyA5KK1UlSM_rgMMlJmkC5U-kMzSe8wiHw8")
                .build().service
            translate.translate(
                text,
                TranslateOption.targetLanguage(to)
            ).also{
                print(it.translatedText)
            }.translatedText
        }
    }
}