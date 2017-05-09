class Token {
	protected Sym symbol;

	public Token(Sym s) {
		symbol=s;
	}

	public Sym symbol() {
		return symbol;
	}


	public String toString(){
		return ""+this.symbol;
	}
}   

class WordToken extends Token {
	private String value;
	public WordToken(Sym s, String v) {
		super(s);
		value=v;
	}
	public String getValue(){
		return value;
	}

	public String toString(){
		return "Symbol : "+symbol+" | Value : "+value;
	}
}

class IdToken extends Token {
	private int value;
	public WordToken(Sym s, int v) {
		super(s);
		value=v;
	}
	public int getValue(){
		return value;
	}

	public String toString(){
		return "Symbol : "+symbol+" | Value : "+value;
	}
}

class ColorToken extends Token {
	private String value;
	public WordToken(Sym s, String v) {
		super(s);
		value=v;
	}
	public String getValue(){
		return value;
	}

	public String toString(){
		return "Symbol : "+symbol+" | Value : "+value;
	}
}
