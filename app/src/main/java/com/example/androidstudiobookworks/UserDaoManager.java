package com.example.androidstudiobookworks;

import android.content.Context;

import com.example.androidstudiobookworks.greendao.DaoMaster;
import com.example.androidstudiobookworks.greendao.DaoSession;

public class UserDaoManager {
    private static final String TAG = "UserDaoManager";

    private static final String DATABASE_NAME = "users.db";
    /**
     * 全局保持一个DaoSession
     */
    private DaoSession daoSession;

    private boolean isInited;

    private static final class UserDaoManagerHolder {
        private static final UserDaoManager sInstance = new UserDaoManager();
    }

    public static UserDaoManager getInstance() {
        return UserDaoManagerHolder.sInstance;
    }

    private UserDaoManager() {

    }

    public void init(Context context) {
        if (!isInited) {
            DaoMaster.OpenHelper openHelper = new DaoMaster.DevOpenHelper(
                    context.getApplicationContext(), DATABASE_NAME, null);
            DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDatabase());
            daoSession = daoMaster.newSession();
            isInited = true;
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

}
