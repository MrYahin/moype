package ru.moype.controllers;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import jade.wrapper.StaleProxyException;
import ru.moype.model.OrderProduction;
import ru.moype.service.OrderProductionService;

@RestController
public class OrderProductionList {

	@Autowired
	OrderProductionService orderProductionService;
	
	@RequestMapping(path="app/plAssistant/orderList", method=RequestMethod.GET)
	@ResponseBody
	public List<OrderProduction> getCategory(){
		
		return orderProductionService.getAll();
	}

	@RequestMapping(path="/launchOrder", method=RequestMethod.POST)
	@ResponseBody
	public String launchOrder(@RequestBody String order) throws Exception{
		
		Map<String, String> responseMap = splitToMap(order, "=");
		String orderN = responseMap.get("order");
		try {
			String result = java.net.URLDecoder.decode(orderN, StandardCharsets.UTF_8.name());
			orderProductionService.createStageAgent(result);
		} catch (UnsupportedEncodingException e) {
			// not going to happen - value came from JDK's own StandardCharsets
		}
		return "ok";
	}
	
	public static Map<String, String> splitToMap(String source, String keyValueSeparator) {
	    Map<String, String> map = new HashMap<String, String>();
        String[] keyValue = source.split(keyValueSeparator);
        map.put(keyValue[0], keyValue[1]);
	    return map;
	}
	
}
