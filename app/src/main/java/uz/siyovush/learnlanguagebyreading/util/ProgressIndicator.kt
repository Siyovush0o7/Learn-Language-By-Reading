package uz.siyovush.learnlanguagebyreading.util

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import uz.siyovush.learnlanguagebyreading.R

class ProgressIndicator(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.custom_progress_indicator, this, true)
        this.animation = AnimationUtils.loadAnimation(context, R.anim.progress_indicator_anim)
    }
}