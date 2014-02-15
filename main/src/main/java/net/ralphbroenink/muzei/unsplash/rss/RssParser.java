package net.ralphbroenink.muzei.unsplash.rss;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ralphje on 15/02/14.
 */
public class RssParser extends DefaultHandler {
    private List<RssItem> items;
    private RssItem currentItem;
    private String currentElement; // We can safely assume there's only one currentElement!
    private StringBuffer currentString;

    public RssParser() {
        this.items = new ArrayList();
    }

    public List<RssItem> getItems() {
        return this.items;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("item".equals(qName)) {
            this.currentItem = new RssItem();
        } else {
            this.currentElement = qName;
            this.currentString = new StringBuffer();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("item")) {
            this.items.add(currentItem);
            currentItem = null;

        } else if (currentItem != null && qName.equals(this.currentElement)) {
            if (this.currentElement.equals("title")) {
                currentItem.setTitle(currentString.toString());
            } else if (this.currentElement.equals("link")) {
                currentItem.setLink(currentString.toString());
            } else if (this.currentElement.equals("description")) {
                currentItem.setDescription(currentString.toString());
            }
            currentString = null;
            this.currentElement = null;
        }
    }
    // Characters method fills current RssItem object with data when title and link tag content is being processed
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.currentItem != null && this.currentElement != null) {
            this.currentString.append(new String(ch, start, length));
        }
    }
}
