package playerfactioncustomization.settings;

import com.fs.starfarer.api.Global;
import org.json.JSONObject;
import org.apache.log4j.Logger;
import playerfactioncustomization.ids.EnumShipRoles;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Settings {

	private static final String MOD_ID = "playerfactioncustomization";
	private static final String CONFIG_PATH = "data/config/playerfactioncustomization_settings.json";
	private static final String MAX_PERCENTAGE_CONFIG_PATH = "data/config/playerfactioncustomization_max_percentage.json";

	private static boolean INDUSTRY_CHECK = true;
	private static boolean PRIORITY_CHECK = true;
	private static boolean COMMISSION_CHECK = true;

	private static Map<EnumShipRoles,Integer>  rolesMaxPercentage = new HashMap<>();


	public static void getSettings() {

		Logger log = Global.getLogger(Settings.class);
		initRolesMaxPercentage();

		try {
			JSONObject configData = Global.getSettings().getMergedJSONForMod(CONFIG_PATH,MOD_ID);
			INDUSTRY_CHECK = configData.getBoolean("requiredIndustryCheck");
			PRIORITY_CHECK = configData.getBoolean("requiredPriorityCheck");
			COMMISSION_CHECK = configData.getBoolean("requiredCommissionCheck");

		} catch (Exception e) {
			log.info(e.getMessage());
		}

		try {
			JSONObject configData = Global.getSettings().getMergedJSONForMod(MAX_PERCENTAGE_CONFIG_PATH,MOD_ID);
			Iterator<String> keys = configData.keys();

			while(keys.hasNext()) {
				String key = keys.next();
				EnumShipRoles role = EnumShipRoles.valueOf(key);
				int value = configData.getInt(key);

				rolesMaxPercentage.merge(role,value,(oldValue,newValue) -> oldValue = newValue);
			}

		} catch (Exception e) {
			log.info(e.getMessage());
		}

	}

	public static String getModId() {
		return MOD_ID;
	}

	public static boolean getIndustryCheck() {
		return INDUSTRY_CHECK;
	}
	public static boolean getPriorityCheck() {
		return PRIORITY_CHECK;
	}
	public static boolean getCommissionCheck() {
		return COMMISSION_CHECK;
	}

	private static void initRolesMaxPercentage() {
		for (EnumShipRoles role : EnumShipRoles.values()) {
			rolesMaxPercentage.put(role,100);
		}
	}

	public static int getRoleMaxPercentage(EnumShipRoles role){
		return rolesMaxPercentage.get(role);
	}
}
