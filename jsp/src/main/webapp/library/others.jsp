<%@ page import="java.io.PrintStream" %>
<%@ page import="java.util.Date" %><%--
  Created by IntelliJ IDEA.
  User: win10
  Date: 2021/1/11
  Time: 19:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<html>
<head>
    <title>others</title>
</head>
<body>
<%
    ps.println("你的IP地址：" + request.getRemoteAddr());
    ps.println("今天的时期：" + (new Date()).toLocaleString());
%>
</body>
</html>
