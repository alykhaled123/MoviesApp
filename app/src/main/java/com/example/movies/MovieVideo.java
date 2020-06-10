package com.example.movies;

public class MovieVideo {
    private int mId;
    private String mName;
    private String mSite;
    private String mKey;
    private String mType;

    public MovieVideo (int id, String name, String site, String key, String type)
    {
        mId = id;
        mName = name;
        mSite = site;
        mKey = key;
        mType = type;
    }

    public String getName()
    {
        return mName;
    }
    public String getSite()
    {
        return mSite;
    }
    public String getKey()
    {
        return mKey;
    }
    public String getType()
    {
        return mType;
    }
    public int getId()
    {
        return  mId;
    }

}
