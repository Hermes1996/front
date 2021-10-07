<%@ page import="java.io.PrintStream" %><%--
  Created by IntelliJ IDEA.
  User: win10
  Date: 2021/1/23
  Time: 10:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title> 读取 Cookie</title>
</head>
<body>
<%
    Cookie[] cookies = request.getCookies();
    PrintStream ps = new PrintStream(System.out, true, "gbk");
    if(null!=cookies){
        ps.println("显示所有cookis");
        for (int i = 0; i < cookies.length; i++)
        {
            Cookie cookie = cookies[i];
            ps.println("参数名："+cookie.getName());
            ps.println("参数值："+cookie.getValue());
            ps.println("------------------------------");
        }
    }else{
        ps.println("没有发现cookie");
    }


%>
</body>
</html>
