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

    static public String apikey = "API-KEY";
    static public String apiurl = "https://www.googleapis.com/language/translate/v2?key=";
    static public String lang = "en";
    static public boolean turnedon = false;
    protected static final String ENCODING = "UTF-8";

    public static String translate(String str) {
        if (!turnedon)
            return str;
        String res = "";
        URL url = url(str);
        if (url == null)
            return str;
        try {
            final HttpURLConnection uc = (HttpURLConnection) url
                    .openConnection();
            uc.setRequestMethod("GET");
            uc.setDoOutput(true);
            try {
                String result;
                try {
                    result = inputStreamToString(uc.getInputStream());
                } catch (Exception e) {
                    return str;
                }
                JSONObject o = (JSONObject) JSONValue.parseWithException(result);
                JSONObject data = (JSONObject) o.get("data");
                JSONArray translations = (JSONArray) data.get("translations");
                JSONObject tr1 = (JSONObject) translations.get(0);
                str = (String) tr1.get("translatedText");
                String sourceLanguage = (String) tr1.get("detectedSourceLanguage");
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
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, ENCODING));
                while (null != (string = reader.readLine())) {
                    outputBuilder.append(string).append('\n');
                }
            }
        } catch (Exception ex) {
            throw new Exception("[google-api-translate-java] Error reading translation stream.", ex);
        }

        return outputBuilder.toString();
    }

    private static URL url(String str) {
        try {
            str = URLEncoder.encode(str, ENCODING);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        URL url;
        try {
            url = new URL(apiurl + apikey + "&target=" + lang + "&q=" + str);
        } catch (MalformedURLException e) {
            return null;
        }
        return url;
    }
}
