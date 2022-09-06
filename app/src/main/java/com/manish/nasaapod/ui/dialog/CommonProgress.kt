package com.manish.nasaapod.ui.dialog

import android.app.Dialog
import android.content.Context
import com.manish.nasaapod.R
import android.graphics.drawable.AnimationDrawable
import android.content.DialogInterface
import com.manish.nasaapod.ui.dialog.CommonProgress
import android.view.WindowManager
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class CommonProgress : Dialog {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, theme: Int) : super(context!!, theme) {}

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        val imageView = findViewById<View>(R.id.spinnerImageView) as ImageView
        val spinner = imageView.background as AnimationDrawable
        spinner.start()
    }

    fun setMessage(message: CharSequence?) {
        if (message != null && message.length > 0) {
            findViewById<View>(R.id.message).visibility = View.VISIBLE
            val txt = findViewById<View>(R.id.message) as TextView
            txt.text = message
            txt.invalidate()
        }
    }

    companion object {
        fun show(
            context: Context?, message: CharSequence?, cancelable: Boolean,
            cancelListener: DialogInterface.OnCancelListener?
        ): CommonProgress {
            val dialog = CommonProgress(context, R.style.CommonProgress)
            dialog.setTitle("")
            dialog.setContentView(R.layout.progress_common)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            val messageView = dialog.findViewById<View>(R.id.message) as TextView
            if (message == null || message.isEmpty()) {
                messageView.visibility = View.GONE
            } else {
                messageView.text = message
            }
            dialog.setCancelable(cancelable)
            dialog.setOnCancelListener(cancelListener)
            dialog.window!!.attributes.gravity = Gravity.CENTER
            val lp = dialog.window!!.attributes
            lp.dimAmount = 0.2f
            dialog.window!!.attributes = lp
            dialog.show()
            return dialog
        }
    }
}