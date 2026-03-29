const { merge: webpackMerge } = require('webpack-merge');
const BrowserSyncPlugin = require('browser-sync-webpack-plugin');
const SimpleProgressWebpackPlugin = require('simple-progress-webpack-plugin');
const WebpackNotifierPlugin = require('webpack-notifier');
const path = require('path');
const sass = require('sass');

const utils = require('./utils.js');
const commonConfig = require('./webpack.common.js');

const ENV = 'development';

module.exports = async options =>
  webpackMerge(await commonConfig({ env: ENV }), {
    mode: ENV,

    // Ajută la mesajele de eroare corecte în React
    devtool: 'cheap-module-source-map',

    entry: ['./src/main/webapp/app/index'],

    output: {
      path: utils.root('target/classes/static/'),
      filename: '[name].[contenthash:8].js',
      chunkFilename: '[name].[chunkhash:8].chunk.js',
      publicPath: '/', // important pentru SPA + historyApiFallback
    },

    optimization: {
      moduleIds: 'named',
    },

    module: {
      rules: [
        {
          test: /\.(sa|sc|c)ss$/,
          use: [
            'style-loader',
            {
              loader: 'css-loader',
              options: { url: false },
            },
            {
              loader: 'postcss-loader',
            },
            {
              loader: 'sass-loader',
              options: { implementation: sass },
            },
          ],
        },
      ],
    },

    devServer: {
      hot: true,
      static: {
        directory: './target/classes/static/',
      },
      port: 9060,

      // IMPORTANT: rutele care trebuie trimise la backend-ul Spring Boot
      proxy: [
        {
          context: ['/api', '/services', '/management', '/actuator', '/v3/api-docs', '/h2-console'],
          target: `http${options.tls ? 's' : ''}://localhost:8080`,
          secure: false,
          changeOrigin: options.tls,
          ws: true,
        },
      ],

      // pentru SPA (react-router) să servească index.html pentru rute necunoscute
      historyApiFallback: true,
      client: {
        overlay: { errors: true, warnings: false },
      },
    },

    stats: process.env.JHI_DISABLE_WEBPACK_LOGS ? 'none' : options.stats,

    plugins: [
      process.env.JHI_DISABLE_WEBPACK_LOGS
        ? null
        : new SimpleProgressWebpackPlugin({
            format: options.stats === 'minimal' ? 'compact' : 'expanded',
          }),

      // rulează site-ul pe http://localhost:9000, proxiat către dev-server (9060) și backend (8080)
      new BrowserSyncPlugin(
        {
          https: options.tls,
          host: 'localhost',
          port: 9000,
          proxy: {
            // când watch=true, mergi direct pe backendul 8080; altfel pe dev-server 9060
            target: `http${options.tls ? 's' : ''}://localhost:${options.watch ? '8080' : '9060'}`,
            ws: true,
            proxyOptions: {
              // păstrează Host header-ul pentru backend
              changeOrigin: false,
            },
          },
          socket: { clients: { heartbeatTimeout: 60000 } },
          // ghostMode poate fi dezactivat dacă vrei
          // ghostMode: { clicks:false, location:false, forms:false, scroll:false }
        },
        { reload: false },
      ),

      new WebpackNotifierPlugin({
        title: 'Medicalsystem',
        contentImage: path.join(__dirname, 'logo-jhipster.png'),
      }),
    ].filter(Boolean),
  });
