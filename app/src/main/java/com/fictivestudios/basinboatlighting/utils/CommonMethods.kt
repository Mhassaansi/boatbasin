package com.fictivestudios.basinboatlighting.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.CursorLoader
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.os.Looper
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import com.fictivestudios.basinboatlighting.BuildConfig
import com.fictivestudios.basinboatlighting.R
import com.google.firebase.messaging.FirebaseMessaging
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


fun getFirebaseToken(
        token: (String) -> Unit,
    ) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = task.result ?: ""
            Log.e("dsfdsfsdf", "dfdfgdg  ${token}")
            token(token)
        }
    }

fun startTimer(textView: TextView, sec: Int) {
    // textView: TextView,
    val downTimer: CountDownTimer = object : CountDownTimer((1000 * sec).toLong(), 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val v = String.format("%02d", millisUntilFinished / 60000)
            val va = (millisUntilFinished % 60000 / 1000).toInt()
            textView.text = v + ":" + String.format("%02d", va)
        }

        override fun onFinish() {
            textView.text = "00.00"
//                    view.visibility=View.VISIBLE
//                    textView1.visibility=View.VISIBLE
        }
    }
    downTimer.start()
}

fun validateEmail(email: String?): Boolean {
    val pattern: Pattern
    val matcher: Matcher
    val EMAIL_PATTERN =
        "[a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+"
    pattern = Pattern.compile(EMAIL_PATTERN)
    matcher = pattern.matcher(email)
    return matcher.matches()
}

fun validatePassword(password: String?): Boolean {
    val pattern: Pattern
    val matcher: Matcher
    val EMAIL_PATTERN =
        "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    pattern = Pattern.compile(EMAIL_PATTERN)
    matcher = pattern.matcher(password)
    return matcher.matches()
}

fun getURi(outputFile:String,context: Context):Uri{
    val file = File(outputFile)
    Log.e("OutPutFile", outputFile)
    val uri: Uri = FileProvider.getUriForFile(
        context,
        BuildConfig.APPLICATION_ID.toString() + ".provider",
        file
    )

    return uri
}

fun deleteFile(path:String,context:Context):String{
    var message:String?=null

  val uri= getURi(path,context)
    val file: File = File(getMediaFilePathFor(uri, context))
    if (file.exists()) {
        if (file.delete()) {
             message = "\"file Deleted :\" + uri.getPath()"

        } else {
             message = "\"file not Deleted :\" + uri.getPath()"
        }
    }
    return message!!
}

 fun resizeDialogView(dialog: Dialog, percent: Int,activity:Activity) {
    val displayMetrics = DisplayMetrics()

    activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels

    val layoutParams = WindowManager.LayoutParams()
    layoutParams.copyFrom(dialog.window?.attributes)

    val dialogWidth = screenWidth * 95 / 100
    val dialogHeight = screenHeight * percent / 100

    layoutParams.width = dialogWidth
    layoutParams.height = dialogHeight

    dialog.window?.attributes = layoutParams
    dialog.window?.setGravity(Gravity.BOTTOM)
    dialog.window?.setBackgroundDrawableResource(R.color.transparent)
}

@SuppressLint("NewApi")
fun getRealPathFromURI_API20(context: Context, uri: Uri?): String? {
    var filePath = ""
    val wholeID = DocumentsContract.getDocumentId(uri)

    // Split at colon, use second item in the array
    val id = wholeID.split(":".toRegex()).toTypedArray()[1]
    val column = arrayOf(MediaStore.Images.Media.DATA)

    // where id is equal to
    val sel = MediaStore.Images.Media._ID + "=?"
    val cursor: Cursor? = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        column, sel, arrayOf(id), null
    )
    val columnIndex: Int? = cursor?.getColumnIndex(column[0])
    if (cursor?.moveToFirst() == true) {
        filePath = columnIndex?.let { cursor?.getString(it) }!!
    }
    cursor?.close()
    return filePath
}


fun getRealPathFromURI_API11to19(context: Context?, contentUri: Uri): String? {
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    var result: String? = null
    if (Looper.myLooper() == null) {
        Looper.prepare()
    }
    val cursorLoader = CursorLoader(
        context,
        contentUri, proj, null, null, null
    )
    val cursor: Cursor = cursorLoader.loadInBackground()
    result = if (cursor != null) {
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        cursor.getString(column_index)
    } else {
        contentUri.path
    }
    return result
}

fun getRealPathFromURI_BelowAPI11(context: Context, contentUri: Uri?): String? {
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
    val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor.moveToFirst()
    return cursor.getString(column_index)
}

fun getMediaFilePathFor(
    uri: Uri,
    context: Context
): String {
    val returnCursor =
        context.contentResolver.query(uri, null, null, null, null)
    val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
    returnCursor.moveToFirst()
    val name = returnCursor.getString(nameIndex)
    val size = returnCursor.getLong(sizeIndex).toString()
    val file = File(context.filesDir, name)
    try {
        val inputStream =
            context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        var read = 0
        val maxBufferSize = 1 * 1024 * 1024
        val bytesAvailable = inputStream!!.available()
        //int bufferSize = 1024;
        val bufferSize = bytesAvailable.coerceAtMost(maxBufferSize)
        val buffers = ByteArray(bufferSize)
        while (inputStream.read(buffers).also { read = it } != -1) {
            outputStream.write(buffers, 0, read)
        }
        Log.e("File Size %d", "" + file.length())
        inputStream.close()
        outputStream.close()
        Log.e("File Size %s", file.path)
        Log.e("File Size %d", "" + file.length())
    } catch (e: java.lang.Exception) {
        Log.e("File Size %s", e.message!!)
    }
    return file.path
}

fun getBitmapFromIntent(context: Context, data: Intent): Bitmap? {
    var bitmap: Bitmap? = null
    if (data.data == null) {
        bitmap = data.extras!!["data"] as Bitmap?
    } else {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, data.data)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return bitmap
}

fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path =
        MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
    return Uri.parse(path)
}


fun getDayOfWeek(timestamp: Long): String {
    return SimpleDateFormat("EEEE", Locale.ENGLISH).format(timestamp * 1000)
}

fun getMonthFromTimeStamp(timestamp: Long): String {
    return SimpleDateFormat("MMM", Locale.ENGLISH).format(timestamp * 1000)
}

fun getYearFromTimeStamp(timestamp: Long): String {
    return SimpleDateFormat("yyyy", Locale.ENGLISH).format(timestamp * 1000)
}


fun getDayOfWeekmonthyear(timestamp: Long): String {
    return SimpleDateFormat("EEEE dd MMM, yyyy", Locale.ENGLISH).format(timestamp * 1000)
}

fun getDayOfWeekwithmonth(timestamp: Long): String {
    return SimpleDateFormat("EEEE-MMM", Locale.ENGLISH).format(timestamp * 1000)
}

fun getTime(timestamp: Long):String
{

    return SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(timestamp * 1000)
}


fun kelivntocelsius(kelvin:Int):Int{

    return kelvin-273

}

fun checkGPSEnable(activity: Activity) {
    val dialogBuilder = AlertDialog.Builder(activity)
    dialogBuilder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
        .setCancelable(false)
        .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id
            ->
            activity.startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        })
        .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
            dialog.cancel()
        })
    val alert = dialogBuilder.create()
    alert.show()
}


 fun checkPermissionslocation(activity: Activity): Boolean {
    return ActivityCompat.checkSelfPermission(
        activity!!,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        activity!!,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

}




