package haven;

/**
* // TODO: write javadoc
* Created by IntelliJ IDEA.
* Date: 18.05.11
* Time: 18:42
*
* @author Vlad.Rassokhin@gmail.com
*/
public class ResClassLoader extends ClassLoader {
    private Resource resource;

    public ResClassLoader(ClassLoader parent, Resource res) {
        super(parent);
        resource = res;
    }

    public Resource getres() {
        return (resource);
    }
}
