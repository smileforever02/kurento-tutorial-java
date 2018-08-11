'use strict'

const webpack = require('webpack')
const merge = require('webpack-merge')
const baseConfig = require('./webpack.config.base')
const {resolve} = require('./utils')

module.exports = merge(baseConfig, {
    mode: 'development',
    devServer: {
        hot: true,
        watchOptions: {
            poll: true
        }
    }
})