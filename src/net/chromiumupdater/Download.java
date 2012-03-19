package net.chromiumupdater;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author morth, cfstras
 */
public class Download {
    private final GUI g;
    private boolean downloading = false;

    public Download(GUI g) {
        this.g = g;
    }

    public boolean download(URL url, File saveFile) {
        if(downloading) {
            System.out.println("Error: already downloading");
            return false;
        } else {
            downloading = true;
        }
        BufferedInputStream in = null;
        HttpURLConnection http = null;
        OutputStream out = null;
        try {
            g.startProgBar();
            http = (HttpURLConnection) url.openConnection();
            int size = Integer.parseInt(http.getHeaderField("Content-Length"));

            in = new BufferedInputStream(http.getInputStream());
            out = new BufferedOutputStream( new FileOutputStream(saveFile));
            byte[] buffer = new byte[1024];
            int n = 0;
            int done = 0;
            g.setProgressMinMax(0, size);
            while((n=in.read(buffer))>=0) {
                out.write(buffer, 0, n);
                done += n;
                //TODO: show download speed
                g.setProgressBarText(done/1024+" / "+size/1024+" KB");
                g.setProgress(done);
            }
            out.flush();
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException ex) {}
            }
            if(out != null) {
                try {
                    out.close();
                } catch (IOException ex) {}
            }
            if(http != null) {
                http.disconnect();
            }
        }
        return true;
    }
}