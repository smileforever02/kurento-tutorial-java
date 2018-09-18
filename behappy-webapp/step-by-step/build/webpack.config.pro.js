'use strict'

const merge = require('webpack-merge')
const WebpackShellPlugin = require('webpack-shell-plugin')
const baseConfig = require('./webpack.config.base')

let config = merge(baseConfig, {
    mode: 'production'
})

config.plugins = config.plugins || [];
config.plugins.push(new WebpackShellPlugin({
    onBuildEnd: 'node ./build/copy-to-pro.js'
}));

module.exports = config;