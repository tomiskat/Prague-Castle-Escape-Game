package escape.game.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Toast

object ImageSaver {

    fun saveImage(context: Context, bitmap: Bitmap, contentValues: ContentValues) {
        val contentResolver = context.contentResolver
        val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (imageUri != null) {
            contentResolver.openOutputStream(imageUri).use { outputStream ->
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }
        Toast.makeText(context, "Error, try screenshot", Toast.LENGTH_SHORT).show()
    }
}