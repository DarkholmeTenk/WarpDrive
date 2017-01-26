package cr0s.warpdrive.command;

import java.util.ArrayList;
import java.util.List;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.world.SpaceTeleporter;

import javax.annotation.Nonnull;

@MethodsReturnNonnullByDefault
public class CommandSpace extends CommandBase {
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Override
	public String getCommandName() {
		return "space";
	}
	
	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender commandSender, @Nonnull String[] args) throws CommandException {
		if (commandSender == null) { return; } 
		
		// set defaults
		int targetDimensionId = Integer.MAX_VALUE;

		List<EntityPlayerMP> entityPlayerMPs = null;
		if (commandSender instanceof EntityPlayerMP) {
			entityPlayerMPs = new ArrayList<>(1);
			entityPlayerMPs.add((EntityPlayerMP) commandSender);
		}
		
		// parse arguments
		//noinspection StatementWithEmptyBody
		if (args.length == 0) {
			// nop
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
				WarpDrive.addChatMessage(commandSender,  new TextComponentString(getCommandUsage(commandSender)));
				return;
			}
			List<EntityPlayerMP> entityPlayerMPs_found = getOnlinePlayerByNameOrSelector(server, commandSender, args[0]);
			if (entityPlayerMPs_found != null) {
				entityPlayerMPs = entityPlayerMPs_found;
			} else if (commandSender instanceof EntityPlayer) {
				targetDimensionId = getDimensionId(args[0]);
			} else {
				WarpDrive.addChatMessage(commandSender, new TextComponentString("/space: player not found '" + args[0] + "'"));
				return;
			}
			
		} else if (args.length == 2) {
			List<EntityPlayerMP> entityPlayerMPs_found = getOnlinePlayerByNameOrSelector(server, commandSender, args[0]);
			if (entityPlayerMPs_found != null) {
				entityPlayerMPs = entityPlayerMPs_found;
			} else {
				WarpDrive.addChatMessage(commandSender, new TextComponentString("/space: player not found '" + args[0] + "'"));
				return;
			}
			targetDimensionId = getDimensionId(args[1]);
			
		} else {
			WarpDrive.addChatMessage(commandSender, new TextComponentString("/space: too many arguments " + args.length));
			return;
		}
		
		// check player
		if (entityPlayerMPs == null || entityPlayerMPs.isEmpty()) {
			WarpDrive.addChatMessage(commandSender, new TextComponentString("/space: undefined player"));
			return;
		}
		
		for (EntityPlayerMP entityPlayerMP : entityPlayerMPs) {
			// toggle between overworld and space if no dimension was provided
			if (targetDimensionId == Integer.MAX_VALUE) {
				if (entityPlayerMP.worldObj.provider.getDimension() == WarpDriveConfig.G_SPACE_DIMENSION_ID) {
					targetDimensionId = 0;
				} else {
					targetDimensionId = WarpDriveConfig.G_SPACE_DIMENSION_ID;
				}
			}
			
			// get target world
			WorldServer targetWorld = server.worldServerForDimension(targetDimensionId);
			if (targetWorld == null) {
				WarpDrive.addChatMessage(commandSender, new TextComponentString("/space: undefined dimension '" + targetDimensionId + "'"));
				return;
			}
			
			// inform player
			String message = "Teleporting player " + entityPlayerMP.getName() + " to dimension " + targetDimensionId + "..."; // + ":" + targetWorld.getWorldInfo().getWorldName();
			WarpDrive.addChatMessage(commandSender, new TextComponentString(message));
			WarpDrive.logger.info(message);
			if (commandSender != entityPlayerMP) {
				WarpDrive.addChatMessage(entityPlayerMP, new TextComponentString(commandSender.getName() + " is teleporting you to dimension " + targetDimensionId)); // + ":" + targetWorld.getWorldInfo().getWorldName());
			}
			
			// find a good spot
			int newX = MathHelper.floor_double(entityPlayerMP.posX);
			int newY = Math.min(255, Math.max(0, MathHelper.floor_double(entityPlayerMP.posY)));
			int newZ = MathHelper.floor_double(entityPlayerMP.posZ);
			
			if ( (targetWorld.isAirBlock(new BlockPos(newX, newY - 1, newZ)) && !entityPlayerMP.capabilities.allowFlying)
			  || !targetWorld.isAirBlock(new BlockPos(newX, newY    , newZ))
			  || !targetWorld.isAirBlock(new BlockPos(newX, newY + 1, newZ)) ) {// non solid ground and can't fly, or inside blocks
				newY = targetWorld.getTopSolidOrLiquidBlock(new BlockPos(newX, newY, newZ)).getY() + 1;
				if (newY == 0) {
					newY = 128;
				} else {
					for (int safeY = newY - 3; safeY > Math.max(1, newY - 20); safeY--) {
						if (!targetWorld.isAirBlock(new BlockPos(newX, safeY - 1, newZ))
						  && targetWorld.isAirBlock(new BlockPos(newX, safeY    , newZ))
						  && targetWorld.isAirBlock(new BlockPos(newX, safeY + 1, newZ))) {
							newY = safeY;
							break;
						}
					}
				}
			}
			
			// actual teleportation
			SpaceTeleporter teleporter = new SpaceTeleporter(targetWorld, 0, newX, newY, newZ);
			server.getPlayerList().transferPlayerToDimension(entityPlayerMP, targetDimensionId, teleporter);
			entityPlayerMP.setPositionAndUpdate(newX + 0.5D, newY + 0.2D, newZ + 0.5D);
			entityPlayerMP.sendPlayerAbilities();
		}
	}
	
	@Override
	public String getCommandUsage(@Nonnull ICommandSender commandSender) {
		return "/space (<playerName>) ([overworld|nether|end|theend|space|hyper|hyperspace|<dimensionId>])";
	}
	
	private List<EntityPlayerMP> getOnlinePlayerByNameOrSelector(MinecraftServer server, ICommandSender sender, final String playerNameOrSelector) {
		List<EntityPlayerMP> result = new ArrayList<>();
		List<EntityPlayerMP> onlinePlayers = server.getPlayerList().getPlayerList();
		for (EntityPlayerMP onlinePlayer : onlinePlayers) {
			if (onlinePlayer.getName().equalsIgnoreCase(playerNameOrSelector)) {
				result.add(onlinePlayer);
				return result;
			}
		}
		
		List<EntityPlayerMP> entityPlayerMPs_found = EntitySelector.matchEntities(sender, playerNameOrSelector, EntityPlayerMP.class);
		if (entityPlayerMPs_found != null && !entityPlayerMPs_found.isEmpty()) {
			result.addAll(entityPlayerMPs_found);
			return result;
		}
		
		return null;
	}
	
	private int getDimensionId(String stringDimension) {
		if (stringDimension.equalsIgnoreCase("overworld")) {
			return 0;
		} else if (stringDimension.equalsIgnoreCase("nether")) {
			return -1;
		} else if (stringDimension.equalsIgnoreCase("end") || stringDimension.equalsIgnoreCase("theend")) {
			return 1;
		} else if (stringDimension.equalsIgnoreCase("space")) {
			return WarpDriveConfig.G_SPACE_DIMENSION_ID;
		} else if (stringDimension.equalsIgnoreCase("hyper") || stringDimension.equalsIgnoreCase("hyperspace")) {
			return WarpDriveConfig.G_HYPERSPACE_DIMENSION_ID;
		}
		try {
			return Integer.parseInt(stringDimension);
		} catch(Exception exception) {
			// exception.printStackTrace();
			WarpDrive.logger.info("/space: invalid dimension '" + stringDimension + "', expecting integer or overworld/nether/end/theend/space/hyper/hyperspace");
		}
		return 0;
	}
}
