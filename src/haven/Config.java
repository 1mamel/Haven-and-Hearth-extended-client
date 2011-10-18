/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven;

import ender.GoogleTranslator;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static haven.Utils.getprop;

public class Config {
    public static byte[] authck;
    public static String authuser;
    public static String authserv;
    public static String defaultServer;
    public static URL resurl, mapurl;
    public static boolean fullscreen;
    public static boolean dbtext;
    public static boolean bounddb;
    public static boolean profile;
    public static boolean nolocalres;
    public static String resdir;
    public static boolean nopreload;
    public static String loadwaited, allused;
    public static Map<Pattern, String> smileys = new ConcurrentHashMap<Pattern, String>();
    public static String currentCharName;
    public static Properties options;
    public static GoogleTranslator translator = new GoogleTranslator();

    public static boolean quick_login = false; // быстрый логин дефолт чаром
    public static boolean ark_state_activate_char = false; // стадия аткивации чара
    public static int ark_button_activate_char = 0; // ид кнопки которую надо нажать
    public static String auto_start_script = ""; // имя скрипта запускаемого после логина
    public static boolean FirstLogin = true; // первый ли запуск клиента
    public static boolean inactive_exit = false; // закрывать клиент при неактивности (нет новых виджетов)
    public static boolean keep_connect = false; // подерживать ли подключение. (реконнекты)

    static {
        try {
            String p;
            if ((p = getprop("haven.authck")) != null)
                authck = Utils.hex2byte(p);
            authuser = getprop("haven.authuser");
            authserv = getprop("haven.authserv");
            defaultServer = getprop("haven.defserv");
            if ((p = getprop("haven.resurl", "https://www.havenandhearth.com/res/")).length() != 0)
                resurl = new URL(p);
            if (!(p = getprop("haven.mapurl", "http://www.havenandhearth.com/mm/")).isEmpty())
                mapurl = new URL(p);
            fullscreen = getprop("haven.fullscreen", "off").equals("on");
            loadwaited = getprop("haven.loadwaited");
            allused = getprop("haven.allused");
            dbtext = getprop("haven.dbtext", "off").equals("on");
            bounddb = getprop("haven.bounddb", "off").equals("on");
            profile = getprop("haven.profile", "off").equals("on");
            nolocalres = getprop("haven.nolocalres", "").equals("yesimsure");
            resdir = getprop("haven.resdir");
            nopreload = getprop("haven.nopreload", "no").equals("yes");
            translator.useLanguage("en");
            translator.stop();
            currentCharName = "";
            options = new Properties();
            loadOptions();
            loadSmileys();
        } catch (MalformedURLException e) {
            throw (new RuntimeException(e));
        }
    }

    public static String mksmiley(String str) {
        for (final Map.Entry<Pattern, String> patternStringEntry : Config.smileys.entrySet()) {
            final String res = patternStringEntry.getValue();
            str = patternStringEntry.getKey().matcher(str).replaceAll(res);
        }
        return str;
    }

    private static void usage(final PrintStream out) {
        out.println("usage: haven.jar [-hdPf] [-u USER] [-C HEXCOOKIE] [-r RESDIR] [-U RESURL] [-A AUTHSERV] [SERVER]");
    }

    public static void cmdline(final String[] args) {
        final PosixArgs opt = PosixArgs.getopt(args, "hqdPU:fr:A:m:u:b:k:C:");
        if (opt == null) {
            usage(System.err);
            System.exit(1);
        }
        for (final char c : opt.parsed()) {
            switch (c) {
                case 'h':
                    usage(System.out);
                    System.exit(0);
                    break;
                case 'd':
                    dbtext = true;
                    break;
                case 'P':
                    profile = true;
                    break;
                case 'f':
                    fullscreen = true;
                    break;
                case 'r':
                    resdir = opt.arg;
                    break;
                case 'A':
                    authserv = opt.arg;
                    break;
                case 'q':
                    quick_login = true;
                    break;
//                case 'm':
//                    mapdir = opt.arg;
//                    break;
                case 'k':
                    keep_connect = true;
                    break;
                case 'b':
                    auto_start_script = opt.arg;
                    break;

                case 'U':
                    try {
                        resurl = new URL(opt.arg);
                    } catch (MalformedURLException e) {
                        System.err.println(e);
                        System.exit(1);
                    }
                    break;
                case 'u':
                    authuser = opt.arg;
                    break;
                case 'C':
                    authck = Utils.hex2byte(opt.arg);
                    break;
            }
        }
        if (opt.rest.length > 0)
            defaultServer = opt.rest[0];
    }

    private static void loadSmileys() {
        smileys.clear();
        try {
            final FileInputStream fstream;
            fstream = new FileInputStream("smileys.conf");
            final DataInputStream in = new DataInputStream(fstream);
            final BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                final String[] tmp = Utils.tabulationPattern.split(strLine);
                final String smile;
                final String res;
                smile = tmp[0];
                res = "\\$img\\[smiley\\/" + tmp[1] + "\\]";
                smileys.put(Pattern.compile(smile, Pattern.CASE_INSENSITIVE | Pattern.LITERAL), res);
            }
            in.close();
        } catch (FileNotFoundException ignored) {
        } catch (IOException ignored) {
        }

    }

    private static void loadOptions() {
        translator.useKey(CustomConfig.getGoogleTranslateApiKey());
    }

}
