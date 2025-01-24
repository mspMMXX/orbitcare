package com.yama.orbitcare.features.login

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper

/**
 * Utility class for showing a confirmation dialog with a timed auto-dismiss feature.
 * This dialog is non-interactive and automatically calls a completion callback after dismissal.
 */
class ConfirmationDialog {

    /**
     * Displays a non-interactive confirmation dialog.
     *
     * @param context The context in which the dialog is to be shown.
     * @param title The title of the dialog.
     * @param message The message displayed in the dialog.
     * @param onComplete A callback executed after the dialog is dismissed.
     */
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