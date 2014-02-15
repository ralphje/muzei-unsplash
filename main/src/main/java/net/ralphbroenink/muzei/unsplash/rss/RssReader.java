package net.ralphbroenink.muzei.unsplash.rss;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by ralphje on 15/02/14.
 */
public class RssReader {
    private String url;

    public RssReader(String url) {
        this.url = url;
    }

    public List<RssItem> getItems() throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        RssParser handler = new RssParser();
        parser.parse(this.url, handler);
        return handler.getItems();
    }
}
