package restool;

import haven.Utils;
import haven.resources.layers.Image;
import haven.resources.layers.Layer;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class HavenTextureResExtractor {

    public static List<Image> load(final InputStream in, final ResourceMetaData meta) throws Exception {
        final String sig = "Haven Resource 1";
        final byte[] sigBuf = new byte[sig.length()];
        readall(in, sigBuf);
        if (!sig.equals(new String(sigBuf))) {
            throw (new Exception("Invalid res signature"));
        }
        final byte[] verBuf = new byte[2];
        readall(in, verBuf);
        meta.ver = Utils.uint16d(verBuf, 0);
        TestTextureExtractor.log("ver=" + meta.ver);
        final List<Image> layers = new LinkedList<Image>();
        outer:
        while (true) {
            final StringBuilder tbuf = new StringBuilder();
            while (true) {
                final byte bb;
                final int ib;
                if ((ib = in.read()) == -1) {
                    if (tbuf.length() == 0)
                        break outer;
                    throw (new Exception("Incomplete resource "));
                }
                bb = (byte) ib;
                if (bb == 0)
                    break;
                tbuf.append((char) bb);
            }
            final byte[] lenBuf = new byte[4];
            readall(in, lenBuf);
            final int len = Utils.int32d(lenBuf, 0);
            TestTextureExtractor.log("len=" + len);
            final byte[] layerBuf = new byte[len];
            readall(in, layerBuf);
            final String layerTypeName = tbuf.toString();
            TestTextureExtractor.log("layerType=" + layerTypeName);
            if ("image".equals(layerTypeName)) {
                final Image img = new Image(layerBuf);
                layers.add(img);
                final ByteArrayOutputStream imgFlags = new ByteArrayOutputStream();
                for (int i = 0; i < 11; i++) {
                    imgFlags.write(layerBuf[i]);
                }
                meta.imgFlags.put(img, imgFlags.toByteArray());
            } else {
                TestTextureExtractor.log("non image layer found:" + layerTypeName);
                final byte[] layerNameBytes = layerTypeName.getBytes();
                final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bytes.write(layerNameBytes);//layer name
                final byte nullByte = 0;
                bytes.write(nullByte);//null terminator
                bytes.write(lenBuf);//byte length
                bytes.write(layerBuf);//byte data
                meta.nonImgLayers.add(bytes.toByteArray());
            }
        }
        for (final Layer l : layers)
            l.init();
        return layers;
    }

    private static void readall(final InputStream in, final byte[] buf) throws Exception {
        int ret, off = 0;
        while (off < buf.length) {
            ret = in.read(buf, off, buf.length - off);
            if (ret < 0)
                throw (new Exception("Incomplete resource "));
            off += ret;
        }
    }

}

