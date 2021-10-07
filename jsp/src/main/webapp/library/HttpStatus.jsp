<%--
  Created by IntelliJ IDEA.
  User: win10
  Date: 2021/1/18
  Time: 20:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>HTTP状态码</title>
</head>
<body>
<%
    // 设置错误代码，并说明原因
    response.sendError(407, "Need authentication!!!" );
%>
</body>
</html>
