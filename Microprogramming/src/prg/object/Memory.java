package prg.object;

public class Memory {

	private String[] memContent;
	private String memSequence;
	
	public Memory() {
		
	}

	public String[] getMemContent() {
		return memContent;
	}

	public void setMemContent(String[] memContent) {
		this.memContent = memContent;
	}

	public String getMemSequence() {
		return memSequence;
	}

	public void setMemSequence(String memSequence) {
		this.memSequence = memSequence;
	}
	
	public String parseMemory() {
		String str = "";
		
		switch(memSequence) {
			case "r":
			case "v":
				str = memContent[0];
				break;
			case "r+v":
				str = memContent[0] + "out, ALUin\n" + memContent[1] + "out, Yin, ADD, Zin\nZ";
				break;
			case "r*c":
				str = memContent[0] + "out, ALUin\n" + memContent[1] + "out, Yin, MUL, Zin\nZ";
				break;
			case "r+r*c":
				str = memContent[1] + "out, ALUin\n" + memContent[2] + "out, Yin, MUL, Zin\nZout, Yin\n"
						+ memContent[0] + "out, ALUin, ADD, Zin,\nZ";
				break;
			case "r*c+v":
				str = memContent[0] + "out, ALUin\n" + memContent[1] + "out, Yin, MUL, Zin\nZout, ALUin\n"
					+ memContent[2] + "out, Yin, ADD, Zin\nZ";
				break;
			case "r+r*c+v":
				str = memContent[1] + "out, ALUin\n" + memContent[2] + "out, Yin, MUL, Zin\nZout, Yin\n"
						+ memContent[0] + "out, ALUin, ADD, Zin,\nZout, ALUin\n" + memContent[3] + "out, Yin, ADD, Zin\nZ";
				break;
		}
		
		return str;
	}
}
