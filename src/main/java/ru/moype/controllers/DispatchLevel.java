package ru.moype.controllers;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ru.moype.service.DispatchResponseBody;

@RestController
public class DispatchLevel {

	@Autowired
	DispatchResponseBody responseService;
	
	@RequestMapping(path="/app/plAssistant/dispatchData", method=RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String dispatch() throws IOException, JSONException, ParseException {

		return responseService.DispatchResponseBodyToJSON();
	}

	@RequestMapping(path="/app/plAssistant/resourceData", method=RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String dispatchResorces() throws IOException, JSONException, ParseException {

		return responseService.resourceResponseBodyToJSON();
	}

}
