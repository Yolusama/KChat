import axios from "axios";
import { app, BrowserWindow, ipcMain } from "electron";
import fs from "fs"
import path from "path";

const fileStorePath = "AppData";

function processCorresponse(window) {
  ipcMain.on("minimize", () => {
    window.minimize();
  });

  ipcMain.on("maximize", () => {
    window.setFullScreen(true);
  });

  ipcMain.on("close", () => {
    window.close();
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

  ipcMain.on("userLogan", (event, userId, token) => {
    window.userOption = {
      userId: userId,
      token: token
    };
  });
}

function handleRenderInvoke() {
  ipcMain.handle("writeFile", (event, account, fileName, content) => {
    const dir = `${fileStorePath}/${account}`;
    if(!fs.existsSync(dir))
      fs.mkdir(dir,{recursive:true},()=>{});
    const pathToWrite = `${dir}/${fileName}`;
    return new Promise((resolve,reject)=>{
      fs.writeFile(pathToWrite, content, {}, err => {
        if(err) reject(err);
        else resolve();
      });
    });
  });

  ipcMain.handle("readFile", (event, account, fileName) => {
    const pathToRead = `${fileStorePath}/${account}/${fileName}`;
    return new Promise((resolve, reject) => {
      fs.readFile(pathToRead, { encoding: "utf-8" }, (err, data) => {
        if (err) reject(err);
        else resolve(data);
      });
    })
  });

 /* ipcMain.handle("testRead", (event, path) => {
    return new Promise((resolve, reject) => {
      fs.readFile(path, { encoding: "utf-8" }, (err, data) => {
        if (err) reject(err);
        else resolve(data);
      });
    });
  });

  ipcMain.handle("testWrite", (event, path, content) => {
    return new Promise((resolve, reject) => {
      const index = path.lastIndexOf('/');
      const dir = path.substring(0,index);
      if(!fs.existsSync(dir))
          fs.mkdir(dir,{recursive:true},()=>{});
      fs.writeFile(path, content,{flag:"w+"}, err => {
        if(err) reject(err);
        else resolve();
      });
    });

  });*/
}

function subWinCallback(subWin) {
  const eventFuncs = {
    "search-minimize": () => subWin.minimize(),
    "search-maximize": () => subWin.setFullScreen(true),
    "search-close": () => {
      subWin.close();
      subWin.destroy();
      for (var eventName in eventFuncs)
        ipcMain.off(eventName, eventFuncs[eventName]);
    },
    "search-restoreSize": () => {
      subWin.setFullScreen(false);
    }
  };

  for (var eventName in eventFuncs)
    ipcMain.on(eventName, eventFuncs[eventName]);
}

function createSubWindow(parent) {
  const win = new BrowserWindow({
    frame: false,
    parent: parent,
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

  win.on("close", async (event) => {
    if (win.userOption == undefined) return;
    const { userId, token } = win.userOption;
    await axios.patch(`http://localhost:5725/Api/User/GoOffline/${userId}`, {}, {
      headers: {
        token: token
      }
    });
  });

  ipcMain.on("openSearch", () => {
    const subWin = createSubWindow(win);
    if (app.isPackaged)
      subWin.loadURL(`file://${__dirname}/index.html/#/Search`);
    else
      subWin.loadURL("http://localhost:5435/#/Search");
  });

  handleRenderInvoke();

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