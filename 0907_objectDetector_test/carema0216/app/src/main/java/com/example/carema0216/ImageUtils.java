package com.example.carema0216;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class ImageUtils {
    public static Bitmap convertMatToBitmap(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    public static Mat convertBitmapToMat(Bitmap bitmap) {
        Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, mat);
        return mat;
    }

    public static void applyBlurToMat(Mat mat, int kernelSize) {
        Imgproc.GaussianBlur(mat, mat, new org.opencv.core.Size(kernelSize, kernelSize), 0);
    }

    public static Bitmap applyBlurToBitmap(Bitmap bitmap, int kernelSize) {
        Mat mat = convertBitmapToMat(bitmap);
        applyBlurToMat(mat, kernelSize);
        return convertMatToBitmap(mat);
    }

    public static Bitmap drawRectOnBitmap(Bitmap bitmap, RectF rect, Paint paint) {
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawRect(rect, paint);
        return mutableBitmap;
    }

    public static Bitmap drawRectsOnBitmap(Bitmap bitmap, RectF[] rects, Paint paint) {
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        for (RectF rect : rects) {
            canvas.drawRect(rect, paint);
        }
        return mutableBitmap;
    }
}
