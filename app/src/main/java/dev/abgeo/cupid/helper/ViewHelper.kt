package dev.abgeo.cupid.helper

import android.widget.TextView

fun TextView.setErrorWithFocus(error: CharSequence) {
    this.error = error
    this.requestFocus()
}
