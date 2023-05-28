package uz.siyovush.learnlanguagebyreading.util

import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import uz.siyovush.learnlanguagebyreading.R

class ProgressDialog : DialogFragment(R.layout.custom_progress_indicator) {
    override fun onStart() {
        super.onStart()
        isCancelable = false
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        view?.findViewById<ImageView>(R.id.progress_indicator_image)?.animation =
            AnimationUtils.loadAnimation(
                this@ProgressDialog.context,
                R.anim.progress_indicator_anim
            )

    }
}

fun DialogFragment.show(fragmentManager: FragmentManager) {
    if (!this.isVisible)
        this.show(fragmentManager, "progress_dialog")
}

fun DialogFragment.hide() {
    if (this.isVisible)
        this.dismiss()
}