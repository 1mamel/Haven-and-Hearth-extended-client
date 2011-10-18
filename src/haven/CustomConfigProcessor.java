package haven;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * Custom configuration loader/saver.
 * Using json for storing configuration. (Gson lib)
 *
 * @author Vlad.Rassokhin@gmail.com
 * @version 2.0
 */

public class CustomConfigProcessor {
    public static final String CONFIG_DEF_FILE_NAME = "config.json";

    protected static final Logger LOG = Logger.getLogger(CustomConfigProcessor.class);

    public static boolean loadConfig() {
        Reader reader = null;
        try {
            final Gson gson = new Gson();

            if (ResCache.global != null) {
                try {
                    reader = new InputStreamReader(ResCache.global.fetch(CONFIG_DEF_FILE_NAME));
                } catch (IOException e) {
                    LOG.warn("config file not founded in cache, trying to load from file", e);
                }
            }
            if (reader == null) {
                reader = new FileReader(new File(CONFIG_DEF_FILE_NAME));
            }
            final CustomConfig config = gson.fromJson(reader, CustomConfig.class);
            checkAndFixConfig(config);
            CustomConfig.setConfig(config);
            return true;
        } catch (FileNotFoundException e) {
            LOG.warn("Cannot load config: config file not found", e);
        } catch (JsonIOException e) {
            LOG.error("Cannot load config: IO problem", e);
        } catch (JsonSyntaxException e) {
            LOG.error("Cannot load config: file has bad syntax", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ignored) {
            }
        }
        return false;
    }

    public static boolean saveConfig() {
        Writer writer = null;
        try {
            final Gson gson = new Gson();
            final CustomConfig config = CustomConfig.getConfig();
            checkAndFixConfig(config);
            final String json = gson.toJson(config);

            if (ResCache.global != null) {
                try {
                    writer = new BufferedWriter(new OutputStreamWriter(ResCache.global.store(CONFIG_DEF_FILE_NAME), "UTF-8"));
                } catch (IOException e) {
                    LOG.warn("Cannot save config into resource cache: IO problem", e);
                }
            }
            if (writer == null) {
                final File file = new File(CONFIG_DEF_FILE_NAME);
                if (file.exists()) {
                    if (!file.delete()) {
                        LOG.warn("Cannot remove old config file, trying to overwrite");
                    }
                }
                writer = new FileWriter(new File(CONFIG_DEF_FILE_NAME));
            }
            writer.append(json);
            return true;
        } catch (IOException e) {
            LOG.warn("Cannot save config: IO problem", e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ignored) {
            }
        }
        return false;
    }


    private static void checkAndFixConfig(@NotNull final CustomConfig config) {
        if (config.windowSize.x < 800 || config.windowSize.y < 600) {
            LOG.warn("Fix config: Window size must be at least 800x600");
            config.windowSize.set(800, 600);
        }
        config.windowCenter = config.windowSize.div(2);
    }

}
