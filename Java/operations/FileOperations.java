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

    public static String startZippingFiles(String file, String files, int compression) {
        String mail = SessionData.getThreadLocalSessionData().getMail();
        String taskId = generateUploadId(file);
        DataStorage.getFileOperationsMetaStore().offer(taskId, new FileOperationsMeta(file, 0L, compression, taskId, files, TaskName.ZIP, mail));
        return taskId;
    }

    private static String generateUploadId(String fileName) {
        long time = System.currentTimeMillis();
        return utility.Utils.hashHex(time + fileName);
    }
}