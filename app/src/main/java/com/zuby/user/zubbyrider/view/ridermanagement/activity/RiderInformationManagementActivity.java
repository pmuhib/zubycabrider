package com.zuby.user.zubbyrider.view.ridermanagement.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.maps.model.LatLng;
import com.zuby.user.zubbyrider.R;
import com.zuby.user.zubbyrider.databinding.ActivityRiderInformationManagementBinding;
import com.zuby.user.zubbyrider.interfaces.ResultInterface;
import com.zuby.user.zubbyrider.utils.ApiKeys;
import com.zuby.user.zubbyrider.utils.BaseActivity;
import com.zuby.user.zubbyrider.utils.PreferenceConnector;
import com.zuby.user.zubbyrider.utils.Utility;
import com.zuby.user.zubbyrider.view.navigationdrawer.activity.GPSTracker;
import com.zuby.user.zubbyrider.view.registration_login.activity.OtpVarifyActivity;
import com.zuby.user.zubbyrider.view.registration_login.model.GetRiderDataModel;
import com.zuby.user.zubbyrider.view.registration_login.model.SetPasswordModel;
import com.zuby.user.zubbyrider.view.registration_login.model.UpdatePasswordModel;
import com.zuby.user.zubbyrider.view.registration_login.model.UpdateRiderDataModel;
import com.zuby.user.zubbyrider.view.registration_login.presenter.AddRiderDataPresenter;
import com.zuby.user.zubbyrider.view.registration_login.presenter.GetRiderDataPresenter;
import com.zuby.user.zubbyrider.view.registration_login.presenter.UpdatePasswordPresenter;
import com.zuby.user.zubbyrider.view.registration_login.presenter.UpdateRiderDataPresenter;
import com.zuby.user.zubbyrider.view.registration_login.presenter.setPasswordPresenter;
import com.zuby.user.zubbyrider.view.ridermanagement.util.HttpConnectorSendJsonDataLatest;
import com.zuby.user.zubbyrider.view.ridermanagement.util.UploadImage;
import com.zuby.user.zubbyrider.view.tokengenerate.GenerateTokenClass;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static com.zuby.user.zubbyrider.view.navigationdrawer.activity.MainActivity.PERMISSION;

public class RiderInformationManagementActivity extends BaseActivity implements ResultInterface, View.OnClickListener {
    private String mArea, mCity, mLatitude, mLongitude;
    private static final String TAG = RiderInformationManagementActivity.class.getSimpleName();
    private ActivityRiderInformationManagementBinding mActivityRiderInformationManagementBinding;
    private Boolean mIsRiderData = false;
    private String mFileUri = "";
    private int REQUEST_CAMERA = 111, SELECT_FILE = 112;
    static Uri selectedImageUri;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityRiderInformationManagementBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_rider_information_management);
        setSupportActionBar(mActivityRiderInformationManagementBinding.toolbar);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Uploading image...");
        mProgressDialog.setCancelable(false);
        mActivityRiderInformationManagementBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActivityRiderInformationManagementBinding.riderProfileData.setpassword.addPass.getVisibility() == View.VISIBLE) {
                    mActivityRiderInformationManagementBinding.riderProfileData.datashow.setVisibility(View.VISIBLE);
                    mActivityRiderInformationManagementBinding.riderProfileData.nodata.setVisibility(View.GONE);
                    mActivityRiderInformationManagementBinding.fab.setVisibility(View.VISIBLE);
                    mActivityRiderInformationManagementBinding.riderProfileData.setpassword.addPass.setVisibility(View.GONE);
                } else {
                    onBackPressed();
                }
            }
        });
        getDetails();
        getRiderData();
        mActivityRiderInformationManagementBinding.fab.setOnClickListener(this);
        mActivityRiderInformationManagementBinding.riderProfileData.passwordData.setOnClickListener(this);
        mActivityRiderInformationManagementBinding.riderProfileData.setpassword.reset.
                setOnClickListener(this);
        mActivityRiderInformationManagementBinding.riderProfileData.editImage.setOnClickListener(this);
    }

    public static String ConvertBitmapToString(Bitmap bitmap) {
        String encodedImage = "";

//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//        try {
//            encodedImage = URLEncoder.encode(Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT), "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    String image = "";

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                selectedImageUri = data.getData();
                mFileUri = getPath(context, selectedImageUri);
                File file = new File(mFileUri);
                long length = file.length() / (1024 * 1024);
                Bitmap bitmap;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                    image = ConvertBitmapToString(resizedBitmap);
//                convertToString(image);
                    new UploadFile().execute();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e("error", "error");
                }
            } else if (requestCode == REQUEST_CAMERA) {

            }

        }
        if (requestCode == 0) {
            if (data != null) {
                String firstname = data.getStringExtra("firstname");
                String lastname = data.getStringExtra("lastname");
                String email = data.getStringExtra("email");
                Log.e("result", "dhvsjvgahjvhg" + mArea + "\n " + firstname + "\n" + lastname + "\n" + email);
                mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.VISIBLE);
                new AddRiderDataPresenter().show(new ResultInterface() {
                                                     @Override
                                                     public void onSuccess(Object object) {
                                                         Toast.makeText(context, "Rider data added successfully", Toast.LENGTH_LONG).show();
                                                         getRiderData();
                                                     }

                                                     @Override
                                                     public void onFailed(String string) {
                                                         Toast.makeText(context, string, Toast.LENGTH_LONG).show();
                                                     }
                                                 }, context, PreferenceConnector.readString(context, ApiKeys.COUNTRY_CODE, ""),
                        PreferenceConnector.readString(context, ApiKeys.MOBILE, ""),
                        firstname, lastname, "", "English",
                        mCity, mLatitude, mLongitude, email, mArea);
            }
        } else if (requestCode == 1) {
            try {
                String firstname = data.getStringExtra("firstname");
                String lastname = data.getStringExtra("lastname");
                String email = data.getStringExtra("email");
                Log.e("result", "dhvsjvgahjvhg" + "\n " + firstname + "\n" + lastname + "\n" + email);
                mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.VISIBLE);
                new UpdateRiderDataPresenter().show(new ResultInterface() {
                    @Override
                    public void onSuccess(Object object) {
                        mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.GONE);
                        UpdateRiderDataModel updateRiderDataModel = (UpdateRiderDataModel) object;
                        Toast.makeText(context, updateRiderDataModel.getMessage(), Toast.LENGTH_LONG).show();
                        getRiderData();
                    }

                    @Override
                    public void onFailed(String string) {
                        Toast.makeText(context, string, Toast.LENGTH_LONG).show();
                        mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.GONE);
                    }
                }, context, firstname, lastname, email);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {

        }
    }

    public void getDetails() {
        GPSTracker mGps = new GPSTracker(context);
        if (mGps.canGetLocation()) {

            double latitude = mGps.getLatitude();
            double longitude = mGps.getLongitude();
            mLatitude = "" + latitude;
            mLongitude = "" + longitude;
            if (latitude == 0) {

            } else {
                LatLng coordinate = new LatLng(latitude, longitude);
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(context, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    mArea = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    mCity = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    Log.e(TAG, mArea + "\n" + mCity + "\n" + mLatitude + "\n" + mLongitude);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onSuccess(Object object) {
        mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.GONE);
        if (object instanceof GetRiderDataModel) {
            mIsRiderData = true;
            GetRiderDataModel getRiderDataModel = (GetRiderDataModel) object;
            Toast.makeText(context, getRiderDataModel.getMessage(), Toast.LENGTH_LONG).show();
            PreferenceConnector.writeString(context, ApiKeys.IMAGEPATH, getRiderDataModel.getData().getProfile_image_path());
            mActivityRiderInformationManagementBinding.riderProfileData.ccp1.setActivated(false);
            mActivityRiderInformationManagementBinding.riderProfileData.ccp1.dispatchSetSelected(false);
            mActivityRiderInformationManagementBinding.riderProfileData.fname.setText(getRiderDataModel.getData().getFirst_name());
            mActivityRiderInformationManagementBinding.riderProfileData.lname.setText(getRiderDataModel.getData().getLast_name());
            mActivityRiderInformationManagementBinding.riderProfileData.pnumber.setText(PreferenceConnector.readString(context, ApiKeys.MOBILE, ""));
            mActivityRiderInformationManagementBinding.riderProfileData.email.setText("" + getRiderDataModel.getData().getEmail_id());
            if (PreferenceConnector.readString(context, ApiKeys.HASPASS, "").equalsIgnoreCase("no")) {
                mActivityRiderInformationManagementBinding.riderProfileData.password.setInputType(InputType.TYPE_CLASS_TEXT);
                mActivityRiderInformationManagementBinding.riderProfileData.password.setText("Set a Password");
            } else {
                mActivityRiderInformationManagementBinding.riderProfileData.password.setText("anup");
            }
            String path = ApiKeys.RIDER_BASE_URL + getRiderDataModel.getData().getProfile_image_path();
            Glide.with(RiderInformationManagementActivity.this)
                    .load(path)
                    .error(R.mipmap.ic_launcher)
                    .into(mActivityRiderInformationManagementBinding.riderProfileData.userimage);
            PreferenceConnector.writeString(context, ApiKeys.EMAIL, "" + getRiderDataModel.getData().getEmail_id());
        }
    }

    @Override
    public void onFailed(String string) {
        mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.GONE);
        if (string.equalsIgnoreCase("token Expired")) {
            new GenerateTokenClass(context);
        } else {
            mActivityRiderInformationManagementBinding.riderProfileData.datashow.setVisibility(View.GONE);
            mActivityRiderInformationManagementBinding.riderProfileData.nodata.setVisibility(View.VISIBLE);
            Toast.makeText(context, "Please add Rider Data First", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.editImage:
                selectImage();
                break;
            case R.id.fab:
                if (mIsRiderData) {
                    Intent intent = new Intent(context, OtpVarifyActivity.class);
                    intent.putExtra("type", "updatedata");
                    intent.putExtra("fname", mActivityRiderInformationManagementBinding.riderProfileData.fname.getText().toString().trim());
                    intent.putExtra("lname", mActivityRiderInformationManagementBinding.riderProfileData.lname.getText().toString().trim());
                    intent.putExtra("email", mActivityRiderInformationManagementBinding.riderProfileData.email.getText().toString().trim());
                    startActivityForResult(intent, 1);
                } else {
                    Intent intent = new Intent(context, OtpVarifyActivity.class);
                    intent.putExtra("type", "registrationback");
                    startActivityForResult(intent, 0);
                }
                break;
            case R.id.password_data:
                mActivityRiderInformationManagementBinding.riderProfileData.datashow.setVisibility(View.GONE);
                mActivityRiderInformationManagementBinding.riderProfileData.nodata.setVisibility(View.GONE);
                mActivityRiderInformationManagementBinding.fab.setVisibility(View.GONE);
                mActivityRiderInformationManagementBinding.riderProfileData.setpassword.addPass.setVisibility(View.VISIBLE);
                if (PreferenceConnector.readString(context, ApiKeys.HASPASS, "").equals("no")) {
                    mActivityRiderInformationManagementBinding.riderProfileData.setpassword.passward.setHint("Enter Password");
                    mActivityRiderInformationManagementBinding.riderProfileData.setpassword.newpassward.setHint("Reenter Password");
                } else {
                    mActivityRiderInformationManagementBinding.riderProfileData.setpassword.passward.setHint("Enter Old Password");
                    mActivityRiderInformationManagementBinding.riderProfileData.setpassword.newpassward.setHint("Enter New Password");
                }
                break;
            case R.id.reset:
                setAddPassword();
                break;
        }
    }

    public void getRiderData() {
        mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.VISIBLE);
        new GetRiderDataPresenter().show(this, context);
    }

    public void setAddPassword() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mActivityRiderInformationManagementBinding.riderProfileData.setpassword.
                newpassward.getWindowToken(), 0);
        mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.VISIBLE);
        if (PreferenceConnector.readString(context, ApiKeys.HASPASS, "").equalsIgnoreCase("no")) {
            if (Utility.passwordCheck(mActivityRiderInformationManagementBinding.riderProfileData.setpassword.passward.getText().toString().trim())) {
                if (mActivityRiderInformationManagementBinding.riderProfileData.setpassword.passward.getText().toString().trim().equals(
                        mActivityRiderInformationManagementBinding.riderProfileData.setpassword.newpassward.getText().toString().trim()
                )) {
                    new setPasswordPresenter().show(new ResultInterface() {
                        @Override
                        public void onSuccess(Object object) {
                            mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.GONE);
                            SetPasswordModel setPasswordModel = (SetPasswordModel) object;
                            Toast.makeText(context, setPasswordModel.getMessage(), Toast.LENGTH_LONG).show();
                            PreferenceConnector.writeString(context, ApiKeys.HASPASS, "yes");
                            mActivityRiderInformationManagementBinding.riderProfileData.datashow.setVisibility(View.VISIBLE);
                            mActivityRiderInformationManagementBinding.riderProfileData.nodata.setVisibility(View.GONE);
                            mActivityRiderInformationManagementBinding.fab.setVisibility(View.VISIBLE);
                            mActivityRiderInformationManagementBinding.riderProfileData.setpassword.addPass.setVisibility(View.GONE);
                            getRiderData();
                        }

                        @Override
                        public void onFailed(String string) {
                            mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.GONE);
                            Toast.makeText(context, string, Toast.LENGTH_LONG).show();
                        }
                    }, context, mActivityRiderInformationManagementBinding.riderProfileData.setpassword.newpassward.getText().toString().trim());
                } else {
                    Toast.makeText(context, "password doesnot match", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Please enter more then 6 character", Toast.LENGTH_LONG).show();
            }
        } else {
            if (Utility.passwordCheck(mActivityRiderInformationManagementBinding.riderProfileData.setpassword.passward.getText().toString().trim())) {
                if (!mActivityRiderInformationManagementBinding.riderProfileData.setpassword.passward.getText().toString().trim().equals("") &&
                        !mActivityRiderInformationManagementBinding.riderProfileData.setpassword.newpassward.getText().toString().trim().equals("")
                        ) {
                    new UpdatePasswordPresenter().show(new ResultInterface() {
                                                           @Override
                                                           public void onSuccess(Object object) {
                                                               mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.GONE);
                                                               UpdatePasswordModel setPasswordModel = (UpdatePasswordModel) object;
                                                               Toast.makeText(context, setPasswordModel.getMessage(), Toast.LENGTH_LONG).show();
                                                               PreferenceConnector.writeString(context, ApiKeys.HASPASS, "yes");
                                                               mActivityRiderInformationManagementBinding.riderProfileData.datashow.setVisibility(View.VISIBLE);
                                                               mActivityRiderInformationManagementBinding.riderProfileData.nodata.setVisibility(View.GONE);
                                                               mActivityRiderInformationManagementBinding.fab.setVisibility(View.VISIBLE);
                                                               mActivityRiderInformationManagementBinding.riderProfileData.setpassword.addPass.setVisibility(View.GONE);
                                                               getRiderData();
                                                           }

                                                           @Override
                                                           public void onFailed(String string) {
                                                               mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.GONE);
                                                               Toast.makeText(context, string, Toast.LENGTH_LONG).show();
                                                           }
                                                       }, context, mActivityRiderInformationManagementBinding.riderProfileData.setpassword.passward.getText().toString().trim(),
                            mActivityRiderInformationManagementBinding.riderProfileData.setpassword.newpassward.getText().toString().trim());
                } else {
                    Toast.makeText(context, "Please enter your old and new password", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Please enter more then 6 character", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            selectImage();
        }
        if (requestCode == PERMISSION) {
            if (Utility.checkPermission(RiderInformationManagementActivity.this) == false) {
                Utility.checkPermission(RiderInformationManagementActivity.this);
            }
        }
    }

    private void selectImage() {
        final CharSequence[] items = {getResources().getString(R.string.takephoto), getResources().getString(R.string.chooselib), getResources().getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(RiderInformationManagementActivity.this);
        builder.setTitle(Html.fromHtml("<font color=#000000>" + "Select Image" + "</font>"));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(RiderInformationManagementActivity.this);
                if (items[item].equals(getResources().getString(R.string.takephoto))) {
                    if (result)
                        cameraIntent();
                } else if (items[item].equals(getResources().getString(R.string.chooselib))) {
                    if (result)
                        galleryIntent();
                } else if (items[item].equals(getResources().getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Utility.IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("", "Oops! Failed create " + Utility.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private void galleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mFileUri = (getOutputMediaFileUri(MEDIA_TYPE_IMAGE)).getPath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        selectedImageUri = uri;
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        selectedImageUri = uri;
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        selectedImageUri = uri;
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        selectedImageUri = uri;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    String mResponseOfServer;

    private class UploadFile extends AsyncTask<String, Void, Void> {

        protected void onPreExecute() {
            mProgressDialog.show();
        }

        protected Void doInBackground(String... urls) {
            UploadDriverDocs(PreferenceConnector.readString(context, ApiKeys.UID, ""),
                    "data:image/jpeg;base64," + image, PreferenceConnector.readString(context, ApiKeys.TOKEN, ""));
            return null;

        }

        protected void onPostExecute(Void unused) {
            mProgressDialog.dismiss();
            Glide.with(RiderInformationManagementActivity.this)
                    .load(ApiKeys.RIDER_BASE_URL + PreferenceConnector.readString(
                            RiderInformationManagementActivity.this, ApiKeys.IMAGEPATH, ""))
                    .placeholder(R.mipmap.ic_launcher)
                    .into(mActivityRiderInformationManagementBinding.riderProfileData.userimage);
        }
    }

    public String UploadDriverDocs(String driverid, String image, String tokenid) {

        String url = ApiKeys.RIDER_BASE_URL + "updateImagePath";

        HttpConnectorSendJsonDataLatest httpConnectorSendJsonDataLatest = new HttpConnectorSendJsonDataLatest(url);
        Log.e("image", image);
        String sendparams = postToSendDataInServer(driverid, image, tokenid).toString();


        Log.e("Em", "sendparams to driver docs" + " " + sendparams);

        String response = httpConnectorSendJsonDataLatest.postData(sendparams);

        Log.e("Em", "response in documentfragment" + " " + response);

        if (response != null && response.length() > 0) {
            mResponseOfServer = checkResponseOfUploadData(response);
//            Toast.makeText(context,mResponseOfServer,Toast.LENGTH_LONG).show();
            Log.e("Em", "mResponseOfServer" + " " + mResponseOfServer);
        }
        return mResponseOfServer;
    }

    String mTypeOfDocUpload;

    private String checkResponseOfUploadData(String resrponse) {
        try {
            JSONObject jsonObject = new JSONObject(resrponse);
            mTypeOfDocUpload = jsonObject.getString("type");
            String mTypeOfMessageUpload = jsonObject.getString("message");
            String data = jsonObject.getString("data");
            JSONObject jsonObject1 = new JSONObject(data);
            String imagepath = jsonObject1.getString("profile_image_path");
            PreferenceConnector.writeString(context, ApiKeys.IMAGEPATH, imagepath);
            Log.e("data", imagepath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mTypeOfDocUpload;
    }

    public JSONObject postToSendDataInServer(String driver_id, String document_image, String tokenid) {
//        driver_id,document_id,document_expiry,document_image,tokenid
        JSONObject mJsonObject = null;
        try {
            mJsonObject = new JSONObject();
            mJsonObject.put("rider_id", driver_id);
            mJsonObject.put("profile_image_path", document_image);
            mJsonObject.put("tokenid", tokenid);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mJsonObject;

    }

    public void convertToString(String image) {
        byte[] imageBytes = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        mActivityRiderInformationManagementBinding.riderProfileData.userimage.setImageBitmap(decodedImage);
    }
}
