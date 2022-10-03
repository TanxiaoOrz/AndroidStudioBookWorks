package com.example.androidstudiobookworks;

import android.app.Application;

import com.example.androidstudiobookworks.greendao.DaoSession;
import com.example.androidstudiobookworks.greendao.UserDao;

import org.greenrobot.greendao.AbstractDaoSession;

import java.util.HashMap;

public class Myapplication extends Application {
    private static Myapplication myapplication;
    public HashMap<String,String>mInfoMap= new HashMap<>();
    private static DaoSession daoSession;

    public static Myapplication getInstances(){
        return myapplication;
    }

    public DaoSession getDaoSession() {
        return UserDaoManager.getInstance().getDaoSession();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        myapplication = this;
        UserDaoManager.getInstance().init(getApplicationContext());
    }
}
