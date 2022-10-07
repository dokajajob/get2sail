package Utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class GPS {

    public String lat;
    public String lng;
    public String user_is;
    public String uid;
    public String date;
    public String username;
    public String password;
    public String uToken;


    public GPS() {
    }

    public GPS(String lat, String  lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public GPS(String lat, String lng, String user_is, String uid, String date, String password, String username, String uToken) {
        this.lat = lat;
        this.lng = lng;
        this.user_is = user_is;
        this.uid = uid;
        this.date = date;
        this.password = password;
        this.username = username;
        this.uToken = uToken;
    }

    public String getUtoken() {return uToken; }

    public void setUtoken(String uToken) {this.uToken = uToken; }

    public String getUsername() { return  username; }

    public void setUsername(String username) {this.username = username; }

    public String getPassword() {return password; }

    public void setPassword(String password) {this.password = password; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getLat() { return lat; }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getUser_is() { return user_is; }

    public void setUser_is(String user_is) { this.user_is = user_is; }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }

}
