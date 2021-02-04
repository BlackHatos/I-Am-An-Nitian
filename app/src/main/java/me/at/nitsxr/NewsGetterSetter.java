package me.at.nitsxr;

import java.io.Serializable;

public class NewsGetterSetter implements Serializable
{
    private String imageUrl;
    private String imageUrl2;
    private String newsTitle;
    private String newsDescp;
    private String newsDate;
    private String newsId;
    private String status;
    private String count;

    public NewsGetterSetter getObject()
    {
        return this;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNewsId(String newsId)
    {
        this.newsId = newsId;
    }

    public void setImageUrl2(String imageUrl2)
    {
        this.imageUrl2 = imageUrl2;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public void  setNewsTitle(String newsTitle)
    {
        this.newsTitle = newsTitle;
    }

    public  void setNewsDescp(String newsDescp)
    {
        this.newsDescp = newsDescp;
    }

    public void setNewsDate(String newsDate)
    {
        this.newsDate = newsDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

   public String getNewsTitle()
   {
       return newsTitle;
   }

   public String getNewsDescp()
   {
       return newsDescp;
   }

   public String getNewsDate()
   {
       return newsDate;
   }

   public String getNewsId()
   {
       return newsId;
   }

    public String getImageUrl2()
    {
        return imageUrl2;
    }

    public String getStatus() {
        return status;
    }

    public String getCount() {
        return count;
    }
}
