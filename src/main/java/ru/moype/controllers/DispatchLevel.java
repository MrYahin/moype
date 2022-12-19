package ru.moype.controllers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;
import ru.moype.model.Event;
import ru.moype.model.RowStageSchemeResgroup;
import ru.moype.model.Stage;
import ru.moype.service.DispatchResponse;
import ru.moype.service.EventsService;

@RestController
public class DispatchLevel {

	@Autowired
	DispatchResponse responseService;

	@Autowired
	EventsService evService;

	@RequestMapping(path="/app/plAssistant/getDispatchDataToFront", method=RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String getDispatchDataToFront(@RequestParam(name = "order") String order) throws IOException, JSONException, ParseException {

		return responseService.getDispatchResponseBodyFront_JSON(order);
	}

	@RequestMapping(path="/app/plAssistant/getDispatchData", method=RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	@ResponseBody
	//@PathVariable(value = "email") String email
	//
	public String getDispatchData(@RequestBody String order) throws IOException, ParseException {

		return responseService.getDispatchResponseBody_JSON(order);
	}

	@RequestMapping(path="/saveChanges", method=RequestMethod.POST)
	@ResponseBody
	public String saveChanges() throws IOException, JSONException, ParseException {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		//
		// Authentication
		//
		String auth = "Jacob:lawisdead";
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
		String authHeader = "Basic " + new String(encodedAuth);
		headers.set("Authorization", authHeader);

		RestTemplate restTemplate = new RestTemplate();

		Stage st = new Stage(1,  1, 1, "test", "1", 1, 1, "test", "test", "test", new Date(), new Date(), 1, new Date(), new Date(), "test", "test", true, true, 1, 1, 1, "1");

		ObjectMapper objectMapper = new ObjectMapper();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		objectMapper.setDateFormat(df);

		String body = objectMapper.writeValueAsString(st);

		HttpEntity<String> entity = new HttpEntity<String>(body, headers);

		ResponseEntity<String> response = restTemplate.exchange("http://localhost/erp_demonstration/hs/dispatch", HttpMethod.POST, entity, String.class);
		// Code = 200.
		if (response.getStatusCode() == HttpStatus.OK) {
			String result = response.getBody();
			//System.out.println("ок");
		}
		return "Записано успешно.";
	}

	@RequestMapping(path="/app/plAssistant/getResourceData", method=RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String dispatchResorces() throws IOException, JSONException, ParseException {

		return responseService.resourceResponseBodyToJSON();
	}

	@RequestMapping(path="app/events", method=RequestMethod.POST)
	@ResponseBody
	public String events(@RequestBody String events) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
		Map<?, ?> map = objectMapper.readValue(events, Map.class);

		for (Map.Entry<?, ?> entry : map.entrySet()) {
			if ("events" == (String)entry.getKey()) {
				List<?> events_list = (ArrayList<?>) entry.getValue();
				for (int i = 0; i < events_list.size(); i++) {
					Map<?, ?> ev = (Map) events_list.get(i);
					Event ev_cl = objectMapper.convertValue(ev, Event.class);
					evService.register(ev_cl);
				}
			}
		}
		return "ok";
	}

}
