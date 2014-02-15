package net.ralphbroenink.muzei.unsplash.rss;

import android.net.Uri;

/**
 * Created by ralphje on 15/02/14.
 */
public class RssItem {
    private String title;
    private Uri link;
    private String description;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Uri getLink() {
        return this.link;
    }

    public void setLink(Uri link) {
        this.link = link;
    }

    public void setLink(String link) {
        this.link = Uri.parse(link);
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.title;
    }

}
