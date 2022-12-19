package ru.moype.controllers;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.moype.model.Event;
import ru.moype.model.OrderProduction;
import ru.moype.service.EventsService;
import ru.moype.service.OrderProductionService;

@RestController
public class OrderProductionCtrl {

    @Autowired
    OrderProductionService orderProductionService;

    @Autowired
    EventsService evServicе;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @RequestMapping(path="app/plAssistant/getOrderList", method=RequestMethod.GET)
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

    @RequestMapping(path="/actualOrder", method=RequestMethod.POST)
    @ResponseBody
    public String actualOrder(@RequestBody String order) throws Exception{
        //Перепланирование всех просроченных этапов
        Map<String, String> responseMap = splitToMap(order, "=");
        String orderN = responseMap.get("actualOrder");
        try {
            String result = java.net.URLDecoder.decode(orderN, StandardCharsets.UTF_8.name());
            OrderProduction orderProduction = orderProductionService.getOrderById(result);
            orderProduction.setStartDate(new Date());
            orderProduction.setState("replan");
            orderProductionService.register(orderProduction);
            orderProductionService.createStageAgent(result);
        } catch (UnsupportedEncodingException e) {
            // not going to happen - value came from JDK's own StandardCharsets
        }
        return "ok";
    }

    @RequestMapping(path="/replanChangeProvision", method=RequestMethod.POST)
    @ResponseBody
    public String replanOrder(@RequestParam("newProvDate") String newProvDate, @RequestParam("stage") String idStage, @RequestParam("orderId") String orderId, @RequestParam("codeNom") String codeNom) throws Exception {
        //Изменение даты снабжения
        Date notEarlier = formatter.parse(newProvDate);
        //Запись в БД и старт агента
        orderProductionService.setReplanStage(idStage, orderId, notEarlier);

        Event ev = new Event();
        ev.setType("replanOrder");
        ev.setStageId(idStage);
        ev.setOrderId(orderId);
        ev.setState("new");
        Date date = new Date();
        ev.setTimeOfEvent(date);

        evServicе.register(ev);

        return "ok";
    }

    public static Map<String, String> splitToMap(String source, String keyValueSeparator) {
        Map<String, String> map = new HashMap<String, String>();
        String[] keyValue = source.split(keyValueSeparator);
        map.put(keyValue[0], keyValue[1]);
        return map;
    }

}