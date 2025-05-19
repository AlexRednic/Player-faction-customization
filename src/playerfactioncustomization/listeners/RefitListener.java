package playerfactioncustomization.listeners;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.RefitScreenListener;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import playerfactioncustomization.faction.PlayerFaction;

public class RefitListener implements RefitScreenListener {
	@Override
	public void reportFleetMemberVariantSaved(FleetMemberAPI member, MarketAPI dockedAt) {
		PlayerFaction.updateVariants();
	}
}
