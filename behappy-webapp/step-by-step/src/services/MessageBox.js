import $ from '../utils'

const LEVEL = {
    SUCCESS: 'alert alert-success',
    INFO: 'alert alert-info',
    WARN: 'alert alert-warning',
    ERROR: 'alert alert-danger'
}
const msgBox = $('.message-box')
let timeoutIdx
export default {
    info(msg){
        this.showMsg(LEVEL.INFO, msg)
    },
    success(msg){
        this.showMsg(LEVEL.SUCCESS, msg)
    },
    warn(msg){
        this.showMsg(LEVEL.WARN, msg)
    },
    error(msg){
        this.showMsg(LEVEL.ERROR, msg)
    },
    showMsg(level, msg){
        msgBox
            .stop()
            .children('div').removeClass().addClass(level)
            .children('span').text(msg)
            .end().end().slideDown(250, () => {
                clearTimeout(timeoutIdx)
                timeoutIdx = setTimeout(() => msgBox.slideUp(250), 1500)
            })
    }
}