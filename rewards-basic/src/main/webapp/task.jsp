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
        nextStepUI = document.getElementById("nextStepUI");
		for(i=0;i<nextStepUI.options.length;i++){
			if(nextStepUI.options[i].selected)
				{nextStep = nextStepUI.options[i].value}
		}
        window.location.assign("task?user="+user+"&taskId="+taskId+"&cmd=approve&nextStep="+nextStep);
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
<th>Action</th>
</tr>
<% for (TaskSummary task : (List<TaskSummary>)request.getAttribute("taskList")) { %>
<tr>
<td><%= task.getName() %></td>
<td><%= task.getId() %></td>
<td><%= task.getProcessInstanceId() %></td>
<td>Go to: <select id="nextStepUI">
				<option>1</option>
				<option>2</option>
				<option>3</option>
				<option>4</option>
				<option value="4">default</option>
		   </select>
<td><a href="javascript:letsgo('<%= user %>',<%= task.getId() %>)">Let's go next</a></td>
</tr>
<% } %>
</table>
</body>
</html>