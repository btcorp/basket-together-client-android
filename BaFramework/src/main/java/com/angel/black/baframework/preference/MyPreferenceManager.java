package com.angel.black.baframework.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.angel.black.baframework.security.AESCrypto;
import com.angel.black.baframework.util.BASE64Utils;

/**
 * Created by KimJeongHun on 2016-08-30.
 */
public class MyPreferenceManager {
    private SharedPreferences mShared;
    private SharedPreferences.Editor mEditor;

    public MyPreferenceManager(Context context) {
        this(context, context.getPackageName());
    }

    public MyPreferenceManager(Context context, String packageName) {
        createSetting(context, packageName);
    }

    public void createSetting(Context context, String packageName) {
        mShared = context.getSharedPreferences(packageName + ".preferences", Context.MODE_PRIVATE);
        mEditor = mShared.edit();
    }

    /**
     * DES 로 암호화 하여 데이터를 저장
     *
     * @param name
     * @param value
     */
    public void saveStringAES(String name, String value) {
        synchronized (this) {
            try {
                String enValue = BASE64Utils.encodedString(AESCrypto.encrypt_ecb(AESCrypto.KEY, value.getBytes()));
                mEditor.putString(name, enValue);
                mEditor.commit();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param name
     * @param value
     */
    public void saveString(String name, String value) {
        synchronized (this) {
            mEditor.putString(name, value);
            mEditor.commit();
        }
    }

    public void saveInt(String name, int value) {
        synchronized (this) {
            mEditor.putInt(name, value);
            mEditor.commit();
        }
    }

    public void saveBoolean(String name, boolean value) {
        synchronized (this) {
            mEditor.putBoolean(name, value);
            mEditor.commit();
        }
    }

    public String loadStringDES(String name) {
        synchronized (this) {
            try {
                return BASE64Utils.decodedString(mShared.getString(name, ""));
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public String loadString(String name) {
        synchronized (this) {
            return mShared.getString(name, "");
        }
    }

    public int loadInt(String name) {
        synchronized (this) {
            return mShared.getInt(name, 0);
        }
    }

    public boolean loadBoolean(String name) {
        synchronized (this) {
            return mShared.getBoolean(name, false);
        }
    }
}
