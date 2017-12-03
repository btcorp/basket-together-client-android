package com.blackangel.baskettogether.user.domain;

/**
 * Created by kimjeonghun on 2017. 12. 3..
 */

public class User {
    public enum UserRegType {
        TYPE_APP(0),
        TYPE_FACEBOOK(1);

        int regType;

        private UserRegType(int regType) {
            this.regType = regType;
        }

        public static UserRegType valueOf(int regType) {
            switch(regType) {
                case 0:
                    return TYPE_APP;
                case 1:
                    return TYPE_FACEBOOK;
            }

            return null;
        }

        public int getValue() {
            return regType;
        }
    }

    private long id;
    private String password;
    private String nickname;
    private String email;
    private String snsId;

    public User(String password, String nickname, String email, String snsId) {
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.snsId = snsId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSnsId() {
        return snsId;
    }

    public void setSnsId(String snsId) {
        this.snsId = snsId;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", snsId='" + snsId + '\'' +
                '}';
    }
}
