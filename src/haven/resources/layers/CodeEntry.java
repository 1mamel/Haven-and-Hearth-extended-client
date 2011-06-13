package haven.resources.layers;

import haven.ResClassLoader;
import haven.Resource;
import haven.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * // TODO: write javadoc
 * Created by IntelliJ IDEA.
 * Date: 18.05.11
 * Time: 17:36
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public class CodeEntry extends Layer {
    private String clnm;
    private Map<String, Code> clmap = new TreeMap<String, Code>();
    private Map<String, String> pe = new TreeMap<String, String>();
    final transient private Map<String, Class<?>> lpe = new TreeMap<String, Class<?>>();
    final transient private Map<Class<?>, Object> ipe = new HashMap<Class<?>, Object>();
    private Resource resource;

    public CodeEntry(Resource resource, byte[] buf) {
        this.resource = resource;
        int[] off = new int[1];
        off[0] = 0;
        while (off[0] < buf.length) {
            pe.put(Utils.strd(buf, off), Utils.strd(buf, off));
        }
    }

    public void init() {
        for (Code c : resource.layers(Code.class)) {
            clmap.put(c.name, c);
        }
        ClassLoader loader = new ResClassLoader(Resource.class.getClassLoader(), resource) {
            public Class<?> findClass(String name) throws ClassNotFoundException {
                Code c = clmap.get(name);
                if (c == null) {
                    throw (new ClassNotFoundException("Could not find class " + name + " in resource (" + resource + ')'));
                }
                return (defineClass(name, c.data, 0, c.data.length));
            }
        };
        try {
            for (Map.Entry<String, String> e : pe.entrySet()) {
                String name = e.getKey();
                String clnm = e.getValue();
                Class<?> cl = loader.loadClass(clnm);
                lpe.put(name, cl);
            }
        } catch (ClassNotFoundException e) {
            throw (new Resource.LoadException(e, resource));
        }
    }

    public <T> T get(Class<T> cl) {
        Resource.PublishedCode entry = cl.getAnnotation(Resource.PublishedCode.class);
        if (entry == null)
            throw (new RuntimeException("Tried to fetch non-published res-loaded class " + cl.getName() + " from " + resource.name));
        Class<?> acl;
        synchronized (lpe) {
            if (lpe.get(entry.name()) == null) {
                throw (new RuntimeException("Tried to fetch non-present res-loaded class " + cl.getName() + " from " + resource.name));
            } else {
                acl = lpe.get(entry.name());
            }
        }
        try {
            synchronized (ipe) {
                if (ipe.get(acl) != null) {
                    return (cl.cast(ipe.get(acl)));
                } else {
                    T inst;
                    if (entry.instancer() != Resource.PublishedCode.Instancer.class)
                        inst = cl.cast(entry.instancer().newInstance().make(acl));
                    else
                        inst = cl.cast(acl.newInstance());
                    ipe.put(acl, inst);
                    return (inst);
                }
            }
        } catch (InstantiationException e) {
            throw (new RuntimeException(e));
        } catch (IllegalAccessException e) {
            throw (new RuntimeException(e));
        }
    }
}
