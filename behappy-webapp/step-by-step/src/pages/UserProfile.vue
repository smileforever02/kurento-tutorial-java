<template>
    <div class="full-width center-content">
        <form class="full-width page-content" v-on:submit.prevent="submit" v-on:click="formItemClick" v-on:change="onchange" v-on:focusout="formItemBlur">
            <div class="form-group row list-item user-img">
                <label for="photo" class="col-xs-3 col-form-label">photo</label>
                <img class="col-xs-9 align-right" :src="userImg" v-on:click.stop="editPhoto" alt="">
            </div>
            <div class="form-group row list-item">
                <label for="nickName" class="col-xs-3 col-form-label">nickName</label>
                <span v-if="nickNameEditable === false" class="col-xs-9 align-right" data-editfor="nickName">{{nickName}}</span>
                <input v-if="nickNameEditable === true" class="col-xs-9" id="nickName" name="nickName" data-editdone="nickName" v-model="nickName">
            </div>
            <div class="form-group row list-item">
                <label for="phoneNumber" class="col-xs-3 col-form-label">phoneNumber</label>
                <span v-if="phoneNumberEditable === false" class="col-xs-9 align-right" data-editfor="phoneNumber">{{phoneNumber}}</span>
                <input v-if="phoneNumberEditable === true" class="col-xs-9" id="phoneNumber" name="phoneNumber" data-editdone="phoneNumber" v-model="phoneNumber">
            </div>
            <div class="form-group row list-item">
                <label for="email" class="col-xs-3 col-form-label">email</label>
                <span v-if="emailEditable === false" class="col-xs-9 align-right" data-editfor="email">{{email}}</span>
                <input v-if="emailEditable === true" class="col-xs-9" id="email" name="email" data-editdone="email" v-model="email">
            </div>
        </form>
    </div>
</template>

<script>
import MessageBox from '../services/MessageBox'
import Services from '../services/Services'

export default {
    data(){
        return {
            nickNameEditable: false,
            phoneNumberEditable: false,
            emailEditable: false,
            userImg: '',
            nickName: '',
            phoneNumber: '',
            email: ''
        };
    },
    mounted(){
        this.loadUserInfo();
    },
    methods:{
        loadUserInfo(){
            let userId = this.$store.state.logonUser;
            Services.getUser(userId)
                .done(user => {
                    this.userImg = user.photo || Services.getDefPhoto();
                    this.nickName = user.nickName;
                    this.phoneNumber = user.phoneNumber;
                    this.email = user.email;
                }).fail(error => MessageBox.error('Load user information error.'));
        },
        editPhoto(evt){
            let that = this;
            var dialog = new BootstrapDialog({
                title: 'Upload Photo',
                closable: false,
                message: function(dialogRef){
                    var $message = $('<div class="row"><input class="col-xs-12" type="file" accept="image/png, image/jpeg" placeholder="upload your photo"></div>');
                    return $message;
                },
                buttons: [{
                    label: 'Cancel',
                    cssClass: 'btn-warning',
                    action: function(dialog) {
                        dialog.close();
                    }
                }, {
                    label: 'OK',
                    cssClass: 'btn-success',
                    action: function(dialog) {
                        let file = dialog.$modalBody[0].querySelector('input').files[0];
                        if(file){
                            Services.uploadPhoto(file)
                                .done(() => {
                                    MessageBox.success('Upload successfully.');
                                    that.loadUserInfo();
                                }).fail(() => MessageBox.error('Failed to upload photo'))
                                .always(() => dialog.close());
                        }else{
                            MessageBox.warn('You should choose a photo from your device.')
                        }
                    }
                }]
            });
            dialog.open();
        },
        enableEditNickName(){
            this.editNickName = true;
            this.$nextTick(() => document.querySelector('#nickName').focus());
        },
        saveNickName(){
            this.editNickName = false;
        },
        formItemClick(evt){
            let editFor = evt.target.dataset.editfor;
            if(editFor && this[editFor + 'Editable'] === false){
                this[editFor + 'Editable'] = true;
                this.$nextTick(() => document.querySelector('#' + editFor).focus());
            }
        },
        onchange(evt){
            let edirDone = evt.target.dataset.editdone;
            if(edirDone && this[edirDone + 'Editable'] === true){
                Services.updateUser({
                    userId: this.$store.state.logonUser,
                    nickName: this.nickName,
                    phoneNumber: this.phoneNumber,
                    email: this.email
                }).done(() => MessageBox.success('Update successfully'))
                .fail(() => MessageBox.error('Failed to update information.'));
            }
        },
        formItemBlur(evt){
            let edirDone = evt.target.dataset.editdone;
            if(edirDone && this[edirDone + 'Editable'] === true){
                this[edirDone + 'Editable'] = false;
            }
        },
        // onchange(evt){
        //     console.log('onchange')
        //     console.log(evt.target)
        // },
        submit(){
            console.log('nothing to do here')
        },
        addFriend(){
            this.$router.push('/user-list')
        }
    }
}
</script>
