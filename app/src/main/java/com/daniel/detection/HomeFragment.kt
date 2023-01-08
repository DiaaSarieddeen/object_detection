package com.daniel.detection

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.daniel.detection.databinding.FragmentHomeBinding
import com.daniel.detection.ml.ModelM
import kotlinx.android.synthetic.main.fragment_home.*
import org.tensorflow.lite.support.image.TensorImage


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var launchBtn: Button
    private lateinit var loadBtn: Button
    private lateinit var image: ImageView
    private lateinit var output: TextView
    private lateinit var pitchBar: SeekBar
    private lateinit var speedBar: SeekBar
    private lateinit var translateBtn: Button
    private var bitMapPhoto: Bitmap? = null

    @SuppressLint("IntentReset")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(layoutInflater)
        launchBtn = binding.cameraButton
        loadBtn = binding.loadButton
        image = binding.imageView
        output = binding.textView
        pitchBar = binding.seekBarPitch
        speedBar = binding.seekBarSpeed
        translateBtn=binding.translateButton



        launchBtn.setOnClickListener {
            if (context?.let { it1 -> ContextCompat.checkSelfPermission(it1, android.Manifest.permission.CAMERA) }
                    != PackageManager.PERMISSION_GRANTED
            ) {
                    sendRequest.launch(android.Manifest.permission.CAMERA)
            } else {
                    launchCam.launch(null)
            }

        }

        loadBtn.setOnClickListener {
            if (context?.let { it1 -> ContextCompat.checkSelfPermission(it1, android.Manifest.permission.READ_EXTERNAL_STORAGE) }
                != PackageManager.PERMISSION_GRANTED
            ) {
                loadImage.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type="image/*"
                selectImage.launch(intent.type)
            }
        }

        //Now I have the Intent of moving data from Home fragment to translation fragment
        translateBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment2_to_translationFragment)
            val fragment= TranslationFragment() //fragment we want to move data to
            val bundle=Bundle()
            bundle.putString("output", textView.text.toString())
            fragment.arguments=bundle
            //now accomplish the transaction process
            parentFragmentManager.beginTransaction().replace(R.id.fragment,fragment).commit()
        }


        return binding.root
    }

    private val launchCam = registerForActivityResult(ActivityResultContracts.TakePicturePreview())
    { imageTaken ->
        if (imageTaken != null) {
            imageView.setImageBitmap(imageTaken)
            modelOutput(imageTaken)
        } else {
            Toast.makeText(context, "No image!", Toast.LENGTH_LONG).show()
        }
    }

    private val sendRequest = registerForActivityResult(ActivityResultContracts.RequestPermission())
    { grantedPer ->
        if (grantedPer == true) {
            launchCam.launch(null)
        } else {
            Toast.makeText(context, "Permission Denied!", Toast.LENGTH_LONG).show()
        }
    }

    private fun modelOutput(bitmap: Bitmap)
    {
        val model= context?.let { ModelM.newInstance(it) }
        val image2=TensorImage.fromBitmap(bitmap)
        val output= model?.process(image2)
        val detection= output?.detectionResultList?.get(0)
        detection?.locationAsRectF
        detection?.categoryAsString
        detection?.scoreAsFloat

        if (detection != null) {
            textView.text=detection.categoryAsString
        }
    }

    private val loadImage = registerForActivityResult(ActivityResultContracts.RequestPermission())
    { grantedPer2 ->
        if (grantedPer2) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selectImage.launch(intent.type)

        } else {
            Toast.makeText(context, "Permission Denied!", Toast.LENGTH_LONG).show()
        }
    }

    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent())
    {loadedImage ->
        imageView.setImageURI(loadedImage)
        try{
            if(Uri.parse(loadedImage.toString())!=null)
            {

                bitMapPhoto = MediaStore.Images.Media.getBitmap(activity?.contentResolver,loadedImage)
                imageView.setImageBitmap(bitMapPhoto!!)
                modelOutput(bitMapPhoto!!)
            }
        }
        catch(E : Exception)
        {
                Toast.makeText(context,"Null value!",Toast.LENGTH_SHORT)
        }
    }
}
