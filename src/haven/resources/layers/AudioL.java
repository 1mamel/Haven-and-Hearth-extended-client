package haven.resources.layers;

import haven.resources.Resource;
import haven.Utils;
import haven.VorbisDecoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
* // TODO: write javadoc
* Created by IntelliJ IDEA.
* Date: 18.05.11
* Time: 17:31
*
* @author Vlad.Rassokhin@gmail.com
*/
public class AudioL extends Layer {
    transient public byte[] clip;

    public AudioL(byte[] buf) {
        try {
            clip = Utils.readall(new VorbisDecoder(new ByteArrayInputStream(buf)));
        } catch (IOException e) {
            throw (new Resource.LoadException(e));
        }
    }

    public void init() {
    }
}
