<%--
  Created by IntelliJ IDEA.
  User: win10
  Date: 2021/1/19
  Time: 20:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>从复选框中读取数据</title>
</head>
<body>
<ul>
    <li>
        <p>Google被选中：
        <%=request.getParameter("google")%></p>
    </li>
    <li>
        <p>firefox被选中：
        <%=request.getParameter("firefox")%></p>
    </li>
    <li>
        <p>xx被选中：
        <%=request.getParameter("xx")%></p>
    </li>
</ul>
没有/没选中都为null
</body>
</html>
