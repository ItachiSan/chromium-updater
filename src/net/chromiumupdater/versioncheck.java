/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.chromiumupdater;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author morth
 */
public class VersionCheck {

    String remoteurl = "http://commondatastorage.googleapis.com/chromium-browser-continuous/Win/LAST_CHANGE";
    byte platform;
    public static byte WINDOWS = 0;
    public static byte MAC = 1;
    private String localversion;
    private String remoteversion;

    public VersionCheck(byte platform) {
        this.platform = platform;
    }

    public String checkLocal() {
        String localappdata = System.getenv("LOCALAPPDATA");
        File actual = new File(localappdata+"/Chromium/Application");
        if(actual.canRead()) {
            for (File f : actual.listFiles()) {
                System.out.println(f.getName());
            }
        } else {//it isn't here. check somewhere else.
            
        }
        return localversion;
    }

    public int checkRemote() {
        URL url = null;
        try {
            url = new URL(remoteurl);
        } catch (MalformedURLException ex) {
            Logger.getLogger(VersionCheck.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                try {
                    return Integer.parseInt(inputLine);
                } catch (NumberFormatException ex) {
                    Logger.getLogger(VersionCheck.class.getName()).log(Level.SEVERE,
                    "Error: Checking version failed. Either Google is having problems or this program is outdated.",ex);
                }
            }
            in.close();
        } catch (IOException ex) {
            if(ex instanceof UnknownHostException) {
                Logger.getLogger(VersionCheck.class.getName()).log(Level.SEVERE,"Error: Could not resolve hostname. Maybe Google is down. Are you connected to the Internet?",ex);
            }
            Logger.getLogger(VersionCheck.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
        }
        return 0;
    }
}
