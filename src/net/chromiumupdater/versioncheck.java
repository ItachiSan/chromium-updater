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
        for (File f : actual.listFiles()) {
            System.out.println(f.getName());
        }
        return localversion;
    }

    public String checkRemote() {
        URL url = null;
        try {
            url = new URL(remoteurl);
        } catch (MalformedURLException ex) {
            Logger.getLogger(VersionCheck.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                return inputLine;
            }
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(VersionCheck.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "null";
    }
}
