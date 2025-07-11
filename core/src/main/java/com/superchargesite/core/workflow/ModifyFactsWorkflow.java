package com.superchargesite.core.workflow;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(
	    service=WorkflowProcess.class,
	    property={
	        "process.label=" + "Modificar Content Fragments"
	    }
	)
public class ModifyFactsWorkflow implements WorkflowProcess  {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ModifyFactsWorkflow.class);


	
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap arg2) throws WorkflowException {
		  ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);

	        // Obtén la ruta del payload del Content Fragment
	        String payloadPath = workItem.getWorkflowData().getPayload().toString();

	        try {
	            // Consigue el recurso y su adaptación a un nodo de JCR
	            Resource resource = resolver.getResource(payloadPath);
	            Node node = resource.adaptTo(Node.class);
	            LOGGER.error("dentro de execute");
	            // Comprueba si el nodo es un Content Fragment
	            if (node.hasNode("jcr:content/data/master")) {
	                Node contentFragmentNode = node.getNode("jcr:content/data/master");

	                // Aquí puedes obtener y modificar las propiedades
	                if (contentFragmentNode.hasProperty("prodiuctID")) {
	                    // Obtén la propiedad
	                    String miPropiedad = contentFragmentNode.getProperty("miPropiedad").getString();
	                    LOGGER.error("mi propiedad es {}", miPropiedad);
	                    // Realiza la lógica que necesitas con "miPropiedad"
	                    // ...

	                    // Añade una nueva propiedad
	                    contentFragmentNode.setProperty("miNuevaPropiedad", "valor");
	                }
	            }
	            // Asegúrate de guardar los cambios
	            resolver.commit();
	        } catch (Exception e) {
	            throw new WorkflowException(e.getMessage(), e);
	        }
		
	}
}
