package me.at.nitsxr;

import android.util.Log;

public class CollegeItem
{
    private String collegeName;
    private String logoUrl;
    public CollegeItem(String collegeName, String logoUrl)
    {
        this.logoUrl = logoUrl;
        this.collegeName = collegeName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getCollegeName() {
        return collegeName;
    }
}
