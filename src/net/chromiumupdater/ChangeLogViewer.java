package net.chromiumupdater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author morth
 */
public class ChangeLogViewer {

    public static int build;
    public static byte platform;
    private String changelogXML;
    public String changeLog;

    public ChangeLogViewer(int build, byte platform) {
	this.build = build;
	this.platform = platform; //load from settings instead?
	this.changelogXML = "";
	this.changeLog = "";
    }

    /**
     * @return will fetch the xml-change-log file according to platform and build number and save it into a String
     */
    public static boolean  fetchXMl() {
	//TODO: write the code below
	//make it whithout a File - read the xml directly into a string
	HttpURLConnection http = null;
	BufferedInputStream in = null;
	OutputStream out = null;
	URL url = null;
	try {
	    url = new URL("http://commondatastorage.googleapis.com/chromium-browser-continuous/"+(platform==0?"Win":"Mac")+"/"+build+"/changelog.xml");
	} catch (MalformedURLException ex) {
	    System.out.println("Error whilst fetching changelog");
	}
	File changeLogFile = null;
	try {
	    http = (HttpURLConnection) url.openConnection();
	    int size = Integer.parseInt(http.getHeaderField("Content-Length"));

	    in = new BufferedInputStream(http.getInputStream());
	    out = new BufferedOutputStream(new FileOutputStream(changeLogFile));
	    byte[] buffer = new byte[1024];
	    int n = 0;
	    while ((n = in.read(buffer)) >= 0) {
		out.write(buffer, 0, n);
	    }
	    out.flush();
	} catch (IOException ex) {
	    ex.printStackTrace();
	    return false;
	} finally {
	    if (in != null) {
		try {
		    in.close();
		} catch (IOException ex) {
		}
	    }
	    if (out != null) {
		try {
		    out.close();
		} catch (IOException ex) {
		}
	    }
	    if (http != null) {
		http.disconnect();
	    }
	}
	return true;
    }

    /**
     * @return will remove all XML tags and format the changelog a little
     */
    public static void formatXML() {
	//remove xml tags
	//maybe make a ASCII formatting or tables stuff here
	//put some important changelog parts to uppercase
    }

    /**
     * @return returns the changelog
     * @return
     */
    public static String returnChangeLog() {
	return "";
    }
}
