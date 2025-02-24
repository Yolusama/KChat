"use strict";
const electron = require("electron");
const path = require("path");
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
    minHeight: 600,
    frame: false
  });
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
