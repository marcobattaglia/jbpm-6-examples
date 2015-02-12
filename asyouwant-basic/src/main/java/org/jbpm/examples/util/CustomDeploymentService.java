package org.jbpm.examples.util;

import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.deployment.DeployedUnit;
import org.kie.internal.deployment.DeploymentService;
import org.kie.internal.deployment.DeploymentUnit;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;


// plain CustomDeploymentService solves:
// Caused by: org.jboss.weld.exceptions.DeploymentException: WELD-001408 Unsatisfied dependencies for type [DeploymentService]
// with qualifiers [@Default] at injection point [[field] @Inject private org.jbpm.kie.services.impl.form.FormProviderServiceImpl.deploymentService]
@ApplicationScoped
public class CustomDeploymentService implements DeploymentService {

    public void deploy(DeploymentUnit deploymentUnit) {
    }

    public void undeploy(DeploymentUnit deploymentUnit) {
    }

    public RuntimeManager getRuntimeManager(String s) {
        return null;
    }

    public DeployedUnit getDeployedUnit(String s) {
        return null;
    }

    public Collection<DeployedUnit> getDeployedUnits() {
        return null;
    }
}
