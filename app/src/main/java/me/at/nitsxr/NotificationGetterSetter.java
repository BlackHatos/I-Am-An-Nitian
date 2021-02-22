package me.at.nitsxr;

public class NotificationGetterSetter
{
    private  String notification;
    private  String time;
    private String imageUrl;

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNotification()
    {
        return notification;
    }

    public String getTime()
    {
        return time;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
