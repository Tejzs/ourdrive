package meta;

import java.io.Serializable;

import org.json.JSONObject;

public class FileOperationsMeta implements Serializable {

    public static enum TaskName {
        CHUNK_TO_FOLDER, UNCHUNK_FROM_FOLDER, CHUNK_TO_ZIP, UNCHUNK_FROM_ZIP, ZIP, UNZIP;
    }

    public static enum ProcessState {
        NOT_STARTED, PROCESSING, COMPLETED, FAILED;
    }

    private String fileName;
    private String initiator;
    private long chunkSize;
    private int totalChunks;
    private String taskId;
    private String folderPath;
    private int percentFinished = 0;
    private TaskName task;
    private ProcessState processState;

    public FileOperationsMeta(String filename, long chunkSize, int totalChunks, String taskId, String folderPath, TaskName task,
            String initiator) {
        this.fileName = filename;
        this.chunkSize = chunkSize;
        this.totalChunks = totalChunks;
        this.taskId = taskId;
        this.folderPath = folderPath;
        this.task = task;
        this.processState = ProcessState.NOT_STARTED;
        this.initiator = initiator;
    }

    public String getFileName() {
        return fileName;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public String getTaskId() {
        return taskId;
    }

    public int getPercentFinished() {
        return percentFinished;
    }

    public void setPercentFinished(int percentFinished) {
        this.percentFinished = percentFinished;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public ProcessState getProcessState() {
        return processState;
    }

    public TaskName getTaskName() {
        return task;
    }

    public void setProcessState(ProcessState processState) {
        this.processState = processState;
    }  

    public String getInitiator() {
        return initiator;
    }


    public JSONObject toJson() {
        JSONObject res = new JSONObject();
        res.put("completed", getPercentFinished());
        res.put("taskId", getTaskId());
        res.put("fileName", getFileName());
        res.put("taskName", getTaskName());
        res.put("folderPath", getFolderPath());
        res.put("state", getProcessState().name());

        return res;
    }
}