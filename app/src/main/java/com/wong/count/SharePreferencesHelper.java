package com.wong.count;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by waterway on 2017/7/30.
 */

public class SharePreferencesHelper {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Context context;
    public SharePreferencesHelper(Context c,String name){
        this.context = c;
        this.sp = c.getSharedPreferences(name,0);
        this.editor = this.sp.edit();

    }
    public void putValue(String key,int value){
        this.editor = this.sp.edit();
        this.editor.putInt(key,value);
        this.editor.apply();
    }
    public int getValue(String key){
        return this.sp.getInt(key,0);
    }
}
