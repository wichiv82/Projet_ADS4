class Token {
	protected Sym symbole;

	public Token(Sym s) {
		symbole=s;
	}

	public Sym symbol() {
		return symbole;
	}


	public String toString(){
		return ""+this.symbole;
	}
}   

class ValuedToken extends Token {
	private String value;
	public ValuedToken(Sym s, String v) {
		super(s);
		value=v;
	}
	public String getValue(){
		return value;
	}

	public String toString(){
		return "SYM "+symbole+" - Value : "+value;
	}
}


