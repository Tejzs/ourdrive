const Logger = {
  containerId: 'notification-container',
  maxToasts: 3,

  // Mapping types to simple emoji icons
  icons: {
    info: 'ℹ',
    success: '✔',
    failure: '🗙'
  },

  _getContainer() {
    let container = document.getElementById(this.containerId);
    if (!container) {
      container = document.createElement('div');
      container.id = this.containerId;
      document.body.appendChild(container);
    }
    return container;
  },

  _removeToast(toast) {
    if (toast.classList.contains('out')) return;
    toast.classList.add('out');
    toast.addEventListener('animationend', (e) => {
      if (e.animationName === 'slideOut') {
        toast.remove();
      }
    });
  },

  _log(message, type) {
    const container = this._getContainer();
    const activeToasts = container.querySelectorAll('.toast:not(.out)');
    if (activeToasts.length >= this.maxToasts) {
      this._removeToast(activeToasts[0]);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    toast.innerHTML = `
      <span class="toast-icon">${this.icons[type]}</span>
      <span class="toast-message">${message}</span>
      <div class="log-progress-bar"></div>
    `;

    container.appendChild(toast);

    setTimeout(() => {
      this._removeToast(toast);
    }, 2000);
  },

  info(msg) { this._log(msg, 'info'); },
  success(msg) { this._log(msg, 'success'); },
  failure(msg) { this._log(msg, 'failure'); }
};

function HandleSignIn() {
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$/;
  let email = document.getElementById("mail-input").value.trim();
  let password = document.getElementById("password-input").value.trim();

  if (email.search(emailRegex) == 0) {
    if (password.length > 5) {
      fetch("../login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          email: email,
          pass: password,
        }),
      })
        .then((res) => res.json())
        .then((data) => {
          console.log(data);
          if (data.status == "success") {
            window.location.href = "../index.html";
            document.getElementById("loginFailed").classList.add("hidden");
          }
          if (data.status == "failure") {
            document.getElementById("loginFailed").classList.remove("hidden");
          }
        });
    }
  }
}

function HandleSignUp() {
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$/;
  let email = document.getElementById("mail-input").value.trim();
  let password = document.getElementById("password-input").value.trim();

  if (email.search(emailRegex) == 0) {
    if (password.length > 0) {
      hashSHA256(password).then((passHash) => {
        fetch(`../login?mail=${email}&pass=${passHash}`)
          .then((response) => {
            if (!response.ok) {
              throw new Error("HTTP error " + response.status);
            }
            return response.json(); // convert response to JSON
          })
          .then((data) => {
            console.log(data);
            if (data.status == "success") {
              window.location.href = "./SignIn.html";
            }
            if (data.status == "failure") {
              console.log(data.msg);
            }
          })
          .catch((error) => {
            console.error("Error:", error);
          });
      });
    }
  }
}

function closeBtn() {
  document.getElementById("loginFailed").classList.add("hidden");
}

// { "status": "sucess", "files" : ["a.txt", "b.txt", "Temp/"] }
currentDir = "";
let options = document.getElementById("options");

let pathStack = [];
function updatePath(dir = "") {
  pathStack.push(dir);
  let fullPath = pathStack.join("/");
  let a = document.createElement("a");
  a.dataset.path = fullPath;
  let p = document.createElement("p");
  p.id = "sep";
  p.className = "sep";
  p.innerHTML = "/";
  a.innerHTML = dir;
  options.appendChild(a);
  options.appendChild(p);

  a.onclick = () => {
    retrieveFile(a.dataset.path);
    pathStack = a.dataset.path.split("/");
    while (p.nextSibling) {
      options.removeChild(p.nextSibling);
    }
  };
}

function getPathData() {
  loc = window.location.hash;
  if (loc == "") {
    window.location.href = "./index.html#/";
  } else {
    let substr = loc.startsWith("/") ? 2 : 1;
    loc = loc.substring(1);
    currentDir = loc;
    retrieveFile(loc);
    console.log(loc)
  }
}

function retrieveRootPath() {
  let p = document.getElementById("sep");
  retrieveFile();
  pathStack = [];
  while (p.nextSibling) {
    options.removeChild(p.nextSibling);
  }
}

function toBytes(size) {
  let value = size.split(" ");
  if (value[1] == "TB") {
    return value[0] * 1024 ** 4;
  }
  if (value[1] === "GB") {
    return value[0] * 1024 ** 3;
  }
  if (value[1] === "MB") {
    return value[0] * 1024 ** 2;
  }
  if (value[1] === "KB") {
    return value[0] * 1024;
  }
  return value[0];
}

function renderTable(files, tbody) {
  tbody.innerHTML = "";
  files.forEach((file) => {

    const tr = document.createElement("tr");
    const tdcb = document.createElement("td");
    const cb = document.createElement("input");
    const tdName = document.createElement("td");
    tdName.className = "rClick";
    const tdKind = document.createElement("td");
    tdKind.className = "rClick";
    const tdSize = document.createElement("td");
    tdSize.className = "rClick";
    const tdDate = document.createElement("td");
    tdDate.className = "rClick";
    const tdShared = document.createElement("td");
    tdShared.className = "rClick";

    tdName.textContent = file.name;
    tdKind.textContent = file.type;
    tdSize.textContent = file.size;
    tdDate.textContent = file.lastMod;
    tdShared.textContent = file.owner;
    tdcb.append(cb);
    cb.type = "checkbox";
    cb.name = "fileSelector";
    cb.id = "select-all";
    cb.className = "fileSelector";
    cb.value = file.name;
    tr.append(tdcb, tdName, tdKind, tdSize, tdDate, tdShared);
    tbody.append(tr);

    if (file.type == "folder") {
      tdName.style.cursor = "pointer";
      tdName.onclick = () => {
        let nextDir;
        console.log(file.name);
        if (currentDir) {
          nextDir = currentDir + "/" + file.name;
        } else {
          nextDir = file.name;
        }
        updatePath(file.name);
        retrieveFile(nextDir);
      };
    }
    tbody.append(tr);
  });

}
let sortAsc = false;
function retrieveFile(dir = "") {
  let tbody = document.getElementById("files");
  currentDir = dir;
  fetch(`./file-view?method=listFiles&dir=${dir}`)
    .then((response) => {
      file;
      if (!response.ok) {
        throw new Error("HTTP error " + response.status);
      }
      return response.json(); // convert response to JSON
    })
    .then((data) => {
      console.log(data);
      if (data.status == "success") {
        data.files.sort((a, b) => !sortAsc ? a.name.localeCompare(b.name) : b.name.localeCompare(a.name));
        renderTable(data.files, tbody);

        let name = document.getElementById("name");
        name.onclick = () => {
          data.files.sort((a, b) => sortAsc ? a.name.localeCompare(b.name) : b.name.localeCompare(a.name));
          sortAsc = !sortAsc;
          name.innerText = sortAsc ? "Name ↓" : "Name ↑";
          type.innerHTML = "Type";
          size.innerHTML = "Size";
          date.innerHTML = "Date";
          mO.innerHTML = "mainOwner";
          renderTable(data.files, tbody)
        }
        let type = document.getElementById("type");
        type.onclick = () => {
          data.files.sort((a, b) => sortAsc ? a.type.localeCompare(b.type) : b.type.localeCompare(a.type));
          sortAsc = !sortAsc;
          type.innerText = sortAsc ? "Type ↓" : "Type ↑";
          name.innerHTML = "Name";
          size.innerHTML = "Size";
          date.innerHTML = "Date";
          mO.innerHTML = "mainOwner";
          renderTable(data.files, tbody)
        }
        let size = document.getElementById("size");
        size.onclick = () => {
          data.files.sort((a, b) => sortAsc ? toBytes(a.size) - toBytes(b.size) : toBytes(b.size) - toBytes(a.size));
          sortAsc = !sortAsc;
          size.innerText = sortAsc ? "Size ↓" : "Size ↑";
          name.innerHTML = "Name";
          type.innerHTML = "Type";
          date.innerHTML = "Date";
          mO.innerHTML = "mainOwner";
          renderTable(data.files, tbody)
        }

        let date = document.getElementById("date");
        date.onclick = () => {
          data.files.sort((a, b) => sortAsc ? a.lastMod.localeCompare(b.lastMod) : b.lastMod.localeCompare(a.lastMod));
          sortAsc = !sortAsc;
          date.innerText = sortAsc ? "Date ↓" : "Date ↑";
          name.innerHTML = "Name";
          type.innerHTML = "Type";
          size.innerHTML = "Size";
          mO.innerHTML = "mainOwner";
          renderTable(data.files, tbody)
        }

        let mO = document.getElementById("mainOwner");
        mainOwner.onclick = () => {
          data.files.sort((a, b) => sortAsc ? a.owner.localeCompare(b.owner) : b.owner.localeCompare(a.owner));
          sortAsc = !sortAsc;
          mO.innerText = sortAsc ? "mainOwner ↓" : "mainOwner ↑";
          name.innerHTML = "Name";
          type.innerHTML = "Type";
          size.innerHTML = "Size";
          date.innerHTML = "Date";
          renderTable(data.files, tbody)
        }
      }
      if (data.status == "failure") {
        Logger.failure("Error fetching files: " + data.msg);
        return false;
      }
    })
    .catch((error) => {
      Logger.failure("Error: " + error);
    });
}

function selectAll() {
  let selectAll = document.getElementById("select-all").checked;
  let allCheckboxes = document.getElementsByClassName("fileSelector");
  for (let i = 0; i < allCheckboxes.length; i++) {
    allCheckboxes[i].checked = selectAll;
  }
}

function updateProfile() {
  let profile = document.getElementById("profile");
  let root = document.getElementById("root");
  fetch(`./user?method=currUser`)
    .then((response) => {
      if (!response.ok) {
        throw new Error("HTTP error " + response.status);
      }
      return response.json(); // convert response to JSON
    })
    .then((data) => {
      console.log(data);
      if (data.status == "success") {
        profile.innerHTML = data.currUser;
        root.innerHTML = data.currUser;
      }
      if (data.status == "failure") {
        console.log(data.msg);
      }
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

function logout() {
  fetch(`./user?method=logout`)
    .then((response) => {
      if (!response.ok) {
        throw new Error("HTTP error " + response.status);
      }
      return response.json(); // convert response to JSON
    })
    .then((data) => {
      console.log(data);
      if (data.status == "success") {
        window.location.href = "./Pages/SignIn.html";
      }
      if (data.status == "failure") {
        console.log(data.msg);
      }
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

function showPassword() {
  let password = document.getElementById("password-input");

  if (password.type == "password") {
    password.type = "text";
  } else if (password.type == "text") {
    password.type = "password";
  }
}

function getActiveTasks() {
  fetch("./upload-status?method=list")
    .then((resp) => resp.json())
    .then((data) => {
      console.log(data);
      if (data.status == "failure") {
        Logger.failure("Error getting active operations: " + data.msg);
        return;
      }

      data.data.forEach(file => {
        const progressBar = document.getElementById("progressBar");
        if (!progressBar) return;

        if (progressBar.querySelector(`#${CSS.escape(file.uploadId)}`)) {
          return;
        }

        let parentDiv = document.createElement("div");

        let cancelBtn = document.createElement("button");
        cancelBtn.innerHTML = "🗙";
        cancelBtn.className = "cancel-btn";
        cancelBtn.onclick = () => {
          cancelUpload(file.uploadId);
        }

        parentDiv.classList.add("parentCont", "paused-border");
        parentDiv.id = file.uploadId;
        let div = document.createElement("div");
        div.className = "progress-header";
        let fileNameSpan = document.createElement("span");
        fileNameSpan.className = "file-name";
        let percentSpan = document.createElement("span");
        percentSpan.className = "percent";
        percentSpan.id = file.uploadId + "P";

        let progressbar = document.createElement("div");
        progressbar.id = file.uploadId + "PB";
        progressbar.className = "paused-progress-bar"
        parentDiv.appendChild(cancelBtn, progressbar);

        let infoDiv = document.createElement("div");
        infoDiv.className = "progress-info";
        let sizeSpan = document.createElement("span");
        sizeSpan.className = "size";

        const completedPercentage = (((file.chunksconsumed * file.chunksize) / (file.totalchunks * file.chunksize)) * 100).toFixed(2);
        progressbar.style.right = (100 - completedPercentage) + "%"
        sizeSpan.innerText = formatBytes((file.chunksconsumed * file.chunksize)) + " / " + formatBytes((file.filesize)) + " (" + completedPercentage + " %)";
        sizeSpan.id = file.uploadId + "S";

        let inpLabel = document.createElement("label");
        inpLabel.className = "start-btn";
        let inpFile = document.createElement("input");
        inpFile.id = "fileUploadR" + file.uploadId;
        inpFile.type = "file";
        let inpSpan = document.createElement("span");
        inpSpan.innerText = "Browse";
        inpLabel.append(inpFile, inpSpan);

        let startBtn = document.createElement("button");
        startBtn.innerHTML = "Resume";
        startBtn.className = "start-btn";


        startBtn.onclick = () => {
          resumeUpload(file.uploadId, file.chunksconsumed, file.filesize, file.chunksize, parentDiv);
        };

        fileNameSpan.innerText = file.filename;

        div.append(fileNameSpan, percentSpan, inpLabel, startBtn);
        infoDiv.append(sizeSpan);
        parentDiv.append(div, infoDiv);
        document.getElementById("progressBar").append(parentDiv);
      });
    });
}

async function hashSHA256(text) {
  const encoder = new TextEncoder();
  const data = encoder.encode(text);

  const hashBuffer = await crypto.subtle.digest("SHA-256", data);

  const hashArray = Array.from(new Uint8Array(hashBuffer));
  const hashHex = hashArray
    .map((b) => b.toString(16).padStart(2, "0"))
    .join("");

  return hashHex;
}

async function createFingerprint(file) {
  console.log(file);
  const chunkSize = 1024 * 1024; // 1MB

  const firstChunk = file.slice(0, chunkSize);
  const lastChunk = file.slice(Math.max(0, file.size - chunkSize), file.size);

  const firstHash = await hashBlob(firstChunk);
  const lastHash = await hashBlob(lastChunk);

  return `${file.size}-${file.lastModified}-${firstHash}-${lastHash}`;
}

async function hashBlob(blob) {
  const buffer = await blob.arrayBuffer();
  const hashBuffer = await crypto.subtle.digest("SHA-256", buffer);

  // Convert ArrayBuffer to hex string
  const hashArray = Array.from(new Uint8Array(hashBuffer));
  const hashHex = hashArray
    .map(b => b.toString(16).padStart(2, "0"))
    .join("");

  return hashHex;
}

CHUNK_SIZE = 1 * 1024 * 1024; // 1 MB

let file;
let uploadId = 1;
let currentChunks = {};
let paused = false;

function formatBytes(bytes) {
  if (bytes === 0) return "0 B";
  const k = 1024;
  const sizes = ["B", "KB", "MB", "GB", "TB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  const value = bytes / Math.pow(k, i);
  return value.toFixed(2) + " " + sizes[i];
}


async function uploadMetaData(file, fileHash, cSize, dir = "") {
  let fileName = file.name;
  console.log(fileName);
  let chunkSize = cSize;
  let fileSize = file.size;
  let totalChunks = Math.ceil(file.size / cSize);

  let resp = await fetch(
    `./chunked-upload?filename=${fileName.replaceAll("&", "%26")}&filehash=${fileHash}&chunksize=${chunkSize}&filesize=${fileSize}&totalchunks=${totalChunks}&dest=${currentDir + "/" + dir}`,
  );
  if (!resp.ok) {
    throw new Error("HTTP error " + response.status);
  }
  console.log(resp.json);
  return resp.json();
}

function UploadFiles() {
  let chuckSize = document.getElementById("chunk-size").value;
  startUpload(chuckSize);
}

async function startUpload(chunksize) {
  files = document.getElementById("fileUpload").files;

  if (files.length == 0) {
    Logger.failure("No file selected");
  }

  for (let i = 0; i < files.length; i++) {
    let file = files[i];
    if (chunksize == "auto") {
      chunksize = getOptimalChunkSize(file.size);
    }
    const fileFingerPrint = await createFingerprint(file);
    console.log(fileFingerPrint)
    paused = false;

    let metadata = await uploadMetaData(file, fileFingerPrint, chunksize * 1024 * 1024);
    currentChunks[metadata.uploadId] = 0;
    uploadChunk(file, metadata.uploadId, fileFingerPrint, chunksize * 1024 * 1024, false, false);
  }
  const fileInput = document.getElementById("fileUpload");
  fileInput.value = "";
}

async function resumeUpload(uploadId, resumeChunk, filesize, chunksize, parentDiv) {
  files = document.getElementById("fileUploadR" + uploadId).files;

  if (files.length == 0) {
    Logger.failure("No file selected");
  }

  let file = files[0];
  if (file.size != filesize) {
    Logger.failure("Uploaded file is different");
    return;
  }

  paused = false;
  currentChunks[uploadId] = resumeChunk;
  const fileFingerPrint = await createFingerprint(file);
  uploadChunk(file, uploadId, fileFingerPrint, chunksize, false, true);
  parentDiv.remove();
  const fileInput = document.getElementById("fileUpload");
  fileInput.value = "";
}

function formatSpeed(bytesPerSec) {
  if (bytesPerSec >= 1024 * 1024) {
    return (bytesPerSec / (1024 * 1024)).toFixed(2) + " MB/s";
  }
  if (bytesPerSec >= 1024) {
    return (bytesPerSec / 1024).toFixed(2) + " KB/s";
  }
  return bytesPerSec.toFixed(2) + " B/s";
}


function uploadChunk(file, uploadId, fileHash, chunkSize, show, isResumeCalled) {
  if (file.length != 0 && show == false) {
    Logger.info((isResumeCalled ? "Resuming " : "Uploading ") + file.name);
    let parentDiv = document.createElement("div");
    parentDiv.classList.add("parentCont", "running-border");
    parentDiv.id = uploadId;
    let div = document.createElement("div");
    div.className = "progress-header";
    let fileNameSpan = document.createElement("span");
    fileNameSpan.className = "file-name";
    let percentSpan = document.createElement("span");
    percentSpan.className = "percent";
    percentSpan.id = uploadId + "P";
    let progressbar = document.createElement("div");
    progressbar.id = uploadId + "PB";
    progressbar.className = "progress-bar"

    parentDiv.appendChild(progressbar);

    let infoDiv = document.createElement("div");
    infoDiv.className = "progress-info";
    let sizeSpan = document.createElement("span");
    sizeSpan.className = "size";
    sizeSpan.id = uploadId + "S";

    div.append(fileNameSpan, percentSpan);
    infoDiv.append(sizeSpan);
    parentDiv.append(div, infoDiv);
    fileNameSpan.innerText = file.name;
    document.getElementById("progressBar").append(parentDiv);
  }

  let currentChunk = currentChunks[uploadId];
  if (paused) return;

  const start = currentChunk * chunkSize;
  if (start >= file.size) {
    Logger.success("Uploaded " + file.name);
    document.getElementById(uploadId).remove();
    retrieveFile(currentDir);
    return;
  }
  let chunk = file.slice(start, start + chunkSize);
  let formData = new FormData();

  formData.append("chunk", chunk);
  formData.append("uploadId", uploadId);
  formData.append("chunkIndex", currentChunk);
  formData.append("filehash", fileHash);

  let xhr = new XMLHttpRequest();
  xhr.open("POST", "./chunked-upload");
  let lastTime = Date.now();
  let lastLoaded = 0;

  xhr.upload.onprogress = (e) => {
    if (!e.lengthComputable) return;


    const now = Date.now();
    const deltaTime = (now - lastTime) / 1000;
    const deltaBytes = e.loaded - lastLoaded;

    const speed = deltaTime > 0 ? deltaBytes / deltaTime : 0;

    lastTime = now;
    lastLoaded = e.loaded;

    const uploadedBytes = currentChunk * chunkSize + e.loaded;
    const completedPercentage = ((uploadedBytes / file.size) * 100).toFixed(2);

    document.getElementById(uploadId + "S").innerText = formatBytes((uploadedBytes).toFixed(2)) + " / " + formatBytes((file.size).toFixed(2)) + " (" + completedPercentage + " %) | " + formatSpeed(speed);
    document.getElementById(uploadId + "PB").style.right = (100 - completedPercentage) + "%";
  };

  xhr.onload = () => {
    if (xhr.status === 200) {
      if (isResumeCalled) {
        let resp = null;
        try {
          if (xhr.responseText) {
            resp = JSON.parse(xhr.responseText);
          }
        } catch (e) {
        }

        if (resp && resp.status === "failure") {
          getActiveTasks();
          document.getElementById(uploadId).remove();
          return;
        }

      }

      currentChunks[uploadId] = currentChunk + 1;
      uploadChunk(file, uploadId, fileHash, chunkSize, true, false);
      if (start >= file.size) {
        Logger.success("Uploaded " + file.name);
        document.getElementById(uploadId).remove();
        retrieveFile(currentDir);
      }
    }
  };

  xhr.onerror = () => {
    Logger.failure("Network error, retrying in 5s ...");
    setTimeout(() => {
      uploadChunk(file, uploadId, fileHash, chunkSize, true, false);
    }, 5000);
  };

  xhr.send(formData);
}

function getOptimalChunkSize(fileSize) {
  const MB = 1024 * 1024;

  const rules = [
    { max: 5 * MB, chunk: fileSize => fileSize / MB }, // small files
    { max: 1024 * MB, chunk: 5 },             // ≤ 1 GB
    { max: 10 * 1024 * MB, chunk: 20 },       // ≤ 10 GB
    { max: Infinity, chunk: 64 }              // > 10 GB
  ];

  for (const rule of rules) {
    if (fileSize <= rule.max) {
      return typeof rule.chunk === "function"
        ? rule.chunk(fileSize)
        : rule.chunk;
    }
  }
}

function deleteSelected() {
  const checkedCheckboxes = document.querySelectorAll('input[name="fileSelector"]:checked');
  const selectedValues = Array.from(checkedCheckboxes).map(checkbox => checkbox.value);
  console.log(selectedValues.join(','));

  fetch(`./file-operations?method=delete&parent=${currentDir}&files=${selectedValues.join(",").replaceAll("&", "%26")}`)
    .then((resp) => resp.json())
    .then((data) => {
      if (data.status == "success") {
        Logger.success("Successfully deleted");
        retrieveFile(currentDir);
      } else {
        Logger.failure("Error deleting file/files");
      }
    });
}


function newFolder() {
  let folderName = document.getElementById("folderInput").value.trim();
  if (folderName) {
    fetch(`./file-operations?method=mkdir&parent=${currentDir}&folder=${folderName}`)
      .then((resp) => resp.json())
      .then((data) => {
        if (data.status == "success") {
          Logger.success("Created folder");
          retrieveFile(currentDir);
        } else {
          Logger.failure("Error creating folder");
        }
      });

  }
}

async function createFolder(name, dir = "") {
  await new Promise(r => setTimeout(r, 1000));
  try {
    const resp = await fetch(
      `./file-operations?method=mkdirf&parent=${currentDir + "/" + dir}&folder=${name}`
    );
    const data = await resp.json();
      console.log(data);
      if (data.status === "success" && data.exists == false) {
        Logger.success("Created folder");
        return true;
      }
      else if (data.exists == true) {
        Logger.failure("Folder already exist");
        return false;
      }
      Logger.failure("Error creating folder");
      return false;

  } catch (err) {
    console.error(err);
    Logger.failure("Network error");
    return false;
  }
}


function cancelUpload(uploadId) {
  fetch(`./upload-status?method=delete&uploadId=${uploadId}`)
    .then((resp) => resp.json())
    .then((data) => {
      console.log(data);
      if (data.status == "success") {
        Logger.success("Removed File");
        document.getElementById(uploadId).remove();
      } else {
        Logger.failure("Error Deleting");
      }
    });
}

function UploadFolders() {
  let chuckSize = document.getElementById("chunk-size").value;
  startFolderUpload(chuckSize);
}

async function startFolderUpload(chunksize) {
  const files = document.getElementById("folderUpload").files;
  const folders = new Map();
  for (const file of files) {
    const relativePath = file.webkitRelativePath;
    const directoryName = relativePath.substring(0, relativePath.lastIndexOf('/'));
    if (directoryName && !folders.has(directoryName)) {
      dirName = directoryName.substring(directoryName.lastIndexOf('/') + 1, directoryName.length);
      dirPath = directoryName.substring(0, directoryName.lastIndexOf('/') + 1);
      if (dirName == dirPath) {
        folders.set(dirName, "");
      }
      else {
        folders.set(dirName, dirPath);
      }
    }
  }
  let canProceed = false;
  for (const name of folders) {
    canProceed = await createFolder(name[0], name[1]);
    if (canProceed === false) {
      return;
    }
  }
  console.log(folders);

  if (files.length == 0) {
    Logger.failure("No Folders selected");
  }

  for (let i = 0; i < files.length; i++) {
    let file = files[i];
    if (chunksize == "auto") {
      chunksize = getOptimalChunkSize(file.size);
    }

    const fileFingerPrint = await createFingerprint(file);
    console.log(fileFingerPrint)
    paused = false;

    let metadata = await uploadMetaData(file, fileFingerPrint, chunksize * 1024 * 1024, file.webkitRelativePath.substring(0, file.webkitRelativePath.lastIndexOf('/') + 1));
    currentChunks[metadata.uploadId] = 0;
    uploadChunk(file, metadata.uploadId, fileFingerPrint, chunksize * 1024 * 1024, false, false);
  }
  const fileInput = document.getElementById("fileUpload");
  fileInput.value = "";
}