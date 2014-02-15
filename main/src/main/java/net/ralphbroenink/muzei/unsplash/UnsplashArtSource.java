package net.ralphbroenink.muzei.unsplash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;

import net.ralphbroenink.muzei.unsplash.rss.RssItem;
import net.ralphbroenink.muzei.unsplash.rss.RssReader;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by ralphje on 15/02/14.
 */
public class UnsplashArtSource extends RemoteMuzeiArtSource {
    public static final String SOURCE_NAME = "UnsplashArtSource";
    public static final String PREFS_NAME = "UnSplashArtStatus";
    public static final int RECENT_PICTURES = 10;
    public static final long ROTATE_TIME_MILLIS = 24 * 60 * 60 * 1000; // every 24hrs

    public UnsplashArtSource() {
        super(UnsplashArtSource.SOURCE_NAME);
    }

    @Override
    protected void onTryUpdate(int reason) throws RetryException {
        // Allow getting the next artwork
        this.setUserCommands(RemoteMuzeiArtSource.BUILTIN_COMMAND_ID_NEXT_ARTWORK);

        // Get the current picture ID. As 10 pictures are published every 10 days, we can just count
        // and wrap around when we have reached the end of all pictures.
        SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int pictureID = settings.getInt("pictureID", 0);

        // Retrieve the picture items from the interwebs,
        try {
            RssReader reader = new RssReader("http://unsplash.com/rss");
            List<RssItem> items = reader.getItems();

            // We expect at least RECENT_PICTURES pictures. However, we may have retrieved less
            // this is a quick fix to rotate anyway, but if eg 9 items are retrieved, we get item 0
            // twice.
            if (pictureID >= items.size()) {
                pictureID %= items.size();
            }

            RssItem item = items.get(pictureID);

            // Do some magic to retrieve the author
            String author = null;
            int authorPos = item.getTitle().indexOf("By");
            if (authorPos >= 0) {
                author = item.getTitle().substring(authorPos + 3);
            }

            // Do some magic to retrieve the url
            String url = null;
            int srcPos = item.getDescription().indexOf("src=\"");
            if (srcPos >= 0) {
                int srcEndPos = item.getDescription().substring(srcPos + 5).indexOf("\"");
                if (srcEndPos >= 0) {
                    url = item.getDescription().substring(srcPos + 5, srcPos + srcEndPos + 5);
                    // Get a higher resolution photo
                    url = url.replace("_500.jpg", "_1280.jpg");
                }
            }

            // Publish artwork
            this.publishArtwork(new Artwork.Builder()
                    .imageUri(Uri.parse(url))
                    .byline(author)
                    .viewIntent(new Intent(Intent.ACTION_VIEW, item.getLink()))
                    .build());

            // Schedule new update
            this.
            scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLIS);

            // Do the picture id + 1 mod 10
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("pictureID", (pictureID + 1) % RECENT_PICTURES);
            editor.commit();

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RetryException();
        }

    }
}
