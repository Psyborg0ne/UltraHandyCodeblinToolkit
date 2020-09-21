import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;

@ScriptManifest(author = "Codeblins", category = Category.MONEYMAKING, name = "Hand of God", version = 0.1)
public class HandOfGod extends AbstractScript {

	private static final String USE_WHAT = "Plain pizza";
	private static final String USE_ON = "Anchovies";
	private static final String PRODUCT = "Anchovy pizza";
//	private static final String REMAINS = "Jug";
	private int productCount;
	
	private NPC banker;
	WidgetChild makeButton;

	@Override
	public void onStart() {
		productCount = 0;
		log("+---------------------------------+");
		log("+----- The Codeblins present -----+");
		log("+-------- THE HAND OF GOD --------+");
		log("+------------- v0.1b -------------+");
		log("+---------------------------------+");
		log("Script starting...");

	}

	@Override
	public int onLoop() {
		int amountToWait = Calculations.random(1250, 1500);
		banker = getNpcs().closest("Banker");
		if (getInventory().isEmpty()) {
			log("Inventory empty, need mats...");
			if (banker != null && banker.interact("Bank")) {
				log("Opening bank...");
				sleep(Calculations.random(1500, 2000));
					log("Bank open!");
					getBank().withdraw(USE_WHAT, 14);
					log("+ " + USE_WHAT);
					sleep(amountToWait);
					getBank().withdraw(USE_ON, 14);
					sleep(amountToWait);
					log("+ " + USE_ON);
					getBank().close();
			}

		} else if (getInventory().onlyContains(PRODUCT)) { //(getInventory().onlyContains(PRODUCT, REMAINS))
			log("Finished crafting, depositing...");
			productCount += getInventory().count(PRODUCT);
			if (banker != null && banker.interact("Bank")) {
				log("Opening bank...");
				sleep(Calculations.random(1500, 2000));
				log("Bank open!");
				getBank().depositAll(PRODUCT);
				log("- " + PRODUCT);
				sleep(amountToWait);
//				getBank().depositAll(REMAINS);
//				sleep(amountToWait);
//				log("- " + REMAINS);
				sleep(amountToWait);
				getBank().withdraw(USE_WHAT, 14);
				log("+ " + USE_WHAT);
				sleep(amountToWait);
				getBank().withdraw(USE_ON, 14);
				sleep(amountToWait);
				log("+ " + USE_ON);
				getBank().close();
			}

		} else if (getInventory().contains(USE_WHAT) && getInventory().contains(USE_ON)) {	
			getInventory().get(USE_WHAT).useOn(getInventory().get(USE_ON));
			sleep(Calculations.random(1000, 1300));
			log("Pressing make...");
			log("Crafting...");
			makeButton = getWidgets().getWidgetChild(270, 14);
			makeButton.interact();
			sleepUntil(() -> getInventory().onlyContains(PRODUCT), 120000);//getInventory().onlyContains(PRODUCT, REMAINS)

		}
		return amountToWait;
	}

	@Override
	public void onPaint(Graphics g) {
		g.setColor(new Color(0, 0, 0));
		Font font = new Font("Comic Sans MS", Font.BOLD, 17);
		g.setFont(font);
		g.drawString(String.format("Made %d %s", productCount, PRODUCT), 370, 370);
	}

}
