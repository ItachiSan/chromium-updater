package net.chromiumupdater;

import java.io.*;
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

    public VersionCheck(byte platform) {
        this.platform = platform;
    }
    
    /**
     * 
     * @return returns the build number, if a installation is found or instead 0
     */
    public int checkInstall() {
        File f = new File(ChromiumUpdater.installDir);
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            try {
                return Integer.parseInt(br.readLine());
            } catch (IOException ex) {
                return 0;
            }
        } catch (FileNotFoundException ex) {
            return 0;
        }
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