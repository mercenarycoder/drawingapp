package com.g.baseassignments;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.Toast;
import android.app.ProgressDialog;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Constants;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {
    private PaintView paintView;
    private FirebaseAuth mauth;
    Uri filePath;
    private StorageReference stRef;
    DatabaseReference dbRef;
    ImageButton undo,redo,clear_screen,change_back,blur_thing,save,lock,saved_ss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mauth=FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);
       //Firebase variables are initiated from here
        stRef=FirebaseStorage.getInstance().getReference().child("doodle");
        dbRef=FirebaseDatabase.getInstance().getReference().child("doodle2");
        //special declarations end here
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
                OpenGallery();
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
                    filePath=getImageUri(MainActivity.this,bitmap);
                    Random generator = new Random();
                    int n = 10000;
                    n = generator.nextInt(n);
                    String fname = "Image-"+ n +".jpg";

                    StorageReference stst=stRef.child(fname);
             UploadTask upup=stst.putFile(filePath);
             upup.addOnFailureListener(new OnFailureListener() {
             @Override
              public void onFailure(@NonNull Exception e) {

                 Toast.makeText(MainActivity.this,
                         "Failed ......................", Toast.LENGTH_SHORT).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(MainActivity.this,
                        "Shop Image uploaded Successfully ...", Toast.LENGTH_SHORT).show();

            }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
             if(task.isSuccessful())
             {
                 Toast.makeText(MainActivity.this,
                         "Shop Image uploaded Successfully 2...", Toast.LENGTH_SHORT).show();

             }
            }
            });
                    paintView.clear();
                }
                else
                {
            Toast.makeText(MainActivity.this,"Please Grant the Required Permissions",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null)
        {
            filePath=data.getData();
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fname = "Image-"+ n +".jpg";

            StorageReference stst=stRef.child(fname);
            UploadTask upup=stst.putFile(filePath);
            upup.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(MainActivity.this,
                            "Failed ......................", Toast.LENGTH_SHORT).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(MainActivity.this,
                            "Shop Image uploaded Successfully ...", Toast.LENGTH_SHORT).show();

                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(MainActivity.this,
                                "Shop Image uploaded Successfully 2...", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //save();
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
    //this method is a success
    private void SaveImage(Bitmap finalBitmap) {

//        String root = MainActivity.this.getApplicationContext().getFilesDir();
        File myDir = Environment.getExternalStorageDirectory();
        File subdir = new File(myDir.getAbsolutePath(),"/subdir/");
        if(!subdir.exists())
        {
            subdir.mkdirs();
        }
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir,fname);
        if (file.exists ()) {
             file.delete ();
        }
            try {
                //file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                Toast.makeText(MainActivity.this,"Image Saved",Toast.LENGTH_SHORT).show();
                paintView.clear();
            } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,"Image Not saved ",Toast.LENGTH_SHORT).show();
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
