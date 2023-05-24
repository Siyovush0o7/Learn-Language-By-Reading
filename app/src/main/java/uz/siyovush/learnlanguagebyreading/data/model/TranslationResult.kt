package uz.siyovush.learnlanguagebyreading.data.model

import uz.siyovush.learnlanguagebyreading.data.database.entity.WordTranslation

data class TranslationResult(
    val original: String,
    val translation: String,
    val language: String,
    val isFavorite: Boolean,
) {
    fun toWordTranslation(): WordTranslation {
        return WordTranslation(original, language, translation)
    }
}