/* eslint-disable @typescript-eslint/no-var-requires */
const path = require('path');
const {
  override,
  addWebpackModuleRule,
  addWebpackAlias,
  // addPostcssPlugins
} = require('customize-cra');
const addLessLoader = require('customize-cra-less-loader');
// const px2rem = require('postcss-px2rem-exclude');
module.exports = {
  webpack: override(
    addLessLoader({
      strictMath: true,
      noIeCompat: true,
      // lessOptions: {
      //   javascriptEnabled: true,
      //   modifyVars: {
      //     '@base-font-size': 37.5,
      //   }
      // }
    }),
    // addPostcssPlugins([
    //   px2rem({
    //     remUnit: 75,
    //     propList: ['*'],
    //     exclude: /node_modules/,
    //   })
    // ]),
    addWebpackModuleRule({
      test: /\.svg$/,
      loader: '@svgr/webpack',
    }),
    addWebpackAlias({
      '@': path.resolve(__dirname, 'src'),
    })
  ),

};
