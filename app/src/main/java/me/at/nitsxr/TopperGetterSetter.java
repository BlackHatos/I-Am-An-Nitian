package me.at.nitsxr;

public class TopperGetterSetter
{
    private String id;
    private String name;
    private String exam;
    private String college;
    private String rank;
    private String branch;
    private String image_link;

    public void setId(String id) {
        this.id = id;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public String getCollege() {
        return college;
    }

    public String getBranch() {
        return branch;
    }

    public String getExam() {
        return exam;
    }

    public String getId() {
        return id;
    }

    public String getImage_link() {
        return image_link;
    }
}
