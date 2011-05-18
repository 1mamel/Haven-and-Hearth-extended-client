package haven.resources.sources;

import haven.resources.Resource;
import haven.Utils;

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

    public FileSource(File base) {
        this.base = base;
    }

    public InputStream get(String name) {
        File cur = base;
        String[] parts = Utils.slashPattern.split(name);
        for (int i = 0; i < parts.length - 1; i++)
            cur = new File(cur, parts[i]);
        cur = new File(cur, parts[parts.length - 1] + ".res");
        try {
            return (new FileInputStream(cur));
        } catch (FileNotFoundException e) {
            throw ((Resource.LoadException) (new Resource.LoadException("Could not find resource in filesystem: " + name, this).initCause(e)));
        }
    }

    public String toString() {
        return ("filesystem res source (" + base + ')');
    }
}
