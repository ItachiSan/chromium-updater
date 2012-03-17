package net.chromiumupdater;

import java.io.*;
import java.util.Date;

/**
 *
 * @author morth
 */
public class Settings {

    public int build;
    public int lastChecked;
    public int version;

    public Settings(int remoteVersion) {
        this.version = remoteVersion;
    }

    public void save() {
        String programFiles = System.getenv("PROGRAMFILES");
        File f = new File(programFiles + "\\Chromium\\build");
        int time = (int) (System.currentTimeMillis() / 1000);
        try {
            FileWriter fr = new FileWriter(f);
            fr.write(version);
            fr.write(time);
        } catch (IOException ex) {
        }
    }

    /**
     *
     * @return reads the build number and the date of the last check from file
     */
    public void load() {
        String programFiles = System.getenv("PROGRAMFILES");
        File f = new File(programFiles + "\\Chromium\\build");
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            try {
                build = Integer.parseInt(br.readLine());
                lastChecked = Integer.parseInt(br.readLine());
            } catch (IOException ex) {
            }
        } catch (FileNotFoundException ex) {
            build = 0;
            lastChecked = 0;
        }
    }
}