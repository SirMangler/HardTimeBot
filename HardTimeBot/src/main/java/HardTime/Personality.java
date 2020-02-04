package HardTime;

import java.io.File;

import HardTime.Output.AIOutput;
import net.dv8tion.jda.core.entities.Member;

/**
 * @author SirMangler
 *
 * @date 14 Sep 2019
 */
public class Personality {

	public static enum Type { warden, inmate, neither };
	
	public Type type = Type.neither;
	public String me;
	public String channel = "console";
	public String[] roles;
	public File icon;
	public Member m;
	
	public Personality() {		
		if (AIOutput.r.nextInt(2) == 1) {
			type = Type.warden;
			me = "Warden "+BotLoader.SC.getRandomWord("surname");
			
			File[] files = new File(ClassLoader.getSystemResource("wardens").getPath()).listFiles();
			icon = files[AIOutput.r.nextInt(files.length)];
		} else {
			type = Type.inmate;
			me = BotLoader.SC.getRandomWord("firstname")+" "+BotLoader.SC.getRandomWord("surname");
			
			File[] files = new File(ClassLoader.getSystemResource("inmates").getPath()).listFiles();
			icon = files[AIOutput.r.nextInt(files.length)];
		}
	}
}
