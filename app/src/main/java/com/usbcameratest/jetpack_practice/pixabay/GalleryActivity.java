package com.usbcameratest.jetpack_practice.pixabay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.usbcameratest.jetpack_practice.R;
import com.usbcameratest.jetpack_practice.databinding.ActivityGalleryBinding;

public class GalleryActivity extends AppCompatActivity {

    private ActivityGalleryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NavController controller = Navigation.findNavController(this, R.id.fragment5);
        NavigationUI.setupActionBarWithNavController(this, controller);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController controller = Navigation.findNavController(this, R.id.fragment5);
        controller.navigateUp();
        return super.onSupportNavigateUp();

    }
}
