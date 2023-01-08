package kevkidev.nutri.domain;

import java.util.List;

public class Menu {
	private long id;
	private String name;
	private List<Intake> intakes;

	public Menu() {

	}

	public Menu(long id, String name, List<Intake> intakes) {
		super();
		this.id = id;
		this.name = name;
		this.intakes = intakes;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Intake> getIntakes() {
		return intakes;
	}

	public void setIntakes(List<Intake> intakes) {
		this.intakes = intakes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Menu [name=" + name + ", aliments=" + intakes + "]";
	}

}
