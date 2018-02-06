<%--
  Created by IntelliJ IDEA.
  User: symsimmy
  Date: 2018/1/31
  Time: 15:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<button id="test" type="button">测试</button>
<div id="thelist">

</div>
<script type="text/javascript" src="/js/jquery.min.js"></script>
<script type="text/javascript" src="/js/jquery.fileDownload.js"></script>
<script type="text/javascript">
    $(document).ready(function () {
        $("button").click(function () {
            // asynctask();
            download();
        });
    });


    function download(){
        var url="/download";
        $.fileDownload(url,{
            httpMethod: 'POST',
            data:{
                "fileName":"demo.article_12071234_900000.csv"
            },
            prepareCallback:function(url){
//             common.layer.msg("下载开始，请稍等！");
                $("#thelist").append('<p>下载开始，请稍等!</p>');
            },
            successCallback:function(url){
                $("#thelist").append('<p>下载成功</p>');
            },
            failCallback: function (html, url) {
                var json = JSON.parse(html);
                console.log(json);
                $("#thelist").append('<p>下载失败</p>');
            }
        });
    }

    function asynctask() {
        $.ajax({
            type: "get",
            url: "/asynctask",
            cache: false,
            async: true,
            dataType: "text",
            timeout: 5000,
            success: function (XMLHttpRequest, textStatus, errorThrown) {
                console.log("success asynctask");
                console.log(XMLHttpRequest.readyState + XMLHttpRequest.status + XMLHttpRequest.responseText);
                console.log(textStatus + errorThrown);
                $("#thelist").append('<p>诶哟,还挺快</p>');
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                console.log("failed asynctask");
                console.log(XMLHttpRequest.readyState + XMLHttpRequest.status + XMLHttpRequest.responseText);
                console.log(textStatus + errorThrown);
                $("#thelist").append('<p>还没做完呢,急啥子,等着</p>');
                checkAsyncTaskCompleted();
            }
        });
    }

    function checkAsyncTaskCompleted() {
        $.ajax({
            type: "get",
            url: "/check",
            cache: false,
            async: true,
            dataType: "text",
            timeout: 5000,
            success: function (XMLHttpRequest, textStatus, errorThrown) {
                console.log("success checkAsyncTaskCompleted");
                console.log(XMLHttpRequest);
                console.log(XMLHttpRequest.readyState + XMLHttpRequest.status + XMLHttpRequest.responseText);
                console.log(textStatus + errorThrown);
                $("#thelist").append('<p>呼,终于做完了,累死我了!</p>');
            },
            error:function (XMLHttpRequest, textStatus, errorThrown) {
                console.log("failed checkAsyncTaskCompleted");
                console.log(XMLHttpRequest.readyState + XMLHttpRequest.status + XMLHttpRequest.responseText);
                console.log(textStatus + errorThrown);
                $("#thelist").append('<p>还没做完呢,急啥子,再等等</p>');
                checkAsyncTaskCompleted();
            }
        });
    }
</script>
</body>
</html>
