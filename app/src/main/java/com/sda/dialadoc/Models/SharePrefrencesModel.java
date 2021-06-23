package com.sda.dialadoc.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharePrefrencesModel {



    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }



    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public static  void setCurrentUser(User user,Context context)
    {
        setDefaults("name",user.getName(),context);
        setDefaults("age",user.getAge(),context);
        setDefaults("gender",user.getGender(),context);
        setDefaults("email",user.getEmail(),context);
        setDefaults("phone",user.getPhoneNum(),context);
        setDefaults("id",user.getUserId(),context);
        setDefaults("type",user.getUserType(),context);

    }
    public static User getCurrentUser(Context context)
    {
        com.sda.dialadoc.Models.User user=new User();

        user.setName(getDefaults("name",context));
        user.setAge(getDefaults("age",context));
        user.setGender(getDefaults("gender",context));
        user.setEmail(getDefaults("email",context));
        user.setPhoneNum(getDefaults("phone",context));
        user.setUserId(getDefaults("id",context));
        user.setUserType(getDefaults("type",context));

        return user;
    }
}
