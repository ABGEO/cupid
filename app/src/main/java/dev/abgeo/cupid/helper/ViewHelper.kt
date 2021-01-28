package dev.abgeo.cupid.helper

import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

fun TextView.setErrorWithFocus(error: CharSequence) {
    this.error = error
    this.requestFocus()
}

fun showProgressDisableButton(progressBar: ProgressBar, button: Button, enable: Boolean) {
    progressBar.visibility = if (enable) View.VISIBLE else View.INVISIBLE

    button.isEnabled = !enable
    button.isClickable = !enable
}
