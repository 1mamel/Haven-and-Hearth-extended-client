package haven.resources.sources;

import haven.StreamTee;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
* // TODO: write javadoc
* Created by IntelliJ IDEA.
* Date: 18.05.11
* Time: 16:54
*
* @author Vlad.Rassokhin@gmail.com
*/
public abstract class TeeSource implements ResSource, Serializable {
    public ResSource back;

    public TeeSource(final ResSource back) {
        this.back = back;
    }

    public InputStream get(final String name) throws IOException {
        final StreamTee tee = new StreamTee(back.get(name));
        tee.setncwe();
        tee.attach(fork(name));
        return (tee);
    }

    public abstract OutputStream fork(String name) throws IOException;

    public String toString() {
        return ("forking source backed by " + back);
    }
}
