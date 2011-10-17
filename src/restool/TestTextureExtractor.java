package restool;

import haven.Utils;
import haven.resources.layers.Image;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.regex.Pattern;


public class TestTextureExtractor {
    static JTextArea console = null;
    static JFrame frame = null;
    static StringBuilder sb = new StringBuilder();
    static int msgCount = 0;
    private static final Pattern slashPattern = Pattern.compile("/");

    public static void log(final String msg) {
        sb.append(msg);
        sb.append('\n');
        msgCount++;
        if (msgCount > 1000) {
            msgCount = 0;
            flushConsole();
        }
    }

    private static void flushConsole() {
        console.setText(console.getText() + sb.toString());
        sb = new StringBuilder();
        console.setCaretPosition(console.getText().length());
        frame.invalidate();
        frame.repaint();
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        final File outDir = new File("out");
        outDir.mkdir();

        final File inDir = new File("res");


        final Object[] options = {"extract", "encode"};
        final Object selection = JOptionPane.showInputDialog(null, "choose", "Input", JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        makeConsole();
        try {
            if ("extract".equals(selection.toString())) {
                extractFile(outDir, inDir);
            } else if ("encode".equals(selection.toString())) {
                encodeResources(outDir, inDir);
            }

            log("\n\n\t\tFINISHED\n\n");
        } catch (Exception e) {
            final StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            log(sw.toString());
        }
        flushConsole();
    }

    private static void makeConsole() {
        frame = new JFrame("TheTrav's dodgy haven res tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JTextArea text = new JTextArea();
        final JScrollPane scroll = new JScrollPane(text);
        frame.getContentPane().add(scroll);
        frame.setSize(500, 600);
        frame.setVisible(true);
        frame.invalidate();
        frame.repaint();
        console = text;
    }

    private static void extractFile(final File outDir, final File inDir) throws Exception {
        for (final File file : inDir.listFiles()) {
            if (file.isDirectory()) {
                extractFile(outDir, file);
            } else {
                extractImage(outDir, file);
            }
        }
    }

    private static void extractImage(final File outDir, final File imgFile) throws Exception {
        log("beginning extract for file:" + imgFile.getPath());
        final InputStream in = new FileInputStream(imgFile);
        final ResourceMetaData meta = new ResourceMetaData();
        meta.name = imgFile.getPath();
        final List<Image> loadedLayers = HavenTextureResExtractor.load(in, meta);
        final String filePath = fix(outDir.getAbsolutePath() + File.separator + imgFile.getPath());
        final String imgFileName = imgFile.getName();
        log(loadedLayers.size() + " layers loaded from file " + imgFileName + " saving to:\n\t" + filePath);
        final File outputDir = new File(filePath);
        outputDir.mkdirs();
        //save metadata
        final String fileNameBase = filePath + File.separator + imgFileName;
        final File metaDataFile = new File(fileNameBase + ".metadata");
        final PrintWriter metaOut = new PrintWriter(new FileWriter(metaDataFile));
        metaOut.println("name_" + meta.name);
        metaOut.println("ver_" + meta.ver);
        //save non img byte streams
        int i = 0;
        for (final byte[] byteArray : meta.nonImgLayers) {
            final String layerName = fileNameBase + "_" + i + ".nonimageLayer";
            final File f = new File(layerName);
            metaOut.println(layerName);
            i++;
            final FileOutputStream layerOut = new FileOutputStream(f);
            layerOut.write(byteArray);
            layerOut.close();
        }

        //save img byte streams as pngs
        int j = 0;
        for (final Image img : loadedLayers) {
            final String fileName = fileNameBase + "_" + j + ".png";
            ImageIO.write(img.img, "PNG", new File(fileName));
            final FileOutputStream imgFlags = new FileOutputStream(fileName + ".flags");
            imgFlags.write(meta.imgFlags.get(img));
            imgFlags.close();
            metaOut.println(fileName);
            j++;
        }
        metaOut.close();
        in.close();
    }

    private static void encodeResources(final File srcDir, final File destDir) throws Exception {
        for (final File srcFile : srcDir.listFiles()) {
            if (srcFile.isDirectory()) {
                encodeResources(srcFile, destDir);
            } else {
                if (srcFile.getName().endsWith(".metadata")) {
                    log("encoding:" + srcFile.getName());
                    final BufferedReader in = new BufferedReader(new FileReader(srcFile));
                    try {
                        encodeFromMeta(in, srcDir, destDir);
                    } finally {
                        in.close();
                    }
                }
            }
        }
    }

    private static void encodeFromMeta(final BufferedReader in, final File srcDir, final File destDir) throws Exception {
        final String namePre = in.readLine().split("_")[1];
        final String name = namePre.replace('\\', '/');
        createDir(name, destDir);
        final File of = new File(destDir,name);

        final FileOutputStream out = new FileOutputStream(of);
        try {
            final int ver = Integer.valueOf(in.readLine().split("_")[1]);

            //write haven signature
            final String sig = "Haven Resource 1";
            out.write(sig.getBytes());

            //write version
            final byte[] buf = new byte[2];
            Utils.uint16e(ver, buf, 0);
            out.write(buf);

            //non image layers
            String line;
            int count = 0;
            for (line = in.readLine(); line != null && line.endsWith(".nonimageLayer"); line = in.readLine()) {
                count++;
                //write file to output stream
                final FileInputStream metaIn = new FileInputStream(new File(srcDir, line));
                try {
                    for (int i = metaIn.read(); i != -1; i = metaIn.read()) {
                        out.write(i);
                    }
                } finally {
                    metaIn.close();
                }
            }
            log("wrote " + count + " non img layers");

            //image layers
            count = 0;
            while (line != null) {
                count++;
                final File imgFile = new File(srcDir, line);
                final FileInputStream imgFlagsIn = new FileInputStream(new File(srcDir, line + ".flags"));
                final byte[] imgFlags = new byte[11];
                try {
                    imgFlagsIn.read(imgFlags);
                } finally {
                    imgFlagsIn.close();
                }
                final FileInputStream imgIn = new FileInputStream(imgFile);
                try {
                    HavenTextureResEncoder.save(imgIn, out, imgFlags);
                } finally {
                    imgIn.close();
                }
                line = in.readLine();
            }
            log("wrote " + count + " image layers");
        } finally {
            out.close();
        }
    }

    private static void createDir(final String fileName, final File baseDir) {
        final String[] split = slashPattern.split(fileName);
        File file = baseDir;
        for (int i = 0; i < split.length - 1; i++) {
            final String s = split[i];
            file = new File(file, s);
            file.mkdirs();
        }
    }

    private static String fix(final String filePath) {
        return filePath.substring(0, filePath.lastIndexOf(File.separator)) + File.separator;
    }

}
