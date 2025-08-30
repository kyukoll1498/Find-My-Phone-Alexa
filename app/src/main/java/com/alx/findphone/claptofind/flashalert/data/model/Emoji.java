package com.alx.findphone.claptofind.flashalert.data.model;

public class Emoji {
    private int icon;
    private String title;
    private String subTitle;

    public Emoji(int icon, String title, String subTitle) {
        this.icon = icon;
        this.title = title;
        this.subTitle = subTitle;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
}
