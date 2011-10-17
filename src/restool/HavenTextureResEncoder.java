package restool;

import haven.Utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class HavenTextureResEncoder {

    public static void save(final InputStream in, final OutputStream out, final byte[] imgFlags) throws Exception {
        final byte[] layerName = "image".getBytes();
        out.write(layerName); // layer name
        out.write(0); // null terminator

        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        for (int i = in.read(); i >= 0; i = in.read()) {
            outStream.write(i);
        }
        final byte[] imageBytes = outStream.toByteArray();

        // img length
        final byte[] buf = new byte[4];
        Utils.int32e(imageBytes.length + imgFlags.length, buf, 0);
        out.write(buf);

        // img flags
        out.write(imgFlags);

        // img
        out.write(imageBytes);
        TestTextureExtractor.log("wrote img of len:" + imageBytes.length);
    }
}
