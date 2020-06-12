package com.example.movies;

public class MovieItem {
    private String mId;
    private String mName;
    private String mCategory;
    private String mPoster;
    private String mBack;

    public MovieItem (String id, String name, String category, String poster, String back)
    {
        mId = id;
        mName = name;
        mCategory = category;
        mPoster = poster;
        mBack = back;
    }

    public String getName()
    {
        return mName;
    }
    public String getPoster()
    {
        return mPoster;
    }
    public String getBack()
    {
        return mBack;
    }
    public String getCategory()
    {
        return mCategory;
    }
    public String getId()
    {
        return  mId;
    }

}
