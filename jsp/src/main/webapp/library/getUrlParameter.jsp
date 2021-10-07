<%--
  Created by IntelliJ IDEA.
  User: win10
  Date: 2021/1/18
  Time: 20:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<html>
<head>
    <title>get方法传参</title>
</head>
<body>
<%--需要在网站后面加?name=菜鸟教程&url=http://ww.runoob.com--%>
<%
//    request的中文乱码
    String name=new String ((request.getParameter("name")).getBytes("ISO-8859-1"),"utf-8");%>
<p>站点名：<%=name%></p>
<p>网址：<%=request.getParameter("url")%></p>
</body>
</html>
