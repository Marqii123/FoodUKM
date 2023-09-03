package com.example.fyp_proj.Merchant.ui.AddMenu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fyp_proj.Merchant.testnavdrawer;
import com.example.fyp_proj.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditMenuActivity extends AppCompatActivity {
    int Image_Request_Code = 1;
    Uri FilePathUri;
    String Img, MenuName, Desc, Price, selectedkey, MerchName;
    String EditImg, Editmenuname, Editdesc, Editprice;
    TextInputEditText food, price, desc;
    ImageView menupic;
    Button editBtn, saveBtn, cancelBtn, browseBtn;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merchant_activity_edit_menu);

        storageReference = FirebaseStorage.getInstance().getReference("menu image");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Menu");

        Intent intent = getIntent();
        MenuData datas = intent.getParcelableExtra("Menu");
        selectedkey = datas.getmKey();

        Img = datas.getImg();
        MenuName = datas.getMenuname();
        Desc = datas.getDesc();
        Price = datas.getPrice();
        MerchName = datas.getMerchname();

        food = findViewById(R.id.editFood);
        price = findViewById(R.id.editPrice);
        desc = findViewById(R.id.editDesc);
        menupic = findViewById(R.id.editmenuImg);

        editBtn = findViewById(R.id.editBtn);
        browseBtn = findViewById(R.id.browsebutton);

        food.setText(MenuName);
        price.setText(Price);
        desc.setText(Desc);
        Picasso.get()
                .load(Img)
                .placeholder(R.drawable.applogo)
                .fit()
                .centerCrop()
                .error(R.drawable.ic_error)
                .into(menupic);

        browseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), Image_Request_Code);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FilePathUri == null) {
                    NotUpdateImage();
                }else {
                    UpdateImage();
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            Picasso.get().load(FilePathUri).into(menupic);
        }
    }
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }
    public void UpdateImage(){

        final StorageReference imgref = storageReference.child(System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
        imgref.putFile(FilePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                imgref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imgUrl = uri.toString();
                        Editmenuname = food.getText().toString().trim();
                        Editdesc = desc.getText().toString().trim();
                        Editprice = price.getText().toString().trim();
                        EditImg = imgUrl;

                        Toast.makeText(EditMenuActivity.this, "Updated ＼(^o^)／ ", Toast.LENGTH_SHORT).show();
                        MenuData updateEvent = new MenuData(MerchName, Editmenuname, Editprice, Editdesc, EditImg);
                        databaseReference.child(selectedkey).setValue(updateEvent);
                        openHomeActivity();

                    }
                });
            }
        });

    }
    public void NotUpdateImage(){

        Editmenuname = food.getText().toString().trim();
        Editdesc = desc.getText().toString().trim();
        Editprice = price.getText().toString().trim();
        EditImg = Img;

        Toast.makeText(EditMenuActivity.this, "Updated ＼(^o^)／ ", Toast.LENGTH_SHORT).show();
        MenuData updateEvent = new MenuData(MerchName, Editmenuname, Editprice, Editdesc, EditImg);
        databaseReference.child(selectedkey).setValue(updateEvent);
        openHomeActivity();

    }
    private void openHomeActivity() {
        Intent intent = new Intent(this, testnavdrawer.class);
        startActivity(intent);
    }
    public void onBackPressed(){
        AlertDialog.Builder alertdialog2 = new AlertDialog.Builder(EditMenuActivity.this);
        alertdialog2.setTitle("Cancel");
        alertdialog2.setMessage("Are you sure you want to cancel edit?");
        alertdialog2.setIcon(R.drawable.ic_error);

        alertdialog2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openHomeActivity();
            }
        });

        alertdialog2.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertdialog2.show();

    }
}