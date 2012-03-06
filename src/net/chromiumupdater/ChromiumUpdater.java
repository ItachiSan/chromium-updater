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
    protected static GUI gui;
    static Download download;
    
    public static int localversion;
    public static int remoteversion;
    
    public static void main(String[] args) {
        gui = new GUI();
        gui.runGUI();
    }
    public static void check(GUI g) {
        VersionCheck check = new VersionCheck(VersionCheck.WINDOWS);
        //Integer localversion = Integer.parseInt(check.checkLocal());
        check.checkLocal();
        localversion = 123;
        remoteversion = check.checkRemote();
        g.setLocalVersion(localversion);
        g.setRemoteVersion(remoteversion);
        if(localversion < remoteversion) {
           g.showUpdateButton();
        }
    }

    static void download(GUI g) {
        URL dlurl = null;
        int build=125133; //for now.
        File f = new File("C:\\mini_installer-"+build+".exe");
        try {
            dlurl = new URL("http://commondatastorage.googleapis.com/chromium-browser-continuous/Win/"+build+"/mini_installer.exe");
        } catch (MalformedURLException ex) {
            Logger.getLogger(ChromiumUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }
        //TODO check if already downloading!
        download = new Download(dlurl,f,g);
        try {
            download.download();
        } catch (IOException ex) {
            Logger.getLogger(ChromiumUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void update(GUI g) {
        //start the download in a new thread
        Thread t= new Thread() {
            @Override public void run(){
                //download
                download(gui);
                //install
                //TODO install
                //pop messagebox
                
            }
        };
        t.setPriority((Thread.MIN_PRIORITY+Thread.NORM_PRIORITY)/2);
        t.start();
    }
    
}
