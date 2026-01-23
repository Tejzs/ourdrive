package meta;

import java.io.Serializable;
import java.util.Objects;

import org.json.JSONObject;

import utility.Utils;

public class FileUploadMeta implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fileHash;
    private String filename;
    private long chunkSize;
    private long fileSize;
    private int totalChunks;
    private String owner;
    private String dest;

    private long uploadStartTime;
    private int chunksConsumed;
    private String uploadId;

    public FileUploadMeta(String fileHash, String filename, long chunkSize, long fileSize, int totalChunks, String owner, String dest) {
        this.fileHash = fileHash;
        this.filename = filename;
        this.chunkSize = chunkSize;
        this.fileSize = fileSize;
        this.totalChunks = totalChunks;
        this.owner = owner;
        this.dest = dest;
        
        this.uploadStartTime = System.currentTimeMillis();
        this.chunksConsumed = 0;
        this.uploadId = Utils.hashHex(this.uploadStartTime + this.fileHash);
    }

    public String getUploadId() {
        return this.uploadId;
    }

    public String getFileHash() {
        return fileHash;
    }

    public String getFilename() {
        return filename;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public long getFileSize() {
        return fileSize;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public String getUploader() {
        return owner;
    }

    public String getDestination() {
        return dest;
    }

    public long getUploadStartTime() {
        return uploadStartTime;
    }

    public int getChunksConsumed() {
        return chunksConsumed;
    }

    public void setChunksConsumed(int chunksConsumed) {
        this.chunksConsumed = chunksConsumed;
    }

    public JSONObject toJson() {
        JSONObject metaData = new JSONObject();

        metaData.put("chunksize", getChunkSize());
        metaData.put("filesize", getFileSize());
        metaData.put("filename", getFilename());
        metaData.put("dest", getDestination());
        metaData.put("totalchunks", getTotalChunks());
        metaData.put("chunksconsumed", getChunksConsumed());
        metaData.put("uploadId", getUploadId());

        return metaData;
    }
}
