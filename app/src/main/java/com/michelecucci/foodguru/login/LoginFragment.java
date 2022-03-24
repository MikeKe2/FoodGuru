package com.michelecucci.foodguru.login;

import static com.michelecucci.foodguru.Constants.ACCESS_TOKEN;
import static com.michelecucci.foodguru.Constants.PREF_PASSWORD;
import static com.michelecucci.foodguru.Constants.PREF_USERNAME;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.michelecucci.foodguru.MainActivity;
import com.michelecucci.foodguru.R;
import com.michelecucci.foodguru.databinding.FragmentLoginBinding;
import com.michelecucci.foodguru.model.Login;
import com.michelecucci.foodguru.remote.ApiUtils;
import com.michelecucci.foodguru.remote.UserInterface;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

	EditText edtPsw, edtMail;
	Button btnLogin;
	UserInterface userInterface;
	CheckBox chkPsw, chkAutoLogin;
	LinearLayout mProgressBar;

	private FragmentLoginBinding binding;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		binding = FragmentLoginBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		edtMail = view.findViewById(R.id.et_email);
		edtPsw = view.findViewById(R.id.et_password);
		btnLogin = view.findViewById(R.id.btn_login);
		chkPsw = view.findViewById(R.id.chkRememberMe);
		mProgressBar = view.findViewById(R.id.progress_bar);
		chkAutoLogin = view.findViewById(R.id.chkAutoLogin);

		userInterface = ApiUtils.getUserService();
		mProgressBar.setVisibility(View.GONE);

		SharedPreferences sharedPref = getActivity().getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		String username = sharedPref.getString(PREF_USERNAME, null);
		String password = sharedPref.getString(PREF_PASSWORD, null);
		boolean autologin = sharedPref.getBoolean(ACCESS_TOKEN, false);

		if (autologin) {
			chkAutoLogin.setChecked(true);
			edtMail.setText(username, TextView.BufferType.EDITABLE);
			edtPsw.setText(password, TextView.BufferType.EDITABLE);
			login(username, password);
		} else if (password != null) {
			edtPsw.setText(password, TextView.BufferType.EDITABLE);
			chkPsw.setChecked(true);
		}

		btnLogin.setOnClickListener(v -> {
			String psw = edtPsw.getText().toString();
			String email = edtMail.getText().toString();
			//validate form
			if (validateLogin(email, psw)) {
				//do login
				login(email, psw);
			}
		});
	}

	private boolean validateLogin(String email, String password) {

		if (email == null) {
			edtMail.setError(getText(R.string.invalid_username));
			return false;
		}

		if (password == null || password.trim().length() == 0) {
			edtPsw.setError(getText(R.string.invalid_password));
			return false;
		}
		return true;
	}

	private void login(final String email, final String password) {
		Login login = new Login(email, password);
		Call<Login> call = userInterface.login(login);

		mProgressBar.setVisibility(View.VISIBLE);

		call.enqueue(new Callback<Login>() {
			@Override
			public void onResponse(Call<Login> call, Response<Login> response) {
				if (response.isSuccessful()) {
					String accessToken = response.body().getAccessToken();
					if (!accessToken.equals("")) {
						//login start main activity
						if (chkAutoLogin.isChecked()) {
							getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
									.edit()
									.putString(PREF_USERNAME, email)
									.putString(PREF_PASSWORD, password)
									.putBoolean(ACCESS_TOKEN, chkAutoLogin.isChecked())
									.apply();
						} else if (chkPsw.isChecked()) {
							getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
									.edit()
									.remove(PREF_USERNAME)
									.remove(ACCESS_TOKEN)
									.putString(PREF_PASSWORD, password)
									.apply();
						}
						Intent intent = new Intent(getContext(), MainActivity.class);
						intent.putExtra(ACCESS_TOKEN, accessToken);
						startActivity(intent);
						getActivity().finish();
					} else {
						mProgressBar.setVisibility(View.GONE);
						Toast.makeText(getContext(), "The username or password is incorrect", Toast.LENGTH_SHORT).show();
					}
				} else {
					mProgressBar.setVisibility(View.GONE);
					try {
						JSONObject jObjError = new JSONObject(response.errorBody().string());
						Log.d("onResponse: ", jObjError.getJSONObject("error").getString("message"));
					} catch (Exception e) {
						Log.d("onResponse: ", e.getMessage());
					}
				}
			}

			@Override
			public void onFailure(Call call, Throwable t) {
				Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
}
