package com.android.example.cameraxapp

import android.content.ActivityNotFoundException
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.EditText
import android.widget.TextView
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraX
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import com.example.distrubutingdata.ResultsActivity
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias LumaListener = (luma: Double) -> Unit


class MainActivity : AppCompatActivity() {
    //private lateint var binding: ActivityMainBinding
    private lateint var binding: ActivityMainBinding?
    //private lateint var binding: MainActivityBinding?
    private val resultsView: TextView? = null
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(binding.root)
        //setContentView(R.layout.activity_main)        // old method

        // for camera
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture!!.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))

        // Request camera permissions
        if (allPermissionsGranted()) {
           startCamera()
        } else {
           ActivityCompat.requestPermissions(
               this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listeners for take photo and video capture buttons
        //binding.imageCaptureButton take_photo_view = findViewById<View>(R.id.button_take_photo);
        binding.imageCaptureButton.setOnClickListener { takePhoto() }
        //binding.videoCaptureButton.setOnClickListener { captureVideo() }  // we don't have a video capture button

        cameraExecutor = Executors.newSingleThreadExecutor()

        // listener for the submit button
        //findViewById<View>(R.id.button_submit).setOnClickListener { v: View? -> launchResultsActivity() }
        binding.imageSubmitButton.setOnClickListener { takePhoto() }
        binding.name.text = viewModel.name
        binding.button

    }

    fun bindPreview(cameraProvider : ProcessCameraProvider) {
        var preview : Preview = Preview.Builder()
                .build()

        var cameraSelector : CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

        preview.setSurfaceProvider(previewView.getSurfaceProvider())

        var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
    }
    fun launchMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivityForMain(intent, MainActivity.TEXT_REQUEST)
    }
    
    private fun startActivityForMain(this: Intent, textRequest: Int) {

    }

    fun launchResultsActivity() {
        Log.d(MainActivity.LOG_TAG, "photo submitted!")
        val intent = Intent(this, ResultsActivity::class.java)
        startActivityForResult(intent, MainActivity.TEXT_REQUEST)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, MainActivity.REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    private fun takePhoto() {
        requireActivity().run{
            startActivity(Intent(this, CameraX))
        }
    }

    private fun captureVideo() {}

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                    }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
       ContextCompat.checkSelfPermission(
           baseContext, it) == PackageManager.PERMISSION_GRANTED
   }

    override fun onDestroy() {
       super.onDestroy()
       cameraExecutor.shutdown()
   }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults:
            IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startMyCamera()
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {
        private val LOG_TAG = MainActivity::class.java.simpleName
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10

        const val EXTRA_MESSAGE = "com.example.distributingdata.extra.MESSAGE"
        const val TEXT_REQUEST = 1
        const val REQUEST_IMAGE_CAPTURE = 1
        private val REQUIRED_PERMISSIONS =
                mutableListOf (
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                ).apply {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                        add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }.toTypedArray()
    }

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

}
