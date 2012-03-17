package net.chromiumupdater;

import java.io.*;
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

                String entryFileName = entry.getName();

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
}
