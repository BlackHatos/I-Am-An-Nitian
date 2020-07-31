package me.at.nitsxr;

public class TagGetterSetter
{
    private String tagImage;
    private String tagText;

    public TagGetterSetter(String tagImage, String tagText)
    {
            this.tagImage = tagImage;
            this.tagText = tagText;
    }

    public String getTagText()
    {
      return tagText;
    }

    public String getImage()
    {
        return tagImage;
    }

}
