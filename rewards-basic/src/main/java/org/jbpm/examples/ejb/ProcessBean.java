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
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.kie.api.definition.process.Connection;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;
import org.kie.internal.runtime.manager.context.EmptyContext;

@Startup
@javax.ejb.Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessBean implements ProcessLocal {

    @Resource
    private UserTransaction ut;

    @Inject
    @Singleton
    private RuntimeManager singletonManager;

    @PostConstruct
    public void configure() {
        // use toString to make sure CDI initializes the bean
        // this makes sure that RuntimeManager is started asap,
        // otherwise after server restart complete task won't move process forward 
        singletonManager.toString();
    }

    public long startProcess(String recipient) throws Exception {

        RuntimeEngine runtime = singletonManager.getRuntimeEngine(EmptyContext
                .get());
        KieSession ksession = runtime.getKieSession();

        long processInstanceId = -1;

        ut.begin();

        try {
            // start a new process instance
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("recipient", recipient);
            params.put("processVar1GeneralCTX", "Initial context value");
            WorkflowProcessInstance  processInstance = (WorkflowProcessInstance) ksession.startProcess(
                    "com.sample.rewards-basic", params);
            WorkflowProcess wp = (WorkflowProcess) processInstance.getProcess();
            
            System.out.println("Future activities:");
            for(int i = 0; i< wp.getNodes().length; i++){
            	String name = wp.getNodes()[i].getName();
            	if (name.equals("Phase1")){
            		//printFutureTask(wp, wp.getNodes()[i].getId());
            	}
            			
            	//wp.getNodes()[15].getMetaData().get("Default") 
            	//wp.getNodes()[15].getOutgoingConnections("DROOLS_DEFAULT").get(3).getMetaData().get("UniqueID")
            	System.out.println(name);
            	System.out.println(wp.getNodes()[i].getId());
            	
            }
        
           
            processInstanceId = processInstance.getId();

            System.out.println("Process started ... : processInstanceId = "
                    + processInstanceId);

            ut.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (ut.getStatus() == Status.STATUS_ACTIVE) {
                ut.rollback();
            }
            throw e;
        }

        return processInstanceId;
    }

	private void printFutureTask(WorkflowProcess wp, long id) {
		 long gtwID = wp.getNode(id).getOutgoingConnections("DROOLS_DEFAULT").get(0).getTo().getId();
		 String gtwDefault = (String) wp.getNode(gtwID).getMetaData().get("Default");
		 for (Connection outConn : wp.getNode(gtwID).getOutgoingConnections("DROOLS_DEFAULT")){
			if( outConn.getMetaData().get("UniqueI").equals(gtwDefault)){
				if(outConn.getTo() instanceof org.jbpm.workflow.core.node.Join){
					outConn = getNextGtwConn(wp, outConn.getTo().getId());
				}
			
				System.out.print(outConn.getTo().getName());
				printFutureTask(wp, outConn.getTo().getId());
			}
				
		 }
			
	}	
		
	
	private Connection getNextGtwConn(WorkflowProcess wp, long gtwID){
		String gtwDefault = (String) wp.getNode(gtwID).getMetaData().get("Default");
		 for (Connection outConn : wp.getNode(gtwID).getOutgoingConnections("DROOLS_DEFAULT")){
			if( outConn.getMetaData().get("UniqueI").equals(gtwDefault)){
				return outConn;
			}
}
	
	return null;
}
}
