package com.superchargesite.core.handlers;

import java.util.HashMap;
import java.util.Map;


import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;


@Component(
	    service = EventHandler.class,
	    immediate = true,
	    property = {
	    	org.osgi.service.event.EventConstants.EVENT_TOPIC + "=com/day/cq/replication/ReplicationAction",
	    	org.osgi.service.event.EventConstants.EVENT_FILTER + "=(paths=/content/my-folder-path/.*"
	    }
	)
public class ReplicationEventHandler   implements EventHandler{
	 
	@Reference
	    private ResourceResolverFactory resolverFactory;

	    @Reference
	    private WorkflowSession workflowSession;

	    @Override
	    public void handleEvent(Event event) {
	        ReplicationAction action = ReplicationAction.fromEvent(event);

	        if (action != null && action.getType().equals(ReplicationActionType.ACTIVATE)) {
	            try {
	                Map<String, Object> param = new HashMap<String, Object>();
	                param.put(ResourceResolverFactory.SUBSERVICE, "workflow-service");
	                WorkflowSession wfSession = resolverFactory.getServiceResourceResolver(param).adaptTo(WorkflowSession.class);
	                WorkflowModel wfModel = wfSession.getModel("/conf/global/settings/workflow/models/my-workflow-model");
	                WorkflowData wfData = wfSession.newWorkflowData("JCR_PATH", action.getPath());
	                wfSession.startWorkflow(wfModel, wfData);
	            } catch (WorkflowException | org.apache.sling.api.resource.LoginException e) {
	                // log exception
	            }
	        }
	    }
}
