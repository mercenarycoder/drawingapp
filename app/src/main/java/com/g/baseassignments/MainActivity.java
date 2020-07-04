package com.g.baseassignments;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private PaintView paintView;
    private FirebaseAuth mauth;
    ImageButton undo,redo,clear_screen,change_back,blur_thing,save,lock,saved_ss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mauth=FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);
        paintView = (PaintView) findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
        undo=(ImageButton)findViewById(R.id.undo);
        redo=(ImageButton)findViewById(R.id.redo);
        clear_screen=(ImageButton)findViewById(R.id.clear_screen);
        change_back=(ImageButton)findViewById(R.id.change_back);
        blur_thing=(ImageButton)findViewById(R.id.blur_thing);
        save=(ImageButton)findViewById(R.id.save);
        lock=(ImageButton)findViewById(R.id.lock);
        saved_ss=(ImageButton)findViewById(R.id.saved_ss);
        saved_ss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                  if(checkSelfPermission(Manifest.permission.MEDIA_CONTENT_CONTROL)!=PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission
                                .MEDIA_CONTENT_CONTROL}, 1);
                    }
                    else
                    {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                                Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                    }
                }
                else
                {

                }
                }
        });
        blur_thing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.blur();
            }
        });
        clear_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.clear();
            }
        });
        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //paintView.emboss();
                mauth.signOut();
                Intent intent=new Intent(MainActivity.this,PhoneLogin.class);
                startActivity(intent);
                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStoragePermissionGranted()&&isExternalStorageAvailable()&&isStoragePermissionGranted()) {
                    Bitmap bitmap = paintView.getmBitmap();
                    SaveImage(bitmap);
                    //save(bitmap);
                }
                else
                {
            Toast.makeText(MainActivity.this,"Please Grant the Required Permissions",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                        .WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
    private void save(Bitmap bitmap){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
        String filename = sdf.format(new Date());

        try {
            OutputStream fOut = null;
            File file = new File(Environment.getExternalStorageDirectory()+File.separator+Environment.DIRECTORY_PICTURES
                    +File.separator+"MYFile");
            if (!file.exists()) {
                file.mkdirs();
                Toast.makeText(MainActivity.this,"Directory or folder made",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(MainActivity.this,"Directory or folder not made",Toast.LENGTH_SHORT).show();
            }
            fOut = new FileOutputStream(file+File.separator+"Image" + ".png");
            //your bitmap
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            paintView.clear();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void SaveImage(Bitmap finalBitmap) {

//        String root = MainActivity.this.getApplicationContext().getFilesDir();
        File myDir = new File(Environment.getExternalStorageDirectory(), "saved_images");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File subdir = new File(myDir,"subdir");
        if(!subdir.exists())
        {
            subdir.mkdirs();
        }
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpeg";
        File file = new File (myDir,fname);
        if (file.exists ()) {
             file.delete ();
        }
            try {
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                paintView.clear();

            } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.normal:
                paintView.normal();
                return true;
            case R.id.emboss:
                paintView.emboss();
                return true;
            case R.id.blur:
                paintView.blur();
                return true;
            case R.id.clear:
                paintView.clear();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
