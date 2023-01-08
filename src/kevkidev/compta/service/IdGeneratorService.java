package kevkidev.compta.service;

public class IdGeneratorService {

	private long idCounter;
	private long alimentIdCounter;
	private long intakeIdCounter;
	private long menuIdCounter;

	public IdGeneratorService() {
		super();
		this.idCounter = 1;
		this.alimentIdCounter = 1;
		menuIdCounter = 1;
	}

	public long generateId() {
		return idCounter++;
	}

	public long generateAlimentId() {
		return alimentIdCounter++;
	}

	public long generateIntakeId() {
		return intakeIdCounter++;
	}

	public long generateMenuId() {
		return menuIdCounter++;
	}

	public long getIdCounter() {
		return idCounter;
	}

	public void setIdCounter(long idCounter) {
		this.idCounter = idCounter;
	}

	public long getAlimentIdCounter() {
		return alimentIdCounter;
	}

	public void setAlimentIdCounter(long alimentIdCounter) {
		this.alimentIdCounter = alimentIdCounter;
	}

	public long getMenuIdCounter() {
		return menuIdCounter;
	}

	public void setMenuIdCounter(long menuIdCounter) {
		this.menuIdCounter = menuIdCounter;
	}

	public long getIntakeIdCounter() {
		return intakeIdCounter;
	}

	public void setIntakeIdCounter(long intakeIdCounter) {
		this.intakeIdCounter = intakeIdCounter;
	}
}
