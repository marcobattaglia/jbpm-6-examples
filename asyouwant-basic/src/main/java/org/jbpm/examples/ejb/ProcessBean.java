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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.jbpm.examples.util.BRMSUtil;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.task.TaskService;

import com.redhat.ruledemo.model.Prodotto;

@Startup
@javax.ejb.Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessBean implements ProcessLocal {

	@Resource
	private UserTransaction ut;

	@Inject
	BRMSUtil util;

	public long startProcess(Prodotto product) throws Exception {

		KieSession ksession = util.getStatefulSession();

		long processInstanceId = -1;

		// org.kie.internal.runtime.manager.RuntimeManagerRegistry.getManager
		ut.begin();
		try {
			// start a new process instance
			Map<String, Object> params = new HashMap<String, Object>();
			// You can put here initial variable of process
			params.put("Infos", new ArrayList());
			params.put("product", product);
			ksession.setGlobal("myResultList", new HashMap());

			setAutoFireRuleMode(ksession);

			WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession
					.startProcess("RuleDemo.AperturaPraticaWF", params);
			WorkflowProcess wp = (WorkflowProcess) processInstance.getProcess();

			processInstanceId = processInstance.getId();

			System.out.println("Process started ... : processInstanceId = "
					+ processInstanceId);

			ut.commit();
		} catch (Exception e) {
			e.printStackTrace();
			// if (ut.getStatus() == Status.STATUS_ACTIVE) {
			// ut.rollback();
			// }
			throw e;
		}

		return processInstanceId;
	}

	private void setAutoFireRuleMode(KieSession ksession) {
		ksession.addEventListener(new DefaultAgendaEventListener() {
			public void afterRuleFlowGroupActivated(
					RuleFlowGroupActivatedEvent event) {
				System.out.println("RuleflowGroup ACtivated: "
						+ event.getRuleFlowGroup().getName());
				KieSession kses = (KieSession) event.getKieRuntime();
				kses.fireAllRules();
			}
		});

	}

}
