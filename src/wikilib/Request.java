package wikilib;

import wikilib.dom.Page;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Request {
    public String wiki = "http://ringofbrodgar.com";
    public RequestCallback callback;
    public String result;
    public String search;
    public String title;
    public URL url;

    public Request() {
        super();
    }

    public Request(final String reqString) {
        this();
        initSearch(reqString);
    }

    public Request(final String reqString, final RequestCallback reqCallback) {
        this(reqString);
        callback = reqCallback;
    }

    public void initSearch(final String req) {
        search = req;
        try {
            url = new URL(wiki + "/w/index.php?search=" + URLEncoder.encode(search, "UTF-8"));
        } catch (MalformedURLException e) {
        } catch (UnsupportedEncodingException e) {
        }
    }

    public void initPage(final String req) {
        search = req;
        search.replaceAll(" ", "_");
        try {
            url = new URL(wiki + "/wiki/" + search);
        } catch (MalformedURLException e) {
        }
    }

    public void complete() {
        if (callback != null)
            callback.run(this);
    }

    public void setResult(final Page result) {
        setTitle(result.getTitle());
        this.result = result.toString();
        complete();
    }

    public String getResult() {
        return result;
    }

    public URL getUrl() {
        return url;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
