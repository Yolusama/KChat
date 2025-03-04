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

  ipcMain.on("restoreSize",()=>{
    window.setFullScreen(false);
  });

  ipcMain.on("setSize",(event,width,height,notLogin)=>{
      window.setSize(width,height);
      window.center();
      if(notLogin)
        window.setResizable(true);
  });

  ipcMain.on("setMinBound",(event,width,height)=>{
        window.setMinimumSize(width,height);
  });
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
    center:true,
    movable:true,
    resizable:false
  });
  
  processCorresponse(win);
  // 如果打包了，渲染index.html
  if (app.isPackaged) {
    win.loadFile(path.join(__dirname, "../index.html"));
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