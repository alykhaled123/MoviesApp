package com.alykhaled.Movibes;

public class CastMember {
    private String mId;
    private String mCharacter;
    private String mName;
    private String mPic;

    public CastMember( String mId, String mCharacter,String mName, String mPic) {
        this.mCharacter = mCharacter;
        this.mId = mId;
        this.mName = mName;
        this.mPic = mPic;
    }

    public String getCharacter() {
        return mCharacter;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getPic() {
        return mPic;
    }
}
