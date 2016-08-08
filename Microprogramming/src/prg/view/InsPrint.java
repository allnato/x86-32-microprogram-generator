package prg.view;

import prg.object.Memory;
import prg.object.Operand;

public class InsPrint {

	private static StringBuilder sb;
	private static Memory m;
	
	public InsPrint() {
		
	}
	
	public static void display(Operand o, Memory memInst) {
		
		m = memInst;
		sb = new StringBuilder();
		String[] tokens = o.getOpContent();
		
		sb.append("IPout, MARin, READ, WMFC, ALUin, Set Y to 1, ADD, Zin\n");
		sb.append("Zout, IPin\n");
		sb.append("MDRout, IRin\n");
		
		switch(o.getOpSequence()) {
			case "r":
				insReg(tokens[0]);
				break;
			case "p m":
				insMem();
				break;
		}
		
		sb.append("END");
		System.out.print(sb);
	}
	
	private static void insReg(String dst) {
		sb.append(dst + "out, ALUin, Set Y to 1, ADD, Zin\n");
		sb.append("Zout, " + dst + "in\n");
	}
	
	private static void insMem() {
		String str;
		str = m.parseMemory();
		
		sb.append(str);
		sb.append("out, MARin, READ, WMFC\n");
		sb.append("MDRout, ALUin, Set Y to 1, ADD, Zin\n");
		sb.append("Zout, MDRin, WRITE, WMFC\n");
	}
}
