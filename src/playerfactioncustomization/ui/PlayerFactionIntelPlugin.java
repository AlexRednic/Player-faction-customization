package playerfactioncustomization.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SettingsAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.loading.RoleEntryAPI;
import com.fs.starfarer.api.ui.*;
import playerfactioncustomization.faction.PlayerFaction;
import playerfactioncustomization.ids.EnumShipRoles;

import java.awt.*;
import java.util.List;
import java.util.Set;

public class PlayerFactionIntelPlugin extends BaseIntelPlugin {

	public static final Object LEARN_BLUEPRINTS_BUTTON = new Object();
	public static final Object UPDATE_VARIANTS_BUTTON = new Object();
	private FactionAPI playerFaction = Global.getSector().getPlayerFaction();
	private final String title = "Faction Generated Fleets Configuration";
	private String result = "";

	private static PlayerFactionIntelPlugin instance;

	private PlayerFactionIntelPlugin(){
		setNew(false);
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public boolean hasSmallDescription() {
		return false;
	}

	@Override
	public boolean hasLargeDescription() {
		return true;
	}

	@Override
	public void createLargeDescription(CustomPanelAPI panel, float width, float height) {
		float pad = 3;
		float opad = 10;

		SettingsAPI settings = Global.getSettings();
		Color factionColor = playerFaction.getBaseUIColor();
		TooltipMakerAPI commands = panel.createUIElement(width, 200, true);
		commands.addSectionHeading("Commands", factionColor,
				playerFaction.getDarkUIColor(), Alignment.MID, opad);

		ButtonAPI buttonLearnBlueprint = commands.addButton("Learn Blueprints", LEARN_BLUEPRINTS_BUTTON, 150, 20, pad);
		ButtonAPI buttonUpdateVariants = commands.addButton("Update Variants", UPDATE_VARIANTS_BUTTON, 150, 20, pad);

		if (!result.isEmpty()) {
			commands.addPara(result, Color.GREEN, pad);
			result = "";
		}

		panel.addUIElement(commands).inTL(0, 0);

		TooltipMakerAPI fleetComposition = panel.createUIElement(width, height - commands.getHeightSoFar(), true);
		fleetComposition.addSectionHeading("Fleet Composition",
				playerFaction.getBrightUIColor(), playerFaction.getDarkUIColor(), Alignment.MID, opad);
		// Variant list
		UIPanelAPI firstItem = null;
		for (EnumShipRoles role : EnumShipRoles.values()) {

			TooltipMakerAPI headerElement =  fleetComposition.beginImageWithText(playerFaction.getCrest(),40,width,true);
			headerElement.addPara(role.getValue() + " - Variants", factionColor,opad);
			UIPanelAPI header = fleetComposition.addImageWithText(pad);

			if (firstItem != null)
				header.getPosition().belowLeft(firstItem,pad);
			int currentItem = 1;
			firstItem = null;
			UIPanelAPI prevItem = null;
			UIPanelAPI newItem;
			for (RoleEntryAPI entry : settings.getEntriesForRole(playerFaction.getId(),role.getValue())) {
				TooltipMakerAPI element =  fleetComposition.beginImageWithText(settings.getVariant(entry.getVariantId()).getHullSpec().getSpriteName(),40,width / 4,false);
				element.addPara(settings.getVariant(entry.getVariantId()).getFullDesignationWithHullName()
						+ " - " + role.getValue()
						+ " - " + Math.round(entry.getWeight() * 10) + "%",pad);
				if (currentItem == 1) {
					prevItem = fleetComposition.addImageWithText(pad);
					firstItem = prevItem;
				}
				else {
					newItem = fleetComposition.addImageWithText(pad);
					newItem.getPosition().rightOfTop(prevItem, pad);
					prevItem = newItem;
				}
				currentItem++;
			}
		}
		panel.addUIElement(fleetComposition);
	}

	@Override
	public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
		if (buttonId == LEARN_BLUEPRINTS_BUTTON) {
			PlayerFaction.learnBlueprints();
			result = "Updated Blueprints!";
			ui.updateUIForItem(this);
			return;
		}
		if (buttonId == UPDATE_VARIANTS_BUTTON) {
			PlayerFaction.updateVariants();
			result = "Updated Variants!";
			ui.updateUIForItem(this);
			return;
		}
	}

	@Override
	public String getName() {
		return title;
	}

	@Override
	public String getSortString() {
		return title;
	}

	@Override
	public FactionAPI getFactionForUIColors() {
		return playerFaction;
	}

	@Override
	public String getIcon() {
		return playerFaction.getCrest();
	}

	@Override
	public Set<String> getIntelTags(SectorMapAPI map) {
		Set<String> tags = super.getIntelTags(map);
		tags.add("Personal");
		return tags;
	}

	@Override
	public Color getTitleColor(ListInfoMode mode) {
		return playerFaction.getBaseUIColor();
	}

	@Override
	public Color getBackgroundGlowColor() {
		return playerFaction.getDarkUIColor();
	}

	public static PlayerFactionIntelPlugin getInstance(boolean forceReset) {
		if (instance == null || forceReset) {
			List<IntelInfoPlugin> intel = Global.getSector().getIntelManager().getIntel(PlayerFactionIntelPlugin.class);
			if (intel.isEmpty()) {
				instance = new PlayerFactionIntelPlugin();
				Global.getSector().getIntelManager().addIntel(instance, true);
			} else {
				if (intel.size() > 1) {
					throw new IllegalStateException("Should only be one CouncilIntelPlugin intel registered");
				}
				instance = (PlayerFactionIntelPlugin) intel.get(0);
			}
		}
		return instance;
	}

	public static PlayerFactionIntelPlugin getInstance() {
		return getInstance(false);
	}

}

