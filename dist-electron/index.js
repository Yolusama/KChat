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
  if (electron.app.isPackaged) {
    win.loadFile(path.join(__dirname, "../index.html"));
  } else {
    let url = "http://localhost:5435";
    win.loadURL(url);
  }
};
electron.app.whenReady().then(() => {
  createWindow();
  electron.app.on("activate", () => {
    if (electron.BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});
