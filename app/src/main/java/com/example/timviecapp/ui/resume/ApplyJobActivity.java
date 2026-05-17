package com.example.timviecapp.ui.resume;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.timviecapp.databinding.ActivityApplyJobBinding;
import com.example.timviecapp.models.resume.ResumeRequest;
import com.example.timviecapp.utils.TokenManager;
import com.example.timviecapp.viewmodels.ResumeViewModel;

/**
 * ApplyJobActivity - Màn hình ứng tuyển công việc
 * UC24: Tạo mới resume
 * - Hiển thị thông tin job đang ứng tuyển
 * - Nhập email + link CV
 * - Validate input
 * - Gọi API tạo resume
 */
public class ApplyJobActivity extends AppCompatActivity {
    public static final String EXTRA_JOB_ID = "extra_job_id";
    public static final String EXTRA_JOB_TITLE = "extra_job_title";
    public static final String EXTRA_COMPANY_NAME = "extra_company_name";

    private ActivityApplyJobBinding binding;
    private ResumeViewModel viewModel;
    private int jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityApplyJobBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        jobId = getIntent().getIntExtra(EXTRA_JOB_ID, -1);
        if (jobId == -1) {
            Toast.makeText(this, "Không tìm thấy công việc", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(ResumeViewModel.class);

        setupToolbar();
        setupJobInfo();
        setupListeners();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /**
     * Hiển thị thông tin công việc đang ứng tuyển và tự động điền email
     */
    private void setupJobInfo() {
        String jobTitle = getIntent().getStringExtra(EXTRA_JOB_TITLE);
        String companyName = getIntent().getStringExtra(EXTRA_COMPANY_NAME);

        binding.tvJobTitle.setText(jobTitle != null ? jobTitle : "N/A");
        binding.tvCompanyName.setText(companyName != null ? companyName : "N/A");

        // Tự động điền email từ user đã đăng nhập
        String userEmail = TokenManager.getUserEmail();
        if (userEmail != null) {
            binding.etEmail.setText(userEmail);
        }
    }

    private void setupListeners() {
        binding.btnSubmit.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String cvUrl = binding.etCvUrl.getText().toString().trim();

            // Validate email
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate CV URL
            if (cvUrl.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập link CV", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!cvUrl.startsWith("http://") && !cvUrl.startsWith("https://")) {
                Toast.makeText(this, "Link CV phải bắt đầu bằng http:// hoặc https://",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo request
            int userId = TokenManager.getUserId();
            ResumeRequest request = new ResumeRequest(email, cvUrl, userId, jobId);

            // Gọi API
            viewModel.createResume(request).observe(this, response -> {
                viewModel.setLoading(false);
                if (response != null && response.isSuccess()) {
                    Toast.makeText(this, "Ứng tuyển thành công! 🎉", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Ứng tuyển thất bại. Bạn có thể đã ứng tuyển công việc này rồi.",
                            Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSubmit.setEnabled(!isLoading);
        });
    }
}
