package net.chromiumupdater;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JOptionPane;

/**
 *
 * @author morth, cfstras
 */
public class ChromiumUpdater {

    protected static GUI gui;
    static Download download;
    public static String tempDir = "C:\\temp\\chrome\\"; //TODO replace this with system-builtin temp dir
    public static String installDir = System.getenv("PROGRAMFILES") + "\\Chromium\\"; //TODO replace this with something generic (from settings)
    static Settings settings;
    
    public static void main(String[] args) {
        settings = Settings.load();
        gui = new GUI();
        gui.runGUI();
        
        check(gui);
        
        //TODO add shutdown-hook to save settings
    }

    public static void check(GUI g) {
        VersionCheck check = new VersionCheck(VersionCheck.WINDOWS, settings);
        check.checkRemote();
        g.setLastUpdateTime(settings.lastRemoteCheck);
        g.setLocalVersion(settings.localBuild);
        g.setRemoteVersion(settings.remoteBuild);
        if (settings.localBuild < settings.remoteBuild) {
            g.showUpdateButton();
        } else {
            g.setLabel("already up-to-date!");
        }
    }

    public static void save() {
        settings.save();
    }

    static File download(GUI g, int build) {
        try {
            File f = File.createTempFile("chrome-win32-" + build , ".zip");
            f.deleteOnExit();
            if(!f.exists()) {
                //ohmygodwearegoingtodie
                return null;
            }
            URL dlurl = new URL("http://commondatastorage.googleapis.com/chromium-browser-continuous/Win/" + build + "/chrome-win32.zip");
            
            //TODO check if already downloading!
            download = new Download(dlurl, f, g);
            download.download();
            return f;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param filename the file to unzip
     * @param destination the desinationfolder, should look like c:\\path\\to\\folder\\
     */
    static void unzip(File from, String destination) {
        Unzip zip = new Unzip(from, new File(destination));
        try {
            zip.extract();
        } catch (Exception ex) {
            System.out.println("Error unzipping!");
        }
    }
    
    static boolean delete(File dir) {
        if(dir.isDirectory()) {
            String[] children = dir.list();
            for(int i = 0;i<children.length;i++) {
                boolean success=false;
                if(children[i].endsWith("updater-settings")) {
                    success=true;
                } else {
                    success = delete(new File(dir,children[i]));
                }
                if(!success)
                    return false;
            }
        }
            
        return dir.delete();
    }

    static void update(final GUI g) {
        //start the download in a new thread
        Thread t = new Thread() {
            @Override
            public void run() {
                Settings s = ChromiumUpdater.settings;
                int buildToDownload = s.remoteBuild;
                //check if we updated
                if (s.lastRemoteCheck + s.minCheckTime < System.currentTimeMillis()) {
                    int oldChecked = s.remoteBuild;
                    check(ChromiumUpdater.gui);
                    if (oldChecked != s.remoteBuild) {
                        int response = JOptionPane.showConfirmDialog(g, "The build number changed since we last checked. Before, it was "
                                + oldChecked + ", now it is " + s.remoteBuild + "." + System.getProperty("line.separator")
                                + "If you want to download the new one (" + s.remoteBuild + "), hit \"Yes\", "
                                + "if you still want the one we were looking for before, hit \"No\". If you want to bail out, click \"Cancel\".",
                                "Remote build changed", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                        switch (response) {
                            case JOptionPane.YES_OPTION:
                                buildToDownload = s.remoteBuild;
                                break;
                            case JOptionPane.NO_OPTION:
                                buildToDownload = oldChecked;
                                break;
                            case JOptionPane.CANCEL_OPTION:
                                //bail out!
                                return;
                            default:
                                //okay...
                                System.out.println("weird return value from messagebox, stopping : " + response);
                                return;
                        }
                    }
                }

                //download the latest build
                System.out.println("Downloading ...");
                File f = download(gui, buildToDownload);
                //TODO: delete old build and ask if user wants a backup or cancel
                System.out.println("Download done. Deleting install directory ...");
                //if(settings.localBuild != 0) 
                if(!delete(new File(installDir)))
                    System.out.println("Error while deleting old installation!");
                else 
                    System.out.println("Done. Unzipping new build into install directory ...");
                //unzip to program files folder 
                unzip(f, installDir);      
                System.out.println("deleting tempfiles...");
                if(!delete(f))
                    System.out.println("Error while deleting temp file!");
                else
                    System.out.println("tempfiles deleted.");
                //save new build number and a timestamp
                
                s.localBuild=buildToDownload;
                s.lastUpdate=System.currentTimeMillis();
                save();
                g.setLocalVersion(buildToDownload);
                g.setLastUpdateTime(s.lastUpdate);
                g.setLabel("Done!");
            }
        };
        t.setPriority((Thread.MIN_PRIORITY + Thread.NORM_PRIORITY) / 2);
        t.start();
    }
}