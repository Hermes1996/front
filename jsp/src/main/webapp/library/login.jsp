<%--
  Created by IntelliJ IDEA.
  User: win10
  Date: 2021/1/22
  Time: 14:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登入</title>
</head>
<body>
<form action="../forward/loginIn.jsp" method="post">
    用户名：<input type="text" name="username"/><br>
    密码：<input type="password" name="password"/><br>
    <input type="submit" name="登入">
</form>
</body>
</html>
