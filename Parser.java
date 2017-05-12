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

	public Parser(LookAhead r) {
		this.reader=r;
	}
	
	public Arbre nonterm_Document() throws Exception{
		Arbre res = new Arbre("");
		if (reader.check(Sym.DEBUTDOC))
			res = nonterm_Corps();
		else if (reader.check(Sym.SET)){
			List<Arbre> a0 = nonterm_Declaration();
			System.out.println(a0.size());
			Arbre a1 = new Arbre("<head><meta charset=\"utf-8\" /> \n<style>","\n</style></head>", a0);
			Arbre a2 = nonterm_Corps();
			ArrayList<Arbre> tmp = new ArrayList<Arbre>();
			tmp.add(a1);
			tmp.add(a2);
			res = new Arbre("<!DOCTYPE html>\n<html>\n","\n</html>",tmp);
		}
		return res;
	}
	
	
	public List<Arbre> nonterm_Declaration() throws Exception {
		ArrayList<Arbre> tmp = new ArrayList<Arbre>();
		if (!reader.check(Sym.SET))
			return tmp;
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
		System.out.println("++++++++++++++++++++++++-------------------");
		
		return tmp;
	}
	
	
	public Arbre nonterm_Corps() throws Exception {
		reader.eat(Sym.DEBUTDOC);
		List<Arbre> tmp = nonterm_SuiteElem();
		reader.eat(Sym.FINDOC);
		reader.eat(Sym.EOF);
		return new Arbre("<body>\n","\n</body>",tmp);
	}
	
	/**
	 * @return True si le reader voit un symbole correspond à une balise fermante
	 * @throws Exception
	 */
	private boolean epsilon() throws Exception{
		return reader.check(Sym.FINDOC) || reader.check(Sym.ITEM) || reader.check(Sym.FINENUM) ||
				reader.check(Sym.FINACCOLADE);
	}

	private List<Arbre> nonterm_SuiteElem() throws Exception {
		ArrayList<Arbre> tmp = new ArrayList<Arbre>();
		// Recontrer un Token de balise fermante revient à trouver un epsilon dres ce cas
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
			}  else if (reader.check(Sym.COULEUR)){
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
				}else if (reader.check(Sym.CONSTANTE_COULEUR)){
					c = reader.getValue();
					reader.eat(Sym.CONSTANTE_COULEUR);
					reader.eat(Sym.FINACCOLADE);
					reader.eat(Sym.DEBUTACCOLADE);
					res = new Arbre("<font color=\""+c+"\">","</font>",nonterm_SuiteElem());
					reader.eat(Sym.FINACCOLADE);
				}
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
		// On continue à chercher des items tant qu'on a pas atteint la fin de l'enum
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
