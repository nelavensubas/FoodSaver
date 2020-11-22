package com.example.foodsaver;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodsaver.DTO.RecipeApi;
import com.example.foodsaver.DTO.RecipeApus;
import com.example.foodsaver.util.Constants;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    // Requests codes to identify camera and permission requests
    private final int CAMERA_PERMISSION_REQUEST_CODE = 1000;
    private final int CAMERA_REQUEST_CODE = 1001;

    // Image
    private String currentPhotoPath;
    private Uri photoURI;
    private File photoFile = null;

    // spoonacular Recipe Search API
    private Retrofit retrofit;
    private RecipeApi recipeApi;
    private Call<List<RecipeApus>> call;

    // ML Model
    private InputImage image;
    private LocalModel localModel;
    private CustomImageLabelerOptions customImageLabelerOptions;
    private ImageLabeler imageLabeler;

    private ImageButton cameraImageButton;
    private ImageButton recipeImageButton;
    private AppCompatTextView yourIngredientsTextView;

    private String listOfIngredients = "";

    private List<String> recipeTitles = new ArrayList<>();;
    private List<String> recipeImages = new ArrayList<>();;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter customAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraImageButton = findViewById(R.id.camera_button);
        recipeImageButton = findViewById(R.id.recipe_button);
        yourIngredientsTextView = findViewById(R.id.your_ingredients_text_view);
        recyclerView = findViewById(R.id.recipe_recycler_view);

        // Generate an implementation of the GitHubService interface using the
        // Retrofit class
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        recipeApi = retrofit.create(RecipeApi.class);

        // Create a new local food classification model
        localModel = new LocalModel.Builder().setAssetFilePath("model.tflite").build();
        /**
         * Set the minimum confidence score of detected food to 15%.
         * Only one food label will be returned.
         */
        customImageLabelerOptions = new CustomImageLabelerOptions.Builder(localModel)
                .setConfidenceThreshold(0.01f)
                .setMaxResultCount(1)
                .build();
        // Create a new image labeler with the options
        imageLabeler = ImageLabeling.getClient(customImageLabelerOptions);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        cameraImageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                // Open the camera app to allow the user to take a picture
                if (hasPermission()) {
                    openCamera();
                } else {
                    requestPermission();
                }
            }
        });

        recipeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!listOfIngredients.equals("")) {
                    call = recipeApi.searchRecipe(Constants.API_KEY, listOfIngredients, 10);
                    call.enqueue(new Callback<List<RecipeApus>>() {
                        @Override
                        public void onResponse(Call<List<RecipeApus>> call, Response<List<RecipeApus>> response) {
                            if (!response.isSuccessful() && response.body() != null) {
                                Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG);
                            }

                            if (response.body() != null) {
                                for (int i = 0; i < response.body().size(); i++) {
                                    String recipeName = response.body().get(i).getTitle();
                                    String recipeUrl = response.body().get(i).getImage();
                                    recipeTitles.add(recipeName);
                                    recipeImages.add(recipeUrl);
                                }
                            }

                            customAdapter = new CustomAdapter(getApplicationContext(), recipeTitles, recipeImages);
                            recyclerView.setAdapter(customAdapter);
                        }

                        @Override
                        public void onFailure(Call<List<RecipeApus>> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result of camera image request
        if (requestCode == CAMERA_REQUEST_CODE) {
            try {
                image = InputImage.fromFilePath(getApplicationContext(), photoURI);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageLabeler.process(image)
                    .addOnSuccessListener(labels -> {
                        if (!labels.isEmpty()) {
                            String ingredient = labels.get(0).getText();
                            if (yourIngredientsTextView.getText().toString().equals("Your Ingredients:")) {
                                listOfIngredients = ingredient;
                                yourIngredientsTextView.append(" " + ingredient);
                            } else {
                                listOfIngredients += ", " + ingredient;
                                yourIngredientsTextView.append(", " + ingredient);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Task failed with an exception
                        // ...
                    });
        }
    }

    /**
     * Result for after the user responds to the camera permissions dialog.
     *
     * @param requestCode  the camera permission request code
     * @param permissions  list of permissions that need to be granted
     * @param grantResults list of permissions that were granted
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // The result of our camera permission request
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            // Open the camera if the user gave permission
            if (hasAllPermissions(grantResults)) {
                openCamera();
            } else {
                requestPermission();
            }
        }
    }

    /**
     * Checks whether all the needed permissions have been granted or not.
     *
     * @param grantResults the permission grant results
     * @return true if all the request permission has been granted, otherwise return false
     */
    private boolean hasAllPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check whether camera permission is available or not.
     *
     * @return true if Android version is less than Marshmallo, otherwise returns whether camera
     * permission has been granted or not
     */
    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    // Create and start an intent to take a photo with a camera app
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.i("TAG", "IOException");
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    /**
     * Create a file for the photo.
     *
     * @return File if it was successfully created
     */

    @RequiresApi(api = Build.VERSION_CODES.N)
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "JPEG_" + timeStamp + "_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Requests for permission if the Android version is Marshmallow or above
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        // Checks whether permission can be requested or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(
                        this, "You need to give access to the camera to use " +
                                "this feature. You can grant this permission in app settings.",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }
}