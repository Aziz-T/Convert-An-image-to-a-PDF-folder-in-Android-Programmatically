package com.t.imagestopdf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private ImageView imageView;
    private Button button;
    String[] storagePermission;

    private LinearLayout linearLayout;
    int pos=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        button=findViewById(R.id.resimYukle);
   //     linearLayout = findViewById(R.id.line);







       button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkStoragePermission()){
                    requestStoragePermission();

                }else {
                    pickImage();
                }


            }
        });

       storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE };


    }
    private void pickImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(intent,120);

    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,1);
    }
    private boolean checkStoragePermission() {
        boolean result=  ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 120 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImageUri,filePath,null,null,null);
            cursor.moveToFirst();

            int columnIndex= cursor.getColumnIndex(filePath[0]);
            String myPath = cursor.getString(columnIndex);

            Bitmap bitmap = BitmapFactory.decodeFile(myPath);


            imageView.setImageBitmap(bitmap);

            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pi = new PdfDocument.PageInfo.Builder(bitmap.getWidth(),bitmap.getHeight(),1).create();
            PdfDocument.Page page = pdfDocument.startPage(pi);

            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#FFFFFF"));
            canvas.drawPaint(paint);

            bitmap= Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
            paint.setColor(Color.BLUE);
            canvas.drawBitmap(bitmap,0,0,null);

            pdfDocument.finishPage(page);

            File root = new File(Environment.getExternalStorageDirectory(),"PDF Folder 12");
            if(!root.exists()){
                root.mkdir();
            }
            File file = new File(root,"picture.pdf");
            try{
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                pdfDocument.writeTo(fileOutputStream);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException ee){
                ee.printStackTrace();
            }
            pdfDocument.close();


        }
    }


}