<%--
  Created by IntelliJ IDEA.
  User: symsimmy
  Date: 2018/1/30
  Time: 15:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>上传测试</title>
    <link rel="shortcut icon" href="http://fex-team.github.io/webuploader/images/favicon.ico">
    <link rel="stylesheet" type="text/css" href="/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/css/bootstrap-theme.min.css">
    <link rel="stylesheet" type="text/css" href="/css/font-awesome.min.css">
    <link rel="stylesheet" type="text/css" href="/css/syntax.css">
    <link rel="stylesheet" type="text/css" href="/css/style.css">

    <link rel="stylesheet" type="text/css" href="/css/webuploader.css">
</head>
<body>
<div class="page-body">
    <div id="post-container" class="container">
        <div class="row">
            <div class="col-md-9">
                <div class="page-container">
                    <div id="uploader" class="wu-example">
                        <div class="btns">
                            <div id="picker">选择文件</div>
                            <button id="ctlBtn" class="btn btn-default">开始上传</button>
                        </div>
                        <!--用来存放文件信息-->
                        <div id="thelist" class="uploader-list"></div>

                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


<script type="text/javascript" src="/js/jquery.min.js"></script>
<script type="text/javascript" src="/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/js/webuploader.min.js"></script>
<script type="text/javascript" src="/js/md5.js"></script>

<script type="text/javascript">
    var md5File;
    //监听分块上传过程中的时间点
    WebUploader.Uploader.register({
        "before-send-file": "beforeSendFile",  // 整个文件上传前
        "before-send": "beforeSend",  // 每个分片上传前
        "after-send-file": "afterSendFile"  // 分片上传完毕
    }, {
        //时间点1：所有分块进行上传之前调用此函数 ，检查文件存不存在
        beforeSendFile: function (file) {
            var deferred = WebUploader.Deferred();
            md5File = hex_md5(file.name + file.size);//根据文件名称，大小确定文件唯一标记，这种方式不赞成使用
            $.ajax({
                type: "POST",
                url: "/checkFile",
                data: {
                    md5File: md5File, //文件唯一标记
                },
                async: false,  // 同步
                dataType: "json",
                success: function (response) {
                    if (response) {  //文件存在，跳过 ，提示文件存在
                        $('#' + file.id).find('p.state').text("文件存在");
                    } else {
                        deferred.resolve();  //文件不存在或不完整，发送该文件
                    }
                }
            }, function (jqXHR, textStatus, errorThrown) { //任何形式的验证失败，都触发重新上传
                deferred.resolve();
            });
            return deferred.promise();
        },
        //时间点2：如果有分块上传，则每个分块上传之前调用此函数  ，判断分块存不存在
        beforeSend: function (block) {
            var deferred = WebUploader.Deferred();
            $.ajax({
                type: "POST",
                url: "/checkChunk",
                data: {
                    md5File: md5File,  //文件唯一标记
                    chunk: block.chunk,  //当前分块下标
                },
                dataType: "json",
                success: function (response) {
                    if (response) {
                        deferred.reject(); //分片存在，跳过
                    } else {
                        deferred.resolve();  //分块不存在或不完整，重新发送该分块内容
                    }
                }
            }, function (jqXHR, textStatus, errorThrown) { //任何形式的验证失败，都触发重新上传
                deferred.resolve();
            });
            return deferred.promise();
        },
        //时间点3：分片上传完成后，通知后台合成分片
        afterSendFile: function (file) {
            var chunksTotal = Math.ceil(file.size / (5 * 1024 * 1024));
            if (chunksTotal >= 1) {
                //合并请求
                var deferred = WebUploader.Deferred();
                $.ajax({
                    type: "POST",
                    url: "/merge",
                    data: {
                        name: file.name,
                        md5File: md5File,
                        chunks: chunksTotal
                    },
                    cache: false,
                    async: false,  // 同步
                    dataType: "json",
                    success: function (response) {
                        if (response) {
                            $('#' + file.id).find('p.state').text('上传成功');
                            $('#' + file.id).find('.progress').fadeOut();
                        } else {
                            $('#' + file.id).find('p.state').text('合并出错');
                            deferred.reject();
                        }
                    }
                });
                return deferred.promise();
            }
        }
    });

    var uploader = WebUploader.create({
        auto: true,// 选完文件后，是否自动上传。
        swf: '/js/Uploader.swf',// swf文件路径
        server: '/upload',// 文件接收服务端。
        pick: '#picker',// 选择文件的按钮。可选。
        chunked: true,//开启分片上传
        chunkSize: 5 * 1024 * 1024,//5M
        chunkRetry: 3,//错误重试次数
        multiple: false,
        fileNumLimit: 1
    });

    //上传添加参数
    uploader.on('uploadBeforeSend', function (obj, data, headers) {
        data.md5File = md5File;
    });

    // 当有文件被添加进队列的时候
    uploader.on('fileQueued', function (file) {
        //$("#picker").hide();//隐藏上传框
        $("#thelist").append('<div id="' + file.id + '" class="item">' +
            '<h4 class="info">' + file.name + '</h4>' +
            '<p class="state"></p>' +
            '</div>');
    });

    // 文件上传过程中创建进度条实时显示。
    uploader.on('uploadProgress', function (file, percentage) {
        var $li = $('#' + file.id),
            $percent = $li.find('.progress .progress-bar');

        // 避免重复创建
        if (!$percent.length) {
            $percent = $('<div class="progress progress-striped active">' +
                '<div class="progress-bar" role="progressbar" style="width: 0%"></div>' +
                '</div>').appendTo($li).find('.progress-bar');
        }
        $li.find('p.state').text('上传中....');
        $percent.css('width', percentage * 100 + '%');
    });


</script>
</body>
</html>
