package utility.file;

import meta.FileOperationsMeta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {

    private final FileOperationsMeta meta;

    public Zipper(FileOperationsMeta meta) {
        this.meta = meta;
    }

    private File[] getFiles() {
        String[] fileNames = meta.getFolderPath().split("\"");
        File[] files = new File[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            files[i] = new File(fileNames[i]);
        }
        return files;
    }

    private File getZipOutput() {
        return new File(meta.getFileName());
    }

    public void zipFiles() throws IOException {

        File[] files = getFiles();
        File zipOutput = getZipOutput();

        long totalBytes = 0;
        for (File f : files) {
            totalBytes += calculateSize(f);
        }

        long processedBytes = 0;

        try (FileOutputStream fos = new FileOutputStream(zipOutput); ZipOutputStream zos = new ZipOutputStream(fos)) {

            zos.setLevel(meta.getTotalChunks());

            byte[] buffer = new byte[8192];

            for (File file : files) {
                if (!file.exists()) continue;

                processedBytes = zipRecursive(file, file.getParentFile(), zos, buffer, processedBytes, totalBytes);
            }
        }

        meta.setPercentFinished(100);
    }

    private long zipRecursive(File file, File baseDir, ZipOutputStream zos, byte[] buffer, long processedBytes, long totalBytes) throws IOException {

        String entryName = baseDir == null ? file.getName() : baseDir.toURI().relativize(file.toURI()).getPath();

        if (file.isDirectory()) {

            if (!entryName.endsWith("/")) {
                entryName += "/";
            }

            zos.putNextEntry(new ZipEntry(entryName));
            zos.closeEntry();

            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    processedBytes = zipRecursive(child, baseDir, zos, buffer, processedBytes, totalBytes);
                }
            }

        } else {

            zos.putNextEntry(new ZipEntry(entryName));

            try (FileInputStream fis = new FileInputStream(file)) {
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    zos.write(buffer, 0, len);

                    processedBytes += len;
                    meta.setPercentFinished((int) ((processedBytes * 100) / totalBytes));
                }
            }

            zos.closeEntry();
        }

        return processedBytes;
    }

    private long calculateSize(File file) {
        if (file == null || !file.exists()) return 0;

        if (file.isFile()) return file.length();

        long size = 0;
        File[] children = file.listFiles();
        if (children != null) {
            for (File f : children) {
                size += calculateSize(f);
            }
        }
        return size;
    }
}