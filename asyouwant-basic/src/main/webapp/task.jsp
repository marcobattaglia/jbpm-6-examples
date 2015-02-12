<%@ page import="org.kie.api.task.model.TaskSummary" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
<title>Task management</title>
</head>
<script type="text/javascript">
function letsgo(user, taskId){
		var nextStep = 0;
        
        window.location.assign("CompleteTask?user="+user+"&taskId="+taskId);
}
</script>
<body>
<% String user = request.getParameter("user"); %>
<p><%= user %>'s Task</p>
<table border="1">
<tr>
<th>Task Name</th>
<th>Task Id</th>
<th>ProcessInstance Id</th>
<th>Work</th>
</tr>
<% for (TaskSummary task : (List<TaskSummary>)request.getAttribute("taskList")) { %>
<tr>
<td><%= task.getName() %></td>
<td><%= task.getId() %></td>
<td><%= task.getProcessInstanceId() %></td>
<td><a href="javascript:letsgo('<%= user %>',<%= task.getId() %>)">Work it</a></td>
</tr>
<% } %>
</table>
</body>
</html>