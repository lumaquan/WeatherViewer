package com.example.weatherviewer.json_placeholder;

import java.util.List;

public interface UsersCallback {

    void getUsers(List<User> users);

    void error(String error);
}
