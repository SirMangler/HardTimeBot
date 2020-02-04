package HardTime;

import java.io.IOException;
import java.util.List;

import Discord.DiscordWrapper;
import HardTime.Input.Interpreter;
import HardTime.Output.AIOutput;
import Loader.BotLogger;
import Loader.ScriptLoader;

/**
 * @author SirMangler
 *
 * @date 14 Sep 2019
 */
public class BotLoader {

	public static Personality p;
	public static String[] roles = new String[] { "bots" };
	public static String[] races = new String[] { "nigger", "chink", "bleached", "javascript" };
	public static List<String> nouns;
	
	public static final boolean debug = true;
	public static AIOutput out;
	public static Thread msglistener;
	
	public static ScriptLoader SC;
	public static Interpreter iter = new Interpreter();
	static BotLogger log = new BotLogger("BotLoader");
	
	public static void main(String[] args) {
		try {		
			SC = new ScriptLoader();
			SC.loadScript();
			out = new AIOutput(SC);
			
			nouns = SC.loadNouns();
			
			log.info("Initialising Discord");
			new DiscordWrapper();
			
			
			//msglistener = new Thread(new MessageListener());
			//msglistener.start();
			
			System.out.println("\n");
			//out.randomLine("User", p);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
