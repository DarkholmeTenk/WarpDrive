package cr0s.warpdrive.command;

import java.util.List;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import cr0s.warpdrive.WarpDrive;

@MethodsReturnNonnullByDefault
public class CommandInvisible extends CommandBase {
	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}

	@Override
	public String getCommandName() {
		return "invisible";
	}
		
	@Override
	public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
		EntityPlayer player = (EntityPlayer) commandSender;
		
		if (args.length >= 1) {
			WarpDrive.logger.info("/invisible: setting invisible to " + args[0]);
			
			// get an online player by name
			List<EntityPlayerMP> entityPlayers = server.getPlayerList().getPlayerList();
			for (EntityPlayerMP entityPlayer : entityPlayers) {
				if (entityPlayer.getDisplayNameString().equalsIgnoreCase(args[0])) {
					player = entityPlayer;
				}
			}
		}
		
		if (player == null) {
			return;
		}
		
		// Toggle invisibility
		player.setInvisible(!player.isInvisible());
	}
	
	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return "/invisible [player]";
	}
}
