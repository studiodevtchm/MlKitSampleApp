package com.example.mlkitsampleapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.mlkitsampleapp.ml.ImageLabeler;
import com.example.mlkitsampleapp.ml.ImagePredictor;
import com.example.mlkitsampleapp.ml.ImageTransfer;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mlkitsampleapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Bitmap daisyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.daisy);
        try {
            ImageLabeler model = ImageLabeler.newInstance(this);

            // Creates inputs for reference.
            TensorImage image = TensorImage.fromBitmap(daisyBitmap);

            // Runs model inference and gets result.
            ImageLabeler.Outputs outputs = model.process(image);
            List<Category> probability = outputs.getProbabilityAsCategoryList();
            for (Category category : probability) {
                if (category.getScore() > 0) {
                    Log.d("Test", category.getLabel() + ": " + category.getScore());
                }
            }
        } catch (IOException e) {
            // TODO Handle the exception
        }

        try {
//            Gets style prediction buffer.
            ImagePredictor predictorModel = ImagePredictor.newInstance(this);
            Bitmap styleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.style);
            TensorImage styleImage = TensorImage.fromBitmap(styleBitmap);
            ImagePredictor.Outputs outputs = predictorModel.process(styleImage);
            TensorBuffer styleBottleneck = outputs.getStyleBottleneckAsTensorBuffer();
            // Transfers content image to a styled image.
            ImageTransfer transferModel = ImageTransfer.newInstance(this);
            Bitmap belfryBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.belfry);
            TensorImage contentImage = TensorImage.fromBitmap(belfryBitmap);
            ImageTransfer.Outputs outputs2 = transferModel.process(contentImage, styleBottleneck);
            TensorImage styledImage = outputs2.getStyledImageAsTensorImage();
            Bitmap styledImageBitmap = styledImage.getBitmap();
            ImageView image = (ImageView) findViewById(R.id.imageView);
            image.setImageBitmap(styledImageBitmap);
        } catch (IOException e) { // TODO Handle the exception }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}