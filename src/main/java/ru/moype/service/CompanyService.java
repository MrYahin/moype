package ru.moype.service;

import net.sf.jade4spring.JadeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.moype.dbService.DBCompany;
import ru.moype.dbService.DBResources;
import ru.moype.model.Company;
import ru.moype.model.Division;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CompanyService {
	@Autowired
	DBCompany dbCompany;

	@Autowired
	DBResources dbDivision;

	@Resource(name="testBean")
	private JadeBean jadeBean;

	public List<Company> getAll(){
		return dbCompany.getAll();
	}

	public int delete(Long[] ids) {
		return dbCompany.delete(ids);
	}

	public String start() {
		List<Company> cmpn_list = getAll();
		for (Company cmp: cmpn_list){
			List<Division> div_list = dbDivision.getAll(cmp.getIdBase());
			for (Division d: div_list) {
				Object argsJ[] = new Object[1];
				argsJ[0]= d;
				if (d.getMode() == 1) {
					jadeBean.startAgent("Division:" + d.getCode(), "ru.moype.resources.DivisionAgent", argsJ);
				}
			}
		}
		return "ok";
	}

	public Company register(Company cmp) {
		return dbCompany.register(cmp);
	}
	
}
