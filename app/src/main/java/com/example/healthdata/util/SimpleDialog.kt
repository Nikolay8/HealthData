package com.example.healthdata.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.example.healthdata.R


/**
 * SimpleDialog is a utility class for displaying simple AlertDialogs with a title, message, and action.
 */
class SimpleDialog {

    companion object {

        /**
         * Displays a simple AlertDialog with the provided title, message, and action.
         *
         * @param context The context in which the dialog should be displayed.
         * @param title The title of the dialog.
         * @param message The message to be displayed in the dialog.
         * @param action The action to be executed when the positive button is clicked.
         */
        fun showDialog(context: Context, title: String, message: String, action: () -> Unit) {
            val alert = AlertDialog.Builder(context)
            alert.setCancelable(true)
            alert.setTitle(title)
            alert.setMessage(message)

            alert.setPositiveButton(context.getString(R.string.yes)) { _: DialogInterface?, _: Int ->
                action()
            }

            alert.setNegativeButton(R.string.no) { dialog: DialogInterface?, _: Int ->
                dialog?.dismiss()
            }
            alert.show()
        }
    }
}