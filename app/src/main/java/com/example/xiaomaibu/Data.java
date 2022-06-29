package com.example.xiaomaibu;

import android.app.Application;
import android.graphics.Bitmap;

public class Data extends Application {
    private static String static_username;
    public static String getusername(){
        return static_username;
    }
    private static String static_passwd;
    public static String getpasswd(){
        return static_passwd;
    }
    private static String static_career;
    public static String getcareer(){
        return static_career;
    }
    private static String static_community;
    public static String getcommunity(){
        return static_community;
    }
    public static void setall(String username,String mailbox,String passwd,String career,String community){
        static_username=username;
        static_career=career;
        static_mailbox=mailbox;
        static_community=community;
        static_passwd=passwd;
    }
    public static void setStatic_career(String career){
        static_career=career;
    }
    private static String static_mailbox;
    public static String getmailbox(){
        return static_mailbox;
    }
    @Override
    public void onCreate(){
        static_username="username";
        super.onCreate();
    }
    private static String choose_community_name;
    public static String getChoose_community_name(){ return choose_community_name; }
    public static void setChoose_community_name(String community_name){
        choose_community_name=community_name;
    }
    private static String choose_community_Image;
    private static String choose_community_Background;
    public static String getChoose_community_Image(){return choose_community_Image;}
    public static String getChoose_community_Background(){return choose_community_Background;}
    public static void setChoose_community_Image(String community_image,String community_background){
        choose_community_Image=community_image;
        choose_community_Background=community_background;
    }
    private static String choose_community_intro;
    public static String getChoose_community_intro(){ return choose_community_name; }
    public static void setChoose_community_intro(String community_intro){
        choose_community_intro=community_intro;
    }

}
