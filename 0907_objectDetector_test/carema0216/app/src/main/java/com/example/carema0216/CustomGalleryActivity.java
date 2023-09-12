package com.example.carema0216;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.util.ArrayList;

public class CustomGalleryActivity extends AppCompatActivity {

    ArrayList<String> f = new ArrayList<>(); //ist of file paths
    File[] listFile;
    private String folderName = "MyPhotoDir";
    // Creating object of ViewPager
    ViewPager mViewPager;
    // Creating Object of ViewPagerAdapter
    ViewPagerAdapter mViewPagerAdapter;
    Button mBtnReturn, mBtnDel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        getFromSdcard();
        // Initializing the ViewPager Object
        mViewPager = findViewById(R.id.viewPagerMain);


        mBtnReturn = findViewById(R.id.btnReturn);
        mBtnDel = findViewById(R.id.btnDel);
        mBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomGalleryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        mBtnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = mViewPager.getCurrentItem();
                View currentView = mViewPager.getChildAt(currentItem);
                //mViewPagerAdapter.destroyItem(mViewPager, currentItem, currentView)
                mViewPagerAdapter.delete_Item(mViewPager, currentItem, currentView);
            }
        });
        // Initializing the ViewPagerAdapter
        mViewPagerAdapter = new ViewPagerAdapter(this, f);
        // Adding the Adapter to the ViewPager
        mViewPager.setAdapter(mViewPagerAdapter);
    }

    public void getFromSdcard() {
        //File file = new File(getExternalFilesDir(folderName), "/");
        File picturesFolder = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File file = new File(picturesFolder, "CAMERA_APP");
        if (file.isDirectory()) {
            listFile = file.listFiles();
            for (int i = listFile.length - 1; i >= 0; i--) {
                f.add(listFile[i].getAbsolutePath());
            }
        }
    }
}


