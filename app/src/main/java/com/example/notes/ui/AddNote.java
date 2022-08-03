package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddNote extends AppCompatActivity {
    ImageView img;
    final static int REC_CODE_CAMERA=1;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    AlertDialog alertDialog;
    ScrollView scrollView;
    EditText title_add_note,note_add_note;
    FloatingActionButton add_note_to_firebase;
    DatabaseReference mDatabase;
    FirebaseDatabase database;
    FirebaseStorage storage;
    StorageReference storageReference;
    //uri to image
    Uri filePath;
    information info;
    String strDate,id;
    Task<Uri> taskimage;

    StorageReference ref;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);


        title_add_note=findViewById(R.id.title_add_note);
        note_add_note=findViewById(R.id.note_add_note);
        img=findViewById(R.id.image_add_note);
        add_note_to_firebase=findViewById(R.id.add_note_to_firebase);

        //firebase
        // this root of database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("note").child(mAuth.getCurrentUser().getUid());

        storageReference=FirebaseStorage.getInstance().getReference("note").child(mAuth.getCurrentUser().getUid());

        info =new information();
        add_note_to_firebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title_add_note.getText().toString().equals("")||note_add_note.getText().toString().equals("")){
                    Toast.makeText(AddNote.this,"Enter the information",Toast.LENGTH_LONG).show();
                }else {
                    //write data to firebase
                    Date date = Calendar.getInstance().getTime();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                    strDate = dateFormat.format(date);
                    id=mDatabase.push().getKey();
                    if (filePath != null){
                        ref = storageReference.child("normal").child(id);
                        UploadTask uploadTask = ref.putFile(filePath);
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                // Continue with the task to get the download URL
                                return ref.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    String downloadURL = downloadUri.toString();
                                    mDatabase.child("normal").child(id).child("imageid").setValue(downloadURL);
                                    Toast.makeText(getApplicationContext(),"The image upload process succeeded ",Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(),"The image upload process failed ",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    info = new information(strDate,title_add_note.getText().toString() ,note_add_note.getText().toString() );
                    mDatabase.child("normal").child(id).setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"The addition process succeeded ",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(AddNote.this,MainActivity.class);
                                    startActivity(intent);
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"The addition process failed ",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


















    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.addnote_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.addimage_camera:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkAndRequestPermissions()) {
                        openCamera();
                    }
                } else {
                    openCamera();
                }
                break;
            case R.id.addimage_gallary:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(AddNote.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        openGallery();
                    } else {
                        ActivityCompat.requestPermissions(AddNote.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 401);
                    }
                } else {
                    openGallery();
                }
                break;
///for change background of addnote
            case R.id.change_color:
                scrollView=findViewById(R.id.scrolladdnote);

                AlertDialog.Builder alertBuilder= new AlertDialog.Builder(AddNote.this);
                View view= LayoutInflater.from(AddNote.this).inflate(R.layout.color_of_add_note,null,false);
                RadioGroup radioGroup1=view.findViewById(R.id.radioGroup1);
                RadioGroup radioGroup2=view.findViewById(R.id.radioGroup2);

                alertBuilder.setView(view);
                alertDialog = alertBuilder.create();
                alertDialog.show();

                radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        switch (i){
                            case R.id.white:
                                scrollView.setBackgroundColor(getResources().getColor(R.color.white));
                                alertDialog.dismiss();
                                break;
                            case R.id.red:
                                scrollView.setBackgroundColor(getResources().getColor(R.color.red));
                                alertDialog.dismiss();
                                break;
                            case R.id.blue:
                                scrollView.setBackgroundColor(getResources().getColor(R.color.blue));
                                alertDialog.dismiss();
                                break;
                            case R.id.green:
                                scrollView.setBackgroundColor(getResources().getColor(R.color.green));
                                alertDialog.dismiss();
                                break;
                        }
                    }
                });
                radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        switch (i) {
                            case R.id.yellow:
                                scrollView.setBackgroundColor(getResources().getColor(R.color.yellow));
                                alertDialog.dismiss();
                                break;
                            case R.id.pink:
                                scrollView.setBackgroundColor(getResources().getColor(R.color.pink));
                                alertDialog.dismiss();
                                break;
                            case R.id.redwhite:
                                scrollView.setBackgroundColor(getResources().getColor(R.color.redwhite));
                                alertDialog.dismiss();
                                break;
                            case R.id.symbolic:
                                scrollView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                alertDialog.dismiss();
                                break;
                        }
                    }
                });
                break;


            case R.id.sizeoftitleandnote:
                alertBuilder= new AlertDialog.Builder(AddNote.this);
                view= LayoutInflater.from(AddNote.this).inflate(R.layout.sizeofaddnote,null,false);
                RadioGroup radioGroup4=view.findViewById(R.id.radioGroup4);
                alertBuilder.setView(view);
                alertDialog = alertBuilder.create();
                alertDialog.show();
                radioGroup4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        switch (i) {
                            case R.id.small:
                                title_add_note.setTextSize(20);
                                note_add_note.setTextSize(20);
                                alertDialog.dismiss();
                                break;
                            case R.id.medium:
                                title_add_note.setTextSize(40);
                                note_add_note.setTextSize(40);
                                alertDialog.dismiss();
                                break;
                            case R.id.large:
                                title_add_note.setTextSize(60);
                                note_add_note.setTextSize(60);
                                alertDialog.dismiss();
                                break;
                        }
                    }
                });
                break;
            case R.id.styleoftitleandnote:
                alertBuilder= new AlertDialog.Builder(AddNote.this);
                view= LayoutInflater.from(AddNote.this).inflate(R.layout.styleofaddnote,null,false);
                RadioGroup radioGroup3=view.findViewById(R.id.radioGroup3);
                alertBuilder.setView(view);
                alertDialog = alertBuilder.create();
                alertDialog.show();
                radioGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        switch (i) {
                            case R.id.normal:
                                title_add_note.setTypeface(Typeface.DEFAULT_BOLD);
                                note_add_note.setTypeface(Typeface.DEFAULT_BOLD);
                                alertDialog.dismiss();
                                break;
                            case R.id.italic:
                                title_add_note.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                                note_add_note.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                                alertDialog.dismiss();
                                break;
                            case R.id.bold:
                                title_add_note.setTypeface(Typeface.DEFAULT);
                                note_add_note.setTypeface(Typeface.DEFAULT);
                                alertDialog.dismiss();
                                break;
                        }
                    }
                });
                break;

                //////////////////////////////////
            case R.id.Addtofavorite_menu:
                if(title_add_note.getText().toString().equals("")||note_add_note.getText().toString().equals("")){
                    Toast.makeText(AddNote.this,"Enter the information",Toast.LENGTH_LONG).show();
                }else {
                    //write data to firebase
                    Date date = Calendar.getInstance().getTime();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                    strDate = dateFormat.format(date);
                    id=mDatabase.push().getKey();
                    if (filePath != null){
                        ref = storageReference.child("favorite").child(id);
                        UploadTask uploadTask = ref.putFile(filePath);
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                // Continue with the task to get the download URL
                                return ref.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    String downloadURL = downloadUri.toString();
                                    mDatabase.child("favorite").child(id).child("imageid").setValue(downloadURL);
                                    Toast.makeText(getApplicationContext(),"The image upload process succeeded to favorite",Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(),"The image upload process failed to favorite",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    info = new information(strDate,title_add_note.getText().toString() ,note_add_note.getText().toString() );
                    mDatabase.child("favorite").child(id).setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"The addition process succeeded to favorite",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"The addition process failed to favorite",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;

            case R.id.deletenote_menu:

                break;
            case R.id.help:

                break;
        }
        return true;
    }




    //for save phote
    //Permissions for application
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //premission camera
        if (requestCode == 502 ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
        else if(requestCode == 501) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 502);
    }
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 501);
    }
    private boolean checkAndRequestPermissions() {
        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permissionReadStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (permissionReadStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 402);
            return false;
        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 501 && resultCode == RESULT_OK && data != null) {
            Uri selectedURI = data.getData();
            filePath = data.getData();
            try {
                Bitmap bitmap = convert_UriToBitmap(selectedURI);
                img.setImageBitmap(bitmap);
                img.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),"Failed to capture the picture",Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 502 && resultCode == RESULT_OK) {
                 filePath = data.getData();
                 Bitmap mphoto = (Bitmap) data.getExtras().get("data");
                 img.setImageBitmap(mphoto);
                 img.setVisibility(View.VISIBLE);
        }
        else{
            Toast.makeText(getApplicationContext(),"Failed to capture the picture",Toast.LENGTH_SHORT).show();
        }
    }


    private Bitmap convert_UriToBitmap(Uri selectedURI) throws IOException {
        return (Bitmap) MediaStore.Images.Media.getBitmap(getContentResolver(), selectedURI);
    }

}

