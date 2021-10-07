<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.Calendar" %><%--
  Created by IntelliJ IDEA.
  User: win10
  Date: 2021/1/18
  Time: 19:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>每五秒刷新时间</title>
</head>
<body>
<%
    response.setIntHeader("Refresh",5);
    GregorianCalendar gregorianCalendar = new GregorianCalendar();
    String am_pm;
    int hour = gregorianCalendar.get(Calendar.HOUR);
    int minute = gregorianCalendar.get(Calendar.MINUTE);
    int second = gregorianCalendar.get(Calendar.SECOND);
    if(gregorianCalendar.get(Calendar.AM_PM)==0){
        am_pm="AM";
    }else {
        am_pm="PM";
    }
    String CT=hour+":"+minute+":"+second+" "+am_pm;
    %>
    <%= "当前时间："+CT%>
</body>
</html>
