package ru.moype.service;

import net.sf.jade4spring.JadeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.moype.dbService.DBEvents;
import ru.moype.model.Event;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EventsService {
	@Autowired
	DBEvents dbEvents;

	@Resource(name="testBean")
	private JadeBean jadeBean;

	public List<Event> getAll(){
		return dbEvents.getAll();
	}

	public Event getFirstEvent(){
		return dbEvents.getFirstEvent();
	}

	public int delete(Long[] ids) {
		return dbEvents.delete(ids);
	}

	public Event register(Event ev) {
		return dbEvents.register(ev);
	}

	public void update(Event ev) {
		dbEvents.update(ev);
	}

}
