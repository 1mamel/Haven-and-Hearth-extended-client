package haven.resources.sources;

import haven.Resource;

import java.io.*;

/**
 * // TODO: write javadoc
 * Created by IntelliJ IDEA.
 * Date: 18.05.11
 * Time: 16:52
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public class FileSource implements ResSource, Serializable {
    File base;

    public FileSource(final File base) {
        this.base = base;
    }

    public InputStream get(final String name) {
        final File cur = new File(base, name + ".res");
//        System.out.println("trying to load " + cur.getAbsolutePath());
        if (!cur.exists()) {
//            System.out.println("No such file " + cur.getAbsolutePath());
            throw new Resource.LoadException("No such file " + name);
        }
        try {
            return (new FileInputStream(cur));
        } catch (FileNotFoundException e) {
            throw ((Resource.LoadException) (new Resource.LoadException("Could not find resource in filesystem: " + name).initCause(e)));
        }
    }

    public String toString() {
        return ("filesystem res source (" + base + ')');
    }
}
