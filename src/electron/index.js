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

function subWinCallback(subWin) {
  const eventFuncs = {
    "search-minimize": () => subWin.minimize(),
    "search-maximize": () => subWin.setFullScreen(true),
    "search-close": () => {
      subWin.close();
      subWin.destroy();
      for(var eventName in eventFuncs)
        ipcMain.off(eventName,eventFuncs[eventName]);
    },
    "search-restoreSize": () => {
      subWin.setFullScreen(false);
    }
  };
  
  for(var eventName in eventFuncs)
    ipcMain.on(eventName,eventFuncs[eventName]);
}

function createSubWindow(parent) {
  const win = new BrowserWindow({
    frame: false,
    parent:parent,
    minHeight: 600,
    minWidth: 600,
    maxHeight: 1000,
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

  ipcMain.on("openSearch", () => {
    const subWin = createSubWindow(win);
    if (app.isPackaged)
      subWin.loadURL(`file://${__dirname}/index.htmL/#/Search`);
    else
      subWin.loadURL("http://localhost:5435/#/Search");
  });

  // 如果打包了，渲染index.html
  if (app.isPackaged) {
    win.loadURL(`file://${__dirname}/index.html`);
  } else {
    let url = "http://localhost:5435"; // 本地启动的vue项目路径
    win.loadURL(url);
  }
};


app.whenReady().then(() => {
  createWindow(); // 创建窗口
  app.on("activate", () => {
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});