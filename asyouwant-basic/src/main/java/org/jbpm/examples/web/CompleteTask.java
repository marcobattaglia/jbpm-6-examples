package org.jbpm.examples.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.examples.ejb.TaskLocal;

import com.redhat.ruledemo.model.Info;

/**
 * Servlet implementation class CompleteTask
 */
public class CompleteTask extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 @EJB
	    TaskLocal taskBean;
	 
	 @Inject
	    Logger logger;
   
    public CompleteTask() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		 String taskId = req.getParameter("taskId");
	        String user = req.getParameter("user");
	        approveTask(Long.parseLong(taskId), user);
	        
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
	
	public void approveTask(long taskId, String user) {
        String message;
        try {
            Map<String,Object> result = new HashMap<String,Object>();
            HashMap info_out = new HashMap();
//            for(Info info: infos){
//            	info_out.put(info.getType(), info);
//            	System.out.println("Info: " + info.getContent() + "  " + info.getStatus());
//            }
            Info i = new Info();
            i.setContent("CI99999");
            i.setType("CI");
            info_out.put(i.getType(), i);
            result.put("info_out", info_out);
            taskBean.approveTask(user, taskId, result);
            message = "The task " + taskId + " has been successfully approved.";
            logger.info(message);
        } catch (Exception e) {
            message = "Unable to approve the task " + taskId + ".";
            logger.log(Level.SEVERE, message, e);
        }
        
       
    }

}
