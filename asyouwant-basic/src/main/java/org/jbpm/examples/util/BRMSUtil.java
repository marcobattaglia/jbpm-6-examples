package org.jbpm.examples.util;

import static org.kie.api.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;
import static org.kie.api.runtime.EnvironmentName.TASK_USER_GROUP_CALLBASK;
import static org.kie.api.runtime.EnvironmentName.TRANSACTION_MANAGER;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;

import org.jbpm.runtime.manager.impl.KModuleRegisterableItemsFactory;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.wih.NonManagedLocalHTWorkItemHandler;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;
import org.kie.internal.runtime.manager.context.EmptyContext;

@ApplicationScoped
public class BRMSUtil {

	@Resource
	private UserTransaction ut;

	private KieContainer kContainer = null;

	
	@PersistenceUnit(unitName = "org.jbpm.examples.rewards-basic")
	private EntityManagerFactory emf;

	@Produces
	public EntityManagerFactory produceEntityManagerFactory() {
		if (this.emf == null) {
			this.emf = Persistence
					.createEntityManagerFactory("org.jbpm.examples.rewards-basic");
		}
		return this.emf;
	}
	
	@Inject
	@MyCustomRuntimeManagerProducer
	private RuntimeManager rm;
	
	@Produces
	@MyCustomRuntimeManagerProducer
	public RuntimeManager produceRuntimeManager(){
		if(this.rm==null){

			KModuleRegisterableItemsFactory factory = new KModuleRegisterableItemsFactory(
					kContainer, null);
			//KieSession ksession = kContainer.newKieSession();

			// RuntimeManagerRegistry.get().register(singletonManager);
			// singletonManager.getRuntimeEngine(context)

//			TaskService taskService = HumanTaskServiceFactory
//					.newTaskServiceConfigurator().entityManagerFactory(emf)
//					.userGroupCallback(usergroupCallback).getTaskService();
	//
//			WorkItemHandler htHandler = new NonManagedLocalHTWorkItemHandler(
//					ksession, taskService);
	//
//			ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
//					htHandler);

//			 factory.addWorkItemHandler("Human Task",
//			 TestAsyncWorkItemHandler.class);
			//factory.addWorkItemHandler("HumanTask, clazz);
			RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory
					.get().newDefaultBuilder().userGroupCallback(usergroupCallback)
					.knowledgeBase(kContainer.getKieBase())
					.registerableItemsFactory(factory)
					.entityManagerFactory(emf)
					.get();
			

			RuntimeManager manager = RuntimeManagerFactory.Factory.get()					
					.newPerRequestRuntimeManager(environment,"com.redhat:RuleDemo:1.2.5");
			this.rm = manager;
		}
		return rm;
		
	}

	@Inject
	private UserGroupCallback usergroupCallback;

	@Inject
	private TaskService taskService;

	public BRMSUtil() {

		KieServices kServices = KieServices.Factory.get();
		Environment env = kServices.newEnvironment();
		env.set(ENTITY_MANAGER_FACTORY, emf);
		env.set(TRANSACTION_MANAGER, ut);
		env.set(TASK_USER_GROUP_CALLBASK, usergroupCallback);

		ReleaseId releaseId = kServices.newReleaseId("com.redhat", "RuleDemo",
				"LATEST");

		kContainer = kServices.newKieContainer(releaseId);

		KieScanner kScanner = kServices.newKieScanner(kContainer);

		// Start the KieScanner polling the maven repository every 10 seconds

		kScanner.start(10000L);
	}

	public StatelessKieSession getStatelessSession() {

		return kContainer.newStatelessKieSession();

	}

	/*
	 * KieSession is the new StatefulKnowledgeSession from BRMS 5.3.
	 */
	public KieSession getStatefulSession() {

		
		KieSession ksession = this.rm.getRuntimeEngine(EmptyContext.get()).getKieSession();
		TaskService taskService = HumanTaskServiceFactory
				.newTaskServiceConfigurator().entityManagerFactory(emf)
				.userGroupCallback(usergroupCallback).getTaskService();

		WorkItemHandler htHandler = new NonManagedLocalHTWorkItemHandler(
				ksession, taskService);

		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				htHandler);
		
		return ksession;

	}

}
