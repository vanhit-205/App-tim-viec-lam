package com.example.timviecapp.ui.jobs;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.timviecapp.databinding.ActivityAddJobBinding;
import com.example.timviecapp.models.job.JobRequest;
import com.example.timviecapp.viewmodels.JobViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddJobActivity extends AppCompatActivity {
    private ActivityAddJobBinding binding;
    private JobViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddJobBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(JobViewModel.class);

        setupToolbar();
        setupListeners();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupListeners() {
        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String location = binding.etLocation.getText().toString().trim();
            String salaryStr = binding.etSalary.getText().toString().trim();
            String quantityStr = binding.etQuantity.getText().toString().trim();
            String level = binding.etLevel.getText().toString().trim().toUpperCase();
            String companyIdStr = binding.etCompanyId.getText().toString().trim();
            String description = binding.etDescription.getText().toString().trim();

            if (name.isEmpty() || location.isEmpty() || salaryStr.isEmpty() || quantityStr.isEmpty() || level.isEmpty() || companyIdStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            double salary = Double.parseDouble(salaryStr);
            int quantity = Integer.parseInt(quantityStr);
            int companyId = Integer.parseInt(companyIdStr);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            String startDate = sdf.format(new Date());
            String endDate = sdf.format(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)); // +30 days

            JobRequest request = new JobRequest(
                    name, location, salary, quantity, level, description,
                    startDate, endDate, true, companyId, new ArrayList<>()
            );

            viewModel.createJob(request).observe(this, response -> {
                viewModel.setLoading(false);
                if (response != null && response.isSuccess()) {
                    Toast.makeText(this, "Thêm công việc thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Lỗi khi thêm công việc", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSave.setEnabled(!isLoading);
        });
    }
}
