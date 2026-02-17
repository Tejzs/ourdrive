package operations;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import meta.FileOperationsMeta;
import meta.FileOperationsMeta.TaskName;
import meta.FileOperationsMeta.ProcessState;
import storage.DataStorage;
import utility.Utils;
import utility.file.FileChunker;
import utility.file.Zipper;

public class FileOperationsProcessor implements Runnable {
    private AtomicInteger threadUsed = new AtomicInteger();

    public void run() {
        while (true) {
            try {
                Thread.sleep(3000);
                if (threadUsed.get() > 3) {
                    return;
                }

                for (FileOperationsMeta meta : getUnstartedMeta()) {
                    new Thread(new ProcessMetaData(meta)).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<FileOperationsMeta> getUnstartedMeta() {
        List<FileOperationsMeta> unstartedMetas = new ArrayList<>();

        for (Serializable meta : DataStorage.getFileOperationsMetaStore().values()) {
            FileOperationsMeta operationMeta = (FileOperationsMeta) meta;
            if (meta == null) {
                continue;
            }

            if (operationMeta.getProcessState().equals(ProcessState.NOT_STARTED)) {
                unstartedMetas.add(operationMeta);

                if (unstartedMetas.size() > 3) {
                    return unstartedMetas;
                }
            }
        }

        return unstartedMetas;
    }

    private static class ProcessMetaData implements Runnable {
        private FileOperationsMeta meta;

        private ProcessMetaData(FileOperationsMeta meta) {
            this.meta = meta;
        }

        @Override
        public void run() {
            try {
                TaskName task = meta.getTaskName();

                String newFileNameOrPath = meta.getFileName();
                String filePath = meta.getFolderPath();

                meta.setProcessState(ProcessState.PROCESSING);
                switch (task) {
                    case UNCHUNK_FROM_FOLDER:
                        new FileChunker(filePath, newFileNameOrPath, meta).assembleChunksFromFolder();
                        break;
                    case ZIP:
                        new Zipper(meta).zipFiles();
                        break;
                    default:
                        break;
                }
                meta.setProcessState(ProcessState.COMPLETED);
                Thread.sleep(3000);
                DataStorage.getFileOperationsMetaStore().delete(meta.getTaskId());
            } catch (Exception e) {
                e.printStackTrace();
                meta.setProcessState(ProcessState.FAILED);
            }
        }
    }
}