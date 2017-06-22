package exceptions;

public class Try {

	public static void main(String[] args) {
		Throwed t = new Throwed();
		try{
			String s = t.doit("hol");
			if(s.equals("ciao"))
				throw new IllegalArgumentException("Ancora no!!!");
			System.out.println(s);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		System.out.println("Arrivo anche qua!");

	}

}

class Throwed {
	public String doit(String s){
		if (s.equalsIgnoreCase("hola"))
			throw new IllegalStateException("no!!!");
		return "ciao";
	}
}
