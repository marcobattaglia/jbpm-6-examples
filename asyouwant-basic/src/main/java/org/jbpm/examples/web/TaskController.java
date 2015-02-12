/**
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.examples.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.jbpm.examples.ejb.TaskLocal;

import com.redhat.ruledemo.model.Info;

@Model
public class TaskController {

    @EJB
    TaskLocal taskBean;

    @Inject
    FacesContext facesContext;

    @Inject
    Logger logger;

    private String comment;
    private Map<String,Object> content;
    private Info info;
    private long taskId;
    private String user;
     private List<Info> infos;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public void setContent(Map<String, Object> content) {
        this.content = content;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

 
    public List<Info> getInfos() {
    	return infos;
       
    }
    
    

   
    public void retrieveInfo() {
        String message;
        try {
            infos = taskBean.getInfos(taskId);
            message = "Retrieved " + infos.size();
            logger.info(message);
        } catch (Exception e) {
            message = "Cannot retrieve task list for user " + user + ".";
            logger.log(Level.SEVERE, message, e);
            facesContext.getExternalContext().getFlash()
                    .put("msg", message);
        }
    }

    

    public String approveTask() {
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
        facesContext.getExternalContext().getFlash()
                .put("msg", message);
        return "index.xhtml?faces-redirect=true";
    }
}
