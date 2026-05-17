package com.example.timviecapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.timviecapp.databinding.ActivityMainBinding;
import com.example.timviecapp.models.job.JobResponse;
import com.example.timviecapp.ui.adapters.JobAdapter;
import com.example.timviecapp.ui.auth.LoginActivity;
import com.example.timviecapp.ui.companies.CompanyListActivity;
import com.example.timviecapp.ui.jobs.JobDetailActivity;
import com.example.timviecapp.ui.profile.ProfileActivity;
import com.example.timviecapp.ui.resume.MyResumesActivity;
import com.example.timviecapp.ui.subscriber.SubscriberActivity;
import com.example.timviecapp.utils.TokenManager;
import com.example.timviecapp.viewmodels.JobViewModel;

/**
 * MainActivity - Trang chủ app WorkHub
 * UC14: Xem danh sách công việc
 * UC16: Tìm kiếm công việc theo kỹ năng
 * UC3: Đăng xuất
 * Navigation: Companies, Resumes, Profile, Subscriber
 */
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private JobViewModel viewModel;
    private JobAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(JobViewModel.class);

        setupRecyclerView();
        setupMenuByRole();
        setupListeners();
        observeViewModel();

        loadJobs();
    }

    private void setupMenuByRole() {
        String role = TokenManager.getUserRole();
        android.util.Log.d("TOKEN_CHECK", "Token: " + TokenManager.getToken());
        android.util.Log.d("TOKEN_CHECK", "Role: " + TokenManager.getUserRole());
        if (role != null && (role.toUpperCase().contains("ADMIN") || role.toUpperCase().contains("RECRUITER") || role.toUpperCase().contains("EMPLOYER"))) {
            try {
                binding.toolbar.getMenu().clear();
                binding.toolbar.inflateMenu(R.menu.menu_recruiter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupRecyclerView() {
        adapter = new JobAdapter();
        binding.rvJobs.setLayoutManager(new LinearLayoutManager(this));
        binding.rvJobs.setAdapter(adapter);

        // UC15: Click vào job để xem chi tiết
        adapter.setOnJobClickListener(job -> {
            Intent intent = new Intent(this, JobDetailActivity.class);
            intent.putExtra(JobDetailActivity.EXTRA_JOB_ID, job.getId());
            startActivity(intent);
        });
    }

    private void setupListeners() {
        // UC16: Tìm kiếm công việc
        binding.btnSearch.setOnClickListener(v -> searchJobs());

        // Menu navigation
        binding.toolbar.setNavigationOnClickListener(v -> {
            // Có thể thêm drawer menu ở đây
        });

        // Bộ lọc
        binding.btnApplyFilter.setOnClickListener(v -> {
            searchJobs();
            binding.drawerLayout.closeDrawer(GravityCompat.END);
        });

        // ===== Thêm các nút navigation =====
        binding.toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else if (itemId == R.id.menu_companies) {
                startActivity(new Intent(this, CompanyListActivity.class));
                return true;
            } else if (itemId == R.id.menu_my_resumes) {
                startActivity(new Intent(this, MyResumesActivity.class));
                return true;
            } else if (itemId == R.id.menu_subscriber) {
                startActivity(new Intent(this, SubscriberActivity.class));
                return true;
            } else if (itemId == R.id.menu_manage_jobs) {
                startActivity(new Intent(this, com.example.timviecapp.ui.jobs.ManageJobsActivity.class));
                return true;
            } else if (itemId == R.id.menu_manage_resumes) {
                startActivity(new Intent(this, com.example.timviecapp.ui.resume.ManageResumesActivity.class));
                return true;
            } else if (itemId == R.id.menu_company_info) {
                startActivity(new Intent(this, com.example.timviecapp.ui.companies.ManageCompanyActivity.class));
                return true;
            } else if (itemId == R.id.menu_logout) {
                showLogoutDialog();
                return true;
            }
            return false;
        });
    }

    /**
     * UC14: Tải danh sách công việc
     */
    private void loadJobs() {
        viewModel.getJobs(0, 10).observe(this, response -> {
            viewModel.setLoading(false);
            if (response != null && response.isSuccess() && response.getData() != null) {
                adapter.setJobs(response.getData().getItems());
                binding.tvJobCount.setText(response.getData().getItems().size() + " công việc được tìm thấy");
            } else {
                Toast.makeText(this, "Không thể tải danh sách công việc", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * UC16: Tìm kiếm công việc theo từ khóa và địa điểm
     */
    private void searchJobs() {
        String keyword = binding.etSearchKeyword.getText().toString().trim();
        String location = binding.etSearchLocation.getText().toString().trim();

        if (keyword.isEmpty() && location.isEmpty()) {
            loadJobs(); // Nếu không nhập, load tất cả
            return;
        }

        viewModel.searchJobs(
                keyword.isEmpty() ? null : keyword,
                location.isEmpty() ? null : location,
                null, 0, 20
        ).observe(this, response -> {
            viewModel.setLoading(false);
            if (response != null && response.isSuccess() && response.getData() != null) {
                adapter.setJobs(response.getData().getItems());
                int count = response.getData().getItems().size();
                String searchText = keyword.isEmpty() ? location : keyword;
                binding.tvJobCount.setText(count + " công việc phù hợp với \"" + searchText + "\"");

                if (count == 0) {
                    Toast.makeText(this, "Không tìm thấy công việc phù hợp với kỹ năng này",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * UC3: Đăng xuất với dialog xác nhận
     */
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    // Xóa token và user info
                    TokenManager.clear();

                    // Chuyển về LoginActivity và clear back stack
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                    Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            // Xử lý trạng thái loading nếu cần
        });
    }
}
