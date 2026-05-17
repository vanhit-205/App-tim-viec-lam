package com.example.timviecapp.ui.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.timviecapp.databinding.ActivityForgotPasswordBinding;
import com.example.timviecapp.viewmodels.ForgotPasswordViewModel;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding;
    private ForgotPasswordViewModel viewModel;
    private String currentEmail;
    private String currentOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);

        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        binding.btnSendOtp.setOnClickListener(v -> {
            currentEmail = binding.etEmail.getText().toString().trim();
            if (currentEmail.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.verifyEmail(currentEmail).observe(this, response -> {
                viewModel.setLoading(false);
                if (response != null && response.isSuccess()) {
                    showStepOtp();
                } else {
                    Toast.makeText(this, "Email không tồn tại trong hệ thống", Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.btnVerifyOtp.setOnClickListener(v -> {
            currentOtp = binding.etOtp.getText().toString().trim();
            if (currentOtp.length() != 6) {
                Toast.makeText(this, "Vui lòng nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.verifyOtp(currentEmail, currentOtp).observe(this, response -> {
                viewModel.setLoading(false);
                if (response != null && response.isSuccess()) {
                    showStepPassword();
                } else {
                    Toast.makeText(this, "Mã OTP không chính xác", Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.btnChangePassword.setOnClickListener(v -> {
            String newPassword = binding.etNewPassword.getText().toString().trim();
            String confirmPassword = binding.etConfirmNewPassword.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.changePassword(currentEmail, currentOtp, newPassword).observe(this, response -> {
                viewModel.setLoading(false);
                if (response != null && response.isSuccess()) {
                    Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.btnBack.setOnClickListener(v -> {
            if (binding.layoutStepOtp.getVisibility() == View.VISIBLE) {
                showStepEmail();
            } else if (binding.layoutStepPassword.getVisibility() == View.VISIBLE) {
                showStepOtp();
            } else {
                finish();
            }
        });
    }

    private void showStepEmail() {
        binding.tvTitle.setText("Quên mật khẩu");
        binding.tvDescription.setText("Nhập email để nhận mã OTP");
        binding.layoutStepEmail.setVisibility(View.VISIBLE);
        binding.layoutStepOtp.setVisibility(View.GONE);
        binding.layoutStepPassword.setVisibility(View.GONE);
    }

    private void showStepOtp() {
        binding.tvTitle.setText("Xác thực OTP");
        binding.tvDescription.setText("Nhập mã OTP đã gửi đến email: " + currentEmail);
        binding.layoutStepEmail.setVisibility(View.GONE);
        binding.layoutStepOtp.setVisibility(View.VISIBLE);
        binding.layoutStepPassword.setVisibility(View.GONE);
    }

    private void showStepPassword() {
        binding.tvTitle.setText("Đặt mật khẩu mới");
        binding.tvDescription.setText("Vui lòng nhập mật khẩu mới của bạn");
        binding.layoutStepEmail.setVisibility(View.GONE);
        binding.layoutStepOtp.setVisibility(View.GONE);
        binding.layoutStepPassword.setVisibility(View.VISIBLE);
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSendOtp.setEnabled(!isLoading);
            binding.btnVerifyOtp.setEnabled(!isLoading);
            binding.btnChangePassword.setEnabled(!isLoading);
        });
    }
}
