import { app, BrowserWindow, ipcMain } from "electron";
import path from "path";
function processCorresponse(window) {
  ipcMain.on("minimize", () => {
    window.minimize();
  });

  ipcMain.on("maximize", () => {
    window.setFullScreen(true);
  });

  ipcMain.on("close", () => {
    window.close();
    window.destroy();
  });

  ipcMain.on("restoreSize", () => {
    window.setFullScreen(false);
  });

  ipcMain.on("setSize", (event, width, height, notLogin) => {
    window.setSize(width, height);
    window.center();
    if (notLogin)
      window.setResizable(true);
  });

  ipcMain.on("setMinBound", (event, width, height) => {
    window.setMinimumSize(width, height);
  });
}

function subWinCallback(subWin){
  ipcMain.on("openSearch", () => {
    subWin.show();
  });

  ipcMain.on("search-minimize", () => {
    subWin.minimize();
  });

  ipcMain.on("search-maximize", () => {
    subWin.setFullScreen(true);
  });

  ipcMain.on("search-close", () => {
    subWin.hide();
    subWin.reload();
    subWin.setSize(0,0);
    subWin.center();
  });

  ipcMain.on("search-restoreSize", () => {
    subWin.setFullScreen(false);
  });
}

function createSubWindow() {
  const win = new BrowserWindow({
    frame: false,
    minHeight: 600,
    minWidth: 600,
    maxHeight:1000,
    show: false,
    center:true,
    modal:false,
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false,
      webSecurity:false
    },
    width:600,
    height:600
  });
  subWinCallback(win);
  return win;
}
//ctrl+shit+i 调试模式下打开调试窗口
const createWindow = () => {
  const win = new BrowserWindow({
    webPreferences: {
      contextIsolation: false, // 是否开启隔离上下文
      nodeIntegration: true, // 渲染进程使用Node API
      preload: path.join(__dirname, "src/electron/preload.js"), // 需要引用js文件
    },
    width: 1200,
    height: 800,
    minWidth: 400,
    minHeight: 400,
    frame: false,
    center: true,
    movable: true,
    resizable: false
  });

  processCorresponse(win);

  const subWin = createSubWindow();

  // 如果打包了，渲染index.html
  if (app.isPackaged) {
    win.loadFile(path.join(__dirname, "../index.html"));
  } else {
    let url = "http://localhost:5435"; // 本地启动的vue项目路径
    win.loadURL(url);
    subWin.loadURL(`${url}/#/Search`);
  }
};


app.whenReady().then(() => {
  createWindow(); // 创建窗口
  app.on("activate", () => {
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});