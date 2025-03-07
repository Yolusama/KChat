"use strict";
const electron = require("electron");
const path = require("path");
function processCorresponse(window) {
  electron.ipcMain.on("minimize", () => {
    window.minimize();
  });
  electron.ipcMain.on("maximize", () => {
    window.setFullScreen(true);
  });
  electron.ipcMain.on("close", () => {
    window.close();
    window.destroy();
  });
  electron.ipcMain.on("restoreSize", () => {
    window.setFullScreen(false);
  });
  electron.ipcMain.on("setSize", (event, width, height, notLogin) => {
    window.setSize(width, height);
    window.center();
    if (notLogin)
      window.setResizable(true);
  });
  electron.ipcMain.on("setMinBound", (event, width, height) => {
    window.setMinimumSize(width, height);
  });
}
function subWinCallback(subWin) {
  electron.ipcMain.on("openSearch", () => {
    subWin.show();
  });
  electron.ipcMain.on("search-minimize", () => {
    subWin.minimize();
  });
  electron.ipcMain.on("search-maximize", () => {
    subWin.setFullScreen(true);
  });
  electron.ipcMain.on("search-close", () => {
    subWin.hide();
    subWin.reload();
    subWin.setSize(0, 0);
    subWin.center();
  });
  electron.ipcMain.on("search-restoreSize", () => {
    subWin.setFullScreen(false);
  });
}
function createSubWindow() {
  const win = new electron.BrowserWindow({
    frame: false,
    minHeight: 600,
    minWidth: 600,
    maxHeight: 1e3,
    show: false,
    center: true,
    modal: false,
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false,
      webSecurity: false
    },
    width: 600,
    height: 600
  });
  subWinCallback(win);
  return win;
}
const createWindow = () => {
  const win = new electron.BrowserWindow({
    webPreferences: {
      contextIsolation: false,
      // 是否开启隔离上下文
      nodeIntegration: true,
      // 渲染进程使用Node API
      preload: path.join(__dirname, "src/electron/preload.js")
      // 需要引用js文件
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
  if (electron.app.isPackaged) {
    win.loadFile(path.join(__dirname, "../index.html"));
  } else {
    let url = "http://localhost:5435";
    win.loadURL(url);
    subWin.loadURL(`${url}/#/Search`);
  }
};
electron.app.whenReady().then(() => {
  createWindow();
  electron.app.on("activate", () => {
    if (electron.BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});
