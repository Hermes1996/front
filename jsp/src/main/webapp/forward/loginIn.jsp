<%--
  Created by IntelliJ IDEA.
  User: win10
  Date: 2021/1/22
  Time: 15:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>不希望用户直接进入成功页面</title>
</head>
<body>
<%
    String username=request.getParameter("username");
    String pwd=request.getParameter("password");
    System.out.println(request.getParameter("username"));
    if("admin".equals(username)&&"admin".equals(pwd)){
//    	request.getSession().setAttribute("username",username);等同于
    	session.setAttribute("username",username);
//    	保存一个用户名各个页面共享
    	response.sendRedirect("success.jsp");
    }else{
    	response.sendRedirect("fail.jsp");
    }

%>
</body>
</html>
