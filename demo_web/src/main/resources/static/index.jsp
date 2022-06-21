<!DOCTYPE html>
<meta charset="utf-8">
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"
    import = "java.net.*, java.io.*"%>
<html>
<head>
<%

	String address="http://localhost:8081/api/signup";
	URL url = new URL(address);
	URLConnection conn = url.openConnection();

	
%>
<title>Insert title here</title>
</head>
<body>

<form action="<%=address%>" method="post">
name: <input type="text" name="name"> <br> 
password: <input type="password" name="pw"> <br> 
nickname: <input type="text" name="nickname"> <br> 

<input type="submit" value ="서버로 제출하기">

</form>
</body>
</html>