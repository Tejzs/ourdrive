package servlets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.http.Part;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.MultipartConfig;

import org.json.JSONObject;

import utility.*;
import utility.file.FileChunker;
import meta.FileUploadMeta;
import operations.FileOperations;
import storage.DataStorage;

@MultipartConfig(
    fileSizeThreshold = 0,
    maxFileSize = -1,
    maxRequestSize = -1
)
public class ChunkUploadServlet extends HttpServlet {
    private static final String CLASS_NAME = "ChunkUploadServlet";
    private String tempUploadDir = "";
    private String storageDir = "";

    @Override
    public void init() {
        tempUploadDir = Utils.getServerHomeInServer(getServletContext()) + "UploadedChunkStorage";
        storageDir = Utils.getServerHomeInServer(getServletContext()) + "Files" + File.separatorChar;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        PrintWriter out = resp.getWriter();

        String mail = SessionData.getThreadLocalSessionData().getMail();
        String fileName = req.getParameter("filename");
        String fileHash = req.getParameter("filehash");
        String destination = req.getParameter("dest");
        long chunksize = Long.parseLong(req.getParameter("chunksize"));
        long filesize = Long.parseLong(req.getParameter("filesize"));
        int totalChunks = Integer.parseInt(req.getParameter("totalchunks"));

        FileUploadMeta fileUploadMeta = new FileUploadMeta(
            fileHash,
            fileName,
            chunksize,
            filesize,
            totalChunks,
            mail,
            destination
        );

        String uploadId = fileUploadMeta.getUploadId();

        DataStorage.getFileUploadMetaStore().offer(uploadId, fileUploadMeta);
        Utils.sendSuccessResp(out, new JSONObject().put("uploadId", uploadId));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        PrintWriter out = resp.getWriter();
        
        String mail = SessionData.getThreadLocalSessionData().getMail();

        String uploadId = req.getParameter("uploadId");
        String fileHash = req.getParameter("filehash");
        int chunkIndex = Integer.parseInt(req.getParameter("chunkIndex"));

        FileUploadMeta fileUploadMeta = null;
        if ((fileUploadMeta = (FileUploadMeta) DataStorage.getFileUploadMetaStore().get(uploadId)) == null) {
            Utils.sendFailureResp(out, new JSONObject(), "Upload id does not exist");
            return;
        }

        if (!fileUploadMeta.getFileHash().equals(fileHash)) {
            Utils.sendFailureResp(out, new JSONObject(), "The file you are trying to upload is different from when the meta was created. Please start a new upload");
            return;
        }

        if (fileUploadMeta.getChunksConsumed() != chunkIndex) {
            Utils.sendFailureResp(out, new JSONObject().put("latestChunk", fileUploadMeta.getChunksConsumed()), "Chunk " + chunkIndex + " is not the expected chunk");
            return;
        }

        String storePath = storageDir + mail + File.separatorChar + fileUploadMeta.getDestination();
        String userStorageDir = storePath + File.separatorChar;

        Part chunkPart = req.getPart("chunk");

        File uploadDir = new File(tempUploadDir, uploadId);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        File chunkFile = new File(
                uploadDir,
                "chunk_" + chunkIndex + ".dat"
        );

        try (InputStream in = chunkPart.getInputStream();
             OutputStream outStream = new FileOutputStream(chunkFile)) {

            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
        }
        chunkIndex++;

        if (chunkIndex == fileUploadMeta.getTotalChunks()) {
            DataStorage.getFileUploadMetaStore().delete(uploadId);
            String filename = Utils.getNonDuplicateFileName(storePath, fileUploadMeta.getFilename());
            mergeChunks(storePath + File.separatorChar + filename, uploadDir.getAbsolutePath());
            return;
        }

        fileUploadMeta.setChunksConsumed(chunkIndex);
        // resp.setStatus(HttpServletResponse.SC_OK);
        Utils.sendSuccessResp(out, new JSONObject());
    }

    private void mergeChunks(String newFile, String tempDir)
            throws IOException {

        FileOperations.startUnChunkingFolder(tempDir, newFile);
        // FileChunker fileChunker = new FileChunker(tempDir, newFile);
        // fileChunker.assembleChunksFromFolder();

        // for (File f : tempDir.listFiles()) {
        //     f.delete();
        // }
        // tempDir.delete();
    }
}