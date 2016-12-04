package info.androidhive.firebase;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zty on 2016/12/2.
 */

public class AppUser {
    private String gender;
    private String location;
    private String name;
    private String phone;
    private long score;
    private String email;
    private Map<String, Boolean>friend=new HashMap<>();

    public AppUser() {
    }

    public AppUser(String email, String name, String location, String gender, String phone, long score) {
        this.name = name;
        this.gender =gender;
        this.phone = phone;
        this.score= score;
        this.location=location;
        this.email=email;
        this.friend=friend;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getLocation() {
        return location;
    }
    public long getScore() {
        return score;
    }
    public String getPhone() {
        return phone;
    }
    public String getEmail(){
        return email;
    }


}
