package playerfactioncustomization.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.thoughtworks.xstream.XStream;
import playerfactioncustomization.listeners.MonthlyListener;
import playerfactioncustomization.settings.Settings;

import java.util.Objects;

public class PlayerFactionCustomizationModPlugin extends BaseModPlugin {


	@Override
	public void configureXStream(XStream x) {
		x.alias("listeners.MonthlyListener", MonthlyListener.class);
	}

	@Override
	public void onGameLoad(boolean newGame) {
		SectorAPI sector = Global.getSector();
		boolean monthlyListenerActive = false;
		MonthlyListener listenerPFC = null;

		for (Object listener : sector.getAllListeners())
			if(Objects.equals(listener.getClass(),MonthlyListener.class)) {
				monthlyListenerActive = true;
				listenerPFC = (MonthlyListener) listener;
			}

		if (monthlyListenerActive == false)
			sector.addListener(new MonthlyListener(true));

		Settings.getSettings();
		if (listenerPFC != null) {
			listenerPFC.playerFactionUpdateVariants();
		}
	}
}
