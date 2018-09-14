package chord.net;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class SupervisedTable {
	
	private Map<ChordKey, Entry> table;
	
	public SupervisedTable(){
		table = new TreeMap<ChordKey, Entry>();
	}
	
	/**
	 * add an new entry to record the mobility information of a newly joined device 
	 * @param Key	     the hash key of the device
	 * @param user	     the ip address of mobile device
	 * @param currentController      the controller that the device current connected to
	 */
	public void addEntry(ChordKey Key, String user, String currentController){
		
		Entry entry = new Entry();
		entry.setUser(user);
		entry.setCurrent(currentController);
		entry.setPrevious(currentController);
		entry.setTime(currentTime());
		table.put(Key, entry);
	}
	
	/**
	 * when a mobile device move to a new area, the mobility information in the supervisory controller
	 * 	will be updated
	 * @param currentController		the controller that the device current connected to
	 * @param key		the hash key of the device
	 */
	public void updateEntry(String currentController, ChordKey key){

		Entry entry = this.getTable().get(key);
		entry.setPrevious(entry.getCurrent());
		entry.setCurrent(currentController);
		entry.setTime(currentTime());
	}
	
	public String currentTime(){
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
		return dateFormat.format(date);
	}
	
	public Map<ChordKey, Entry> getTable() {
		return table;
	}
}

class Entry{
	private String User;
	private String Previous;
	private String Current;
	private String time;
	
	public String getUser() {
		return User;
	}
	public void setUser(String user) {
		User = user;
	}
	public String getPrevious() {
		return Previous;
	}
	public void setPrevious(String previous) {
		Previous = previous;
	}
	public String getCurrent() {
		return Current;
	}
	public void setCurrent(String current) {
		Current = current;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}