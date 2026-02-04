package operations;

import utility.SessionData;
import utility.Utils;
import storage.DataStorage;
import meta.FileOperationsMeta;
import meta.FileOperationsMeta.TaskName;

public class FileOperations {
    public static String startUnChunkingFolder(String folderPath, String newFilePath) {
        String mail = SessionData.getThreadLocalSessionData().getMail();
        String taskId = generateUploadId(newFilePath);
        DataStorage.getFileOperationsMetaStore().offer(taskId, new FileOperationsMeta(newFilePath, 0L, 0, taskId, folderPath, TaskName.UNCHUNK_FROM_FOLDER, mail));
        Utils.getLogger("operations").log(taskId);
        Utils.getLogger("FileOperationsProcessor").log(taskId, folderPath, newFilePath);
        return taskId;
    }

    private static String generateUploadId(String fileName) {
        long time = System.currentTimeMillis();
        return utility.Utils.hashHex(time + fileName);
    }
}