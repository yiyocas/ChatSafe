package com.oropeza.utilidades;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.io.File;

/**
 * Created by yiyo on 05/09/15.
 */
public class ImageUtil {
    private static ImageUtil ourInstance = new ImageUtil();

    public static ImageUtil getInstance() {
        return ourInstance;
    }

    private ImageUtil() {
    }

    public Bitmap decodeFile(String filePath) {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 3072;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;

        int scale = 4;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeFile(filePath, o2);// Make bitmp object global

    }

    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public Bitmap getBitmap(String s){
        byte[] byteArray = Base64.decode(s, Base64.DEFAULT);

        //Log.i("Size","Antes=>"+byteArrayImage.length+" Despues=>"+byteArray.length);
        Bitmap b2 = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        Matrix matrix = new Matrix();
        //set image rotation value to 90 degrees in matrix.

        matrix.postRotate(0);
        //supply the original width and height, if you don't want to change the height and width of bitmap.
        b2 = Bitmap.createBitmap(b2, 0, 0, b2.getWidth(),b2.getHeight(), matrix, true);
    return b2;
    }
}
