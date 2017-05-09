import java.util.*;

class Parser {
	/*
	 * document → corps
	 * corps → \begindoc suite_elements \enddoc
	 * suite_elements → element suite_elements | ε
	 * element → mot
	 * 	| \linebreak
	 * 	| \bf{ suite_elements }
	 * 	| \it{ suite_elements }
	 *	| enumeration
	 * enumeration → \beginenum suite_items \endenum
	 * suite_items → item suite_items | ε
	 * item → \item suite_elements
	 */
	
	LookAhead reader;

	public Parser(LookAhead r) {
		this.reader=r;
	}

	public Arbre nonterm_Corps() throws Exception {
		reader.eat(Sym.DEBUTDOC);
		List<Arbre> tmp = nonterm_SuiteElem();
		reader.eat(Sym.FINDOC);
		reader.eat(Sym.EOF);
		return new Arbre("<!DOCTYPE html>\n<html>\n<body>\n","\n</body>\n</html>",tmp);
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
		// Recontrer un Token de balise fermante revient à trouver un epsilon dans ce cas
		if (!epsilon()) {
			tmp.add(nonterm_Elem());
			tmp.addAll(nonterm_SuiteElem());
		}
		return tmp;
	}

	private Arbre nonterm_Elem() throws Exception {
		Arbre ans = new Arbre("");
		if (!reader.check(Sym.FINACCOLADE)) {
			if (reader.check(Sym.MOT)) {
				ans = new Arbre(reader.getValue());
				reader.eat(Sym.MOT);
			} else if (reader.check(Sym.LINEBREAK)) {
				reader.eat(Sym.LINEBREAK);
				ans = new Arbre("\n<br>\n");
			} else if (reader.check(Sym.BF)) {
				reader.eat(Sym.BF);
				reader.eat(Sym.DEBUTACCOLADE);
				ans = new Arbre("<b>","</b>",nonterm_SuiteElem());
				reader.eat(Sym.FINACCOLADE);
			} else if (reader.check(Sym.IT)) {
				reader.eat(Sym.IT);
				reader.eat(Sym.DEBUTACCOLADE);
				ans = new Arbre("<i>","</i>",nonterm_SuiteElem());
				reader.eat(Sym.FINACCOLADE);
			} else if (reader.check(Sym.DEBUTENUM)){
				ans = nonterm_Enum();
			//} else if (reader.check(s))
		}
		return ans;
	}
	
	private Arbre nonterm_Enum() throws Exception {
		reader.eat(Sym.DEBUTENUM);
		Arbre ans = new Arbre("\n<ol>","\n</ol>\n", nonterm_SuiteItems());
		reader.eat(Sym.FINENUM);
		return ans;
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
