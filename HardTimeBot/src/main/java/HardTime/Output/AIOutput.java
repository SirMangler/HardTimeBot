package HardTime.Output;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import javax.swing.DebugGraphics;

import Discord.MessageTarget;
import HardTime.BotLoader;
import HardTime.Personality;
import HardTime.Personality.Type;
import HardTime.Input.Interpreter;
import Loader.BotLogger;
import Loader.ScriptLoader;
import Loader.Tuple;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * @author SirMangler
 *
 * @date 14 Sep 2019
 */
public class AIOutput {


	public static Random r = new Random();
	public static List<Dialogue> random;
	public static List<Dialogue> insult;
	
	static Thread thr_queue;
	static Queue<MessageTarget> q = new LinkedList<MessageTarget>();
	
	ScriptLoader SC;
	BotLogger log = new BotLogger("AIOutput");
	
	String name = "AIOutput";
	
	public AIOutput(ScriptLoader sc) {
		this.SC=sc;
		random = sc.dialogue.get("random");
		insult = sc.dialogue.get("insulted");
	}

	public void queue(MessageTarget c) {
		if (c.p.m == null)
			log.debug("Queuing %1", c.message);
		else {
			log.debug("Queuing [%1] %2", c.channelid, c.message);
		}
		
		q.add(c);

		if (thr_queue == null) {
			thr_queue = new Thread(new QueueThread());
			
			thr_queue.start();
		} else if (!thr_queue.isAlive()) {
			thr_queue = new Thread(new QueueThread());
			thr_queue.start();
		}
	}
	
	
	//public String randomLine(String name, Personality p) {
	//	Dialogue d = random.get(r.nextInt(random.size()));
	//	if (d.type == p.type) {
	//		return gatherResponse(d, name, p);
	//	} else {
	//		return randomLine(name, p);
	//	}
	//}
	
	public String triggerDialogue(String key, String name, Personality p, String c) throws Exception {
		List<Dialogue> ld = SC.dialogue.get(key);
		if (ld == null) throw new Exception("Cannot find key: "+key);
		
		Dialogue dialogue = null;
		for (Dialogue d : SC.dialogue.get(key)) {
			int probability = r.nextInt(100);
			log.debug("random %1 < dialogue %2", probability+"", d.probability+"");
			if (probability < d.probability) {
				dialogue = d;
				break;
			}
		}
		
		if (dialogue == null) {
			log.debug("No dialogue.");
			return "n/a";
		}

		if (dialogue.type == Type.neither || dialogue.type == p.type) {
			return gatherResponse(dialogue, name, p, c);
		} else {
			return triggerDialogue(key, name, p, c);
		}
	}
	
	public String triggerWeaponDialogue(String noun, String name, Personality p, String c) throws Exception {
		if (r.nextInt(100) < 85) {
			List<Dialogue> ld = SC.dialogue.get("weapon");
			
			Dialogue dialogue = null;
			for (Dialogue d : ld) {
				if (r.nextInt(100) < d.probability) {
					dialogue = d;
					break;
				}
			}
			
			if (dialogue == null) return "n/a";

			if (dialogue.type == Type.neither || dialogue.type == p.type) {
				String line = dialogue.lines.get(r.nextInt(dialogue.lines.size()));

				String response = line.replace("{weapon}", noun);
				response = ciperGeneric(response, name, p);
				
				queue(new MessageTarget(p, c, response));
				Interpreter.submitDialogue(name, dialogue);
				
				return response;
			} else {
				return triggerWeaponDialogue(noun, name, p, c);
			}
		}
		
		return null;
	}
	
	public String triggerChannelDialogue(String channel, String name, Personality p, String c) throws Exception {
		if (r.nextInt(100) < 85) {
			List<Dialogue> ld = SC.dialogue.get("channel");
			
			Dialogue dialogue = null;
			for (Dialogue d : ld) {
				if (r.nextInt(100) < d.probability) {
					dialogue = d;
					break;
				}
			}
			
			if (dialogue == null) return "n/a";

			if (dialogue.type == Type.neither || dialogue.type == p.type) {
				String line = dialogue.lines.get(r.nextInt(dialogue.lines.size()));

				String response = line.replace("{currentchannel}", channel);
				response = ciperGeneric(response, name, p);
				
				queue(new MessageTarget(p, c, response));
				Interpreter.submitDialogue(name, dialogue);
				
				return response;
			} else {
				return triggerChannelDialogue(channel, name, p, c);
			}
		}
		
		return null;
	}
	
	public String triggerFriendDialogue(Personality friend, String name, Personality p, String c) throws Exception {
		if (r.nextInt(100) < 85) {
			List<Dialogue> ld = SC.dialogue.get("friendinsulted");
			
			Dialogue dialogue = null;
			for (Dialogue d : ld) {
				if (r.nextInt(100) < d.probability) {
					dialogue = d;
					break;
				}
			}
			
			if (dialogue == null) return "n/a";

			if (dialogue.type == Type.neither || dialogue.type == p.type) {
				String line = dialogue.lines.get(r.nextInt(dialogue.lines.size()));

				String response = line.replace("{friend}", p.me);
				response = ciperGeneric(response, name, friend);
				
				queue(new MessageTarget(p, c, response));
				Interpreter.submitDialogue(name, dialogue);
				
				return response;
			} else {
				return triggerFriendDialogue(friend, name, p, c);
			}
		}
		
		return null;
	}
	
	public String gatherResponse(Dialogue d, String name, Personality p, String c) {
		String line = d.lines.get(r.nextInt(d.lines.size()));

		String response = ciperGeneric(line, name, p);
		queue(new MessageTarget(p, c, response));
		Interpreter.submitDialogue(name, d);
		
		return response;
	}
	
	public String gatherYes(Dialogue d, String name, Personality p, String c) {
		String line = d.yes.get(r.nextInt(d.lines.size()));
		
		String response = ciperGeneric(line, name, p);
		queue(new MessageTarget(p, c, response));
		
		return response;
	}
	
	public String gatherNo(Dialogue d, String name, Personality p, String c) {
		String line = d.no.get(r.nextInt(d.lines.size()));

		String response = ciperGeneric(line, name, p);
		queue(new MessageTarget(p, c, response));
		
		return response;
	}
	
	public String ciperGeneric(String line, String name, Personality p) {
		StringBuilder response = new StringBuilder();
		for (String word : line.split(" ")) {
			if (word.contains("{user}")) response.append(word.replace("{user}", name)+" ");
			else if (word.contains("{me}")) response.append(word.replace("{me}", p.me)+" ");
			else if (word.contains("{channel}")) response.append(word.replace("{channel}", p.channel)+" ");
			else if (word.contains("{role}")) {
				if (p.roles.length != 0) 
					response.append(word.replace("{role}", p.roles[r.nextInt(p.roles.length)])+" ");
				else response.append(word.replace("{role}", "bots"));
			}
			else if (word.contains("{weapon}")) response.append(word.replace("{weapon}", SC.getRandomWord("weapon"))+" ");
			else if (word.contains("{race}")) response.append(word.replace("{race}", BotLoader.races[r.nextInt(BotLoader.races.length)])+" ");
			else if (word.contains("{friend}")) response.append(word.replace("{friend}", "friend")+" ");
			else if (word.contains("{randomuser}")) response.append(word.replace("{randomuser}", "enemy")+" ");
			else if (word.contains("{reasons}")) response.append(word.replace("{reasons}", SC.getRandomWord("reasons"))+" ");
			else if (word.contains("{cash}")) response.append(word.replace("{cash}", formatCash(lerp(1, 1000, (Math.pow(r.nextDouble(), 2)))))+" ");
			else response.append(word+" ");
		}

		return response.toString().trim().replace("\\n", "\r\n");
	}
	
	public String formatCash(double input) {
		int i = (int) input;
		String formatted;
		if (input > 100) {
			formatted = ""+i;
			formatted = formatted.substring(0, formatted.length()-1)+"0";
		} else formatted = i+"";
		
		return "$"+formatted; 
	}
	
	public static double lerp(double min, double max, double factor) {
		return min+(max-min)*factor;
	}
}

class QueueThread implements Runnable {

	BotLogger log = new BotLogger("QueueThread");
	
	@Override
	public void run() {
		while (!AIOutput.q.isEmpty()) {
			try {
				Thread.sleep(1000);
				
				MessageTarget l = AIOutput.q.poll();
				
				log.info("<%1> Sending '%2'", l.p.me, l.message);
				
				if (l.p.m != null) {
					l.p.m.getJDA().getTextChannelById(l.channelid).sendMessage(l.message).complete();
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
