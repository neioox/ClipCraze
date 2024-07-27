const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  outputDir: '../backend/src/main/resources/public/dist',

  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        ws: true,
        changeOrigin: true,
      },
    },
  },
  transpileDependencies: true,
});
