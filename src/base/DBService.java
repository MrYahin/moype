package base;

import base.dataSets.UserDataSet;

public interface DBService {

	String getLocalStatus();
	void save(UserDataSet dataSet);
	UserDataSet read(long id);
	UserDataSet readByName(String name);
	void shutdown();
	
}
