<%@ page import="java.net.URLEncoder" %><%--
  Created by IntelliJ IDEA.
  User: win10
  Date: 2021/1/23
  Time: 9:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>保存cookie</title>
</head>
<body>
<%
    //    request的中文乱码
    String name = URLEncoder.encode(request.getParameter("name"), "utf-8");
//    设置name和url cookie
    Cookie nameCookie = new Cookie("name", name);
    Cookie urlCookie = new Cookie("url", request.getParameter("url"));
//    设置cookie过期时间,单位秒

    nameCookie.setMaxAge(60*60*24);
    urlCookie.setMaxAge(60*60*24);
//    在响应头添加cookie
    response.addCookie(nameCookie);
    response.addCookie(urlCookie);
%>
<ul>
    <li>
<%--        加<p></p>前端页面网站名会在页面布局下移一行--%>
        <p>网站名：<%=request.getParameter("name")%></p>
    </li>
    <li>
        url：<%=request.getParameter("url")%>
    </li>
</ul>
</body>
</html>
