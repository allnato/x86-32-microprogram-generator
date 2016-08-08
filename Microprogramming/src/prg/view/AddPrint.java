package prg.view;

import prg.object.Memory;
import prg.object.Operand;

public class AddPrint {

	private static StringBuilder sb;
	private static Memory m;
	
	public AddPrint() {
		
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
			addRegReg(tokens[0], tokens[1]);
			break;
		case "r m":
			addRegMem(tokens[0]);
			break;
		case "m r":
			addMemReg(tokens[1]);
			break;
		case "p m i":
			addMemReg(tokens[2]); // same microprogramming
			break;
		}
		
		sb.append("END");
		System.out.print(sb);
	}
	
	private static void addRegReg(String dst, String src) {
		sb.append(src + "out, ALUin\n");
		sb.append(dst + "out, Yin, ADD, Zin\n");
		sb.append("Zout, " + dst + "in\n");
	}
	
	private static void addRegMem(String dst) {
		String str;
		str = m.parseMemory();
		
		sb.append(str);
		sb.append("out, MARin, READ, WMFC\n");
		sb.append("MDRout, ALUin\n");
		sb.append(dst + "out, Yin, ADD, Zin\n");
		sb.append("Zout, " + dst + "in\n");
	}
	
	private static void addMemReg(String src) {
		String str;
		str = m.parseMemory();
		
		sb.append(str);
		sb.append("out, MARin, READ, WMFC\n");
		sb.append("MDRout, ALUin\n");
		sb.append(src + "ot, Yin, ADD, Zin\n");
		sb.append("Zout, MDRin, WRITE, WMFC\n");
	}
}
