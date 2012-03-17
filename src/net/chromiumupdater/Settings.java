package net.chromiumupdater;

import java.io.*;

/**
 *
 * @author morth
 */
public class Settings {

    public int build;
    public long lastChecked;
    public int version;

    public Settings(int remoteVersion) {
        this.version = remoteVersion;
    }

    public void save() {
        File f = new File(ChromiumUpdater.installDir+"build");
        long time = System.currentTimeMillis();
        try {
            FileWriter fr = new FileWriter(f);
            fr.write(Integer.toString(version));
            fr.write(System.getProperty("line.separator"));
            fr.write(Long.toString(time));
            fr.close();
        } catch (IOException ex) {
        }
    }

    /**
     *
     * @return reads the build number and the date of the last check from file
     */
    public void load() {
        File f = new File(ChromiumUpdater.installDir+"build");
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            try {
                build = Integer.parseInt(br.readLine());
                lastChecked = Long.parseLong(br.readLine());
                br.close();
                fr.close();
            } catch (IOException ex) {
            }
        } catch (FileNotFoundException ex) {
            build = 0;
            lastChecked = 0;
        }
    }
}