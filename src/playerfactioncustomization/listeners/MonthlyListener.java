package playerfactioncustomization.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SettingsAPI;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.loading.RoleEntryAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import playerfactioncustomization.ids.EnumShipRoles;
import playerfactioncustomization.settings.Settings;

import java.text.DecimalFormat;
import java.util.*;

public class MonthlyListener extends BaseCampaignEventListener {

	public static float LEARNED_HULL_FREQUENCY = 0.1f;

	public MonthlyListener(boolean permaRegister) {
		super(permaRegister);
	}


	/**
	Monthly Listener to check for known blueprints. If there's a known hull blueprint which has a corresponding "player" tech blueprint
	 that is currently unknown, then sets the "player" blueprint as known
	 */
	@Override
	public void reportEconomyMonthEnd() {

		String factionId = Global.getSector().getPlayerFaction().getId();

		if (Objects.equals(factionId, Factions.PLAYER) && Misc.getCommissionFaction() == null && playerHasShipProduction()) {

			FactionAPI faction = Global.getSector().getFaction(factionId);
			List<String> ships = new ArrayList<>();
			List<String> playerShips = new ArrayList<>();
			SettingsAPI settings = Global.getSettings();


			for (String ship : Global.getSector().getAllEmptyVariantIds()) {
				String id = ship.substring(0, ship.lastIndexOf("_Hull"));
				if (settings.getHullSpec(id).getManufacturer().equals(Factions.PLAYER) && faction.knowsShip(id) == false)
					playerShips.add(id);
			}

			for (String knownShip : faction.getKnownShips()) {
				for (String id : playerShips) {
					boolean addShip = false;
					if (settings.getHullSpec(id).getBaseHullId().equals(settings.getHullSpec(knownShip).getBaseHullId())){
						for (String tag : settings.getHullSpec(id).getTags()) {
							if (tag.startsWith("dep_")) {
								if (settings.getHullSpec(knownShip).getHullId().equals(tag.substring(4)))
									addShip = true;
							}
						}
					}
					if (addShip) ships.add(id);

				}
			}

			for (String id : ships) {

				MessageIntel message = new MessageIntel("Blueprint for "
						+ Global.getSettings().getHullSpec(id).getHullName()
						+ " added to known hulls.", faction.getBaseUIColor());
				message.setIcon(Global.getSettings().getHullSpec(id).getSpriteName());
				Global.getSector().getCampaignUI().addMessage(message);

				faction.addKnownShip(id, true);
				faction.addUseWhenImportingShip(id);
				faction.getHullFrequency().put(id, LEARNED_HULL_FREQUENCY);
			}

			if (!ships.isEmpty()) {
				faction.clearShipRoleCache();
			}

			playerFactionUpdateVariants();
		}
	}

	/**
	Check whether player has Heavy Industry or Orbital Works in any of the colonies
	 */
	public boolean playerHasShipProduction() {
		FactionAPI faction = Global.getSector().getPlayerFaction();

		if (Settings.getIndustryCheck() == false)
			return true;

		for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()){
			if (market.getFaction().equals(faction)) {
				if (market.hasIndustry(Industries.HEAVYINDUSTRY) || market.hasIndustry(Industries.ORBITALWORKS))
					return true;
			}
		}

		return false;
	}

	public void playerFactionClearRoles() {

		FactionAPI faction = Global.getSector().getPlayerFaction();
		SettingsAPI settings = Global.getSettings();
		//settings.getEntriesForRole(faction.getId(), null);
		settings.addEntryForRole(faction.getId(), null, "anubis_Standard", 10);

		for (EnumShipRoles role : EnumShipRoles.values()) {
			for (RoleEntryAPI existingRole : settings.getEntriesForRole(faction.getId(),role.getValue()))
				settings.removeEntryForRole(faction.getId(), role.getValue(), existingRole.getVariantId());
		}

		for (RoleEntryAPI existingRole : settings.getEntriesForRole(faction.getId(),null))
			settings.removeEntryForRole(faction.getId(), null, existingRole.getVariantId());

	}

	public EnumShipRoles playerFactionGenerateRole (ShipVariantAPI variant) {
		//Carriers
		if ((variant.isCivilian() == false || variant.isCombat()) && variant.isCarrier()) {
			if (variant.getHullSize().equals(ShipAPI.HullSize.CAPITAL_SHIP) && variant.getFittedWings().size() >= 4)
				return EnumShipRoles.CARRIER_LARGE;
			if (variant.getHullSize().equals(ShipAPI.HullSize.CRUISER) && variant.getFittedWings().size() >= 3)
				return EnumShipRoles.CARRIER_MEDIUM;
			if (variant.getHullSize().equals(ShipAPI.HullSize.DESTROYER) && variant.getFittedWings().size() >= 2)
				return EnumShipRoles.CARRIER_SMALL;
		}

		//Phase
		if ((variant.isCivilian() == false || variant.isCombat()) && variant.getHullSpec().isPhase()) {
			if (variant.getHullSize().equals(ShipAPI.HullSize.CAPITAL_SHIP))
				return EnumShipRoles.PHASE_CAPITAL;
			if (variant.getHullSize().equals(ShipAPI.HullSize.CRUISER))
				return EnumShipRoles.PHASE_LARGE;
			if (variant.getHullSize().equals(ShipAPI.HullSize.DESTROYER))
				return EnumShipRoles.PHASE_MEDIUM;
			if (variant.getHullSize().equals(ShipAPI.HullSize.FRIGATE))
				return EnumShipRoles.PHASE_SMALL;
		}

		//Warships
		if ((variant.isCivilian() == false || variant.isCombat())) {
			if (variant.getHullSize().equals(ShipAPI.HullSize.CAPITAL_SHIP))
				return EnumShipRoles.COMBAT_CAPITAL;
			if (variant.getHullSize().equals(ShipAPI.HullSize.CRUISER))
				return EnumShipRoles.COMBAT_LARGE;
			if (variant.getHullSize().equals(ShipAPI.HullSize.DESTROYER))
				return EnumShipRoles.COMBAT_MEDIUM;
			if (variant.getHullSize().equals(ShipAPI.HullSize.FRIGATE))
				return EnumShipRoles.COMBAT_SMALL;
		}

		return null;
	}

	public void playerFactionUpdateVariants() {
		FactionAPI faction = Global.getSector().getPlayerFaction();
		SettingsAPI settings = Global.getSettings();
		List<ShipVariantAPI> playerVariants = new ArrayList<>();

		playerFactionClearRoles();

		Set<String> priorityShips = faction.getPriorityShips();
		for (String knownShip : faction.getKnownShips()) {
			if (settings.getHullSpec(knownShip).getManufacturer().equals(Factions.PLAYER) && priorityShips.contains(knownShip))
				playerVariants.addAll(Global.getSector().getAutofitVariants().getTargetVariants(knownShip));
		}

		Map<ShipVariantAPI,EnumShipRoles> playerVariantsRoles = new HashMap<>();
		Map<EnumShipRoles,Integer> rolesCounts = new HashMap<>();

		for (ShipVariantAPI playerVariant : playerVariants) {
			EnumShipRoles role = playerFactionGenerateRole(playerVariant);
			if (role == null) continue;

			playerVariantsRoles.put(playerVariant,role);
			rolesCounts.merge(role,1, Integer::sum);
		}

		//String output = "Hull, Variant, Role, Chance (10 is 100%)" + System.lineSeparator();

		for (Map.Entry<ShipVariantAPI,EnumShipRoles> playerVariantRole : playerVariantsRoles.entrySet()) {
			ShipVariantAPI variant = playerVariantRole.getKey();
			EnumShipRoles role = playerVariantRole.getValue();
			Integer count = rolesCounts.get(role);
			float fraction = (float) (10.0 / (float) count);

			settings.addEntryForRole(faction.getId(), role.getValue(), variant.getHullVariantId(), fraction);
			//output += variant.getHullSpec().getHullName() + ", " + variant.getDisplayName() + ", " + role.getValue() + ", " + fraction  + System.lineSeparator();
		}

	}
}