package me.at.nitsxr;

public class CollegeItem
{
    private String collegeName;
    private int collegeLogo;

    public CollegeItem(String collegeName, int collegeLogo)
    {
        this.collegeLogo = collegeLogo;
        this.collegeName = collegeName;
    }

    public int getCollegeLogo() {
        return collegeLogo;
    }

    public String getCollegeName() {
        return collegeName;
    }
}
