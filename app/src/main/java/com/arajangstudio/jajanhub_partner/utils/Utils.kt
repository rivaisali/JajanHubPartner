package com.arajangstudio.jajanhub_partner.utils

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.arajangstudio.jajanhub_partner.ui.merchant.PhotoViewActivity

object Utils {

    @RequiresApi(Build.VERSION_CODES.M)
    fun hasInternetConnection(context: Context?): Boolean {
        if (context == null)
            return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun getAlertDialog(
        context: Context,
        layout: Int,
        setCancellationOnTouchOutside: Boolean
    ): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        val customLayout: View =
            layoutInflater.inflate(layout, null)
        builder.setView(customLayout)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(setCancellationOnTouchOutside)
        dialog.setCancelable(false)
        return dialog
    }

     fun goToDetail(
         context: Context,
        urls: ArrayList<String>,
        initialPos: Int
    ) {
        context.startActivity(
            PhotoViewActivity.createIntent(
                context,
                urls,
                initialPos
            )
        )
    }

    fun showSharingDialog(context: Context, url: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, url)
        context.startActivity(Intent.createChooser(intent, "Bagikan tempat jajanan"))
    }

    fun hideSoftKeyBoard(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}