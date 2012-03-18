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
    String remoteurl;
    Settings settings;

    public VersionCheck(Settings settings) {
        this.settings = settings;   
        remoteurl = ChromiumUpdater.baseDLUrl + ((settings.OS == settings.WIN32)?"Win":"Mac") + "/LAST_CHANGE";
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
                int build = Integer.parseInt(br.readLine());
                settings.localBuild = build;
                return build;
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
                    int build = Integer.parseInt(inputLine);
                    settings.remoteBuild = build;
                    settings.lastRemoteCheck = System.currentTimeMillis();
                    settings.save();
                    return build;
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