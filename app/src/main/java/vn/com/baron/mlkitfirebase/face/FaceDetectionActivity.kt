package vn.com.baron.mlkitfirebase.face

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import kotlinx.android.synthetic.main.activity_face_detection.*
import vn.com.baron.mlkitfirebase.R

class FaceDetectionActivity : AppCompatActivity() {

    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_detection)
        initView()
        initEvent()
    }

    private fun initView() {
        alertDialog = AlertDialog.Builder(this)
            .setMessage("Please wait...")
            .setCancelable(false)
            .create()
    }

    private fun initEvent() {
        btnDetect.setOnClickListener {
            val url = edtLink.text.toString()
            Glide.with(this)
                .asBitmap()
                .load(url)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        alertDialog.show()
                        runDetector(resource)
                        imageView.setImageBitmap(resource)
                    }
                })
//            cameraView.captureImage { _, byteArray ->
//                cameraView.onPause()
//                alertDialog.show()
//                var bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.width, cameraView.height, false)
//                runDetector(bitmap)
//            }
            graphicOverlay.clear()
        }

        btnClear.setOnClickListener {
            graphicOverlay.clear()
        }

        btnClearText.setOnClickListener {
            edtLink.setText("")
        }

        btnPaste.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            edtLink.setText(clipboardManager?.primaryClip?.getItemAt(0)?.text)
        }
    }

    private fun runDetector(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val options = FirebaseVisionFaceDetectorOptions.Builder().build()
        val detector = FirebaseVision.getInstance().getVisionFaceDetector(options)
        detector.detectInImage(image)
            .addOnSuccessListener { faces ->
                processFaceResult(faces)
            }.addOnFailureListener {
                it.printStackTrace()
            }
    }

    private fun processFaceResult(faces: MutableList<FirebaseVisionFace>) {
        faces.forEach {
            val bounds = it.boundingBox
            val boundsByScreen = Rect(
                bounds.left + imageView.left,
                bounds.top + imageView.top,
                bounds.right + imageView.left,
                bounds.bottom + imageView.top
            )
            val rectOverLay = RectOverlay(graphicOverlay, boundsByScreen)
            graphicOverlay.add(rectOverLay)
        }
        alertDialog.dismiss()
    }

//    override fun onStart() {
//        super.onStart()
//        cameraView.onStart()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        cameraView.onResume()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        cameraView.onPause()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        cameraView.onStop()
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        cameraView.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
}
