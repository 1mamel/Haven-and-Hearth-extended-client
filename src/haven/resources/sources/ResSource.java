package haven.resources.sources;

import java.io.IOException;
import java.io.InputStream;

/**
* // TODO: write javadoc
* Created by IntelliJ IDEA.
* Date: 18.05.11
* Time: 16:35
*
* @author Vlad.Rassokhin@gmail.com
*/
public interface ResSource {
    public InputStream get(String name) throws IOException;
}
