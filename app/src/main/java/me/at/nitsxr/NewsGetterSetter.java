package me.at.nitsxr;

public class NewsGetterSetter
{
    private String imageUrl;
    private String newsTitle;
    public NewsGetterSetter(String imageUrl, String newsTitle)
    {
        this.newsTitle = newsTitle;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

   public String getNewsTitle()
   {
       return newsTitle;
   }
}
