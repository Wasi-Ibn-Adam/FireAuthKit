package com.lassanit.authkit.interactions.classes;

public class AndroidApp {
    String name;
    int res;

    public AndroidApp(String name, int res) {
        this.name = name;
        this.res = res;
    }

    public AndroidApp(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }
}
