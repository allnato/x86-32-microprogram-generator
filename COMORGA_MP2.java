import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;


public class COMORGA_MP2 {
	
	private static StringBuilder sb;
	private static String seq; // think of a way in the future so that this will not be a global variable - temp fix
	
	// non-universal
	public static void main(String[] args) throws Exception {

		/**
		 * Coverage: MOV, ADD, INC
		 * GOAL: Check if input is valid or not
		 * Relative addresses are currently not supported
		 */

		//200-208 (flag) chkMismatch(String seq) //to be used by the microprogramming
		//186-199 (flag) chkAddrMode(String seq)
		//45-63 (flag,str) parseStmt(String str)
		//145-155 int argCount(String str)
		// Add *Welcome to Microprogramming Converter* Supported instructions: MOV, ADD, INC
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		String str = "";
		String parsed = "";
		String[] tokens = null;
		int flag = 0;
		
		HashMap <String, String> syntax = new HashMap <String, String>();
		// in the future, let the program itself generate the regular expressions and add them to the HashMap
		syntax.put("MOV", "(p_[0-9]{1,2} )*[rm]_[0-9]{1,2} [rmi]_[0-9]{1,2}"); // spaces are important!
		syntax.put("ADD", "(p_[0-9]{1,2} )*[rm]_[0-9]{1,2} [rmi]_[0-9]{1,2}");
		syntax.put("INC", "(p_[0-9]{1,2} )*[rm]_[0-9]{1,2}");
		
		System.out.print("Enter x86 assembly code(s): Type 'exit' to quit.\n>");
		
		while(!(str = br.readLine()).equalsIgnoreCase("exit")) {
			
			// initialize global variables
			sb = new StringBuilder();
			seq = ""; // truly crucial not only in input checking but also in microprogramming and other assembly operations
			
			if(str.trim().isEmpty())
				flag = -8;// sb.append("ERROR: There is no input.");
			else { //////////// cannot be converted to function because it returns the flag and parsed string
				str = str.toUpperCase().trim();
				parsed = str.split(" ")[0];
				if(syntax.containsKey(parsed)) { // check first here if it contains single or no parameters //(parsed = parseComma(parsed, str))
					if((str = parseMemory(str)) != null) { // parseMemory should start first before comma!!!!!!!!!!!!!!!!!!!
						flag = syntax.get(parsed).length() - syntax.get(parsed).replace(" ", "").length();
						if((parsed = parseComma(flag, str)) != null) {
							str = parsed;
							tokens = str.split("[ ]+");
							flag = chkIns(syntax, tokens); // check first for invalid combinations: immediate as src, memory to memory, prefix if not memory to immediate,
						}
						else
							flag = -9;//sb.append("ERROR: Invalid memory value syntax.");
					}
					else
						flag = -4;// sb.append("ERROR: Invalid comma syntax.");
				}
				else
					flag = -1;
			}
			
			switch(flag) {
				case 0:
					sb.append("Microprogramming Conversion:");
					break;
				case -1:
					sb.append("ERROR: Either invalid or unsupported instruction.");
					break;
				case -2:
					sb.append("ERROR: Invalid " + tokens[0] + " syntax.");
					break;
				case -3:
					sb.append("ERROR: Invalid prefix value syntax.");
					break;
				case -4:
					sb.append("ERROR: Invalid memory value syntax.");
					break;
				case -5:
					sb.append("ERROR: Invalid immediate value syntax.");
					break;
				case -6:
					sb.append("ERROR: Invalid register value syntax.");
					break;
				case -7:
					sb.append("ERROR: Data size mismatch.");
					break;
				case -8:
					sb.append("ERROR: There is no input.");
					break;
				case -9:
					sb.append("ERROR: Missing or invalid comma syntax.");
					break;
				case -10:
					sb.append("ERROR: Invalid number of " + tokens[0] + " argument(s).");
			}
			
			// print microprogramming here
			// use StringBuilder to find the r_
			// if(seq.equals("r r"))
			//   mov(String sequence, String[] tokens);
			
			System.out.print("\n" + sb.toString() + "\n\n>");
		}
	}
	
	// universal
	private static String parseComma(int numArgs, String str) { // fix for single or no parameter instructions
		if(numArgs < 2 || str.split("[ ]+").length <= 2)
			return str;
		else if(Pattern.matches("(\\w+\\s+)+[A-Z\\[\\]+*0-9]+\\s*,\\s*[^,]+$", str))
			return str.replaceAll(",", " ");
		return null;
	}
	// universal
	private static String parseMemory(String str) {
		String mem, end;
		
		if(Pattern.matches("(\\w+\\s+)+\\[.+].*", str)) {
			if(str.length() - str.replaceAll("\\[", "").length() == 1 && str.length() - str.replaceAll("]", "").length() == 1) {
				mem = str.substring(str.indexOf("[") + 1, str.indexOf("]"));
				end = str.substring(str.indexOf("]")); // end of string
				str = str.substring(0, str.indexOf("[") + 1); // start of string
			}
			else
				return null; // means error
			mem = mem.replaceAll(" ", "");
			str = str + mem + end;
		}
		return str; // means either there is no mem mode or successful parsing
	}
	
	private static int chkArgCount(String regEx, int tokenLength) {
		int count;
		
		if(regEx.split(" ").length == 0)
			if(tokenLength == 1)
				return 0;
			else
				return -1;
		
		count = regEx.length() - regEx.replace(" ", "").length();
		if(tokenLength >= count + 1 && tokenLength <= count + 2)
			return 0;
		return -1;
	}
	
	// non-universal due to the various cases offered by others
	private static int chkIns(HashMap <String, String> syntax, String[] tokens) {
		
		if(chkArgCount(syntax.get(tokens[0]), tokens.length) == -1)
			return -10;

		// check first the type of each token
		int pfxBit = 0;
		int regBit = 0;
		int immBit = 0;
		int tempBit = 0; // fix the pfxBit and regBit bug
		
		for(int i = 1; i < tokens.length; i++) {
			String inst = tokens[i];
			seq += " "; // for parsing the input type
			
			////////System.out.println(i + ": " + inst);
			
			if((tempBit = chkPrefix(inst)) != -1) {
				pfxBit = tempBit;
				if(i != 1)
					return -3; // ERROR: Invalid prefix value syntax.
				seq += "p_" + pfxBit;
			}
			else if(inst.charAt(0) == '[') {
				if(chkMemory(inst) == null)
					return -4; //sb.append("ERROR: Invalid memory value syntax.");
				
				if(pfxBit != 0)
					seq += "m_" + pfxBit; // memory bits can only be identified if and only if it is after a prefix or register
				else if(regBit != 0)
					seq += "m_" + regBit;
				else
					seq += "m_0";
			}
			else if(Pattern.matches("[0-9]",String.valueOf(inst.charAt(0)))) { /////// CHECK FIRST IF THE 2ND TOKEN IS A PREFIX!
				if((pfxBit == 0 && (immBit = chkImmediate(inst)) != -1) || 
						(pfxBit != 0 && (immBit = chkImmediate(tokens[1], inst)) != -1)) {
					seq += "i_" + immBit;
					continue;
				}
				return -5; // ERROR: Invalid immediate value syntax.
			}
			else if(Pattern.matches("[A-ES]", String.valueOf(inst.charAt(0)))) {
				if((tempBit = chkRegister(inst)) == -1)
					return -6; // ERROR: Invalid register value syntax.
				else if(regBit != 0 && tempBit != regBit)
					return -7;
				regBit = tempBit;
				seq += "r_" + regBit;
			}
			else // put address code here in the future
				return -2;
		}
		
		// check first with truly invalid combinations before proceeding
		
		seq = seq.trim();
		
		// convert this to 2 functions
		
		// it only matches the invalid
		// register with prefix comb
		String regPref = "^p_[0-9]{1,2}(\\s+\\w+)*\\sr_[0-9]{1,2}.*";
		// memory to memory
		String memMem = ".*m_[0-9]{1,2}.*m_[0-9]{1,2}.*";
		// immediate as destination
		String immDest = "^i_[0-9]{1,2}\\s[rmi]|^p_[0-9]{1,2} i_[0-9]{1,2}[^\\.]";
		// no prefix in memory to immediate or one memory or one immediate in one parameter
		String memImm = "^m_[0-9]{1,2} i_[0-9]{1,2}( [rmi]_[0-9]{1,2})*|^m_[0-9]{1,2}|^i_[0-9]{1,2}";
		// prefix as src or truncate
		String prefSrc = "^m_[0-9]{1,2}\\s*\\w*\\s*\\sp";
		
		////System.out.println(seq);
		
		// check for invalid assembly addressing mode combinations; if they match, return
		if(Pattern.matches(regPref, seq) || Pattern.matches(memMem, seq) || Pattern.matches(immDest, seq) ||
				Pattern.matches(memImm, seq) || Pattern.matches(prefSrc, seq))
			return -2;
		else if(Pattern.matches("^r_[0-9]{1,2} \\w*\\s*r", seq)) { // check for data size mismatches
			String[] regBitArr = seq.replaceAll("r_","").trim().split(" ");
			for(String s: regBitArr) {
				if(regBit != Integer.parseInt(s))
					return -7; // ERROR: Data size mismatch
			}
		}
		else if((pfxBit != 0 && pfxBit < immBit) || (regBit != 0 && immBit > regBit))
			return -7;
		
		// finally do the instruction-specific checking here - if it does not match here, send the return code
		
		if(!Pattern.matches(syntax.get(tokens[0]), seq))
			return -2; // invalid <INS> syntax
		
		sb.append("Sequence: " + seq + "\n"); ///////////////////////////////////////////////////
		
		return 0;
	}
	
	/**
	 * Before being passed to one of these functions, the program already knows if:
	 * it is a memory - it starts with [ 
	 * it is an immediate - it starts with 0-9 
	 * it is a register -  it starts with A-E,S
	 * */
	// universal
	private static int chkRegister(String val) {
		
		/* RegEx Declarations */
		
		String reg32b = "\\bE([A-D]X|[SB]P|[SD]I)\\b";
		String reg16b = "\\b[A-D]X|[SB]P|[SD]I\\b";
		String reg8b = "\\b[A-D][HL]\\b";
		
		if(Pattern.matches(reg32b, val))
			return 32;
		else if(Pattern.matches(reg16b, val))
			return 16;
		else if(Pattern.matches(reg8b, val))
			return 8;
		return -1;
	}
	// universal
	private static int chkImmediate(String val) {
		
		/* Immediate Declarations */
		
		String imm32b = "((^[0][A-F]|^[0-9])[0-9A-F]{4,7}H{0,1})$|^0X[0-9A-F]{5,8}$";
		String imm16b = "((^[0][A-F]|^[0-9])[0-9A-F]{2,3}H{0,1})$|^0X[0-9A-F]{3,4}$";
		String imm8b = "((^[0][A-F]|^[0-9])[0-9A-F]{0,1}H{0,1})$|^0X[0-9A-F]{1,2}$";
		
		if(Pattern.matches(imm8b, val)) /* do not change the order of Pattern or a bug will occur */
			return 8;
		else if(Pattern.matches(imm16b, val))
			return 16;
		else if(Pattern.matches(imm32b, val))
			return 32;
		return -1;
	}
	// universal
	private static int chkImmediate(String pfx, String val) {
		
		int bit = chkImmediate(val);
		
		if(bit <= chkPrefix(pfx))
			return bit;
		return -1;
	}
	// universal
	private static int chkPrefix(String val) {
		switch(val) {
			case "DWORD":
				return 32;
			case "WORD":
				return 16;
			case "BYTE":
				return 8;
		}
		return -1;
	}
	// universal
	private static int chkRelative(String val) {
		
		// the keywords might be delimited from x87 instructions and rename
		
		String[] keywords = {"MOV", "LEA", "PUSH", "POP", "ADD", "INC", "SUB", "CMP", "DEC", "NEG", "MUL",
							"IMUL", "DIV", "IDIV", "AND", "OR", "XOR", "NOT", "BT", "TEST", "JE", "JNE", "JB", 
							"JBE", "JA", "JAE", "JZ", "JNZ", "JNB", "JNBE", "JNA", "JNAE", "JG", "JGE", "JL", 
							"JLE", "JNG", "JNGE", "JNL", "JNLE", "LOOP", "JCXZ", "JECXZ", "CALL", "RET", "FLD",
							"FST", "FLD1", "FLDZ", "FLDL2T", "FLDL2E", "FLDLG2", "FLDPI", "FXCH", "FADD", "FMUL",
							"FSUB", "FSUBR", "FDIV", "FDIVR", "FABS", "FCHS", "FSQRT", "FPREM", "FRNDINT", "FSIN",
							"FCOS", "FSINCOS", "FPTAN", "FPATAN", "FX2M1", "FYL2X", "FYL2XPI", "FXTRACT", "FSCALE",
							"FINCSTP", "FDECSTP", "FFREE", "FINIT", "FNINIT", "FCLEX", "FNCLEX", "FSTCW", "FNSTCW",
							"FLDCW", "FSTSW", "FNSTSW", "FNOP", "FXAM", "FCOM", "FTST", "DB", "DD", "DW", "DQ",
							"TIMES", "SECTION", "GLOBAL", "INCLUDE", "EXTERN", "SEGMENT", ".DATA", ".TEXT", "_PRINTF",
							"_SCANF", "_GETCHAR", "_GETS", "PRINTF", "SCANF", "GETCHAR", "GETS"};
		
		Arrays.sort(keywords);
		
		if(chkPrefix(val) != -1 || Arrays.binarySearch(keywords, val) < 0) {
			
			if(Pattern.matches("([a-zA-Z_][a-zA-Z0-9_]*)", val))
				return 1;
		}
		return -1;
	}
	// universal
	private static int chkConstant(String val) {
		
		int constVal = -1;
		
		if(Pattern.matches("^-?\\d+$", val)) {
			constVal = Integer.parseInt(val);
		
			switch(constVal) {
				case 8:
				case 4:
				case 2:
				case 1:
					break;
				default:
					constVal = -1;
			}
		}
		return constVal;
	}
	// universal
	private static String[] chkMemory(String val) {
		
		val = val.substring(1, val.length() - 1).replaceAll("\\s+","");
		String[] tokens = val.replaceAll("[\\+\\*]", "\\|").split("\\|");
		String operands = val.replaceAll("[A-Za-z0-9_]", "");
		
		//System.out.println("val: " + val + " | tokens: " + tokens + " | operands: " + operands);
		//System.exit(0);
		// INC WORD [EAX + EBX]
		
		String scale = "";
		//int i;
		
		// check if it is a register first, then either a constant of 2^n or variable
		for(int i = 0; i < tokens.length; i++) { // identify first what kind of token they are
			if(chkRegister(tokens[i]) != -1)
				scale += "r";
			else if(chkConstant(tokens[i]) != -1) {
				scale += "c";
				if(i > 0 && (tokens[i-1].equals("ESP") || tokens[i-1].equals("SP"))) // SP is not allowed to partner with const
					return null;
			}
			else if(chkRelative(tokens[i]) != -1)
				scale += "v";
			else
				return null;
			
			if(i < tokens.length - 1)
				scale += Character.toString(operands.charAt(i));
		}
		
		String [] scaleFactor = {"r+r*c+v", "r+r*c", "r+v", "r*c+v", "r", "r*c", "v"}; // apply greedy algorithm
		
		Arrays.sort(scaleFactor);
		if(Arrays.binarySearch(scaleFactor, scale) >= 0)
			return tokens;
		return null;
	}
	
	//private static void movMicroPrint(String sequence, String[] tokens) {
		// Examples of String sequence:
		// p - prefix, r - register, m - memory, i - immediate
		// 8 - 8 bit, 16 - 16 bit, 32 - 32 bit, 0 - taga bitbit
		// p_32 m_32 i_32
		// r_16 r_16
		// r_8 i_8
	
		// Example of String[] tokens:
		// tokens[] = { "MOV", "DWORD", "[EAX+ALPHA]", "12345678" }
		// tokens[] = { "ADD", "EAX", "EBP" }
		// tokens[] = { "ADD", "AH", "0FFh" }
	
		// print microprogramming
	//}
	
	//private static void addMicroPrint(String sequence, String[] tokens) {
			// print microprogramming
	//}
	
	//private static void incMicroPrint(String sequence, String[] tokens) {
			// print microprogramming
	//}

}