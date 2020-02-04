package HardTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Calendar;

import Discord.DiscordWrapper;
import Discord.MessageTarget;
import HardTime.Input.Interpreter;
import HardTime.Output.AIOutput;
import HardTime.Output.Dialogue;
import Loader.BotLogger;

/**
 * @author SirMangler
 *
 * @date 14 Sep 2019
 */
public class MessageListener implements Runnable  {

	Personality p = new Personality();
	static BotLogger log = new BotLogger("MessageListener");
	
	@Override
	public void run() {
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

		String name = "User";
		try {
			String line;
			while ((line = r.readLine()) != null) {
				handleMessage(name, line, p, null);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean handleMessage(String name, String line, Personality p, String c) {
		if (Interpreter.waiting.containsKey(name)) {
			int i = Interpreter.interpretAnswer(line);
			Dialogue d = Interpreter.waiting.get(name);
			
			if (i == 1) BotLoader.out.gatherNo(d, name, p, c);
			else if (i == 0) BotLoader.out.gatherYes(d, name, p, c);
			
			Interpreter.waiting.remove(name);
			return true;
		} else {
			try {
				String com;
				if (Interpreter.isInsult(line)) {
					log.debug("Handling insult");
					
					if (DiscordWrapper.bots != null) {
						if (AIOutput.r.nextInt(100) > 30) {
							Personality bot = DiscordWrapper.bots[AIOutput.r.nextInt(DiscordWrapper.bots.length)];
							while (bot.me == p.me) {
								bot = DiscordWrapper.bots[AIOutput.r.nextInt(DiscordWrapper.bots.length)];
							}
							
							BotLoader.out.triggerFriendDialogue(bot, name, p, c);
							return true;
						}
					}
					
					BotLoader.out.triggerDialogue("insulted", name, p, c);		
					return true;
				} else if ((com = Interpreter.isCommand(line)) != null) {
					log.debug("Handling command: %1", com);
					BotLoader.out.queue(new MessageTarget(p, c, com));
				} else {
					log.debug("Handling random");
					BotLoader.out.triggerDialogue("random", name, p, c);
				}
				
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	public static Instant timer = Instant.now();
	public static boolean handleIndirectMessage(String name, String line, Personality p, String c) {
		Instant now = Instant.now();
		
		if (now.isAfter(timer)) {
			timer = Instant.now();
			
			int cooldown = (int) AIOutput.lerp(5, 1800, (Math.pow(AIOutput.r.nextDouble(), 3)));
			timer = timer.plusSeconds(cooldown);
			log.debug("Cooldown: %1 seconds. (%2 minutes)", cooldown+"", (cooldown/60)+"");
			
			for (String noun : BotLoader.nouns) {
				if (line.toLowerCase().contains(noun.toLowerCase())) {
					try {
						BotLoader.out.triggerWeaponDialogue(noun, name, p, c);
												
						return true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			//BotLoader.out.triggerDialogue(key, name, p, c)
		} else {
			log.debug("Awaiting Cooldown");
		}
		
		return false;
	}
}
