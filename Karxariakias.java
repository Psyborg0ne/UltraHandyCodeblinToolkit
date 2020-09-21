import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.NPC;
@ScriptManifest(author = "Codeblins", category = Category.MONEYMAKING, name = "Karxariakias", version = 0.23)
public class Karxariakias extends AbstractScript{
	Area bankArea = new Area(2569, 2863, 2571, 2864);
	Area spotArea = new Area(2454, 2891, 2459, 2892);
	@Override
	public int onLoop() {
		NPC fishingSpot = getNpcs().closest(spot -> spot != null && spot.getName().equals("Fishing spot") && spot.hasAction("Cage"));
		NPC banker = getNpcs().closest(npc -> npc != null && npc.getName().equals("Yusuf") && npc.hasAction("Bank"));
		
		if(!getInventory().isFull()) {
			if(spotArea.contains(getLocalPlayer())) {
				if(!getLocalPlayer().isAnimating()) {
					if(fishingSpot.interact()) {
						sleepUntil(() -> !getLocalPlayer().isAnimating(), 50000);
					}
				}else if (getLocalPlayer().isAnimating()) {
					sleepUntil(() -> !getLocalPlayer().isAnimating(), 50000);

				}
			}else if(!spotArea.contains(getLocalPlayer())) {
				if(getWalking().walk(spotArea.getRandomTile())) {
					
				}
			}

		}else if(getInventory().isFull() && !bankArea.contains(getLocalPlayer())) {
			if(getWalking().walk(bankArea.getRandomTile())) {
				sleepWhile(() -> bankArea.contains(getLocalPlayer()), 1000);
			}
		}else if(bankArea.contains(getLocalPlayer())) {
			banker.interactForceRight("Bank");
			if(getBank().isOpen()) {
				getBank().depositAll("Raw lobster");
				sleep(Calculations.random(1000, 1500));
				getBank().close();
			}
		}
		return Calculations.random(1000, 1500);
	}

}
