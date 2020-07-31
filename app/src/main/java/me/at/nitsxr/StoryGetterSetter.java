package me.at.nitsxr;

public class StoryGetterSetter
{
    private String name;
    private String rank;
    private String branch;
    private String imageUrl;

    public StoryGetterSetter(String imageUrl,String name, String rank, String branch)
    {
        this.imageUrl = imageUrl;
        this.name = name;
        this.rank = rank;
        this.branch = branch;
    }

    public String getBranch() {
        return branch;
    }

    public String getName() {
        return name;
    }

    public String getRank() {
        return rank;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
