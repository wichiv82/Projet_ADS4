import java.util.*;
import java.io.*;

class Parser {
	/*
	 * document → declarations corps
	 * declarations → \set{ id } { constante_couleur } declarations | ε	
	 * corps → \begindoc suite_elements \enddoc
	 * suite_elements → element suite_elements | ε
	 * element → mot
	 * 	| \linebreak
	 * 	| \bf{ suite_elements }
	 * 	| \it{ suite_elements }
	 *	| enumeration
	 *  | \couleur{ val_col } { suite_elemements }
	 * val_col → constante_couleur | id
	 * enumeration → \beginenum suite_items \endenum
	 * suite_items → item suite_items | ε
	 * item → \item suite_elements
	 */
	
	LookAhead reader;
	ArrayList<String[]> stockage = new ArrayList<String[]>();

	public Parser(LookAhead r) {
		this.reader=r;
	}
	
	public Arbre nonterm_Document() throws Exception{
		Arbre res = new Arbre("");
		if (reader.check(Sym.DEBUTDOC)){
			res = nonterm_Corps();
		}
		else if (reader.check(Sym.SET) || reader.check(Sym.ABB)){
			ArrayList<Arbre> a0 = nonterm_Declaration();
			Arbre a1 = new Arbre("<head><meta charset=\"utf-8\" /> \n<style>","\n</style></head>", a0);
			Arbre a2 = nonterm_Corps();
			ArrayList<Arbre> tmp = new ArrayList<Arbre>();
			tmp.add(a1);
			tmp.add(a2);
			res = new Arbre("<!DOCTYPE html>\n<html>\n","\n</html>",tmp);
		}
		return res;
	}
	
	
	public ArrayList<Arbre> nonterm_Declaration() throws Exception {
		ArrayList<Arbre> res = new ArrayList<Arbre>();
		if (reader.check(Sym.SET)){
			res = nonterm_Set();
		} else if(reader.check(Sym.ABB)){
			res = nonterm_Abb();
		}
		return res;
	}
	
	public ArrayList<Arbre> nonterm_Set() throws Exception {
		ArrayList<Arbre> tmp = new ArrayList<Arbre>();
		reader.eat(Sym.SET);
		reader.eat(Sym.DEBUTACCOLADE);
		reader.eat(Sym.ID);
		String a = reader.getValue();
		reader.eat(Sym.MOT);
		reader.eat(Sym.FINACCOLADE);
		reader.eat(Sym.DEBUTACCOLADE);
		String b = reader.getValue();
		reader.eat(Sym.CONSTANTE_COULEUR);
		reader.eat(Sym.FINACCOLADE);
		tmp.add(new Arbre(a+"{ \ncolor: "+b+"; \n}\n",nonterm_Declaration()));
		return tmp;
	}
	
	public ArrayList<Arbre> nonterm_Abb() throws Exception {
		ArrayList<Arbre> tmp = new ArrayList<Arbre>();
		String [] tab = new String [3];
		reader.eat(Sym.ABB);
		reader.eat(Sym.DEBUTACCOLADE);
		reader.eat(Sym.RACCOURCI);
		tab[0] = reader.getValue();
		reader.eat(Sym.MOT);
		reader.eat(Sym.FINACCOLADE);
		reader.eat(Sym.DEBUTACCOLADE);
		if (reader.check(Sym.BF)){
			tab[2] = "bf";
			reader.eat(Sym.BF);
		}else if (reader.check(Sym.IT)){
			tab[2] = "it";
			reader.eat(Sym.IT);
		}else if (reader.check(Sym.DEBUTACCOLADE))
			tab[2] = "none";
		reader.eat(Sym.DEBUTACCOLADE);
		String a ="";
		while (reader.check(Sym.MOT)){
			a += reader.getValue()+ " ";
			reader.eat(Sym.MOT);
		}
		tab[1] = a;
		reader.eat(Sym.FINACCOLADE);
		reader.eat(Sym.FINACCOLADE);
		stockage.add(tab);
		tmp.add(new Arbre("",nonterm_Declaration()));
		return tmp;
	}
	
	public Arbre nonterm_Corps() throws Exception {
		reader.eat(Sym.DEBUTDOC);
		List<Arbre> tmp = nonterm_SuiteElem();
		reader.eat(Sym.FINDOC);
		reader.eat(Sym.EOF);
		return new Arbre("<body>\n","\n</body>",tmp);
	}
	
	
	private boolean epsilon() throws Exception{
		return reader.check(Sym.FINDOC) || reader.check(Sym.ITEM) || reader.check(Sym.FINENUM) ||
				reader.check(Sym.FINACCOLADE);
	}

	private List<Arbre> nonterm_SuiteElem() throws Exception {
		ArrayList<Arbre> tmp = new ArrayList<Arbre>();
		if (!epsilon()) {
			tmp.add(nonterm_Elem());
			tmp.addAll(nonterm_SuiteElem());
		}
		return tmp;
	}

	private Arbre nonterm_Elem() throws Exception {
		Arbre res = new Arbre("");
		if (!reader.check(Sym.FINACCOLADE)) {
			if (reader.check(Sym.MOT)) {
				res = new Arbre(reader.getValue());
				reader.eat(Sym.MOT);
			} else if (reader.check(Sym.LINEBREAK)) {
				reader.eat(Sym.LINEBREAK);
				res = new Arbre("\n<br>\n");
			} else if (reader.check(Sym.BF)) {
				reader.eat(Sym.BF);
				reader.eat(Sym.DEBUTACCOLADE);
				res = new Arbre("<b>","</b>",nonterm_SuiteElem());
				reader.eat(Sym.FINACCOLADE);
			} else if (reader.check(Sym.IT)) {
				reader.eat(Sym.IT);
				reader.eat(Sym.DEBUTACCOLADE);
				res = new Arbre("<i>","</i>",nonterm_SuiteElem());
				reader.eat(Sym.FINACCOLADE);
			} else if (reader.check(Sym.DEBUTENUM)){
				res = nonterm_Enum();
			} else if (reader.check(Sym.COULEUR)){
				reader.eat(Sym.COULEUR);
				reader.eat(Sym.DEBUTACCOLADE);
				String c ="";
				if (reader.check(Sym.ID)){
					reader.eat(Sym.ID);
					c = reader.getValue();
					reader.eat(Sym.MOT);
					reader.eat(Sym.FINACCOLADE);
					reader.eat(Sym.DEBUTACCOLADE);
					res = new Arbre("<"+c+">","</"+c+">",nonterm_SuiteElem());
					reader.eat(Sym.FINACCOLADE);
				} else if (reader.check(Sym.CONSTANTE_COULEUR)){
					c = reader.getValue();
					reader.eat(Sym.CONSTANTE_COULEUR);
					reader.eat(Sym.FINACCOLADE);
					reader.eat(Sym.DEBUTACCOLADE);
					res = new Arbre("<font color=\""+c+"\">","</font>",nonterm_SuiteElem());
					reader.eat(Sym.FINACCOLADE);
				}
			} else if (reader.check(Sym.RACCOURCI)) {
				reader.eat(Sym.RACCOURCI);
				String abbreviation = recherche(reader.getValue());
				reader.eat(Sym.MOT);
				res = new Arbre(abbreviation,nonterm_SuiteElem());
			}
		}
		return res;
	}
	
	private String recherche(String cle){
		String res = "" ;
		for (int i = 0; i < stockage.size(); i++){
			if (stockage.get(i)[0].equals(cle)){
				if (stockage.get(i)[2].equals("bf"))
					res = " <b> "+stockage.get(i)[1]+" </b> ";
				else if (stockage.get(i)[2].equals("it"))
					res = " <i> "+stockage.get(i)[1]+" </i> ";
				if (stockage.get(i)[2].equals("none"))
					res = stockage.get(i)[1];
				break;
			}
		}
		
		return res;
	}
	
	private Arbre nonterm_Enum() throws Exception {
		reader.eat(Sym.DEBUTENUM);
		Arbre res = new Arbre("\n<ol>","\n</ol>\n", nonterm_SuiteItems());
		reader.eat(Sym.FINENUM);
		return res;
	}
	
	private List<Arbre> nonterm_SuiteItems() throws Exception {
		ArrayList<Arbre> tmp = new ArrayList<Arbre>();
		if (!reader.check(Sym.FINENUM)) {
			tmp.add(nonterm_Item());
			tmp.addAll(nonterm_SuiteItems());
		}
		return tmp;
	}
	
	private Arbre nonterm_Item() throws Exception {
		reader.eat(Sym.ITEM);
		return new Arbre("\n<li>","</li>", nonterm_SuiteElem());
	}
	
}
