package peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StopBuffRead {
	
	public static void main(String[] args) throws InterruptedException {
		StopBuffRead sbr = new StopBuffRead();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		Thread t = new Thread(sbr.new BuffRead(br));
		System.out.println("Starting thread!");
		t.start();
		Thread.sleep(5000);
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	
	public class BuffRead implements Runnable {
		
		private BufferedReader br;
		
		public BuffRead(BufferedReader br){
			this.br = br;
		}
		
		public String interruptibleReadLine(BufferedReader reader)
		        throws InterruptedException, IOException {
		    Pattern line = Pattern.compile("^(.*)\\R");
		    Matcher matcher;
		    StringBuilder result = new StringBuilder();
		    int chr = -1;
		    
		    do {
		        if (reader.ready()) chr = reader.read();
		        if (chr > -1) result.append((char) chr);
		        matcher = line.matcher(result.toString());
		    } while (!matcher.matches());
		   
		    return (matcher.matches() ? matcher.group(1) : "");
		}

	    @Override
		public void run() {
	    	while(true){
	    		try {
	    			System.out.println("Insert string:");
	    			String s = this.interruptibleReadLine(this.br);
	    			System.out.println("You inserted " + s);
	    		}
	    		catch(IOException | InterruptedException e){
	    			System.out.println("Thread is done!");	    			
	    			break;
	    		}
	    	}
	    }

	}
}
