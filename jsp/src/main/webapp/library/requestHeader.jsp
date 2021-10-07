<%@ page import="java.util.Enumeration" %><%--
  Created by IntelliJ IDEA.
  User: Hermes
  Date: 2021/1/17
  Time: 19:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>客户请求</title>
</head>
<body>
<table  width="100%" border="1" align="center">
    <tr  bgcolor="#949494"><th>Header Name</th><th>Header values</th></tr>
    <%
        Enumeration headerNames=request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String paramName = (String) headerNames.nextElement();%>
    <tr><th><%=paramName%></th>
        <%        String paramValue=request.getHeader(paramName);%>
        <th><%=paramValue%></th></tr>
    <%   }
    %>
</table>

</body>
</html>
