package com.example.timviecapp.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.common.PaginationResponse;
import com.example.timviecapp.models.skill.SkillResponse;
import com.example.timviecapp.network.RetrofitClient;
import com.example.timviecapp.network.services.SkillApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * SkillRepository - Xử lý dữ liệu kỹ năng
 * UC19: Xem danh sách skill
 * Dùng bởi SubscriberActivity để chọn skill
 */
public class SkillRepository {
    private final SkillApiService apiService;
    private static final String TAG = "SkillRepository";

    public SkillRepository() {
        apiService = RetrofitClient.getClient().create(SkillApiService.class);
    }

    /**
     * Lấy danh sách tất cả skill (cho UI chọn skill)
     */
    public LiveData<ApiResponse<PaginationResponse<SkillResponse>>> getSkills(int page, int size) {
        MutableLiveData<ApiResponse<PaginationResponse<SkillResponse>>> data = new MutableLiveData<>();

        apiService.getSkills(page, size, "name", "asc", null, null)
                .enqueue(new Callback<ApiResponse<PaginationResponse<SkillResponse>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PaginationResponse<SkillResponse>>> call,
                                           Response<ApiResponse<PaginationResponse<SkillResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            data.setValue(response.body());
                        } else {
                            Log.e(TAG, "getSkills failed: " + response.code());
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PaginationResponse<SkillResponse>>> call, Throwable t) {
                        Log.e(TAG, "getSkills error: " + t.getMessage());
                        data.setValue(null);
                    }
                });

        return data;
    }
}
