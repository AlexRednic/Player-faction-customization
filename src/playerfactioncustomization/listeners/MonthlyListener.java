package playerfactioncustomization.listeners;

import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import playerfactioncustomization.faction.PlayerFaction;

public class MonthlyListener extends BaseCampaignEventListener {


	public MonthlyListener(boolean permaRegister) {
		super(permaRegister);
	}

	/**
	Monthly Listener to check for known blueprints. If there's a known hull blueprint which has a corresponding "player" tech blueprint
	 that is currently unknown, then sets the "player" blueprint as known
	 */
	@Override
	public void reportEconomyMonthEnd() {

		PlayerFaction.learnBlueprints();
		PlayerFaction.updateVariants();
	}
}