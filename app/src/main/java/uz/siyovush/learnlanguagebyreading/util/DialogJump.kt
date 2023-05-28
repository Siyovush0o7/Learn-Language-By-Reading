package uz.siyovush.learnlanguagebyreading.util

import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import uz.siyovush.learnlanguagebyreading.R

class DialogJump : DialogFragment(R.layout.custom_step_to) {
    private lateinit var input: TextInputEditText
    private lateinit var ok: MaterialButton
    private lateinit var cancel: MaterialButton
    private lateinit var onStep: ((Int) -> Unit)
    override fun onStart() {
        super.onStart()
        isCancelable = false
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        input = view?.findViewById(R.id.page_input) as TextInputEditText
        ok = view?.findViewById(R.id.step) as MaterialButton
        cancel = view?.findViewById(R.id.close) as MaterialButton

        ok.setOnClickListener {
            onStep(input.text.toString().toInt())
        }

        cancel.setOnClickListener {
            this.hide()
        }
    }


    fun onStep(func: ((page: Int) -> Unit)) {
        onStep = func
    }
}