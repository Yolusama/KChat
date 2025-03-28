import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import electron from "vite-plugin-electron/simple";
import electronRenderer from 'vite-plugin-electron-renderer';
import polyfillExports from "vite-plugin-electron-renderer" 


export default defineConfig({
  plugins: [vue(),
  electron({
    main: {
      entry: "src/electron/index.js", // 主进程文件
    },
    preload: {
      input: "src/electron/preload.js", // 预加载文件
    },
  }),electronRenderer(),polyfillExports()
  ],
  build: {
    emptyOutDir: false, // 默认情况下，若 outDir 在 root 目录下，则 Vite 会在构建时清空该目录
  },
  server: {
    port: 5435,
    cors: true,
    hmr: true
  }
});
