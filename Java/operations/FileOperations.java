package operations;

import utility.Utils;
import storage.DataStorage;
import meta.FileOperationsMeta;
import meta.FileOperationsMeta.TaskName;

public class FileOperations {
    public static String startUnChunkingFolder(String folderPath, String newFilePath) {
        String uploadId = generateUploadId(newFilePath);
        DataStorage.getFileOperationsMetaStore().offer(uploadId, new FileOperationsMeta(newFilePath, 0, 0, uploadId, folderPath, TaskName.UNCHUNK_FROM_FOLDER));
        Utils.getLogger("operations").log(uploadId);
        Utils.getLogger("FileOperationsProcessor").log(uploadId, folderPath, newFilePath);
        return uploadId;
    }

    private static String generateUploadId(String fileName) {
        long time = System.currentTimeMillis();
        return utility.Utils.hashHex(time + fileName);
    }
}
