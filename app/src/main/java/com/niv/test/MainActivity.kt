package com.niv.test

import android.content.Intent
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import org.jetbrains.anko.doAsync
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.os.StrictMode
import android.view.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // generating some values to display on our graph
        val values: MutableList<List<Float>> = mutableListOf()
        for (i in 0..4) values.add(listOf(0f ,10f, 20f, 30f, 40f , 50f, 60f, 70f, 80f))

        val adapter = ListAdapter(this, values)
        listView.adapter = adapter

        button.setOnClickListener {

            val listBitmap = getWholeListViewItemsToBitmap(listView)

            saveImageExternal(listBitmap){ uri ->

                if (uri != null) {
                    val builder = StrictMode.VmPolicy.Builder()
                    StrictMode.setVmPolicy(builder.build())

                    shareImageUri(uri!!)
                }
            }
        }
    }


    fun getWholeListViewItemsToBitmap(list: ListView): Bitmap {

        val adapter = list.adapter

        val itemsCount = adapter.getCount()
        var allItemsHeight = 0

        val bmps = ArrayList<Bitmap>()

        for (i in 0 until itemsCount) {

            val childView = adapter.getView(i, null, list)

            childView.measure(
                View.MeasureSpec.makeMeasureSpec(list.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight())

            childView.isDrawingCacheEnabled = true
            childView.buildDrawingCache()

            childView.drawingCacheBackgroundColor = Color.WHITE

            bmps.add(childView.drawingCache)

            allItemsHeight += childView.getMeasuredHeight()
        }


        val bigBitmap = Bitmap.createBitmap(list.getMeasuredWidth(), allItemsHeight, Bitmap.Config.ARGB_8888)
        val bigCanvas = Canvas(bigBitmap)

        val paint = Paint()

        var iHeight = 0f
        for (i in bmps.indices) {
            var bmp: Bitmap? = bmps[i]

            bigCanvas.drawBitmap(bmp, 0f, iHeight, paint)
            iHeight += bmp!!.height.toFloat()

            bmp.recycle()
            bmp = null
        }

        return bigBitmap
    }

    /**
     * Saves the image as PNG to the app's private external storage folder.
     * @param image Bitmap to save.
     * @return Uri of the saved file or null
     */
    private fun saveImageExternal(image: Bitmap, callback: (Uri?) -> Unit) {
        doAsync {
            var uri: Uri? = null
            try {
                val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "quickinfoshared.png")
                val stream = FileOutputStream(file)
                image.compress(Bitmap.CompressFormat.PNG, 90, stream)
                stream.close()
                uri = Uri.fromFile(file)
            } catch (e: IOException) {
                println("exception while saving image to external")
            }
            callback(uri)
        }
    }

    /**
     * Shares the image from Uri.
     * @param uri Uri of image to share.
     */
    private fun shareImageUri(uri: Uri) {

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            this.putExtra(Intent.EXTRA_STREAM, uri)
            this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "image/png"
        }
        startActivity(Intent.createChooser(sendIntent, "Send To"))
    }
}
