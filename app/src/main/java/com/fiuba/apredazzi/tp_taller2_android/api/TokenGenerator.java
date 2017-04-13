package com.fiuba.apredazzi.tp_taller2_android.api;

import android.text.TextUtils;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by apredazzi on 4/2/17.
 */

public class TokenGenerator {

    public static final String API_BASE_URL = "https://music-io-shared-server.herokuapp.com/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
        new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null);
    }

    public static <S> S createService(
        Class<S> serviceClass, final String authToken) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(interceptor);

        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor interceptorAuth =
                new AuthenticationInterceptor(authToken);

            if (!httpClient.interceptors().contains(interceptorAuth)) {
                httpClient.addInterceptor(interceptorAuth);

                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }

        return retrofit.create(serviceClass);
    }
}
