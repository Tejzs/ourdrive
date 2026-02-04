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
            if (f != null && f.exists() && f.isFile()) {
                totalBytes += f.length();
            }
        }

        long processedBytes = 0;
        try (FileOutputStream fos = new FileOutputStream(zipOutput);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            zos.setLevel(meta.getTotalChunks());
            byte[] buffer = new byte[8192];
            for (File file : files) {
                if (file == null || !file.exists() || !file.isFile()) {
                    continue;
                }
                try (FileInputStream fis = new FileInputStream(file)) {
                    zos.putNextEntry(new ZipEntry(file.getName()));
                    int len;
                    while ((len = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, len);

                        processedBytes += len;
                        meta.setPercentFinished((int) ((processedBytes * 100) / totalBytes));
                    }
                    zos.closeEntry();
                }
            }
        }
        meta.setPercentFinished(100);
    }
}

