package HardTime.Output;

import java.util.ArrayList;
import java.util.List;
import HardTime.Personality.Type;

/**
 * @author SirMangler
 *
 * @date 14 Sep 2019
 */
public class Dialogue {

	public String name = "EMPTY";
	public int probability = 0;
	public int index = 0;
	public List<String> lines = new ArrayList<String>();
	public List<String> yes = new ArrayList<String>();
	public List<String> no = new ArrayList<String>();
	public Type type = Type.warden;
	
}
