package HardTime.Input;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.HashMap;

import Discord.DiscordWrapper;
import HardTime.MessageListener;
import HardTime.Personality;
import HardTime.Output.Dialogue;

/**
 * @author SirMangler
 *
 * @date 14 Sep 2019
 */
public class Interpreter {

	public static String PAT_your = "(?:your |you)(?:a?)";
	public static String[] insults = new String[] { "fuck", "scum", "idiot", "stupid", "ass", "cunt", "retard", "broken", "faggot", "fag", "inept", "jerk", "dunce", "cretin", "spastic", "twat", "imbecile", "dumb", "suck" };
	public static String[] positives = new String[] { "yes", "okay", "sure", "fine", "deal", "cool", "alright", "thanks" };
	public static String[] negatives = new String[] { "no", "nope", "no way", "na", "no thanks", "go away" };
	
	public static HashMap<String, Dialogue> waiting = new HashMap<String, Dialogue>();
	public static void submitDialogue(String user, Dialogue d) {
		if (!d.yes.isEmpty() && !d.no.isEmpty())
			waiting.put(user, d);
	}
	
	public static int interpretAnswer(String line) {
		String formatted = line.toLowerCase().replaceAll("[^a-zA-Z ]", "").replace("an", "a").replace("youre", "your").replace("thats", "");
		
		if (isInsult(formatted)) return 1;
		
		for (String positive : positives) {
			if (formatted.matches("\b("+positive+")\b")) {
				return 0;
			}
		}
		
		for (String negative : negatives) {
			if (formatted.contains(negative)) {
				return 1;
			}
		}
		
		return -1;
	}
	
	public static boolean isInsult(String formatted) {
		formatted = formatted.toLowerCase().replaceAll("[^a-zA-Z ]", "").replace("an", "a").replace("youre", "your").replace("thats", "");
		
		for (String insult : insults) {
			if (formatted.contains(insult)) {
				if (formatted.matches(PAT_your+"("+insult+")")) return true;
				else if (formatted.contains(insult+" you")) return true;
				else if (formatted.contains(insult+" off")) return true;
			}
		}
		
		return false;
	}
	
	public static String isCommand(String formatted) {
		formatted = formatted.toLowerCase();
		
		if (formatted.equalsIgnoreCase(".resetcooldown")) {
			MessageListener.timer = Instant.now();
			return "Cooldown Reset";
		}
		
		if (formatted.equalsIgnoreCase(".reroll")) {
			DiscordWrapper.rollBots();
			return "Rolled bots.";
		}
		
		if (formatted.equalsIgnoreCase(".remainingcooldown")) {
			Calendar.getInstance();
			int remaining = Instant.now().get(ChronoField.MINUTE_OF_DAY) - MessageListener.timer.get(ChronoField.MINUTE_OF_DAY);
			
			if (remaining > 0)
				return "Remaining Cooldown "+remaining+" minutes.";
			else return "Cooldown has elapsed.";
		}
		
		return null;
	}
}
