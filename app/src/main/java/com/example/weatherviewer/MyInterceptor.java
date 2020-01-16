package com.example.weatherviewer;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public  class MyInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Log.d("retrofit", "intercept: currentThread -> " + Thread.currentThread().getName());

        Log.d("retrofit", "intercept: connectTimeout -> " + chain.connectTimeoutMillis());
        Log.d("retrofit", "intercept: readTimeout -> " + chain.readTimeoutMillis());
        Log.d("retrofit", "intercept: writeTimeout -> " + chain.writeTimeoutMillis());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("retrofit", "intercept: Ive got interrupted");
        }

        Request request = chain.request();

        Log.d("retrofit", "intercept: request: method -> " + request.method());
        Log.d("retrofit", "intercept: request: isHttps -> " + request.isHttps());
        if (request.body() != null) {
            Log.d("retrofit", "intercept: request: body.toString -> " + request.body().toString());
            Log.d("retrofit", "intercept: request: body.contentLenght -> " + request.body().contentLength());
            Log.d("retrofit", "intercept: request: body.contentType -> " + request.body().contentType());
            Log.d("retrofit", "intercept: request: body.isDuplex -> " + request.body().isDuplex());
            Log.d("retrofit", "intercept: request: body.isOneShot -> " + request.body().isOneShot());
        }

        Log.d("retrofit", "intercept: request: isHttps -> " + request.headers().names());

        Response response = null;
        try {
            response = chain.proceed(request);
        }catch (Exception e){
            Log.d("retrofit", "intercept: " + e.getMessage());
        }


        Log.d("retrofit", "intercept: response: isHttps -> " + response.message());
        Log.d("retrofit", "intercept: response: isHttps -> " + response.isSuccessful());
        Log.d("retrofit", "intercept: response: isHttps -> " + response.isRedirect());
        Log.d("retrofit", "intercept: response: isHttps -> " + response.code());
        Log.d("retrofit", "intercept: response: isHttps -> " + response.headers().names());

        return response;
    }
}
