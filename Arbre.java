import java.util.*;

class Arbre {
	
	private List<Arbre> elements;
	private String debut, fin;

	public Arbre(String debut, List<Arbre> elements){
		this.debut = debut;
		this.fin = "";
		this.elements = elements;
	}
	
	public Arbre(String debut, String fin, List<Arbre> elements){
		this.debut = debut;
		this.fin = fin;
		this.elements = elements;
	}
	
	public Arbre(String debut) {
		this.debut = debut;
		this.fin = "";
		this.elements = new ArrayList<Arbre>();
	}

	public String toString(){
		String ans = debut + " ";
		for (Arbre a : elements) ans += a;
		return ans + fin;
	}
}
