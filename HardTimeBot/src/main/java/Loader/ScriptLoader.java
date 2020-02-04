package Loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import HardTime.Personality.Type;
import HardTime.Output.AIOutput;
import HardTime.Output.Dialogue;

/**
 * @author SirMangler
 *
 * @date 14 Sep 2019
 */
public class ScriptLoader {

	public HashMap<String, List<String>> words = new HashMap<String, List<String>>();
	public HashMap<String, List<Dialogue>> dialogue = new HashMap<String, List<Dialogue>>();
	static BotLogger log = new BotLogger("ScriptLoader");
	
	public String getRandomWord(String cat) {
		return words.get(cat).get(AIOutput.r.nextInt(words.get(cat).size()));
	}
	
	public void updateDialogue(String key, Dialogue d) {
		List<Dialogue> dl;
		
		if (dialogue.containsKey(key)) {
			dl = dialogue.get(key);
		} else {
			dl = new ArrayList<Dialogue>();
		}
		
		dl.add(d);
		dialogue.put(key, dl);
	}
	
	public List<String> loadNouns() {
		try {
			return Files.readAllLines(Paths.get(ClassLoader.getSystemResource("nouns.txt").toURI()));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void loadScript() throws IOException {
		log.debug("Loading Script");
		
		BufferedReader reader = new BufferedReader(
								new InputStreamReader(ClassLoader.getSystemResourceAsStream("hardtimescript.txt")));
		String key = null;
		List<String> l = null;
		Dialogue d = null;
		
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.isEmpty() || line.equalsIgnoreCase("\r\n")) {
				if (l != null) words.put(key, l);
				if (d != null) updateDialogue(key, d);
				
				l = null; 
				d = null;
				key = null;
				
				continue;
			}
			
			if (key == null) {
				if (line.startsWith("@")) {
					key = line.substring(1);
					l = new ArrayList<String>();
					
					log.debug("@"+key);
					continue;
				}
				
				if (line.startsWith("#")) {
					key = line.substring(1);								
					d = new Dialogue();
					
					if (key.startsWith("&i")) {
						d.type = Type.inmate;
						key = key.substring(2);
						
						log.debug("inmate");
					} else if (key.startsWith("&p")) {
						d.type = Type.warden;
						key = key.substring(2);
						log.debug("warden");
					} else d.type = Type.neither;
					
					log.debug("#"+key);
					
					if (key.contains(";")) {
						String[] data = key.split(";");
						if (data.length > 2) {
							d.index=Integer.parseInt(data[1]);
							d.probability=Integer.parseInt(data[2]);
							
							log.debug("index: %1; probability: %2", d.index+"", d.probability+"");
						} else {
							d.probability=Integer.parseInt(data[1]);
							log.debug("probability: %1", d.probability+"");
						}
						
						key = key.split(";")[0];
					}
					
					d.name = key;

					continue;
				}
			} else if (d != null) {
				if (line.startsWith("&y")) {
					d.yes.add(line.substring(2));
					
					log.debug("Adding '%1' to YES", line.substring(2));
				} else if (line.startsWith("&n")) {
					d.no.add(line.substring(2));
					
					log.debug("Adding '%1' to NO", line.substring(2));
				} else {
					d.lines.add(line);
					
					log.debug("Adding '%1'", line);
				}
			} else if (l != null) {
				l.add(line);
				
				log.debug("Adding word '%1' to "+key, line);
			}
		}
	}
}
