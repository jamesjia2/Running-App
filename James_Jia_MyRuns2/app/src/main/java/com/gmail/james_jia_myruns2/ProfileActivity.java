package com.gmail.james_jia_myruns2;

/**
 * Created by James on 1/15/2017.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.soundcloud.android.crop.Crop;


public class ProfileActivity extends Activity {

    //user preference tags
    private static final String fill = " ";
    private static final String preferenceTag = "pref";
    private static final String editNameTag = "name";
    private static final String editEmailTag = "email";
    private static final String editPhoneTag = "phone";
    private static final String editGenderTag = "gender";
    private static final String editClassTag = "class";
    private static final String editMajorTag = "major";
    private static final String editPhotoTag = "photo";
    private static final String editGalleryTag = "gallery";

    //dialogue options for photo selection
    String[] pictureOptions = { "Open Camera", "Select from Gallery"};

    //log tag
    private String tag = "jj";

    public static final int maxGender = 2;
    public static final int size = 10;
    public static final int cameraCode = 1;
    public static final int galleryCode = 2;
    private static final String uriCode = "saved_uri";

    private Uri cameraUri;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        checkPermission();

        //intialize the uri as null for comparison later
        cameraUri = null;
        filePath = null;

        //find image view
        ImageView profile = (ImageView) findViewById(R.id.profile);

        //check if there's a saved state
        if (savedInstanceState != null) {

            //if the phone was not turned without a picture being taken
            if(savedInstanceState.getParcelable(uriCode)!=null) {
                cameraUri = savedInstanceState.getParcelable(uriCode);
                profile.setImageURI(cameraUri);
            }
            else{
                loadData();
            }
        }

        //if no saved instance state, load from memory
        else {
            //load saved user data
            loadData();
        }
    }

    //call camera when change button is clicked - now includes dialogue for camera/gallery
    public void onChangeClicked(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Profile Picture");

        builder.setItems(pictureOptions, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int selected) {

                //camera option selected
                if (selected == 0) {
                    //initialize temporary file
                    ContentValues values = new ContentValues(size);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                    cameraUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    //start camera activity
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
                    startActivityForResult(intent, cameraCode);
                }

                //gallery option selected
                else if (selected == 1) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), galleryCode);
                }
            }
        });
        builder.show();
    }

    //handle results of camera and cropping
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if(resultCode == RESULT_OK) {

            //take a picture with camera
            if (requestCode == cameraCode) {
                Crop.of(cameraUri, cameraUri).asSquare().start(this);
            }

            //load picture into imageview from gallery
            if (requestCode == galleryCode) {

                cameraUri = data.getData();
                filePath = getFilePath(cameraUri);

                Bitmap bm;
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                bm = BitmapFactory.decodeFile(filePath, btmapOptions);
                ImageView profile = (ImageView) findViewById(R.id.profile);
                profile.setImageBitmap(bm);


            }

            //crop picture from camera, load it into imageview
            else if (requestCode==Crop.REQUEST_CROP) {
                cameraUri = Crop.getOutput(data);
                Log.d(tag,"hi");
                filePath = getFilePath(cameraUri);

                Bitmap bm;
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                bm = BitmapFactory.decodeFile(filePath, btmapOptions);
                ImageView profile = (ImageView) findViewById(R.id.profile);
                profile.setImageBitmap(bm);
            }
        }
    }


    //get path of image given an uri - thanks http://stackoverflow.com/questions/13209494/how-to-get-the-full-file-path-from-uri
    public String getFilePath(Uri uri) {

        String[] strArr = { MediaStore.Images.Media.DATA };
        String str = null;
        Cursor cursor = getContentResolver().query(uri, strArr, null, null, null);
        if(cursor.moveToFirst()){;
            int column = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            str = cursor.getString(column);
        }
        cursor.close();
        return str;
    }



    //calls save data
    public void onSaveClicked(View view){
        saveData();
        Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
        finish();
    }

    //exit without saving
    public void onCancelClicked(View view){
        finish();
    }

    //loads profile data from memory
    private void loadData() {

        //initialize sharedpref
        SharedPreferences sharedPrefs = getSharedPreferences(preferenceTag, MODE_PRIVATE);

        //load name
        String nameText = sharedPrefs.getString(editNameTag, fill);
        if(nameText!=fill) {
            ((EditText) findViewById(R.id.editName)).setText(nameText);
        }

        //load email
        String emailText = sharedPrefs.getString(editEmailTag, fill);
        if(emailText!=fill) {
            ((EditText) findViewById(R.id.editEmail)).setText(emailText);
        }

        //load phone
        String phoneText = sharedPrefs.getString(editPhoneTag, fill);
        if(phoneText!=fill) {
            ((EditText) findViewById(R.id.editPhone)).setText(phoneText);
        }

        //load Class
        String classText = sharedPrefs.getString(editClassTag, fill);
        if(classText!=fill) {
            ((EditText) findViewById(R.id.editClass)).setText(classText);
        }

        //load Major
        String majorText = sharedPrefs.getString(editMajorTag, fill);
        if(majorText!=fill) {
            ((EditText) findViewById(R.id.editMajor)).setText(majorText);
        }


        //load Gender
        int clicked = sharedPrefs.getInt(editGenderTag, maxGender+1);
        if (clicked >= 0 && clicked<=maxGender) {
            RadioButton radioBtn = (RadioButton) ((RadioGroup) findViewById(R.id.radioGroup)).getChildAt(clicked);
            radioBtn.setChecked(true);
        }

        //load Photo from file
        String photoPath = sharedPrefs.getString(editGalleryTag, fill);
        if(photoPath != fill){
            filePath = photoPath;
            Bitmap bm;
            BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
            bm = BitmapFactory.decodeFile(filePath, btmapOptions);
            ImageView profile = (ImageView) findViewById(R.id.profile);
            profile.setImageBitmap(bm);
        }
        else{
            loadDefault();
        }

    }


    //saves user's form data
    private void saveData() {

        //intialize shared preferences and editor
        SharedPreferences sharedPrefs = getSharedPreferences(preferenceTag, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear();

        //Save name text
        String nameText = ((EditText) findViewById(R.id.editName)).getText().toString();
        editor.putString(editNameTag, nameText);

        //Save email text
        String emailText = ((EditText) findViewById(R.id.editEmail)).getText().toString();
        editor.putString(editEmailTag, emailText);

        //Save phone text
        String phoneText = ((EditText) findViewById(R.id.editPhone)).getText().toString();
        editor.putString(editPhoneTag, phoneText);

        //Save phone text
        String classText = ((EditText) findViewById(R.id.editClass)).getText().toString();
        editor.putString(editClassTag, classText);

        //Save phone text
        String majorText = ((EditText) findViewById(R.id.editMajor)).getText().toString();
        editor.putString(editMajorTag, majorText);

        //Save photo with filepath
        if(filePath !=null) {
            editor.putString(editGalleryTag, filePath);
        }

        //Save radio buttons
        RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int clicked = mRadioGroup.indexOfChild(findViewById(mRadioGroup.getCheckedRadioButtonId()));
        editor.putInt(editGenderTag, clicked);

        //commit changes
        editor.commit();
    }


    //saves instance state
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(uriCode, cameraUri);
    }

    //loads stock photo
    private void loadDefault() {
        ImageView profile = (ImageView) findViewById(R.id.profile);
        profile.setImageResource(R.drawable.default_profile);
    }

    //checks api version and camera and writing permissions
    private void checkPermission() {

        //reading, writing and camera permissions
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        //only need to check if version >23
        if(Build.VERSION.SDK_INT > 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, size);

            }
        }
    }



}