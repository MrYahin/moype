package ru.moype.resources;

import jade.core.Agent;
import net.sf.jade4spring.JadeBean;
import ru.moype.config.SpringContext;
import ru.moype.model.Division;
import ru.moype.model.ResGroup;
import ru.moype.service.ResourcesService;

import java.util.List;

public class DivisionAgent extends Agent{

	JadeBean jadeBean = SpringContext.getBean(JadeBean.class);
	ResourcesService resService = SpringContext.getBean(ResourcesService.class);

	//Initialization
	public void setup() {

		Object[] args = getArguments();
		Division div 	= (Division)args[0];
		List<ResGroup> resGroups = resService.getResGroupsByDivision(div.getId());
		for (ResGroup rsg: resGroups) {
			Object argsJ[] = new Object[1];
			argsJ[0] = rsg;
			jadeBean.startAgent("resGroup:" + rsg.getName(),  "ru.moype.resources.ResGroupAgent", argsJ);
		}

		System.out.println("Agent: "+ getAID().getLocalName() + " is active.");
	}

}
