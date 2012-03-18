package net.chromiumupdater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author morth
 */
public class Unzip {

    public static File archive;
    public static File destDir;
    protected int bufferSizeInBytes = 16384;

    public Unzip(File archive, File destDir) {
        Unzip.archive = archive;
        Unzip.destDir = destDir;
    }

    public void extract() throws Exception {

        if (!destDir.exists()) {
            destDir.mkdir();
        }

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(archive);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            byte[] buffer = new byte[bufferSizeInBytes];
            int len;
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                //removes the name of the archive from the extract path. so we are one folder above ;-)
                String entryFileName = entry.getName().replaceAll("chrome-"+((ChromiumUpdater.settings.OS == ChromiumUpdater.settings.WIN32)?"win32":"mac")+"/", "");
                System.out.println("Unzip: "+entryFileName);
                File dir = buildDirectoryHierarchyFor(entryFileName, destDir);

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                if (!entry.isDirectory()) {
                    File targetFile = new File(destDir, entryFileName);
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFile));
                    BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));

                    while ((len = bis.read(buffer)) > 0) {
                        bos.write(buffer, 0, len);
                    }

                    bos.flush();
                    bos.close();
                    bis.close();
                }
            }
        } finally {
            if (zipFile != null) {
                zipFile.close();
            }
        }

    }

    private File buildDirectoryHierarchyFor(String entryName, File destDir) {
        int lastIndex = entryName.lastIndexOf('/');
        String internalPathToEntry = entryName.substring(0, lastIndex + 1);
        return new File(destDir, internalPathToEntry);
    }
    
    /**
     * takes a name of a file in the res folder, extracts it to a temp file and returns the created file.
     * the file will be set as "delete on exit" and have +x.
     * please delete it when you're finished, just to be sure
     * @param name
     * @return a file object pointing to a temp file or null, if not found
     */
    public static File extractResource(String name) {
        InputStream is = Unzip.class.getResourceAsStream("res/"+name);
        if(is==null) {
            System.out.println("Error: could not find resource "+name+" in package.");
            return null;
        }
        File f=null;
        FileOutputStream fos = null;
        try {
            f = File.createTempFile(name, ".tmp");
            if(f==null) {
                System.out.println("Error creating temp file");
                return null;
            }
            byte[] buf = new byte[1024];
            int n=0;
            fos = new FileOutputStream(f);
            
            while( (n=is.read(buf)) > -1) {
                fos.write(buf, 0, n);
            }
            fos.flush();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally  {
            if(is!=null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if(fos!=null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if(fos!=null) {
            f.setExecutable(true);
            return f;
        }
        return null;
    }
}
