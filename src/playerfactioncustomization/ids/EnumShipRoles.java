package playerfactioncustomization.ids;

public enum EnumShipRoles {


	COMBAT_SMALL ("combatSmall"),
	COMBAT_MEDIUM ("combatMedium"),
	COMBAT_LARGE ("combatLarge"),
	COMBAT_CAPITAL ("combatCapital"),

	PHASE_SMALL ("phaseSmall"),
	PHASE_MEDIUM ("phaseMedium"),
	PHASE_LARGE ("phaseLarge"),
	PHASE_CAPITAL ("phaseCapital"),

	CARRIER_SMALL ("carrierSmall"),
	CARRIER_MEDIUM ("carrierMedium"),
	CARRIER_LARGE ("carrierLarge");

	private String value;
	private EnumShipRoles(String value) {
		this.value = value;
	}
	public String getValue() {
		return this.value;
	}

}
