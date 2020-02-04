package Discord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import HardTime.BotLoader;
import HardTime.MessageListener;
import HardTime.Personality;
import HardTime.Input.Relationship;
import HardTime.Output.AIOutput;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Icon;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author SirMangler
 *
 * @date 14 Sep 2019
 */
public class DiscordWrapper extends ListenerAdapter {

	static JDA BOT1;
	static JDA BOT2;
	static JDA BOT3;
	
	List<Relationship> relationships = new ArrayList<Relationship>();
	static Guild g;
	public static Personality[] bots;
	
	static String guildid = "270234493124608000";
	public DiscordWrapper() {
		try {
			BOT1 = new JDABuilder(AccountType.BOT).setToken(System.getenv("token1")).buildBlocking();
			BOT2 = new JDABuilder(AccountType.BOT).setToken(System.getenv("token2")).buildBlocking();
			BOT3 = new JDABuilder(AccountType.BOT).setToken(System.getenv("token3")).buildBlocking();
			
			BOT1.addEventListener(this);
			
			g = BOT1.getGuildById(guildid);
			
			for (Member m : g.getMembers()) {
				Relationship r = new Relationship();
				r.m=m;
				r.friend = AIOutput.r.nextInt(2) == 1 ? true : false;
				
				relationships.add(r);
			}
			
			//g.getMemberById("622547420953444402"), g.getMemberById("622547988954349623"), g.getMemberById("622548084555251722")
			
			rollBots();
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void rollBots() {
		List<TextChannel> cs = g.getTextChannels();
		
		Personality p1 = new Personality();
		p1.m = g.getMemberById("622547420953444402");
		p1.channel = cs.get(AIOutput.r.nextInt(g.getTextChannels().size())).getName();
		p1.roles = BOT1.getGuildById(guildid).getSelfMember().getRoles().toArray(new String[0]);
		BOT1.getGuildById(guildid).getController().setNickname(p1.m, p1.me).complete();
		try {
			BOT1.getSelfUser().getManager().setAvatar(Icon.from(p1.icon)).queue();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Personality p2 = new Personality();
		p2.m = g.getMemberById("622547988954349623");
		p2.channel = cs.get(AIOutput.r.nextInt(g.getTextChannels().size())).getName();
		p2.roles = BOT1.getGuildById(guildid).getSelfMember().getRoles().toArray(new String[0]);
		BOT2.getGuildById(guildid).getController().setNickname(p2.m, p2.me).complete();
		try {
			BOT2.getSelfUser().getManager().setAvatar(Icon.from(p2.icon)).queue();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Personality p3 = new Personality();
		p3.m = g.getMemberById("622548084555251722");
		p3.channel = cs.get(AIOutput.r.nextInt(g.getTextChannels().size())).getName();;
		p3.roles = BOT1.getGuildById(guildid).getSelfMember().getRoles().toArray(new String[0]);
		BOT3.getGuildById(guildid).getController().setNickname(p3.m, p3.me).complete();
		try {
			BOT3.getSelfUser().getManager().setAvatar(Icon.from(p3.icon)).queue();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		bots = new Personality[] { p1, p2, p3 };		
	}

	@Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
		if (event.getAuthor().equals(event.getJDA().getSelfUser())) return;
		System.out.println("<"+event.getTextChannel().getName()+"> "+event.getMember().getEffectiveName()+": "+event.getMessage().getContentDisplay());
		
		for (Member m : event.getMessage().getMentionedMembers()) {
			for (Personality bot : bots)
				if (m.equals(bot.m)) {
					String msg = event.getMessage().getContentStripped().replace("@"+bot.me+" ", "");
					if (MessageListener.handleMessage(event.getMember().getEffectiveName(), 
							msg, bot, event.getTextChannel().getId())) 
						return;
				}		
		}
				
		if (MessageListener.handleIndirectMessage(
				event.getMember().getEffectiveName(), 
				event.getMessage().getContentDisplay(), 
				randomPersonality(), 
				event.getTextChannel().getId())) return;
		
		for (Relationship r : relationships) {
			if (event.getMember().equals(r.m)) {
				if (!r.seenin.contains(event.getChannel().getId())) {
					r.seenin.add(event.getChannel().getId());
					
					try {
						BotLoader.out.triggerChannelDialogue(event.getChannel().getName(), event.getMember().getNickname(), randomPersonality(), event.getTextChannel().getId());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
    }
	
	@Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event)
    {
		try {
			BotLoader.out.triggerDialogue("join", event.getMember().getNickname(), randomPersonality(), event.getGuild().getDefaultChannel().getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	public Personality randomPersonality() {
		return bots[AIOutput.r.nextInt(2)];
	}
}
