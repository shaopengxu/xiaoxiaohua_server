var YT = {
    getParameterByName: function(name) {
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
        return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    },
    initForm: function(config) {
        var formElements = config.formElements; 
        var type = config.type; 
        var onFinish = config.onFinish; 
        var customActionMap = config.customActionMap;
        var confMap = config.confMap;
        var channelConfig = config.channelConfig;

        formElements.empty();
        $.getJSON(
            '/conf/listAsConfigItem.json',
            {'confGroup': config.type},
            function(result) {
                var confs = result.data;
                console.log('form confs', confs);
                $.each(confs, function(i, conf) {
                    confMap[conf.code] = conf;
                    var template = $('.template-' + conf.type);
                    var ele = template.clone();
                    ele.removeClass().addClass('form-group');
                    ele.attr('data-code', conf.code);
                    $('label', ele).html(conf.name);
                    if(conf.required) {
                        ele.addClass('required');
                        $('label.control-label', ele).append($('<span class="text-danger">*<span>'));
                    }
                    if(conf.tip) {
                        $('.help-block', ele).html(conf.tip);
                    }
                    switch(conf.type) {
                        case 'pass':
                            break;
                        case 'string':
                        case 'int':
                        case 'counter':
                            $('input', ele).attr('name', conf.code);
                            break;
                        case 'date':
                            $('input', ele).attr('name', conf.code);
                            $('input', ele).datepicker({
                                dateFormat: "yy-mm-dd"
                            });
                            break;
                        case 'datetime':
                            $('input', ele).attr('name', conf.code);
                            $('input', ele).datetimepicker({
                                //controlType: 'select',
                                minDate: "0",
                                dateFormat: "yy-mm-dd",
                                timeFormat: 'HH:mm:ss'
                            });
                            break;
                        case 'videoHtml':
                            $('input', ele).attr('name', conf.code);
                            $('.link-result', ele).addClass('link-result-' + conf.code);
                            $('input', ele).on('change', function(e) {
                                $('.link-result', ele).attr('href', '/redis/get12?key=feilv_url_' + $(this).val());
                            });
                            break;
                        case 'text':
                            $('textarea', ele).attr('name', conf.code);
                            break;
                        case 'video':
                            $('input', ele).attr('name', conf.code);
                            break;
                        case 'url':
                            $('input[type=file]', ele).attr('name', conf.code + 'File');
                            $('input[type=hidden]', ele).attr('name', conf.code);
                            $('img', ele).addClass('img-' + conf.code);
                            $('a', ele).addClass('a-' + conf.code);
                            break;
                        case 'image':
                            $('input[type=hidden]', ele).attr('name', conf.code);
                            $('input[type=file]', ele).change(function(event) {
                                var _ele = ele;
                                YT.sendFile(type, this.files[0], function(url) {
                                    $('img', _ele).attr('src', url);
                                    $('a', _ele).attr('href', url);
                                    $('input[type=hidden]', _ele).val(url);
                                });
                            });
                            break;
                        case 'sel':
                            var tmpOptions = JSON.parse(conf.options);
                            console.log('tmpOptions ' + conf.code, tmpOptions);
                            var tmpSel = $('select', ele);
                            tmpSel.attr('name', conf.code);
                            var tmpVal = '';
                            $.each(tmpOptions, function(i, e) {
                                var op = $('<option>');
                                op.attr('value', e.code);
                                op.text(e.name);
                                if(e['default']) {
                                    tmpVal = e.code;
                                }
                                op.appendTo(tmpSel);
                            });
                            if(tmpVal) {
                                tmpSel.val(tmpVal);
                            }
                            break;
                        case 'checkbox':
                            var ccc = $('div.input-container', ele);
                            $('<input />', { type: 'hidden', name: conf.code}).appendTo(ccc);
                            var tmpOptions = JSON.parse(conf.options);
                            console.log('tmpOptions:', tmpOptions);
                            $.each(tmpOptions, function(i, e) {
                                var tmpId = conf.code + '_' + e.code;
                                $('<input />', { type: 'checkbox', id: tmpId, name: conf.code + '_checkbox', 'checked': '', value: e.code}).data('c',e.code).appendTo(ccc);
                                $('<label />', { 'for': tmpId, text: e.name }).css('margin-right','8px').appendTo(ccc);
                            });
                            break;
                        case 'channel':
                            var ccc = $('div', ele);
                            console.log('channelConfig:', channelConfig);
                            $.each(channelConfig, function(i, e) {
                                $('<input />', { type: 'checkbox', id: 'channel_' + e.code, name: 'channel_c', 'checked': 'checked'}).data('c',e.code).appendTo(ccc);
                                var l = $('<label />', { 'for': 'channel_' + e.code, text: e.name }).css('margin-right','8px').appendTo(ccc);
                            });
                            break;
                        case 'videoRef':
                            $('input', ele).attr('name', conf.code);
                            $('.btn-view', ele).addClass('btn-view-' + conf.code);
                            var selBtn = $('.btn-sel', ele);
                            selBtn.click(function(evt) {
                                popupVideoRef(conf.code);
                            });
                            break;
                        default:
                            if(customActionMap && customActionMap[conf.type]) {
                                customActionMap[conf.type](conf);
                            }
                            break;
                    }
                    ele.appendTo(formElements);
                });
                if(onFinish) {
                    onFinish();
                }
            }
        );
    },
    populateForm: function(config) {
        var api = config.api;
        var data = config.data;
        var confMap = config.confMap;
        var onFinish = config.onFinish; 
        var allChannelVal = config.allChannelVal;
        $.getJSON(
            api,
            data,
            function(result) {
                console.log(api, result);
                for(var p in result) {
                    var conf = confMap[p];
                    var ele = $('[data-code="' + p + '"]');
                    var val = result[p];
                    var input = $('input[name=' + p + ']');
                    var hasSet = 0;
                    if(input && input.length) {
                        input.val(val);
                    }
                    if(!hasSet) {
                        input = $('textarea[name=' + p + ']');
                        if(input && input.length) {
                            input.val(val);
                            hasSet = 1;
                        }
                    }
                    if(!hasSet) {
                        input = $('select[name=' + p + ']');
                        if(input && input.length) {
                            input.val(val);
                            hasSet = 1;
                        }
                    }
                    if(!hasSet) {
                        input = $('input[name=' + p + '_checkbox]');
                        if(input && input.length) {
                            input.each(function(){
                                //console.log('a', $(this).prop("name"));
                                $(this).prop("checked", false);
                            });
                            var vs = val.split(",");
                            //console.log('checkbox', p, vs.length);
                            for(var i=0;i < vs.length; i ++){
                                $("#"+p +"_"+vs[i]).prop("checked", true);
                            }
                        }
                    }
                    if(conf && conf.type == 'url') {
                        $('.img-' + conf.code).attr('src', val);
                        $('.a-' + conf.code).attr('href', val);
                    }
                    if(conf && conf.type == 'image') {
                        var url = val;
                        $('img', ele).attr('src', url);
                        $('a', ele).attr('href', url);
                        $('input[type=hidden]', ele).val(url);
                    }
                    if(p == 'channel') {
                        $('input[name=channel_c]').prop('checked', false);
                        $('input[name=channel_c]').each(function(i ,e) {
                            var cbox = $(e);
                            if((cbox.data('c') == allChannelVal && val == allChannelVal) 
                                || (cbox.data('c') != allChannelVal && val != allChannelVal && cbox.data('c') & val)) {
                                    cbox.prop('checked', true);
                                }
                        });
                    }
                }
                if(onFinish) {
                    onFinish(result);
                }
            }
        );
    },
    submitForm: function(config) {
        var api = config.api;
        var form = config.form;
        var onFinish = config.onFinish; 
        var allChannelVal = config.allChannelVal;

        var channelVal = 0;
        var isAllChannel = 0;
        $('input[name=channel_c]').each(function(i, e){
            var cbox = $(e);
            if(cbox.prop('checked')) {
                if($(e).data('c') == allChannelVal) {
                    isAllChannel = 1;
                }else {
                    channelVal |= $(e).data('c');
                }
            }
        });
        if(isAllChannel) {
            channelVal = allChannelVal;
        }
        console.log('channelVal', channelVal);
        $('input[name=channel]').val(channelVal ? channelVal:'');

        var tmpCheckboxValMap = {};
        $('input[type=checkbox]', mainForm).each(function(i, e){
            var cbox = $(e);
            var tmpName = cbox.attr('name');
            if(tmpName.endsWith('_checkbox') && cbox.prop('checked')) {
                var tmpRealName = tmpName.substring(0, tmpName.length - '_checkbox'.length);
                if(!tmpCheckboxValMap[tmpRealName]) {
                    tmpCheckboxValMap[tmpRealName] = [];
                }
                tmpCheckboxValMap[tmpRealName].push(cbox.val());
            }
        });
        for(var realName in tmpCheckboxValMap) {
            $('input[name=' + realName + ']', mainForm).val(tmpCheckboxValMap[realName].join(','));
        }
        var formData = new FormData(form);
        $.ajax({
            url: api,  //Server script to process data
            type: 'POST',
            xhr: function() {  // Custom XMLHttpRequest
                var myXhr = $.ajaxSettings.xhr();
                if(myXhr.upload){ // Check if upload property exists
                    myXhr.upload.addEventListener(
                        'progress', 
                        function (e){
                            if(e.lengthComputable){
                                //$('progress').attr({value:e.loaded,max:e.total});
                            }
                        }, 
                        false
                    ); // For handling the progress of the upload
                    return myXhr;
                }
            },
            //Ajax events
            //beforeSend: beforeSendHandler,
            success: function(result) {
                console.log(api, result);
                if(onFinish) {
                    onFinish(result);
                }
            },
            //error: errorHandler,
            // Form data
            data: formData,
            //Options to tell jQuery not to process data or worry about content-type.
            cache: false,
            contentType: false,
            processData: false
        });
    },
    sendFile: function(fileType, file, fnSuccess) {
        var data = new FormData();
        data.append('file', file);
        data.append('fileType', fileType);
        $.ajax({
            type: 'post',
            url: '/file/upload.json',
            data: data,
            cache: false,
            success: function (result) {
                // do something
                console.log('upload success', result);
                if(result.code != 0) {
                    alert('上传失败:' + result.errorMessage);
                    return;
                }
                if(fnSuccess) {
                    fnSuccess(result.data);
                }
            },
            processData: false,
            contentType: false
        });
    }
};
