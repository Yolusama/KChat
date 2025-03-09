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
  const eventFuncs = {
    "search-minimize": () => subWin.minimize(),
    "search-maximize": () => subWin.setFullScreen(true),
    "search-close": () => {
      subWin.close();
      subWin.destroy();
      for (var eventName2 in eventFuncs)
        electron.ipcMain.off(eventName2, eventFuncs[eventName2]);
    },
    "search-restoreSize": () => {
      subWin.setFullScreen(false);
    }
  };
  for (var eventName in eventFuncs)
    electron.ipcMain.on(eventName, eventFuncs[eventName]);
}
function createSubWindow(parent) {
  const win = new electron.BrowserWindow({
    frame: false,
    parent,
    minHeight: 600,
    minWidth: 600,
    maxHeight: 1e3,
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
  electron.ipcMain.on("openSearch", () => {
    const subWin = createSubWindow(win);
    if (electron.app.isPackaged)
      subWin.loadURL(`file://${__dirname}/index.htmL/#/Search`);
    else
      subWin.loadURL("http://localhost:5435/#/Search");
  });
  if (electron.app.isPackaged) {
    win.loadURL(`file://${__dirname}/index.html`);
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
