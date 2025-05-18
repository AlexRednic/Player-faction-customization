package playerfactioncustomization.settings;

import com.fs.starfarer.api.Global;
import org.json.JSONObject;
import org.apache.log4j.Logger;

public class Settings {

	private static final String MOD_ID = "playerfactioncustomization";
	private static boolean INDUSTRY_CHECK = true;

	public static void getSettings() {

		Logger log = Global.getLogger(Settings.class);

		try {
			JSONObject configData = Global.getSettings().getMergedJSONForMod("data/config/playerfactioncustomization_settings.json",Settings.MOD_ID);
			INDUSTRY_CHECK = configData.getBoolean("requiredIndustryCheck");
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
}
