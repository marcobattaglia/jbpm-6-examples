<html>
<head>
<title>Apertura Pratiche di Credito</title>
</head>
<body>
<p>Apertura Pratiche di Credito</p>
<p><%= request.getAttribute("message") == null ? "" : request.getAttribute("message") %></p>
<ul>
<li><a href="startProcess.jsp">Apri pratica</a></li>
<li><a href="task?user=erics&cmd=list">Task List</a></li>

</ul>
</body>
</html>