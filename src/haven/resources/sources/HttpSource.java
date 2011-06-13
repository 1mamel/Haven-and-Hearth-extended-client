package haven.resources.sources;

import haven.Resource;
import haven.SslHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

/**
* // TODO: write javadoc
* Created by IntelliJ IDEA.
* Date: 18.05.11
* Time: 16:53
*
* @author Vlad.Rassokhin@gmail.com
*/
public class HttpSource implements ResSource, Serializable {
    private final transient SslHelper ssl;
    public URL baseurl;

    {
        ssl = new SslHelper();
        try {
            ssl.trust(SslHelper.loadX509(Resource.class.getResourceAsStream("ressrv.crt")));
        } catch (java.security.cert.CertificateException e) {
            throw (new Error("Invalid built-in certificate", e));
        }
        ssl.ignoreName();
    }

    public HttpSource(URL baseurl) {
        this.baseurl = baseurl;
    }

    private static URL encodeurl(URL raw) throws IOException {
        /* This is "kinda" ugly. It is, actually, how the Java
        * documentation recommend that it be done, though... */
        try {
            return (new URL(new URI(raw.getProtocol(), raw.getHost(), raw.getPath(), raw.getRef()).toASCIIString()));
        } catch (URISyntaxException e) {
            throw (new IOException(e));
        }
    }

    public InputStream get(String name) throws IOException {
        URL resurl = encodeurl(new URL(baseurl, name + ".res"));
        URLConnection c;
        if (resurl.getProtocol().equals("https"))
            c = ssl.connect(resurl);
        else
            c = resurl.openConnection();
        c.addRequestProperty("User-Agent", "Haven/1.0");
        return (c.getInputStream());
    }

    public String toString() {
        return ("HTTP res source (" + baseurl + ')');
    }
}
