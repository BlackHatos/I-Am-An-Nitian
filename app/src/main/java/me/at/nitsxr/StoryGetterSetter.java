package me.at.nitsxr;

public class StoryGetterSetter
{
    private String name;
    private String rank;
    private String branch;
    private String imageUrl;
    private String story;
    private String college;
    private String exam;
    private String id;

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getId() {
        return id;
    }

    public String getExam() {
        return exam;
    }

    public String getBranch() {
        return branch;
    }

    public String getCollege() {
        return college;
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

    public String getStory() {
        return story;
    }
}
