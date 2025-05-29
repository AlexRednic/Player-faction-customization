package playerfactioncustomization.console;

import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.Console;
import playerfactioncustomization.faction.PlayerFaction;

public class PlayerFactionLearnBlueprints implements BaseCommand {

	@Override
	public CommandResult runCommand(String args, CommandContext context) {

		PlayerFaction.learnBlueprints();

		Console.showMessage("Player Faction Blueprints Learned");
		return CommandResult.SUCCESS;
	}
}
