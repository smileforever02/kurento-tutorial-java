const copydir = require('copy-dir');
const path = require('path');
const fromDir = path.resolve(__dirname, '../dist');
const toDir = path.resolve(__dirname, '../../../kurento-group-call/src/main/resources/static');

console.log('start to copy to ' + toDir)
copydir(fromDir, toDir, function(err){
    if(err){
        console.log(err);
    } else {
        console.log('copy done');
    }
});

// just for test
const devDir = 'C:/github-repo/kurento-tutorial-java/kurento-group-call/src/main/resources/static/dist';
console.log('copy to develop dir')
copydir(fromDir, devDir, function(err){
    if(err){
        console.log(err);
    } else {
        console.log('copy to develop dir done');
    }
});