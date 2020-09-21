import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;

@ScriptManifest(author = "Codeblins", category = Category.RUNECRAFTING, name = "Rune Infuse", version = 0.16)
public class Infuse extends AbstractScript{
	String ESSENCE = "Pure essence";
	Tile ALTAR_TILE = new Tile(3055, 3443);
	Area insideArea = new Area(2530, 4849, 2518, 4832);
	Area bankArea = new Area(3091, 3488, 3098, 3497);

	@Override
	public int onLoop() {
		GameObject altar = getGameObjects().closest(obj -> obj != null && obj.getName().equals("Mysterious ruins") && obj.hasAction("Enter"));
		GameObject insideAltar = getGameObjects().closest(obj -> obj != null && obj.getName().equals("Altar") && obj.hasAction("Craft-rune"));
		GameObject portal = getGameObjects().closest(obj -> obj != null && obj.getName().equals("Portal"));
		

		if(getInventory().isFull() && getInventory().onlyContains(ESSENCE)) {
			if(getLocalPlayer().getTile().equals(ALTAR_TILE)) {
				altar.interactForceRight("Enter");
				sleep(Calculations.random(2000, 3000));

				}else if (insideArea.contains(getLocalPlayer())) {
					if(insideAltar.interact()) {
						sleepWhile(() -> getLocalPlayer().isAnimating(), 6000);
					}
					
				}else if(!getLocalPlayer().getTile().equals(ALTAR_TILE)) {
				getWalking().walk(ALTAR_TILE);
			}
		}else if (getInventory().onlyContains("Body rune") && insideArea.contains(getLocalPlayer())) {
			portal.interact();
			sleepUntil(() -> getLocalPlayer().isStandingStill(), Calculations.random(1250, 3000));
		}
		else if ((getInventory().onlyContains("Body rune") || getInventory().isEmpty()) && !bankArea.contains(getLocalPlayer())) {
			getWalking().walk(bankArea.getRandomTile());
		}
		else if (getInventory().onlyContains("Body rune") && bankArea.contains(getLocalPlayer())) {
			if(getBank().openClosest()) {
				if(getBank().isOpen()) {
					getBank().depositAll("Body rune");
					sleep(Calculations.random(2000, 3000));
					getBank().withdrawAll(ESSENCE);
					sleep(Calculations.random(2000, 3000));
					getBank().close();
				}
			}
		}else if (getInventory().isEmpty() && bankArea.contains(getLocalPlayer())) {
			if(getBank().isOpen()) {
				getBank().withdrawAll(ESSENCE);
				sleep(Calculations.random(2000, 3000));
				getBank().close();
			}else if (!getBank().isOpen()) {
				getBank().openClosest();
			}
		}
		return Calculations.random(1000, 2500);
	}

}
