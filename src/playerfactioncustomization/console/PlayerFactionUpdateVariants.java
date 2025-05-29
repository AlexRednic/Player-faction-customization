package playerfactioncustomization.console;

import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.Console;
import playerfactioncustomization.faction.PlayerFaction;

public class PlayerFactionUpdateVariants implements BaseCommand {

	@Override
	public CommandResult runCommand(String args, CommandContext context) {

		PlayerFaction.updateVariants();

		Console.showMessage("Player Faction Updated Variants");
		return CommandResult.SUCCESS;
	}
}
