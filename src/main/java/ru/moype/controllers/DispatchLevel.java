package ru.moype.controllers;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ru.moype.model.OrderProduction;
import ru.moype.model.Stage;
import ru.moype.resources.DispatchResponseBody;
import ru.moype.service.OrderProductionService;


@RestController
public class DispatchLevel {

	@Autowired
	OrderProductionService orderService;
	
	@RequestMapping(path="/app/plAssistant/dispatchData", method=RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	@ResponseBody
//	public List<Stage> dispatch() throws IOException {
	public String dispatch() throws IOException, JSONException, ParseException {
		//List<OrderProduction> orderList = orderService.getAll();
		//Iterator<OrderProduction> itOrder = orderList.iterator();
		//while (itOrder.hasNext()){
		//	OrderProduction order = itOrder.next();
		//	List<Stage> stages = orderService.getStageList(order.getOrderId());
		//	return stages;
		//}	
		//return null;
		DispatchResponseBody response = new DispatchResponseBody();
		
		return response.DispatchResponseBodyToJSON();
	}

}
