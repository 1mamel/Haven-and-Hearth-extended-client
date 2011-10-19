package haven.scriptengine;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by IntelliJ IDEA.
 * Date: 20.10.11
 * Time: 2:23
 *
 * @author Vlad.Rassokhin@gmail.com
 */
class MulticastWriter extends Writer {

    private final Set<Writer> myWriters = new CopyOnWriteArraySet<Writer>();

    @Override
    public Writer append(final char c) throws IOException {
        for (final Writer writer : myWriters) {
            writer.append(c);
        }
        return this;
    }

    @Override
    public Writer append(final CharSequence csq) throws IOException {
        for (final Writer writer : myWriters) {
            writer.append(csq);
        }
        return this;
    }

    @Override
    public Writer append(final CharSequence csq, final int start, final int end) throws IOException {
        for (final Writer writer : myWriters) {
            writer.append(csq, start, end);
        }
        return this;
    }

    @Override
    public void write(final int c) throws IOException {
        for (final Writer writer : myWriters) {
            writer.write(c);

        }
    }

    @Override
    public void write(final char[] cbuf) throws IOException {
        for (final Writer writer : myWriters) {
            writer.write(cbuf);
        }
    }

    @Override
    public void write(final String str) throws IOException {
        for (final Writer writer : myWriters) {
            writer.write(str);
        }
    }

    @Override
    public void write(final String str, final int off, final int len) throws IOException {
        for (final Writer writer : myWriters) {
            writer.write(str,off,len);
        }
    }

    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        for (final Writer writer : myWriters) {
            writer.write(cbuf, off, len);
        }
    }

    @Override
    public void flush() throws IOException {
        for (final Writer writer : myWriters) {
            writer.flush();
        }
    }

    @Override
    public void close() throws IOException {
        for (final Writer writer : myWriters) {
            writer.close();
        }
    }

    public void addWriter(@NotNull final Writer writer) {
        myWriters.add(writer);
    }

    public void removeWriter(@NotNull final Writer writer) {
        myWriters.remove(writer);
    }
}
