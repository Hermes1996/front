<%--
  Created by IntelliJ IDEA.
  User: win10
  Date: 2021/1/9
  Time: 9:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>%的使用</title>
</head>
<body>
<%! int n=1;%>
<% int m=7;%>
<% int n=8;%>
<h1><%  System.out.println("n:"+n);%></h1>
<h1><%  System.out.println("m:"+m);%></h1>
<h1><%= m%></h1>
<h1><%= n%></h1>
<h1><%= this.n%></h1><%--全局--%>
</body>
</html>
