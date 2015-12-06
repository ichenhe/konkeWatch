package cc.chenhe.konke.watch.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 宸赫 on 2015/8/26.
 */
public class Settings {
    public static final String SP_NAME = "main";

    public static final String ITEM_VERSION = "version";
    public static final String ITEM_KONKE_USER_NAME = "konkeUserName";
    public static final String ITEM_KONKE_USER_ID = "konkeUserId";
    public static final String ITEM_KONKE_ACCESS_TOKEN = "konkeAccessToken";
    public static final String ITEM_KONKE_REFRESH_TOKEN = "konkeRefreshToken";
    public static final String ITEM_KONKE_EXPIRES_IN = "konkeExpiresIn";
    public static final String ITEM_KONKE_GET_TIME = "konkeGetTime";

    public static void clearAuth(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(ITEM_KONKE_ACCESS_TOKEN);
        editor.remove(ITEM_KONKE_EXPIRES_IN);
        editor.remove(ITEM_KONKE_REFRESH_TOKEN);
        editor.remove(ITEM_KONKE_USER_ID);
        editor.remove(ITEM_KONKE_GET_TIME);
        editor.remove(ITEM_KONKE_USER_NAME);
        editor.commit();
    }

    public static void writeAuth(Context context, String userName, String userId, String accessToken,
                                 String refreshToken, int exrires_in, long getTime) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (userName != null) {
            editor.putString(ITEM_KONKE_USER_NAME, userName);
        }
        if (userId != null) {
            editor.putString(ITEM_KONKE_USER_ID, userId);
        }
        if (accessToken != null) {
            editor.putString(ITEM_KONKE_ACCESS_TOKEN, accessToken);
        }
        if (refreshToken != null) {
            editor.putString(ITEM_KONKE_REFRESH_TOKEN, refreshToken);
        }
        if (exrires_in > 0) {
            editor.putInt(ITEM_KONKE_EXPIRES_IN, exrires_in);
        }
        if (getTime > 0) {
            editor.putLong(ITEM_KONKE_GET_TIME, getTime);
        }
        editor.commit();

    }
}
