package me.alapon.reaz.friendfinder.model;

/**
 * Created by prince on 2/27/2017.
 */

public class RegisterRequest {

    private String username;
    private String password;
    private String email;


    public boolean validateUsername(String username) {

        if(username.length() >= 4)
        {
            this.username = username;
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean validatePassword(String pass) {

        if(pass.length() >= 4)
        {
            this.password = pass;
            return true;
        }
        else
        {
            return false;
        }
    }


}
