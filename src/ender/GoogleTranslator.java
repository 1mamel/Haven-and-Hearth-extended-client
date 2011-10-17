package ender;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class GoogleTranslator {

    protected static final String ENCODING = "UTF-8";

    private String apiKey;
    private static final String apiUrl = "https://www.googleapis.com/language/translate/v2?key=";
    private String lang = "en";
    private boolean turnedon = false;

    public String getKey() {
        if (apiKey == null) apiKey = "";
        return apiKey;
    }

    public String translate(String str) {
        if (!turnedon || apiKey == null)
            return str;
        String res = "";
        final URL url = url(str);
        if (url == null)
            return str;
        try {
            final HttpURLConnection uc = (HttpURLConnection) url
                    .openConnection();
            uc.setRequestMethod("GET");
            uc.setDoOutput(true);
            try {
                final String result;
                try {
                    result = inputStreamToString(uc.getInputStream());
                } catch (Exception e) {
                    return str;
                }
                final JSONObject o = (JSONObject) JSONValue.parseWithException(result);
                final JSONObject data = (JSONObject) o.get("data");
                final JSONArray translations = (JSONArray) data.get("translations");
                final JSONObject tr1 = (JSONObject) translations.get(0);
                str = (String) tr1.get("translatedText");
                final String sourceLanguage = (String) tr1.get("detectedSourceLanguage");
                res = '[' + sourceLanguage + "] " + str;
            } catch (ParseException e) {
                return str;
            } catch (ClassCastException e) {
                return str;
            } catch (Exception e) {
                return str;
            } finally { // http://java.sun.com/j2se/1.5.0/docs/guide/net/http-keepalive.html
                uc.getInputStream().close();
                if (uc.getErrorStream() != null) {
                    uc.getErrorStream().close();
                }
            }
        } catch (IOException e) {
            return str;
        }

        return res;
    }

    private static String inputStreamToString(final InputStream inputStream) throws Exception {
        final StringBuilder outputBuilder = new StringBuilder();

        try {
            String string;
            if (inputStream != null) {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, ENCODING));
                while (null != (string = reader.readLine())) {
                    outputBuilder.append(string).append('\n');
                }
            }
        } catch (Exception ex) {
            throw new Exception("[google-api-translate-java] Error reading translation stream.", ex);
        }

        return outputBuilder.toString();
    }

    private URL url(String str) {
        try {
            str = URLEncoder.encode(str, ENCODING);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        final URL url;
        try {
            url = new URL(apiUrl + apiKey + "&target=" + lang + "&q=" + str);
        } catch (MalformedURLException e) {
            return null;
        }
        return url;
    }

    public void useKey(final String apiKey) {
        this.apiKey = apiKey;
    }

    public void start() {
        turnedon = true;
    }

    public void stop() {
        turnedon = false;
    }

    public void turn(final boolean run) {
        turnedon = run;
    }

    public void useLanguage(final String lang) {
        this.lang = lang;
    }

    public boolean isWorking() {
        return turnedon;
    }

    public String getLanguage() {
        return lang;
    }
}
