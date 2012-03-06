package net.chromiumupdater;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author morth, cfstras
 */
public class ChromiumUpdater {
    public static int localversion;
    public static int remoteversion;
    
    public static void main(String[] args) {
        GUI g = new GUI();
        g.rungui();
    }
    public static void check(GUI g) {
        VersionCheck check = new VersionCheck(VersionCheck.WINDOWS);
        //Integer localversion = Integer.parseInt(check.checkLocal());
        check.checkLocal();
        localversion = 123;
        remoteversion = Integer.parseInt(check.checkRemote());
        g.setLocalVersion(localversion);
        g.setRemoteVersion(remoteversion);
        if(localversion < remoteversion) {
           g.showUpdateButton();
        }
    }

    static void download(GUI g) {
        URL dlurl = null;
        File f = new File("C:\\mini_installer.exe");
        try {
            dlurl = new URL("http://commondatastorage.googleapis.com/chromium-browser-continuous/Win/125133/mini_installer.exe");
        } catch (MalformedURLException ex) {
            Logger.getLogger(ChromiumUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }
        Download d = new Download(dlurl,f,g);
        try {
            d.download();
        } catch (IOException ex) {
            Logger.getLogger(ChromiumUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
