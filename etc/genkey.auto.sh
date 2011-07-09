#!/bin/sh

keytool -v -genkey -alias ExtClient -validity 3650 -keypass autosign \
    -keystore etc/AutoExtClient.ks -storepass autosign \
    -dname "CN=Autosign, OU=N/A, O=N/A, L=Saint Petersburg, ST=N/A, C=RU"
