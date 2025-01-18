package com.yama.orbitcare.features.login

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper

class ConfirmationDialog {

    fun showConfirmation(context: Context, title: String, message: String, onComplete: () -> Unit) {
        Handler(Looper.getMainLooper()).post {
            val dialog = AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .create()

            dialog.show()

            Handler(Looper.getMainLooper()).postDelayed({
                if (dialog.isShowing) {
                    dialog.dismiss()
                    onComplete()
                }
            }, 2000)
        }

    }
}