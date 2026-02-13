package com.example.firstml

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class MainActivity : AppCompatActivity() {
    private lateinit var resultView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        resultView = findViewById(R.id.resultTextDisplay)

        val cameraButton = findViewById<Button>(R.id.camera)
        cameraButton.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(intent.resolveActivity(packageManager) != null){
                startActivityForResult(intent,123)
            }else{
                Toast.makeText(this, "No Camera Found", Toast.LENGTH_SHORT).show()

            }

        }
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123 && resultCode == RESULT_OK){
            val extras = data?.extras
            val bitmap = extras?.get("data") as? Bitmap
            if(bitmap != null) {
                detectFace(bitmap)
            }

        }

    }

    fun detectFace(bitmap: Bitmap) {
        // High-accuracy landmark detection and face classification
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()


        val faceDetector = FaceDetection.getClient(options)

        val image = InputImage.fromBitmap(bitmap, 0)

        faceDetector.process(image)
            .addOnSuccessListener { faces ->
                var resultText = " "
                var i = 1
                for (face in faces) {
                    resultText = "Face Number? : $i" +
                            "\nSmile : ${face.smilingProbability?.times(100)}%" +
                            "\nLeft Eye Open : ${face.leftEyeOpenProbability?.times(100)}%" +
                            "\nRight Eye Open : ${face.rightEyeOpenProbability?.times(100)}%"
                    i++
                }
//                var resultText = ""
//                for ((index, face) in faces.withIndex()) {
//                    // Use ?.let or ?: to handle null probabilities
//                    val smile = face.smilingProbability?.let { (it * 100).toInt() } ?: "Unknown"
//                    val leftEye = face.leftEyeOpenProbability?.let { (it * 100).toInt() } ?: "Unknown"
//                    val rightEye = face.rightEyeOpenProbability?.let { (it * 100).toInt() } ?: "Unknown"
//
//                    resultText += "Face ${index + 1}:\n" +
//                            "Smile: $smile%\n" +
//                            "Left Eye: $leftEye%\n" +
//                            "Right Eye: $rightEye%\n\n"
//                }
                if (faces.isEmpty()) {
                    Toast.makeText(this, "No Face Detected", Toast.LENGTH_SHORT).show()
                }else{
                    resultView.text = resultText.trim()
                }

            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
            }


    }
}