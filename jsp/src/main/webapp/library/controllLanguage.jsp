<%--
  Created by IntelliJ IDEA.
  User: win10
  Date: 2021/1/11
  Time: 20:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%! int day = 2;%>
<html>
<head>
    <title>控制语句</title>
</head>
<body>
<h1>if-else</h1>
<% if (day == 6 | day == 7) { %>
    <%= "今天星期" + this.day + "是周末"%>
<% } else { %>
    <%= "今天星期" + this.day + "不是周末"%>
<% } %>
<h1>For循环</h1>
<% for (int i = 0; i < this.day; i++) {%>
<font color="#7fffd4" size="<%= i*2%>">
    <%= "打印" + i%>
</font>
<br>
<% }%>
<h1>while循环</h1>
<%  int i=0;
    while(i<day){%>
<font color="#7fffd4" size="<%= i*2%>">
    <%= "打印" + i%>
</font>
<br>
<% i++ ;}%>
</body>
</html>
