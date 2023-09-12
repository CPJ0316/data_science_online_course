package com.example.carema0216;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class ViewPagerAdapter extends PagerAdapter {
    // Context object
    Context context;

    // Array of images
    ArrayList<String> imagePaths = new ArrayList<>();

    // Layout Inflater
    LayoutInflater mLayoutInflater;

    // Viewpager Constructor
    public ViewPagerAdapter(Context context, ArrayList<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // return the number of images
        return imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        // inflating the item.xml
        //mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = mLayoutInflater.inflate(R.layout.galleryitem, container, false);

        //referencing the image view from the item.xml file
        ImageView imageView = itemView.findViewById(R.id.imageViewMain);

        // setting the image in the imageView
        String imagePath = imagePaths.get(position);
        Bitmap mBitmap = BitmapFactory.decodeFile(imagePath);
        if (mBitmap == null) {
            Log.d("DEBUG", "Bitmap is null for image path: " + imagePaths.get(position));
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
        imageView.setImageBitmap(rotatedBitmap);

        // Store the file path in the ImageView's tag
        imageView.setTag(imagePath);

        // Adding the View
        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        container.removeView((LinearLayout) object);
    }

    public void delete_Item( ViewGroup container, int position, @NonNull Object object) {
        Log.d("DEBUG", "delete\n");
        // 取得當前的 View
        View currentView = (View) object;
        // 取得當前照片的檔案路徑
        ImageView imageView = currentView.findViewById(R.id.imageViewMain);
        String filePath = (String) imageView.getTag();

        // 刪除對應的檔案
        File file = new File(filePath);
        if (file.exists()) {
            imagePaths.remove(filePath);
            file.delete();
        }

        // 移除指定位置上的 View
        container.removeView((LinearLayout) object);
        // 通知 ViewPager 重新載入畫面
        {
            Log.d("DEBUG", "size=0\n");
            //imagePaths.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        // 最简单解决 notifyDataSetChanged() 页面不刷新问题的方法
        return POSITION_NONE;
    }

}