package com.hababk.userapp.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hababk.userapp.model.CategoryFood;
import com.hababk.userapp.model.RefineSetting;
import com.hababk.userapp.model.Store;
import com.hababk.userapp.model.User;
import com.hababk.userapp.network.ApiUtils;
import com.hababk.userapp.network.ChefService;
import com.hababk.userapp.network.response.Address;
import com.hababk.userapp.network.response.BankDetailResponse;
import com.hababk.userapp.network.response.MenuItem;
import com.hababk.userapp.network.response.RestaurantProfile;
import com.hababk.userapp.network.response.SettingResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by a_man on 12-03-2018.
 */

public class Helper {

    public static void openPlayStoreIntent(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        } else {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static String timeString(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date startDate = simpleDateFormat.parse(date);
            return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(startDate);
        } catch (ParseException pe) {
            pe.printStackTrace();
            return "";
        }
    }

    public static CharSequence timeDiff(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date startDate = new Date();
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            startDate = simpleDateFormat.parse(date);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return DateUtils.getRelativeTimeSpanString(startDate.getTime(), calendar.getTimeInMillis(), DateUtils.SECOND_IN_MILLIS);
    }

    public static void setLoggedInUser(SharedPreferenceUtil sharedPreferenceUtil, User user) {
        sharedPreferenceUtil.setStringPreference(Constants.KEY_USER, new Gson().toJson(user, new TypeToken<User>() {
        }.getType()));
    }

    public static User getLoggedInUser(SharedPreferenceUtil sharedPreferenceUtil) {
        User toReturn = null;
        String savedInPrefs = sharedPreferenceUtil.getStringPreference(Constants.KEY_USER, null);
        if (savedInPrefs != null) {
            toReturn = new Gson().fromJson(savedInPrefs, new TypeToken<User>() {
            }.getType());
        }
        return toReturn;
    }

    public static boolean isNumber(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static boolean isLoggedIn(SharedPreferenceUtil sharedPreferenceUtil) {
        User user = getLoggedInUser(sharedPreferenceUtil);
        return user != null && user.getMobile_verified() == 1;
    }

    public static void setBankDetails(SharedPreferenceUtil sharedPreferenceUtil, BankDetailResponse bankDetailResponse) {
        sharedPreferenceUtil.setStringPreference(Constants.KEY_BANK_DETAIL, new Gson().toJson(bankDetailResponse, new TypeToken<BankDetailResponse>() {
        }.getType()));
    }

    public static BankDetailResponse getBankDetails(SharedPreferenceUtil sharedPreferenceUtil) {
        BankDetailResponse toReturn = null;
        String savedInPrefs = sharedPreferenceUtil.getStringPreference(Constants.KEY_BANK_DETAIL, null);
        if (savedInPrefs != null) {
            toReturn = new Gson().fromJson(savedInPrefs, new TypeToken<BankDetailResponse>() {
            }.getType());
        }
        return toReturn;
    }

    public static String getSpacedAccountNumber(String account_number) {
        StringBuilder accNum = new StringBuilder();
        int count = 0;
        for (char c : account_number.toCharArray()) {
            if (count == 4) {
                accNum.append("-");
                count = 0;
            }
            accNum.append(c);
            count += 1;
        }
        return accNum.toString();
    }

    public static String getApiToken(SharedPreferenceUtil sharedPreferenceUtil) {
        return "Bearer " + sharedPreferenceUtil.getStringPreference(Constants.KEY_TOKEN, null);
    }

    public static String getSpaceRemovedAccountNumber(String s) {
        return s.replaceAll("-", "");
    }

    public static void logout(SharedPreferenceUtil sharedPreferenceUtil) {
        sharedPreferenceUtil.removePreference(Constants.KEY_USER);
        sharedPreferenceUtil.removePreference(Constants.KEY_TOKEN);
    }

    public static ArrayList<CategoryFood> getCategories(SharedPreferenceUtil sharedPreferenceUtil) {
        ArrayList<CategoryFood> toReturn = new ArrayList<>();
        String savedInPrefs = sharedPreferenceUtil.getStringPreference(Constants.KEY_CATEGORY, null);
        if (savedInPrefs != null) {
            ArrayList<CategoryFood> categories = new Gson().fromJson(savedInPrefs, new TypeToken<ArrayList<CategoryFood>>() {
            }.getType());
            toReturn.addAll(categories);
        }
        return toReturn;
    }

    public static void setCategories(SharedPreferenceUtil sharedPreferenceUtil, ArrayList<CategoryFood> menuItemCategories) {
        sharedPreferenceUtil.setStringPreference(Constants.KEY_CATEGORY, new Gson().toJson(menuItemCategories, new TypeToken<ArrayList<CategoryFood>>() {
        }.getType()));
    }

    public static RestaurantProfile getRestaurantDetails(SharedPreferenceUtil sharedPreferenceUtil) {
        RestaurantProfile toReturn = null;
        String savedInPrefs = sharedPreferenceUtil.getStringPreference(Constants.KEY_RESTAURANT_PROFILE, null);
        if (savedInPrefs != null) {
            toReturn = new Gson().fromJson(savedInPrefs, new TypeToken<RestaurantProfile>() {
            }.getType());
        }
        return toReturn;
    }

    public static void setRestaurantDetails(SharedPreferenceUtil sharedPreferenceUtil, RestaurantProfile body) {
        sharedPreferenceUtil.setStringPreference(Constants.KEY_RESTAURANT_PROFILE, new Gson().toJson(body, new TypeToken<RestaurantProfile>() {
        }.getType()));
    }

    public static ArrayList<MenuItem> getCart(SharedPreferenceUtil sharedPreferenceUtil) {
        ArrayList<MenuItem> toReturn = new ArrayList<>();
        String savedInPrefs = sharedPreferenceUtil.getStringPreference(Constants.KEY_CART, null);
        if (savedInPrefs != null) {
            ArrayList<MenuItem> saved = new Gson().fromJson(savedInPrefs, new TypeToken<ArrayList<MenuItem>>() {
            }.getType());
            toReturn.addAll(saved);
        }
        return toReturn;
    }

    public static void setCart(SharedPreferenceUtil sharedPreferenceUtil, ArrayList<MenuItem> cartItems) {
        sharedPreferenceUtil.setStringPreference(Constants.KEY_CART, new Gson().toJson(cartItems, new TypeToken<ArrayList<MenuItem>>() {
        }.getType()));
    }

    public static Store getCartStore(SharedPreferenceUtil sharedPreferenceUtil) {
        Store toReturn = null;
        String savedInPrefs = sharedPreferenceUtil.getStringPreference(Constants.KEY_CART_STORE, null);
        if (savedInPrefs != null) {
            toReturn = new Gson().fromJson(savedInPrefs, new TypeToken<Store>() {
            }.getType());
        }
        return toReturn;
    }

    public static void setCartStore(SharedPreferenceUtil sharedPreferenceUtil, Store store) {
        sharedPreferenceUtil.setStringPreference(Constants.KEY_CART_STORE, new Gson().toJson(store, new TypeToken<Store>() {
        }.getType()));
    }

    public static ArrayList<Address> getAddresses(SharedPreferenceUtil sharedPreferenceUtil) {
        ArrayList<Address> toReturn = new ArrayList<>();
        String savedInPrefs = sharedPreferenceUtil.getStringPreference(Constants.KEY_ADDRESSES, null);
        if (savedInPrefs != null) {
            ArrayList<Address> saved = new Gson().fromJson(savedInPrefs, new TypeToken<ArrayList<Address>>() {
            }.getType());
            toReturn.addAll(saved);
        }
        return toReturn;
    }

    public static void setAddresses(SharedPreferenceUtil sharedPreferenceUtil, ArrayList<Address> addresses) {
        sharedPreferenceUtil.setStringPreference(Constants.KEY_ADDRESSES, new Gson().toJson(addresses, new TypeToken<ArrayList<Address>>() {
        }.getType()));
    }

    public static void setAddressDefault(SharedPreferenceUtil sharedPreferenceUtil, Address addressDefault) {
        sharedPreferenceUtil.setStringPreference(Constants.KEY_ADDRESS_DEFAULT, new Gson().toJson(addressDefault, new TypeToken<Address>() {
        }.getType()));
    }

    public static Address getAddressDefault(SharedPreferenceUtil sharedPreferenceUtil) {
        Address toReturn = null;
        String savedInPrefs = sharedPreferenceUtil.getStringPreference(Constants.KEY_ADDRESS_DEFAULT, null);
        if (savedInPrefs != null) {
            toReturn = new Gson().fromJson(savedInPrefs, new TypeToken<Address>() {
            }.getType());
        }
        return toReturn;
    }

    public static android.location.Address getLastFetchedAddress(SharedPreferenceUtil sharedPreferenceUtil, boolean checkToDefaultToNY) {
        android.location.Address toReturn = null;
        String savedInPrefs = sharedPreferenceUtil.getStringPreference(Constants.KEY_ADDRESS_LAST_FETCH, checkToDefaultToNY ? "{\"mAddressLines\":{\"0\":\"230 Broadway, New York, NY 10007, USA\"},\"mAdminArea\":\"New York\",\"mCountryCode\":\"US\",\"mCountryName\":\"United States\",\"mFeatureName\":\"230\",\"mHasLatitude\":true,\"mHasLongitude\":true,\"mLatitude\":40.7127421,\"mLocale\":\"en\",\"mLocality\":\"New York\",\"mLongitude\":-74.00596890000001,\"mMaxAddressLineIndex\":0,\"mPostalCode\":\"10007\",\"mSubAdminArea\":\"New York County\",\"mSubLocality\":\"Manhattan\",\"mSubThoroughfare\":\"230\",\"mThoroughfare\":\"Broadway\"}" : null);
        if (savedInPrefs != null) {
            toReturn = new Gson().fromJson(savedInPrefs, new TypeToken<android.location.Address>() {
            }.getType());
        }
        return toReturn;
    }

    public static void setLastFetchedAddress(SharedPreferenceUtil sharedPreferenceUtil, android.location.Address fetchedAddress) {
        String locationString = new Gson().toJson(fetchedAddress, new TypeToken<android.location.Address>() {
        }.getType());
        Log.d("LOCATION", locationString);
        sharedPreferenceUtil.setStringPreference(Constants.KEY_ADDRESS_LAST_FETCH, locationString);
    }

    public static void setRefineSetting(SharedPreferenceUtil sharedPreferenceUtils, RefineSetting refineSetting) {
        sharedPreferenceUtils.setStringPreference(Constants.KEY_REFINE, new Gson().toJson(refineSetting, new TypeToken<RefineSetting>() {
        }.getType()));
    }

    public static RefineSetting getRefineSetting(SharedPreferenceUtil sharedPreferenceUtils) {
        RefineSetting toReturn = RefineSetting.getDefault();
        String savedInPrefs = sharedPreferenceUtils.getStringPreference(Constants.KEY_REFINE, null);
        if (savedInPrefs != null) {
            toReturn = new Gson().fromJson(savedInPrefs, new TypeToken<RefineSetting>() {
            }.getType());
        }
        return toReturn;
    }

    public static void closeKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void clearCart(SharedPreferenceUtil sharedPreferenceUtil) {
        sharedPreferenceUtil.removePreference(Constants.KEY_CART);
        sharedPreferenceUtil.removePreference(Constants.KEY_CART_STORE);
    }

    private static void setSettings(SharedPreferenceUtil sharedPreferenceUtil, ArrayList<SettingResponse> body) {
        sharedPreferenceUtil.setStringPreference(Constants.KEY_SETTING, new Gson().toJson(body, new TypeToken<ArrayList<SettingResponse>>() {
        }.getType()));
    }

    private static ArrayList<SettingResponse> getSettings(SharedPreferenceUtil sharedPreferenceUtil) {
        ArrayList<SettingResponse> toReturn = new ArrayList<>();
        String savedInPrefs = sharedPreferenceUtil.getStringPreference(Constants.KEY_SETTING, null);
        if (savedInPrefs != null) {
            ArrayList<SettingResponse> settings = new Gson().fromJson(savedInPrefs, new TypeToken<ArrayList<SettingResponse>>() {
            }.getType());
            toReturn.addAll(settings);
        }
        return toReturn;
    }

    public static void refreshSettings(final SharedPreferenceUtil sharedPreferenceUtil) {
        ApiUtils.getClient().create(ChefService.class).getSettings().enqueue(new Callback<ArrayList<SettingResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<SettingResponse>> call, Response<ArrayList<SettingResponse>> response) {
                if (response.isSuccessful() && sharedPreferenceUtil != null)
                    setSettings(sharedPreferenceUtil, response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<SettingResponse>> call, Throwable t) {

            }
        });
    }

    public static String getSetting(SharedPreferenceUtil sharedPreferenceUtil, String settingName) {
        ArrayList<SettingResponse> settings = getSettings(sharedPreferenceUtil);
        int index = settings.indexOf(new SettingResponse(settingName));
        if (index != -1) {
            return settings.get(index).getValue();
        } else {
            return null;
        }
    }

    public static String getReadableDateTime(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date startDate = simpleDateFormat.parse(date);
            return new SimpleDateFormat("hh:mm aa, dd MMM yyyy", Locale.getDefault()).format(startDate);
        } catch (ParseException pe) {
            pe.printStackTrace();
            return "";
        }
    }
}
