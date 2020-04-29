
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.function.Predicate;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptMeta;
import org.rspeer.ui.Log;

@ScriptMeta(desc = "Small net fishing and banking in Draynor Village", developer = "Codeblins", name = "FishFiris", version = 1.3)
public class SmallNet extends Script implements RenderListener {

	private static final Area DRAYNOR_FISH = Area.rectangular(3083, 3231, 3090, 3223);
	private static final Area DRAYNOR_BANK = Area.rectangular(3091, 3246, 3093, 3240);
	private static final Predicate<Item> SMALL_NET = item -> item.getName().equals("Small fishing net");
	private static final Predicate<Item> FISH = item -> item.getName().contains("Raw");

	private Npc b;
	private Npc f;
	private double caught, startXp, totalCaught, totalXp;
	private StopWatch timeRan;

	public void onStart() {
		caught = 0;
		totalCaught = 0;
		startXp = Skills.getExperience(Skill.FISHING);
		timeRan = StopWatch.start();
	}

	@Override
	public int loop() {
		if (Inventory.isFull() && DRAYNOR_BANK.contains(Players.getLocal())) {
			Log.info("Inside bank, inventory full...");
			if (Bank.isOpen()) {
				Log.info("Bank is open");
				Log.info("Depositing raw fish...");
				totalCaught += Inventory.getCount(FISH);
				Bank.depositAll(FISH);
				Time.sleep(Random.high(600, 1000));
				Bank.close();
			} else if (!Bank.isOpen()) {
				Log.info("Bank not open, looking for nearby bankers...");
				b = Npcs.getNearest(npc -> npc != null && npc.getName().equals("Banker"));
				Log.info("Found " + b);
				b.interact("Bank");
			}
		} else if (Inventory.isFull() && !DRAYNOR_BANK.contains(Players.getLocal())) {
			Movement.walkTo(DRAYNOR_BANK.getCenter().randomize(3));
		} else if (!Inventory.contains(SMALL_NET) && DRAYNOR_BANK.contains(Players.getLocal())) {
			Log.info("Inside bank, missing net...");
			if (Bank.isOpen()) {
				Log.info("Bank is open");
				if (Bank.contains(SMALL_NET)) {
					Log.info("Bank contains small fishing net, withdrawing...");
					Bank.depositInventory();
					Time.sleep(Random.high(600, 1000));
					Bank.withdraw(SMALL_NET, 1);
					Time.sleep(Random.high(600, 1000));
					Bank.close();
				} else if (!Bank.contains(SMALL_NET)) {
					Log.severe("Small fishing net was not found in bank, exiting...");
					return -1;
				}
			} else if (!Bank.isOpen()) {
				Log.info("Bank not open, looking for nearby bankers...");
				b = Npcs.getNearest(npc -> npc != null && npc.getName().equals("Banker"));
				Log.info("Found " + b);
				b.interact("Bank");
			}

		} else if (!Inventory.contains(SMALL_NET) && !DRAYNOR_BANK.contains(Players.getLocal())) {
			Log.info("Walking to bank...");
			Movement.walkTo(DRAYNOR_BANK.getCenter().randomize(3));
		} else if (Inventory.contains(SMALL_NET) && !DRAYNOR_FISH.contains(Players.getLocal())) {
			Log.info("Walking to fishing spots...");
			Movement.walkTo(DRAYNOR_FISH.getCenter().randomize(5));
		} else if (Inventory.contains(SMALL_NET) && DRAYNOR_FISH.contains(Players.getLocal())) {
			if (Players.getLocal().isAnimating()) {
				Log.info("Fishing...");

				Time.sleepUntil(() -> !Players.getLocal().isAnimating(), 10000);
			} else if (!Players.getLocal().isAnimating()) {
				Log.info("Inside fishing area, looking for spots to fish...");
				f = Npcs.getNearest(fs -> fs != null && fs.getName().equals("Fishing spot"));
				Log.info("Found " + f);
				f.interact("Small net");
			}

		}

		caught = totalCaught + Inventory.getCount(FISH);
		totalXp = Skills.getExperience(Skill.FISHING) - startXp;

		return Random.high(600, 1200);
	}

	@Override
	public void notify(RenderEvent re) {
		Graphics g = re.getSource();
		Graphics2D g2 = (Graphics2D) g;
		g2.drawString("Running for: " + timeRan.toElapsedString(), 15, 40);
		g2.drawString("Fish Caught: " + (int) caught, 15, 60);
		g2.drawString("EXP Gained : " + totalXp, 15, 75);

	}

}
