package cam.ac.uk.hackathon;

import java.io.PrintWriter;

public class translateToC {
	static String input[] = new String[10];
	static String val;
	
	public static void initializeArr() {
		val = "2 15000.1 15000.3 15000.1 15000.3 15000.";
	}
	private static void lightup(PrintWriter writer, int pinVal, int duration, int nextpinVal) {
		//turn off the rest
		int holdDirtime = 0;
		writer.println("	uBit.io.P0.setDigitalValue(0);");
		writer.println("	uBit.io.P1.setDigitalValue(1);");
		writer.println("	uBit.io.P2.setDigitalValue(0);");
		
		//set how long you will keep the light in one direction
		if (duration > 3000) {
			duration-=3000;
			holdDirtime = 3000;
		} else {
			holdDirtime = duration;
			duration =0;
		}
		
		if (nextpinVal != 1) {
			if (duration >= 6000) {
				//continue straight --> last 6 secs swap to next direction blinking mode
				writer.println("	uBit.sleep("+(duration-6000)+"); ");
				writer.println("	uBit.io.P1.setDigitalValue(0);");
				writer.println("	for (int i=0; i<5; i++) {");
				writer.println("	uBit.io.P"+nextpinVal+".setDigitalValue(1);");
				writer.println("	uBit.sleep(300); ");
				writer.println("	uBit.io.P"+nextpinVal+".setDigitalValue(0);");
				writer.println("	uBit.sleep(300);} ");
				writer.println("	uBit.io.P"+nextpinVal+".setDigitalValue(1);");
				writer.println("	uBit.sleep(3000); ");
				writer.println("	uBit.io.P1.setDigitalValue(1);");
			} else {
				writer.println("	uBit.io.P1.setDigitalValue(0);");
				writer.println("	for (int i=0; i<duration-3000; i+=600) {");
				writer.println("	uBit.io.P"+nextpinVal+".setDigitalValue(1);");
				writer.println("	uBit.sleep(300); ");
				writer.println("	uBit.io.P"+nextpinVal+".setDigitalValue(0);");
				writer.println("	uBit.sleep(300);} ");
				writer.println("	uBit.io.P1.setDigitalValue(1);");
			}
		} else {
			writer.println("	uBit.sleep("+duration+"); ");
		}
		
		
		
	}
	
	public static void microBitOutput() {
		PrintWriter writer = null;
		int timeFactor = 1;
		try {
			
			String[] singleCommands = null;
			//break up into single commands of direction and duration
			singleCommands = val.split("\\.");
			
			//break up into duration and line commands
			String[][] dirNtime = new String[singleCommands.length][2];
			for (int i=0; i<singleCommands.length; i++) {
				dirNtime[i] = singleCommands[i].split(" ");
			}
			
			writer = new PrintWriter("main.cpp");
			writer.println("#include \"MicroBit.h\"");
			writer.println("#include <string.h>");
			writer.println();
			writer.println("MicroBit uBit;");
			writer.println("int main() {");
			writer.println("	//int timeFactor = 10;");
			writer.println("	uBit.init();");
			
			int duration, pinVal=1,nextpinVal;
			
			for (int i=0; i<dirNtime.length; i++) {
				duration = Integer.parseInt(dirNtime[i][1]);
				duration /= timeFactor;
				nextpinVal=1;
				if (Integer.parseInt(dirNtime[i][0]) == 1) {
					//left
					pinVal = 0;
					
					//get next pin value if there is one
					if ((i+1)<dirNtime.length) {
						if (Integer.parseInt(dirNtime[i+1][0]) == 1) nextpinVal = 0;
						if (Integer.parseInt(dirNtime[i+1][0]) == 3) nextpinVal = 2;
					}
					//only light for short while
					lightup(writer,pinVal,duration, nextpinVal);
					
				} else if (Integer.parseInt(dirNtime[i][0]) == 2) {
					//straight
					pinVal = 1;
					if ((i+1)<dirNtime.length) {
						if (Integer.parseInt(dirNtime[i+1][0]) == 1) nextpinVal = 0;
						if (Integer.parseInt(dirNtime[i+1][0]) == 3) nextpinVal = 2;
					}
					lightup(writer,pinVal,duration, nextpinVal);
				} else if (Integer.parseInt(dirNtime[i][0]) == 3) {
					//right
					pinVal = 2;
					if ((i+1)<dirNtime.length) {
						if (Integer.parseInt(dirNtime[i+1][0]) == 1) nextpinVal = 0;
						if (Integer.parseInt(dirNtime[i+1][0]) == 3) nextpinVal = 2;
					}
					lightup(writer,pinVal,duration, nextpinVal);
				}
			}
			writer.println("	release_fiber(); }");
			
		} catch (Exception e) {
			System.err.println("We're dead");
		}
		writer.close();
		System.out.println("Print successful");
	}
}

   
    
