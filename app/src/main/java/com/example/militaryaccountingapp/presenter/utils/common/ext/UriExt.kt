package com.example.militaryaccountingapp.presenter.utils.common.ext

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Get Intent to View Uri backed File
 *
 * @param context
 * @param uri
 * @return Intent
 */
fun Uri.getUriViewIntent(context: Context): Intent {
    return Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(this@getUriViewIntent, "image/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        putExtra(Intent.EXTRA_LOCAL_ONLY, true)
    }
}