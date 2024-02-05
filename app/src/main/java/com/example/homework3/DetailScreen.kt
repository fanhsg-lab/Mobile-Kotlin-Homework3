package com.example.homework3

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavHostController) {

    fun saveImageToInternalStorage(uri: Uri, context: Context): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val timeStamp = System.currentTimeMillis()
                val fileName = "myImage_$timeStamp.jpg"
                val outputFile = File(context.filesDir, fileName)
                FileOutputStream(outputFile).use { outputStream ->
                    stream.copyTo(outputStream)
                }

                val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("lastImagePath", outputFile.absolutePath).apply()

                outputFile.absolutePath
            }
        } catch (e: Exception) {
            Log.e("saveImage", "Error saving image: ${e.message}")
            null
        }
    }


    fun saveTextToSharedPreferences(text: String, context: Context) {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("savedText", text).apply()
    }
    // State and context variables
    var title by remember { mutableStateOf("") }
    var selectedImagePath by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Launchers and side effects
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                selectedImagePath = saveImageToInternalStorage(it, context)
            }
        }
    )

    LaunchedEffect(Unit) {
        // Load the last saved image path, if needed
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val lastImagePath = sharedPreferences.getString("lastImagePath", null)
        selectedImagePath = lastImagePath

        val savedText = sharedPreferences.getString("savedText", "")
        title = savedText ?: ""
    }

    // UI Components
    Column(modifier = Modifier.padding(16.dp)) {
        // Text Field for title
        TextField(
            value = title,
            onValueChange = {
                title = it
                saveTextToSharedPreferences(title, context) // Save text whenever it changes
            },
            label = { Text("Title") }
        )

        // Image Display and Picker
        selectedImagePath?.let { path ->
            AsyncImage(
                model = File(path),
                contentDescription = "Saved Image",
                modifier = Modifier
                    .size(40.dp)
                    .padding(0.dp, 10.dp, 0.dp, 10.dp)
                    .clip(CircleShape)
                    .clickable {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            )
        } ?: run {
            Image(
                painter = painterResource(R.drawable.kermit),
                contentDescription = "Default Profile Picture",
                modifier = Modifier
                    .size(40.dp)
                    .padding(0.dp, 10.dp, 0.dp, 10.dp)
                    .clip(CircleShape)
                    .clickable {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            )
        }



        // Back to Home button
        Text(
            modifier = Modifier.clickable {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            },
            text = "Back to Home",
            color = Color.Red,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
@Preview
fun DetailScreenPreview() {
    HomeScreen(
        navController = rememberNavController()
    )
}
