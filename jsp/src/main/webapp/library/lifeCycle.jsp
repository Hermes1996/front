<%@ page import="java.nio.charset.Charset" %>
<%@ page import="java.io.PrintStream" %>
<%@ page import="java.io.UnsupportedEncodingException" %><%--
  Created by IntelliJ IDEA.
  User: win10
  Date: 2021/1/9
  Time: 9:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<html>
<head>
    <title>life.jsp</title>
</head>
<body>

<%!
    private int initVar=0;
    private int serviceVar=0;
    private int destroyVar=0;
%>

<%!

    public void jspInit()  {
        initVar++;
        PrintStream ps1 = null;
        try {
            ps1 = new PrintStream(System.out,true,"gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ps1.println("jspInit(): JSP被初始化了"+initVar+"次");
    }
    public void jspDestroy()  {
        destroyVar++;
        PrintStream ps2 = null;
        try {
            ps2 = new PrintStream(System.out,true,"gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ps2.println("jspDestroy(): JSP被销毁了"+destroyVar+"次");
    }
%>

<%
    serviceVar++;
    PrintStream ps = new PrintStream(System.out,true,"gbk");
    ps.println("_jspService(): JSP共响应了"+serviceVar+"次请求");
    System.out.println(Charset.defaultCharset());

    String content1="初始化次数 : "+initVar;
    String content2="响应客户请求次数 : "+serviceVar;
    String content3="销毁次数 : "+destroyVar;
%>
<h1>Jsp生命周期</h1>
<p><%=content1 %></p>
<p><%=content2 %></p>
<p><%=content3 %></p>

</body>
</html>