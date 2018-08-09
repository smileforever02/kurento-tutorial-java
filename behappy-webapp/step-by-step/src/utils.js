let jq = jQuery
console.log('try to find jQuery')
if(typeof jq === 'undefined'){
    jq = function(){
        console.error('there is no jQuery');
    };
}
export default jq