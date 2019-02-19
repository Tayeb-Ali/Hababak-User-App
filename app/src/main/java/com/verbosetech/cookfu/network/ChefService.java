package com.verbosetech.cookfu.network;

import android.app.DownloadManager;

import com.google.gson.JsonObject;
import com.verbosetech.cookfu.model.Order;
import com.verbosetech.cookfu.model.User;
import com.verbosetech.cookfu.network.request.FcmTokenUpdateRequest;
import com.verbosetech.cookfu.network.request.RateRequest;
import com.verbosetech.cookfu.network.request.ResetPasswordRequest;
import com.verbosetech.cookfu.network.response.Address;
import com.verbosetech.cookfu.model.CategoryFood;
import com.verbosetech.cookfu.model.Review;
import com.verbosetech.cookfu.model.Store;
import com.verbosetech.cookfu.network.request.CreateOrderRequest;
import com.verbosetech.cookfu.network.request.LoginRequest;
import com.verbosetech.cookfu.network.request.MobileVerifiedRequest;
import com.verbosetech.cookfu.network.request.RegisterRequest;
import com.verbosetech.cookfu.network.request.SupportRequest;
import com.verbosetech.cookfu.network.response.AuthResponse;
import com.verbosetech.cookfu.network.response.BaseListModel;
import com.verbosetech.cookfu.network.response.CouponResponse;
import com.verbosetech.cookfu.network.response.CreateOrderResponse;
import com.verbosetech.cookfu.network.response.Favorite;
import com.verbosetech.cookfu.network.response.FavoriteResponse;
import com.verbosetech.cookfu.network.response.GoogleMapsRoute;
import com.verbosetech.cookfu.network.response.RateResponse;
import com.verbosetech.cookfu.network.response.SettingResponse;
import com.verbosetech.cookfu.network.response.StoreDetail;
import com.verbosetech.cookfu.network.response.SupportResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by a_man on 05-12-2017.
 */

public interface ChefService {
    @Headers("Accept: application/json")
    @POST("api/login")
    Call<AuthResponse> login(@Body LoginRequest loginRequest);

    @Headers("Accept: application/json")
    @POST("api/register")
    Call<AuthResponse> register(@Body RegisterRequest registerRequest);

    @Headers("Accept: application/json")
    @POST("api/verify-mobile")
    Call<JsonObject> verifyMobile(@Body MobileVerifiedRequest mobileVerifiedRequest);

    @Headers("Accept: application/json")
    @POST("api/support")
    Call<SupportResponse> support(@Body SupportRequest supportRequest);

    @Headers("Accept: application/json")
    @POST("api/forgot-password")
    Call<JsonObject> forgetPassword(@Body ResetPasswordRequest resetPasswordRequest);

    @Headers("Accept: application/json")
    @PUT("api/user")
    Call<User> updateFcmToken(@Header("Authorization") String token, @Body FcmTokenUpdateRequest fcmTokenUpdateRequest);

    @Headers("Accept: application/json")
    @POST("api/customer/favourite/{id}")
    Call<FavoriteResponse> markFavorite(@Header("Authorization") String token, @Path("id") Integer resturantId);

    @Headers("Accept: application/json")
    @GET("api/customer/favourite")
    Call<BaseListModel<Favorite>> getFavorite(@Header("Authorization") String token, @Query("page") Integer page);

    @Headers("Accept: application/json")
    @POST("api/customer/order")
    Call<CreateOrderResponse> createOrder(@Header("Authorization") String token, @Body CreateOrderRequest createOrderRequest);

    @Headers("Accept: application/json")
    @GET("api/customer/coupon-validity")
    Call<CouponResponse> checkCoupon(@Header("Authorization") String token, @Query("code") String code);

    @Headers("Accept: application/json")
    @GET("api/customer/category")
    Call<BaseListModel<CategoryFood>> getCategories(@Header("Authorization") String token, @Query("page") Integer page);

    @Headers("Accept: application/json")
    @GET("api/customer/store")
    Call<BaseListModel<Store>> getStores(@Header("Authorization") String token, @Query("lat") Double latitude, @Query("long") Double longitude, @Query("page") Integer page);

    @Headers("Accept: application/json")
    @GET("api/customer/store")
    Call<BaseListModel<Store>> searchStores(@Header("Authorization") String token, @Query("search") String search, @Query("lat") Double latitude, @Query("long") Double longitude, @Query("cost_for_two_min") int cost_for_two_min, @Query("cost_for_two_max") int cost_for_two_max, @Query("veg_only") int veg_only, @Query("cost_for_two_sort") String cost_for_two_sort, @Query("category_id") Long category_id, @Query("page") Integer page);

    @Headers("Accept: application/json")
    @GET("api/customer/store/{id}")
    Call<StoreDetail> getStoreById(@Header("Authorization") String token, @Path("id") Integer resturantId);

    @Headers("Accept: application/json")
    @GET("api/customer/rating/{id}")
    Call<BaseListModel<Review>> getReviews(@Header("Authorization") String token, @Path("id") Integer resturantId, @Query("page") Integer page);

    @Headers("Accept: application/json")
    @GET("api/customer/order")
    Call<BaseListModel<Order>> getOrders(@Header("Authorization") String token, @Query("page") Integer page);

    @Headers("Accept: application/json")
    @GET("api/customer/rating/me")
    Call<BaseListModel<Review>> getReviewsMine(@Header("Authorization") String token, @Query("page") Integer page);

    @Headers("Accept: application/json")
    @GET("api/customer/address")
    Call<ArrayList<Address>> getAddresses(@Header("Authorization") String token);

    @Headers("Accept: application/json")
    @POST("api/customer/address")
    Call<Address> addAddress(@Header("Authorization") String token, @Body Address addAddressRequest);

    @Headers("Accept: application/json")
    @POST("api/customer/rating/{id}")
    Call<RateResponse> rateStore(@Header("Authorization") String token, @Path("id") Integer resturantId, @Body RateRequest rateRequest);

    @Headers("Accept: application/json")
    @PUT("api/customer/address/{id}/update")
    Call<Address> updateAddress(@Header("Authorization") String token, @Body Address addAddressRequest, @Path("id") Integer resturantId);

    @Headers("Accept: application/json")
    @GET("api/settings")
    Call<ArrayList<SettingResponse>> getSettings();

    @GET("https://maps.googleapis.com/maps/api/directions/json?units=metric")
    Call<GoogleMapsRoute> getRoute(@Query("key") String apiKey, @Query("origin") String commaSeparatedSourceLatLong, @Query("destination") String commaSeparatedDestLatLong);
}
