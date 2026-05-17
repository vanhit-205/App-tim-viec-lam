package com.example.timviecapp.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.timviecapp.models.auth.UserResponse;
import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.user.UpdateUserRequest;
import com.example.timviecapp.network.RetrofitClient;
import com.example.timviecapp.network.services.UserApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * UserRepository - Xử lý API liên quan đến User
 * UC4: Cập nhật thông tin cá nhân
 */
public class UserRepository {
    private final UserApiService apiService;
    private static final String TAG = "UserRepository";

    public UserRepository() {
        apiService = RetrofitClient.getClient().create(UserApiService.class);
    }

    /**
     * Lấy thông tin user bằng ID
     */
    public LiveData<ApiResponse<UserResponse>> getUserById(int id) {
        MutableLiveData<ApiResponse<UserResponse>> data = new MutableLiveData<>();
        apiService.getUserById(id).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Log.e(TAG, "getUserById error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }

    /**
     * Cập nhật thông tin user (UC4)
     */
    public LiveData<ApiResponse<UserResponse>> updateUser(int id, UpdateUserRequest request) {
        MutableLiveData<ApiResponse<UserResponse>> data = new MutableLiveData<>();
        apiService.updateUser(id, request).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "updateUser failed: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Log.e(TAG, "updateUser error: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }
}
