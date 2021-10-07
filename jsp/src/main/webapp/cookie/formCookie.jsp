<%--
  Created by IntelliJ IDEA.
  User: win10
  Date: 2021/1/23
  Time: 9:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>提交表格保存在cookie</title>
</head>
<body>
<form action="saveCookie.jsp" method="get">
    网站名：<input type="text" name="name"/><br>
    网站:  <input type="url" name="url"/>
    <input type="submit" value="提交"/>
</form>
</body>
</html>
