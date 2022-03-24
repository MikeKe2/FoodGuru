package com.michelecucci.foodguru.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.michelecucci.foodguru.MainActivity;
import com.michelecucci.foodguru.R;
import com.michelecucci.foodguru.databinding.FragmentRegisterBinding;
import com.michelecucci.foodguru.model.Login;
import com.michelecucci.foodguru.model.Register;
import com.michelecucci.foodguru.remote.ApiUtils;
import com.michelecucci.foodguru.remote.UserInterface;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

	private EditText edtPsw, edtUser, edtMail, edtRePsw;
	private Button btnRegister;
	private LinearLayout mProgressBar;
	private UserInterface userInterface;
	private FragmentRegisterBinding binding;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		binding = FragmentRegisterBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		edtMail = view.findViewById(R.id.et_email);
		edtUser = view.findViewById(R.id.et_name);
		edtPsw = view.findViewById(R.id.et_password);
		edtRePsw = view.findViewById(R.id.et_re_password);
		btnRegister = view.findViewById(R.id.btn_register);
		mProgressBar = view.findViewById(R.id.progress_bar);

		userInterface = ApiUtils.getUserService();

		btnRegister.setOnClickListener(v -> {
			String username = edtUser.getText().toString();
			String password = edtPsw.getText().toString();
			String rePassword = edtRePsw.getText().toString();
			String email = edtMail.getText().toString();
			//validate form
			if (validateLogin(username, email, password, rePassword)) {
				//do login
				register(username, email, password);
			}
		});

		edtPsw.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (edtPsw.getText().toString().trim().length() < 8 || !IsValidPassword(edtPsw.getText().toString())) {
					edtPsw.setError(getText(R.string.invalid_password));
					btnRegister.setEnabled(false);
				} else
					btnRegister.setEnabled(true);
			}
		});

	}

	private boolean validateLogin(String username, String email, String password, String rePassword) {

		if (username == null || username.trim().length() == 0) {
			edtUser.setError(getText(R.string.invalid_username));
			return false;
		}
		if (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			edtMail.setError(getText(R.string.wrong_email));
			return false;
		}
		if (!password.equals(rePassword)) {
			edtRePsw.setError(getText(R.string.same_password));
			return false;
		}
		return true;
	}

	private boolean IsValidPassword(String password) {
		Pattern pattern;
		Matcher matcher;
		final String PASSWORD_PATTERN = "^(?=.*?[0-9]).{8,}$";

		pattern = Pattern.compile(PASSWORD_PATTERN);
		matcher = pattern.matcher(password);
		return matcher.matches();
	}

	private void register(final String username, final String email, final String password) {
		mProgressBar.setVisibility(View.VISIBLE);
		Register register = new Register(username, email, password);
		Call<Register> call = userInterface.register(register);
		call.enqueue(new Callback<Register>() {
			@Override
			public void onResponse(Call<Register> call, Response<Register> response) {
				if (response.isSuccessful()) {
					String id = response.body().getId();
					if (!id.equals("")) {
						login(email, password);
					} else {
						Toast.makeText(getContext(), "The username or password is incorrect", Toast.LENGTH_SHORT).show();
					}
				} else {
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

	private void login(final String email, final String password) {
		Login login = new Login(email, password);
		Call<Login> call = userInterface.login(login);
		call.enqueue(new Callback<Login>() {
			@Override
			public void onResponse(Call<Login> call, Response<Login> response) {
				if (response.isSuccessful()) {
					String accessToken = response.body().getAccessToken();
					if (!accessToken.equals("")) {
						//login start main activity
						Intent intent = new Intent(getContext(), MainActivity.class);
						startActivity(intent);
						getActivity().finish();
					} else {
						Toast.makeText(getContext(), "The username or password is incorrect", Toast.LENGTH_SHORT).show();
					}
				} else {
					try {
						JSONObject jObjError = new JSONObject(response.errorBody().string());
						Toast.makeText(getContext(), jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
					} catch (Exception e) {
						Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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
