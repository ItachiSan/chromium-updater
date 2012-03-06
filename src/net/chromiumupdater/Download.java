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
 * @author morth
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
        //get the file size
        //start loading the file
        //display the download speed and estimated time
        //copy file to a dir and let the updater take over again...
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        int size = Integer.parseInt(http.getHeaderField("Content-Length"));
        http.connect(); 
        BufferedInputStream in = new BufferedInputStream(http.getInputStream());
        OutputStream out = new BufferedOutputStream( new FileOutputStream(saveFile));
        byte[] buffer = new byte[256];
        int n = 0;
        int i = 1;
        int progress=1;
        int donesize=size;
        int done = 0;
        while((n=in.read(buffer))>=0) {
            out.write(buffer, 0, n);
            donesize = donesize-256;
            done = size - donesize;
            System.out.println(done);
            System.out.println(size);
            //g.setProgress(done/size);
            i++;
        }
        out.flush();
        out.close();
    }    
}
