package com.example.distrubutingdata

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.common.util.concurrent.ListenableFuture

class MainActivityOrig : AppCompatActivity() {
    private val mMessageEditText: EditText? = null
    private val mReplyHeadTextView: TextView? = null
    private val mReplyTextView: TextView? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // for camera
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)


        // listener for the send button
        findViewById<View>(R.id.button_submit).setOnClickListener { v: View? -> launchSecondActivity() }
    }

    fun launchSecondActivity() {
        Log.d(LOG_TAG, "Button clicked!")
        val intent = Intent(this, SecondActivity::class.java)
        val message = mMessageEditText!!.text.toString()
        intent.putExtra(EXTRA_MESSAGE, message)
        startActivityForResult(intent, TEXT_REQUEST)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    companion object {
        private val LOG_TAG = MainActivityOrig::class.java.simpleName

        /*
    // TextureView is required for the camera to show in the assigned pane
    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {

        }
        override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {

        }

        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture) {

        }
        override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {

        }
    }*/
        const val EXTRA_MESSAGE = "com.example.distributingdata.extra.MESSAGE"
        const val TEXT_REQUEST = 1
        const val REQUEST_IMAGE_CAPTURE = 1
    }
}