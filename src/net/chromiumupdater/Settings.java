package net.chromiumupdater;

import java.io.*;

/**
 *
 * @author morth
 */
public class Settings implements Serializable {
    
    /**
     * the current local build number
     */
    public int localBuild;
    
    /**
     * the last-checked remote build number
     */
    public int remoteBuild;
    
    /**
     * the last time we checked for a new remote build,
     * in milliseconds since 1.1.1970 00:00:00 UTC (unixtime*1000)
     */
    public long lastRemoteCheck;
    
    /**
     * the last time we updated our local install to the newest version.
     */
    public long lastUpdate;
    
    public int remoteVersion;
    public int localVersion;
    
    public byte OS = -1;
    
    public byte WIN32 = 0;
    public byte MACOSX = 1;
    
    public long minCheckTime = 60*60*1000; // download max. 60 minutes old versions

    public Settings() {
    }

    public void save() {
        File f = new File(ChromiumUpdater.installDir+"updater-settings");
        
        ObjectOutputStream oos = null;
        try { //TODO serialize this class
            if(!f.exists()){
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            oos = new ObjectOutputStream(new FileOutputStream(f,false));
            oos.writeObject(this);
            oos.flush();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if(oos != null) {
                try {
                    oos.close();
                } catch (IOException ex) {}
            }
        }
    }

    /**
     *
     * @return reads the build number and the date of the last check from file
     */
    public static Settings load() {
        File f = new File(ChromiumUpdater.installDir+"updater-settings");
        
        ObjectInputStream ois = null;
        Object o=null;
        try { //TODO serialize this class
            ois = new ObjectInputStream(new FileInputStream(f));
            o = ois.readObject();
        } catch (FileNotFoundException ex) {
            System.out.println("Could not find settings file. it probably doesn't exist.");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if(ois != null) {
                try {
                    ois.close();
                } catch (IOException ex) {}
            }
        }
        if(o instanceof Settings) {
            System.out.println("settings file loaded.");
            return (Settings)o;
        } else {
            System.out.println("Settings file in wrong format!");
        }
        
        return new Settings();
    }
}