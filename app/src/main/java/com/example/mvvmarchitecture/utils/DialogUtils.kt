package com.example.mvvmarchitecture.utils

import android.app.ProgressDialog
import android.content.Context
import android.os.Handler
import android.util.Log
import com.example.mvvmarchitecture.R

class DialogUtils {

    companion object {

        private var progress: ProgressDialog? = null

        fun showLoadingDialog(context: Context) {
            try {
                if (progress != null) {
                    progress!!.dismiss()
                }

                progress = ProgressDialog(context)
                progress!!.setTitle(context.getString(R.string.loading_title))
                progress!!.setMessage(context.getString(R.string.loading_content))
                progress!!.setCancelable(true)
                progress!!.setCanceledOnTouchOutside(false)
                progress!!.show()

                Handler().postDelayed({
                    try {
                        progress?.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("ERROR", "Unknown activity")
                    }
                }, 10000)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun dismissLoadingDialog() {
            progress?.dismiss()
        }
    }

}