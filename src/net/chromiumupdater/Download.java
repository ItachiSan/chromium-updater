/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.chromiumupdater;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author morth, cfstras
 */
public class Download {
    private final URL url;
    private final File saveFile;
    private final GUI g;
    public Download(URL url,File saveFile, GUI g) {
        this.url = url;
        this.saveFile = saveFile;
        this.g = g;
    }
    
    public void download() throws IOException {
        g.startProgBar();
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        int size = Integer.parseInt(http.getHeaderField("Content-Length"));
        http.connect(); 
        BufferedInputStream in = new BufferedInputStream(http.getInputStream());
        OutputStream out = new BufferedOutputStream( new FileOutputStream(saveFile));
        byte[] buffer = new byte[512];
        int n = 0;
        int done = 0;
        g.setProgressMinMax(0, size);
        while((n=in.read(buffer))>=0) {
            out.write(buffer, 0, n);
            done += n;
            //System.out.println("got "+done/1000+" KB of "+size/1000+" KB");
            //TODO: show download speed
            g.setProgressBarText(done/1000+" / "+size/1000+" KB");
            g.setProgress(done);
        }
        out.flush();
        out.close();
        
        http.disconnect();
    }    
}
