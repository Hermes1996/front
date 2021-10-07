<%--
  Created by IntelliJ IDEA.
  User: win10
  Date: 2021/1/23
  Time: 11:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>上传文件</title>
</head>
<body>
<form action="/TomcatTest/UploadFileServlet" method="post" enctype="multipart/form-data">
    选择一个文件：<input type="file" name="uploadFile"/>
    <br>
    <input type="submit" value="上传"/>
</form>
</body>
</html>
