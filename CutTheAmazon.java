import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;

@ScriptManifest(author = "Codeblin", category = Category.WOODCUTTING, name = "Cut the Amazon", version = 0.1)
public class Cut extends AbstractScript{

	@Override
	public int onLoop() {
		GameObject tree = getGameObjects().closest(obj -> obj != null && obj.getName().equals("Maple tree") && obj.hasAction("Chop down"));
		if(getInventory().isFull()) {
			getInventory().dropAll("Maple logs");
		}else if (!getInventory().isFull()) {
			if(getLocalPlayer().isAnimating()) {
				sleepUntil(() -> !getLocalPlayer().isAnimating(), 50000);
			}else if(!getLocalPlayer().isAnimating()) {
				if(tree.interact()) {
					sleepWhile(() -> getLocalPlayer().isAnimating(), 50000);
				}
			}
		}
		return Calculations.random(1000, 3000);
	}

}
