package Loader;

import HardTime.BotLoader;

/**
 * @author SirMangler
 *
 * @date 14 Sep 2019
 */
public class BotLogger {

	private static final String ANSI_RESET = "\033[0m";
	private static final String ANSI_BLACK = "\u001B[30m";
	private static final String ANSI_RED = "\u001B[31;1m";
	private static final String ANSI_GREEN = "\033[0;32;1m";
	private static final String ANSI_YELLOW = "\033[0;33m";
	private static final String ANSI_BLUE = "\u001B[34m";
	private static final String ANSI_PURPLE = "\u001B[35m";
	private static final String ANSI_CYAN = "\u001b[36m";
	private static final String ANSI_WHITE = "\u001B[37m";
	String name;
	String formatname;
	
	public BotLogger(String name) {
		this.name=name;
		
		formatname = "["+name+"] "+ANSI_RESET;
	}
	
	public void info(String info, String... strs) {
		for (int i = 0; i < strs.length; i++) {
			info = info.replace("%"+(i+1), ANSI_GREEN+strs[i]+ANSI_RESET);
		}

		System.out.println(ANSI_YELLOW+formatname+info);
	}
	
	public void info(String info) {
		System.out.println(ANSI_YELLOW+formatname+info);
	}
	
	public void debug(String info) {
		if (BotLoader.debug) {
			info = info.replace("{", ANSI_RED+"{");
			info = info.replace("}", "}"+ANSI_GREEN);
			System.out.println(ANSI_CYAN+formatname+info);
		}
	}
	
	public void debug(String info, String... strs) {
		if (BotLoader.debug) {		
			for (int i = 0; i < strs.length; i++) {
				info = info.replace("%"+(i+1), ANSI_GREEN+strs[i]+ANSI_RESET);
				info = info.replace("{", ANSI_RED+"{");
				info = info.replace("}", "}"+ANSI_GREEN);
			}
			
			System.out.println(ANSI_CYAN+formatname+info);
		}
	}
	
	@Override
	public String toString() {
		return name;
	}
}
