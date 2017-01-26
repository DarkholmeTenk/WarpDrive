package cr0s.warpdrive.command;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import cr0s.warpdrive.WarpDrive;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

@MethodsReturnNonnullByDefault
public class CommandJumpgates extends CommandBase {
	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}

	@Override
	public String getCommandName() {
		return "jumpgates";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return "Lists jumpgates";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
		EntityPlayerMP player = (EntityPlayerMP) commandSender;
		WarpDrive.addChatMessage(player, new TextComponentString("Jumpgates: " + WarpDrive.jumpgates.commaList()));
	}
}
