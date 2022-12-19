package ru.moype.service;

import net.sf.jade4spring.JadeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.moype.dbService.DBCompany;
import ru.moype.dbService.DBOrderProduction;
import ru.moype.dbService.DBResources;
import ru.moype.model.Company;
import ru.moype.model.Division;
import ru.moype.model.Event;
import ru.moype.model.OrderProduction;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class CompanyService {
	@Autowired
	DBCompany dbCompany;

	@Autowired
	DBResources dbDivision;

	@Autowired
	DBOrderProduction dbOrderProduction;

	@Autowired
	OrderProductionService orderProductionService;

	@Autowired
	EventsService evService;

	@Resource(name="testBean")
	private JadeBean jadeBean;

	public List<Company> getAll(){
		return dbCompany.getAll();
	}

	public List<OrderProduction> getAllOrders(){
		//return dbOrderProduction.getAllByState("new");
		return dbOrderProduction.getAll();
	}

	public int delete(Long[] ids) {
		return dbCompany.delete(ids);
	}

	public String start() throws Exception {
		//Resource start
		List<Company> cmpn_list = getAll();
		for (Company cmp: cmpn_list){
			List<Division> div_list = dbDivision.getAll(cmp.getIdBase());
			for (Division d: div_list) {
				Object argsJ[] = new Object[1];
				argsJ[0]= d;
				if (d.getMode() == 1) {
					jadeBean.startAgent("Division:" + d.getCode(), "ru.moype.agents.DivisionAgent", argsJ);
				}
			}
		}
		//Order start

		List<OrderProduction> order_list = getAllOrders();
		for (OrderProduction order: order_list){
			Event ev = new Event();
			ev.setType("startOrder");
			ev.setStageId("");
			ev.setOrderId(order.getNumber());
			ev.setState("new");
			Date date = new Date();
			ev.setTimeOfEvent(date);

			evService.register(ev);
		}

		//Event manager
		jadeBean.startAgent("EventsManager", "ru.moype.agents.EventManagerAgent", new Object[0]);

		return "ok";
	}

	public Company register(Company cmp) {
		return dbCompany.register(cmp);
	}
	
}
