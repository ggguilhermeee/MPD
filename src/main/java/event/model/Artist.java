package event.model;


/**
 * Created by dmigu on 30/03/2017.
 */
public class Artist {
    private String name;
    private String bio;
    private String url;
    private String[] imagesUri;
    private String mBid;

    public Artist(String name, String bio, String url, String[] imagesUri, String mBid) {
        this.name = name;
        this.bio = bio;
        this.url = url;
        this.imagesUri = imagesUri;
        this.mBid = mBid;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getUrl() {
        return url;
    }

    public String[] getImagesUri() {
        return imagesUri;
    }

    public String getmBid() {
        return mBid;
    }

    @Override
    public String toString() {
        return name;
    }
}
