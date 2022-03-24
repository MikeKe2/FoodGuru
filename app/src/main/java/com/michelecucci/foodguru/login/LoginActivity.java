package com.michelecucci.foodguru.login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.michelecucci.foodguru.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		com.michelecucci.foodguru.databinding.ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		ViewPager2 viewPager = binding.viewpager;

		AuthenticationStateAdapter pagerAdapter = new AuthenticationStateAdapter(this);
		pagerAdapter.addFragment(new LoginFragment());
		pagerAdapter.addFragment(new RegisterFragment());
		viewPager.setAdapter(pagerAdapter);
	}
}

