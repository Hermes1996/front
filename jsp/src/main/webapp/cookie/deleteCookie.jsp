<%--
  Created by IntelliJ IDEA.
  User: win10
  Date: 2021/1/23
  Time: 10:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>删除cookie</title>
</head>
<body>
<%
    Cookie cookie = null;
    Cookie[] cookies = null;
    // 获取当前域名下的cookies，是一个数组
    cookies = request.getCookies();
    if( cookies != null ){
        for (int i = 0; i < cookies.length; i++)
        {
            cookie = cookies[i];
            if ((cookie.getName()).compareTo("name") == 0)
            {
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }else{
        System.out.println("There is no cookies of web");
    }
%>
</body>
</html>
