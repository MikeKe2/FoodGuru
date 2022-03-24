package com.michelecucci.foodguru.login;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class AuthenticationStateAdapter extends FragmentStateAdapter {
	private final ArrayList<Fragment> fragmentList = new ArrayList<>();

	public AuthenticationStateAdapter(FragmentActivity fa) {
		super(fa);
	}

	@Override
	public Fragment createFragment(int i) {
		return fragmentList.get(i);
	}

	@Override
	public int getItemCount() {
		return fragmentList.size();
	}

	public void addFragment(Fragment fragment) {
		fragmentList.add(fragment);
	}
}
