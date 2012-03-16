package net.chromiumupdater;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
        localversion = check.checkInstall();
        System.out.println(localversion);
        remoteversion = check.checkRemote();
        g.setLocalVersion(localversion);
        g.setRemoteVersion(remoteversion);
        if (localversion < remoteversion) {
            g.showUpdateButton();
        }
        //show if there is already a installed version
        //or if we should download and install one first
    }

    static void download(GUI g) {
        URL dlurl = null;
        File f = new File("C:\\chrome-win32-" + remoteversion + ".zip");
        try {
            dlurl = new URL("http://commondatastorage.googleapis.com/chromium-browser-continuous/Win/" + remoteversion + "/chrome-win32.zip");
        } catch (MalformedURLException ex) {
        }
        //TODO check if already downloading!
        download = new Download(dlurl, f, g);
        try {
            download.download();
        } catch (IOException ex) {
        }
    }

    static void update(GUI g) {
        //start the download in a new thread
        Thread t = new Thread() {

            @Override
            public void run() {
                //download
                download(gui);
                //unzip to programfiles folder
                unzip("C:\\chrome-win32-" + remoteversion + ".zip", System.getenv("PROGRAMFILES") + "\\Chromium\\");
                //pop messagebox

            }
        };
        t.setPriority((Thread.MIN_PRIORITY + Thread.NORM_PRIORITY) / 2);
        t.start();
    }

    /**
     *
     * @param filename the file to unzip
     * @param destination the desinationfolder, should look like c:\\path\\to\\folder\\
     */
    static void unzip(String filename, String destination) {
        File dest = new File(destination);
        if(!dest.exists()) {
            dest.mkdirs();
        }
        byte[] buffer = new byte[1024];
        ZipInputStream in = null;
        FileOutputStream out = null;
        ZipEntry entry = null;
        try {
            in = new ZipInputStream(new FileInputStream(filename));
            entry = in.getNextEntry();
        } catch (IOException ex) {
        }
        while (entry != null) {
            try {
                String entryName = entry.getName();
                System.out.println("entryname " + entryName);
                int n;
                out = null;
                File f = new File(entryName);
                String directory = f.getParent();
                if (directory == null) {
                    if (f.isDirectory()) {
                        break;
                    }
                }
                out = new FileOutputStream(destination + entryName);
                while ((n = in.read(buffer, 0, 1024)) > -1) {
                    out.write(buffer, 0, n);
                }
            } catch (IOException ex) {
            } finally {
                try {
                    if(out != null)
                        out.close();
                } catch (IOException ex) {
                }
            }
            try {
                in.close();
            } catch (IOException ex) {
            }
            try {
                entry = in.getNextEntry();
            } catch (IOException ex) {
                System.out.println("Can't get next file entry ...");
                break;
            }
        }
    }
}