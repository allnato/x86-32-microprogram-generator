package prg.view;

import prg.object.Memory;
import prg.object.Operand;

public class MovPrint {

	private static StringBuilder sb;
	private static Memory m;
	
	public MovPrint() {
		
	}
	
	public static void display(Operand o, Memory memInst) {
		
		m = memInst;
		sb = new StringBuilder();
		String[] tokens = o.getOpContent();
		
		sb.append("IPout, MARin, READ, WMFC, ALUin, Set Y to 1, ADD, Zin\n");
		sb.append("Zout, IPin\n");
		sb.append("MDRout, IRin\n");
		
		switch(o.getOpSequence()) {
		case "r r":
		case "r i": // same microprogramming
			movRegReg(tokens[0], tokens[1]);
			break;
		case "r m":
			movRegMem(tokens[0]);
			break;
		case "m r":
			movMemReg(tokens[1]);
			break;
		case "p m i":
			movMemReg(tokens[2]); // same microprogramming
			break;
		}
		
		sb.append("END");
		System.out.print(sb);
	}
	
	
	
	private static void movRegReg(String dst, String src) {
		sb.append(src + "out, " + dst + "in\n");
	}
	
	private static void movRegMem(String dst) {
		String str;
		str = m.parseMemory(); // remember that below is incomplete
		sb.append(str);
		sb.append("out, MARin, READ, WMFC\n");
		sb.append("MDRout, " + dst + "in\n");
	}
	
	private static void movMemReg(String src) {
		String str;
		sb.append(src + "out, MDRin\n");
		str = m.parseMemory();
		sb.append(str);
		sb.append("out, MARin, WRITE, WMFC\n");
	}
}
