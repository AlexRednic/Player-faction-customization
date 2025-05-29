package playerfactioncustomization.console;

import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.Console;
import playerfactioncustomization.faction.PlayerFaction;

public class PrintPlayerFactionFleets implements BaseCommand {

	@Override
	public CommandResult runCommand(String args, CommandContext context) {

		boolean economicOnly = false;
		String[] argsArr = args.split(" ");
		if (argsArr.length == 1) {
			if (argsArr[0].equals("true") || argsArr[0].equals("false"))
				economicOnly = Boolean.parseBoolean(argsArr[0]);
			else
				Console.showMessage("Invalid argument. Defaulting economicOnly to false");
		}
		else
			Console.showMessage("Invalid arguments. Defaulting economicOnly to false");

		String output = PlayerFaction.printFleets(economicOnly);

		Console.showMessage(output);
		Console.showMessage("Print full player faction fleet list to log");
		return CommandResult.SUCCESS;
	}
}
