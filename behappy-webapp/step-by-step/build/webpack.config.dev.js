'use strict'

const {HotModuleReplacementPlugin} = require('webpack')
const {VueLoaderPlugin} = require('vue-loader')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const CopyWebpackPlugin = require('copy-webpack-plugin')
const {resolve} = require('./utils')

module.exports = {
    mode: 'development',
    entry: ['./src/app.js'],
    resolve: {
        alias: {vue: 'vue/dist/vue.js'}
    },
    devServer: {
        hot: true,
        watchOptions: {
            poll: true
        }
    },
    module: {
        rules: [
            {
                test: /\.vue$/,
                use: 'vue-loader'
            },
            {
                test: /\.styl(us)?$/,
                use: [
                  'vue-style-loader',
                  'css-loader',
                  'stylus-loader'
                ]
            },
            {
                test: /\.js$/,
                use: 'babel-loader'
            }
        ]
    },
    plugins: [
        new HotModuleReplacementPlugin(),
        new VueLoaderPlugin(),
        new HtmlWebpackPlugin({
            filename: 'index.html',
            template: 'index.html',
            inject: true
        }),
        new CopyWebpackPlugin([
            {
                from: resolve('static/img'),
                to: resolve('dist/static/img'),
                toType: 'dir'
            },
            {
                from: resolve('static/3rd-js'),
                to: resolve('dist/static/3rd-js'),
                toType: 'dir'
            },
            {
                from: resolve('static/3rd-css'),
                to: resolve('dist/static/3rd-css'),
                toType: 'dir'
            }
        ])
    ]
}