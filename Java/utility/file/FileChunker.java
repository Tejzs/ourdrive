package utility.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import meta.FileOperationsMeta;
import operations.FileOperations;
import storage.DataStorage;
import utility.*;


public class FileChunker {
    private File file;
    private File res;
    private FileOperationsMeta meta;

    public FileChunker(File file, File res, FileOperationsMeta meta) {
        this.meta = meta;
        this.file = file;
        this.res = res;

        if (!file.exists()) {
            throw new RuntimeException("File " + file.getName() + " does not exist");
        }
    }
    
    public void chunkFileToSize(DataSize chunkSize) {

        if (!res.exists()) {
            try {
                res.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(res)));
        ) {
            String filename = file.getName();
            ZipEntry entry = new ZipEntry(filename + "/");
            out.putNextEntry(entry);
            out.closeEntry();

            long chunkLength = chunkSize.getSize();
            int chunkNum = 0;           
            long bytesRead = 0L;
            while (true) { 
                if (bytesRead == 0) {
                    bytesRead = 0L;
                    ZipEntry chunkEntry = new ZipEntry(filename + "/chunk_" + ++chunkNum + ".dat");

                    out.putNextEntry(chunkEntry);
                }

                int byt = in.read();
                if (byt == -1) {
                    break;
                }
                out.write(byt);

                if (++bytesRead == chunkLength) {
                    out.closeEntry();
                    bytesRead = 0;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void chunkFileInNumber(int num) throws IOException {
        chunkFileToSize(new DataSize(file.length() / num, DataSizeUnit.B));
    }

    public void assembleChunks() {
        try (
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(res));
            ZipInputStream in = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
        ) {
            in.getNextEntry();
            while (in.getNextEntry() != null) {
                writeToStream(in, out, 0, 0L, 0);
                in.closeEntry();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void chunkFileToFolderSize(DataSize chunkSize) {
        if (!res.exists()) {
            if (!res.mkdirs()) {
                throw new RuntimeException("Failed to create directory " + res.getAbsolutePath());
            }
        } else if (!res.isDirectory()) {
            throw new RuntimeException(res.getName() + " is not a directory");
        }

        try (
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))
        ) {
            long chunkLength = chunkSize.getSize();
            int chunkNum = 0;
            long bytesWritten = 0;

            BufferedOutputStream out = null;

            int byt;
            while ((byt = in.read()) != -1) {
                if (bytesWritten == 0) {
                    File chunk = new File(res, "chunk_" + (++chunkNum) + ".dat");
                    out = new BufferedOutputStream(new FileOutputStream(chunk));
                }

                out.write(byt);
                bytesWritten++;

                if (bytesWritten == chunkLength) {
                    out.close();
                    bytesWritten = 0;
                }
            }

            if (out != null) {
                out.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void chunkFileToFolderInNumber(int num) {
        chunkFileToFolderSize(new DataSize(file.length() / num, DataSizeUnit.B));
    }

    public void assembleChunksFromFolder() {
        if (!file.exists() || !file.isDirectory()) {
            throw new RuntimeException("Source " + file.getAbsolutePath() + " is not a directory");
        }

        File[] chunks = file.listFiles((dir, name) -> name.startsWith("chunk_") && name.endsWith(".dat"));

        if (chunks == null || chunks.length == 0) {
            throw new RuntimeException("No chunk files found in " + file.getAbsolutePath());
        }

        Arrays.sort(chunks, Comparator.comparingInt(f -> {
            String num = f.getName()
                    .replace("chunk_", "")
                    .replace(".dat", "");
            return Integer.parseInt(num);
        }));

        try (
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(res))
        ) {
            int chunkNum = 0;
            for (File chunk : chunks) {
                try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(chunk))) {
                    writeToStream(in, out, chunkNum++, chunk.length() + 1, chunks.length);
                }
            }
            meta.setPercentFinished(100);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeToStream(InputStream in, OutputStream out, int chunkNum, long chunkSize, int totalChunks) throws IOException {
		int byt;
        long completed = 0L;

		while ((byt = in.read()) != -1) {
            completed++;
			out.write(byt);
            if (completed % 50_00_000 == 0) {
                meta.setPercentFinished((int) ((((chunkNum * chunkSize) + completed) * 100) / (totalChunks * chunkSize)));
            }
		}

        meta.setPercentFinished((int) (((((chunkNum + 1)  * chunkSize)) * 100) / (totalChunks * chunkSize)));
	}

    public static class DataSize {
        private final long size;
        public DataSize(long size, DataSizeUnit unit) {
            this.size = size * unit.toByteLength();
        }

        public long getSize() {
            return size;
        }
    }

    public enum DataSizeUnit {
        B(1),
        KB(1024),
        MB(1024 * 1024),
        GB(1024 * 1024 * 1024);

        int value;
        DataSizeUnit(int value) {
            this.value = value;
        }

        public int toByteLength() {
            return value;
        }
    }
}