package com.example.weatherviewer.json_placeholder;

import android.os.AsyncTask;
import android.util.Log;

import com.example.weatherviewer.Utils.Clock;
import com.example.weatherviewer.ServiceManager;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;

public interface JsonPlaceholderAPI {

    Clock clock = new Clock();
    String TAG = JsonPlaceholderAPI.class.getSimpleName();
    String BASE_URL = "https://jsonplaceholder.typicode.com/";
    String noSuccessful = "no successful";
    String sucessful = "successful";
    String error = "error";
    String somethingWentWrong = "ups something went wrong";
    boolean showUsers = false;

    @GET("users")
    Call<List<User>> users();

    interface UsersCallback {

        void getUsers(List<User> users);

        void error(String error);
    }



    static void useRetrofitEnquenig(UsersCallback callBack) throws RuntimeException {
        clock.init();
        JsonPlaceholderAPI api = ServiceManager.getJsonPlaceholderClient();
        clock.logMessageElapsedTimeMillis(TAG, "useRetrofitEnquenig: Time to crate client : ");
        api.users().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                clock.logMessageElapsedTimeMillis(TAG, "time to get response: ");
                if (response.isSuccessful()) {
                    List<User> users = response.body();
                    logUserResults(users, "onResponse");
                    if (callBack != null) {
                        callBack.getUsers(users);
                    }
                } else {
                    Log.d(TAG, "onResponse: " + noSuccessful);
                    if (callBack != null) {
                        callBack.error(noSuccessful);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                clock.logMessageElapsedTimeMillis(TAG, "onFailure: time to get response: ");
                if (t instanceof IOException) {
                    Log.d(TAG, "onFailure:Network error: " + t.getLocalizedMessage());
                } else {
                    Log.d(TAG, "onFailuer: Error: " + t.getLocalizedMessage());
                }
            }
        });
    }

    class UsersAsyncTask extends AsyncTask<Void, Void, List<User>> {

        UsersCallback usersCallback;

        public UsersAsyncTask(UsersCallback usersCallback) {
            super();
            this.usersCallback = usersCallback;
        }

        @Override
        protected List<User> doInBackground(Void... voids) {
            clock.init();
            JsonPlaceholderAPI api = ServiceManager.getJsonPlaceholderClient();
            clock.logMessageElapsedTimeMillis(TAG, "doInBackground: time to create client: ");
            List<User> users = null;
            try {
                Response<List<User>> response = api.users().execute();
                clock.logMessageElapsedTimeMillis(TAG, "doInBackground:time to get response: ");
                if (response.isSuccessful()) {
                    users = response.body();
                    Log.d(TAG, "doInBackground: " + sucessful);
                } else {
                    Log.d(TAG, "doInBackground: " + noSuccessful);
                }
            } catch (IOException e) {
                Log.d(TAG, "doInBackground: " + error + e.getLocalizedMessage());
                e.printStackTrace();
            }
            return users;
        }

        @Override
        protected void onPostExecute(List<User> users) {
            clock.logMessageElapsedTimeMillis(TAG, "onPostExecute:time to get response: ");
            if (users != null) {
                logUserResults(users, "onPostExecute");
                if (usersCallback != null) {
                    usersCallback.getUsers(users);
                }
            } else {
                Log.d(TAG, "onPostExecute: " + somethingWentWrong);
                usersCallback.error(somethingWentWrong);
            }
        }
    }

    static void logUserResults(List<User> users, String baseMessage) {
        if (!showUsers) return;
        for (int i = 0; i < users.size(); i++) {
            Log.d(TAG, baseMessage + ":user(" + i + ") = " + users.get(i));
        }
    }
}
