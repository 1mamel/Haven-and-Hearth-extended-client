package haven;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    public static CustomConfig loadConfig() {
        Reader reader = null;
        try {
            final Gson gson = new GsonBuilder().setPrettyPrinting().create();

            if (ResCache.global != null) {
                try {
                    reader = new InputStreamReader(ResCache.global.fetch(CONFIG_DEF_FILE_NAME));
                    LOG.info("Loading config from cache");
                } catch (IOException e) {
                    LOG.warn("config file not founded in cache, trying to load from file", e);
                }
            }
            if (reader == null) {
                reader = new FileReader(new File(CONFIG_DEF_FILE_NAME));
                LOG.info("Loading config from file");
            }
            final CustomConfig config = gson.fromJson(reader, CustomConfig.class);
            checkAndFixConfig(config);
            return config;
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
        return null;
    }

    public static boolean saveConfig(@NotNull final CustomConfig config) {
        Writer writer = null;
        try {
            final Gson gson = new GsonBuilder().setPrettyPrinting().create();
            checkAndFixConfig(config);
            final String json = gson.toJson(config);

            if (ResCache.global != null) {
                try {
                    writer = new BufferedWriter(new OutputStreamWriter(ResCache.global.store(CONFIG_DEF_FILE_NAME), "UTF-8"));
                    LOG.info("Saving config into cache");
                } catch (IOException e) {
                    LOG.warn("Cannot save config into resource cache: IO problem", e);
                }
            }
            if (writer == null) {
                LOG.info("Saving config into file");
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
        if (config.windowSize.x < 800) {
            LOG.warn("Fix config: Window width must be at least 800px");
            config.windowSize.x = 800;
        }
        if (config.windowSize.y < 600) {
            LOG.warn("Fix config: Window height must be at least 600px");
            config.windowSize.y = 600;
        }
        config.windowCenter = config.windowSize.div(2);
    }

}
