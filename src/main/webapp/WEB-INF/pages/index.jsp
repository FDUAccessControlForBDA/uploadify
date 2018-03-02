<%--
  Created by IntelliJ IDEA.
  User: symsimmy
  Date: 2018/2/5
  Time: 14:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en" ng-app="lufiApp">
<head>
    <meta charset="UTF-8">
    <title>LUFI</title>
    <link rel="stylesheet" href="/lib/bootstrap-3.3.5/css/bootstrap.min.css">
    <link rel="stylesheet" href="/lib/font-awesome/font-awesome.min.css">
    <link rel="stylesheet" href="/lib/webuploader/webuploader.css">
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/main.css">
    <link rel="stylesheet" href="/css/body.css">

    <script src="/lib/jquery-3.1.1.js"></script>
    <script src="/lib/angular/angular.min.js"></script>
    <script src="/lib/angular-ui-router/angular-ui-router.js"></script>
    <script src="/lib/bootstrap-3.3.5/js/bootstrap.min.js"></script>
    <script src="/lib/webuploader/webuploader.min.js"></script>
    <script src="/lib/md5.js"></script>
    <script src="/js/jquery.fileDownload.js"></script>
    <script src="/js/app.js"></script>
    <script src="/js/controllers/main-controller.js"></script>
    <script src="/js/controllers/body-controller.js"></script>
</head>
<body>
<nav class="navbar my-navbar" role="navigation">
    <div class="container-fluid">
        <a href="#main" class="a-title">
            <h1><span class="glyphicon glyphicon-cloud-upload" style="float: left; line-height: 18px;">LUFI&nbsp;</span></h1>
        </a>
        <div class="lufi-subtitle">
            <div>Privacy Data Detector</div>
            <a href="http://crypto.fudan.edu.cn/people/weili/" class="a-title">复旦大学数据分析与安全实验室</a>
        </div>
        <div class="lufi-nav-right">
            <ul class="nav navbar-nav">
                <li class="active"><a href="#main" class="a-title">首页</a></li>
                <li><a href="#law" class="a-title">法律</a></li>
                <li><a href="#privacy" class="a-title">隐私</a></li>
                <li><a href="#about" class="a-title">关于</a></li>
                <li><a href="#contact" class="a-title">联系我们</a></li>
            </ul>
        </div>
    </div>
</nav>
<div id="main">
    <div ui-view=""></div>
</div>
<footer class="modal-footer footer">Copyright © 2018 Fudan University. All Rights Reserved.</footer>
</body>
</html>
