package Discord;

import HardTime.Personality;
import net.dv8tion.jda.core.JDA;

/**
 * @author SirMangler
 *
 * @date 15 Sep 2019
 */
public class MessageTarget {

	public Personality p;
	public String channelid;
	public String message;
	
	public MessageTarget(Personality p, String channelid, String message) {
		this.p=p;
		this.channelid=channelid;
		this.message=message;
	}
}
