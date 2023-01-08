package kevkidev.nutri.domain;

public class HumainProfil {
	private int weight; // g
	private int height; // cm
	private int age;

//	13,7516 x Poids(kg) + 500,33 x Taille(m) – 6,7550 x Age(années) + 66,479
	public double calculateMetaBase() {
		return 13.7516d * (double) weight / 1000d + 500.33d * (double) height / 100d - 6.7550d * (double) age + 66.479d;
	}
}
