package translation;

import com.memetix.mst.detect.Detect;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Vlad.Rassokhin@gmail.com
 */
public class MicrosoftTranslatorProvider {

    public static final Logger LOG = Logger.getLogger(MicrosoftTranslatorProvider.class);

    @Nullable
    private String myAPIKey;
    @NotNull
    private Language myDestLanguage = Language.ENGLISH;
    private boolean myTurnedOn;

    public Map<String, Language> getAvailableLanguages() {
        Map<String, Language> retMap;
        try {
            Translate.setKey(myAPIKey);
            retMap = Language.values(myDestLanguage);
        } catch (Exception e) {
            LOG.warn("Some translation error", e);
            final Map<String, Language> map = new TreeMap<String, Language>();
            for (final Language lang : Language.values()) {
                map.put(lang.name(), lang);
            }
            retMap = map;
        }
        retMap.values().remove(Language.AUTO_DETECT);
        retMap.put("!!ENGLISH!!", Language.ENGLISH);
        return retMap;
    }

    public MicrosoftTranslatorProvider() {
        Translate.setKey(myAPIKey);
    }

    public void useKey(@Nullable final String key) {
        myAPIKey = key;
        Translate.setKey(key == null ? "" : key);
    }

    public String getKey() {
        return myAPIKey;
    }

    public void useLanguage(@NotNull final Language language) {
        myDestLanguage = language;
    }

    @NotNull
    public Language getLanguage() {
        return myDestLanguage;
    }

    public void turn(final boolean state) {
        myTurnedOn = state;
    }

    public boolean isWorking() {
        return myTurnedOn;
    }

    @NotNull
    public String translate(@NotNull final String string) {
        if (!myTurnedOn || myAPIKey == null) {
            return string;
        }
        try {
            final Language language = Detect.execute(string);
            if (myDestLanguage.equals(language)) {
                return string;
            }
            final StringBuilder sb = new StringBuilder();
            sb.append('[').append(language.getName(myDestLanguage)).append("] ");
            sb.append(Translate.execute(string, language, myDestLanguage));
            return sb.toString();
        } catch (Exception e) {
            LOG.warn("Some translation error", e);
            return string;
        }
    }
}
