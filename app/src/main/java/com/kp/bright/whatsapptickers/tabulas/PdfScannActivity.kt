package com.kp.bright.whatsapptickers.tabulas

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.kp.bright.whatsapptickers.databinding.ActivityPdfScannBinding
import com.kp.bright.whatsapptickers.tabulas.detecttable.PdfTableExtractor
import com.kp.bright.whatsapptickers.tabulas.detecttable.saveToJsonFile
import java.io.File
import java.io.FileOutputStream


class PdfScannActivity : AppCompatActivity() {

    val binding: ActivityPdfScannBinding by lazy {
        ActivityPdfScannBinding.inflate(layoutInflater)
    }

    private val storagePermissionCode = 101
    private lateinit var pdfPickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        intiView()

    }

    private fun intiView() {
        pdfPickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                Log.d("PDFPicker", "File Path: ${result.resultCode}  ${result.data?.data}")

                if (result.resultCode == RESULT_OK) {
                    val data: Intent? = result.data
                    data?.data?.let { uri ->
                        val filePath = getFilePathFromUri(uri)
                        filePath?.let {
                            Log.d("PDFPicker", "File Path: $it")
                            // Replace with actual PDF file path
                            val pdfFile = File(it)

                            if (pdfFile.exists()) {

                                //================================================================
                                //Give better but have all over
                                //================================================================
//                                val extractor = PDFTableExtractor(pdfFile)
                                // Extract the table as a list of records
//                                val records = extractor.extractTable()
//                                // Convert the list of records to JSON
//                                val json = extractor.convertToJSON(records)
                                // Save the JSON to a file
//                                saveToJsonFile(extractor.extractText())


//                                var list =extractTextPositions(pdfFile)
//                                saveToJsonFile(Gson().toJson(list))

//                                extractTableSaveCSV(pdfFile)

                                var json = PdfTableExtractor.extractTablesFromPdf(pdfFile.absolutePath)
                                saveToJsonFile(Gson().toJson(json))
//                                Log.d("PDFTableExtractor", json)
                            } else {
                                Log.e("PDFTableExtractor", "PDF file not found")
                            }

                        }
                    }
                }
            }

        binding.btnPickPdf.setOnClickListener {
            checkDocPermissin()
        }


    }

    private fun checkDocPermissin() {
//        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//            == PackageManager.PERMISSION_GRANTED
//        ) {
        openPdfPicker()
//        } else {
//            requestPermissions(
//                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                storagePermissionCode
//            )
//        }
    }

    private fun openPdfPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        pdfPickerLauncher.launch(intent)
    }

    private fun getFilePathFromUri(uri: Uri): String? {
        val fileName = getFileName(uri)
        val file = File(cacheDir, fileName)
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String {
        var name = "document.pdf"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                name = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return name
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == storagePermissionCode) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                openPdfPicker()
            } else {
                Toast.makeText(
                    this,
                    "Storage permission is required to pick a PDF",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}