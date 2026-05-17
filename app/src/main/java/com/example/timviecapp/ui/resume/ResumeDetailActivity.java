package com.example.timviecapp.ui.resume;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.timviecapp.databinding.ActivityResumeDetailBinding;
import com.example.timviecapp.models.resume.ResumeResponse;
import com.example.timviecapp.viewmodels.ResumeViewModel;

/**
 * ResumeDetailActivity - Xem chi tiết hồ sơ ứng tuyển
 * UC27: Lấy thông tin chi tiết resume
 * - Hiển thị email, trạng thái, tên công việc, link CV
 */
public class ResumeDetailActivity extends AppCompatActivity {
    public static final String EXTRA_RESUME_ID = "extra_resume_id";
    private ActivityResumeDetailBinding binding;
    private ResumeViewModel viewModel;
    private int resumeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResumeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        resumeId = getIntent().getIntExtra(EXTRA_RESUME_ID, -1);
        if (resumeId == -1) {
            Toast.makeText(this, "Không tìm thấy hồ sơ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(ResumeViewModel.class);

        setupToolbar();
        loadResumeDetail();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /**
     * UC27: Tải và hiển thị chi tiết resume
     */
    private void loadResumeDetail() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutContent.setVisibility(View.GONE);

        viewModel.getResumeById(resumeId).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);

            if (response != null && response.isSuccess() && response.getData() != null) {
                binding.layoutContent.setVisibility(View.VISIBLE);
                displayResumeDetail(response.getData());
            } else {
                Toast.makeText(this, "Không tìm thấy hồ sơ", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * Hiển thị thông tin chi tiết resume
     */
    private void displayResumeDetail(ResumeResponse resume) {
        // Trạng thái
        binding.chipStatus.setText(resume.getStatus());

        // Email ứng viên
        binding.tvEmail.setText("Email: " + (resume.getEmail() != null ? resume.getEmail() : "N/A"));

        // Thông tin công việc
        if (resume.getJob() != null) {
            binding.tvJobName.setText(resume.getJob().getName());
            if (resume.getJob().getCompany() != null) {
                binding.tvJobCompany.setText("Công ty: " + resume.getJob().getCompany().getName());
            } else {
                binding.tvJobCompany.setText("Công ty: N/A");
            }
        } else {
            binding.tvJobName.setText("N/A");
            binding.tvJobCompany.setText("");
        }

        // Link CV
        binding.tvCvUrl.setText(resume.getUrl() != null ? resume.getUrl() : "Không có link CV");
    }
}
