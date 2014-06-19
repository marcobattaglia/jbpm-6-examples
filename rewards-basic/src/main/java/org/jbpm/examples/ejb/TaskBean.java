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

package org.jbpm.examples.ejb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.internal.task.api.TaskContentService;
import org.kie.internal.task.api.TaskQueryService;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class TaskBean implements TaskLocal {


    @Resource
    private UserTransaction ut;

    @Inject
    TaskService taskService;   
    
    public List<TaskSummary> retrieveTaskList(String actorId) throws Exception {
        ut.begin();        
        List<TaskSummary> list;        
        try {
            list = taskService.getTasksAssignedAsPotentialOwner(actorId, "en-UK");
            ut.commit();
        } catch (RollbackException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        System.out.println("retrieveTaskList by " + actorId);
        for (TaskSummary task : list) {
            System.out.println(" task.getId() = " + task.getId());
           
           getTaskData(taskService.getTaskById(task.getId()));
            
        }

        return list;
    }
    public Map<?, ?> getTaskData(Task task){
    	 Content content = taskService.getContentById(task.getTaskData().getDocumentContentId());
         Object result = ContentMarshallerHelper.unmarshall(content.getContent(), null);
         Map<?, ?> map = (Map<?, ?>) result;
         for (Map.Entry<?, ?> entry : map.entrySet()) {
             System.out.println(entry.getKey() + " = " + entry.getValue());
         }
        return map;
    }

    public void approveTask(String actorId, long taskId, int nextStep) throws Exception {

        ut.begin();

        try {
            System.out.println("approveTask (taskId = " + taskId + ") by " + actorId + " next Step is: " +nextStep);
            taskService.start(taskId, actorId);
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("taskNextStep", ""+nextStep);
            taskService.complete(taskId, actorId, results);
          
            //Thread.sleep(10000); // To test OptimisticLockException

            ut.commit();
            
        } catch (RollbackException e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            if (cause != null && cause instanceof OptimisticLockException) {
                // Concurrent access to the same process instance
                throw new ProcessOperationException("The same process instance has likely been accessed concurrently",
                        e);
            }
            throw new RuntimeException(e);
        } catch (PermissionDeniedException e) {
            e.printStackTrace();
            // Transaction might be already rolled back by TaskServiceSession
            if (ut.getStatus() == Status.STATUS_ACTIVE) {
                ut.rollback();
            }
            // Probably the task has already been started by other users
            throw new ProcessOperationException("The task (id = " + taskId
                    + ") has likely been started by other users ", e);
        } catch (Exception e) {
            e.printStackTrace();
            // Transaction might be already rolled back by TaskServiceSession
            if (ut.getStatus() == Status.STATUS_ACTIVE) {
                ut.rollback();
            }
            throw new RuntimeException(e);
        } 
    }

}
