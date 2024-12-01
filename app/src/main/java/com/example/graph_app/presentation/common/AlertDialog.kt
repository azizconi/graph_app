package com.example.graph_app.presentation.common

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

fun Context.getDialog(
    title: String? = null,
    message: String,
    positiveButtonText: String,
    onPositiveButtonClick: () -> Unit = {},
    negativeButtonText: String? = null,
    onNegativeButtonClick: () -> Unit = {},
): AlertDialog.Builder {
    return AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(
            positiveButtonText,
            dialogButtonClickListener(DialogInterface.BUTTON_POSITIVE) {
                onPositiveButtonClick()
            }
        )
        .setNegativeButton(
            negativeButtonText,
            dialogButtonClickListener(DialogInterface.BUTTON_NEGATIVE) {

                onNegativeButtonClick()
            }
        )
}

private fun dialogButtonClickListener(
    which: Int,
    onClick: () -> Unit,
) = DialogInterface.OnClickListener { dialog, whichButton ->
    if (whichButton == which) onClick()
}