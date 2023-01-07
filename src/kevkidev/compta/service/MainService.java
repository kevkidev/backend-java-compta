package kevkidev.compta.service;

public class MainService {

	private long idCounter;

	public MainService() {
		super();
		this.idCounter = 1;
	}

	public long generateId() {
		return idCounter++;
	}

	public long getIdCounter() {
		return idCounter;
	}

	public void setIdCounter(long idCounter) {
		this.idCounter = idCounter;
	}

}
