package cr0s.warpdrive.command;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;

/*
 *   /wdebug <dimension> <coordinates> <blockId> <Metadata> <actions>
 */

@MethodsReturnNonnullByDefault
public class CommandDebug extends CommandBase {
	
	@Override
	public String getCommandName()
	{
		return "wdebug";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	@Override
	public String getCommandUsage(ICommandSender par1ICommandSender)
	{
		return "/" + getCommandName() + " <dimension> <x> <y> <z> <blockId> <Metadata> <action><action>...\n"
				+ "dimension: 0/world, 2/space, 3/hyperspace\n"
				+ "coordinates: x,y,z\n"
				+ "action: I(nvalidate), V(alidate), A(set air), R(emoveEntity), P(setBlock), S(etEntity)";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
		EntityPlayerMP player = (EntityPlayerMP)commandSender;
		if(args.length > 6 )
		{
			int dim, x, y, z, metadata;
			int block;
			String actions;
			try
			{
				String par = args[0].toLowerCase();
				switch (par) {
					case "world":
					case "overworld":
					case "0":
						dim = 0;
						break;
					case "nether":
					case "thenether":
					case "-1":
						dim = -1;
						break;
					case "s":
					case "space":
						dim = WarpDriveConfig.G_SPACE_DIMENSION_ID;
						break;
					case "h":
					case "hyper":
					case "hyperspace":
						dim = WarpDriveConfig.G_HYPERSPACE_DIMENSION_ID;
						break;
					default:
						dim = Integer.parseInt(par);
						break;
				}

				x = Integer.parseInt(args[1]);
				y = Integer.parseInt(args[2]);
				z = Integer.parseInt(args[3]);
				block = Integer.parseInt(args[4]);
				metadata = Integer.parseInt(args[5]);
				actions = args[6];
			}
			catch (Exception exception)
			{
				exception.printStackTrace();
				WarpDrive.addChatMessage(player, new TextComponentString(getCommandUsage(commandSender)));
				return;
			}

			WarpDrive.logger.info("/" + getCommandName() + " " + dim + " " + x + "," + y + "," + z + " " + block + ":" + metadata + " " + actions);
			World worldObj = DimensionManager.getWorld(dim);
			BlockPos blockPos = new BlockPos(x, y, z);
			TileEntity tileEntity = worldObj.getTileEntity(blockPos);
			WarpDrive.logger.info("[" + getCommandName() + "] In dimension " + worldObj.getProviderName() + " - " + worldObj.getWorldInfo().getWorldName()
			                     + ", Current block is " + worldObj.getBlockState(blockPos) + ", tile entity is " + ((tileEntity == null) ? "undefined" : "defined"));
			String side = FMLCommonHandler.instance().getEffectiveSide().isClient() ? "Client":"Server";

			// I(nvalidate), V(alidate), A(set air), R(emoveEntity), P(setBlock), S(etEntity)
			boolean bReturn;
			for (char ch: actions.toUpperCase().toCharArray()) {
				switch (ch) {
				case 'I':
					WarpDrive.logger.info("[" + getCommandName() + "] " + side + ": invalidating");
					if (tileEntity != null) {
						tileEntity.invalidate();
					}
					break;
				case 'V':
					WarpDrive.logger.info("[" + getCommandName() + "] " + side + ": validating");
					if (tileEntity != null) {
						tileEntity.validate();
					}
					break;
				case 'A':
					WarpDrive.logger.info("[" + getCommandName() + "] " + side + ": setting to Air");
					bReturn = worldObj.setBlockToAir(blockPos);
					WarpDrive.logger.info("[" + getCommandName() + "] " + side + ": returned " + bReturn);
					break;
				case 'R':
					WarpDrive.logger.info("[" + getCommandName() + "] " + side + ": remove entity");
					worldObj.removeTileEntity(blockPos);
					break;
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
					WarpDrive.logger.info("[" + getCommandName() + "] " + side + ": set block " + x + ", " + y + ", " + z + " to " + block + ":" + metadata);
					bReturn = worldObj.setBlockState(blockPos, Block.getBlockById(block).getStateFromMeta(metadata), ch - '0');
					WarpDrive.logger.info("[" + getCommandName() + "] " + side + ": returned " + bReturn);
					break;
				case 'P':
					WarpDrive.logger.info("[" + getCommandName() + "] " + side + ": set block " + x + ", " + y + ", " + z + " to " + block + ":" + metadata);
					bReturn = worldObj.setBlockState(blockPos, Block.getBlockById(block).getStateFromMeta(metadata), 2);
					WarpDrive.logger.info("[" + getCommandName() + "] " + side + ": returned " + bReturn);
					break;
				case 'S':
					WarpDrive.logger.info("[" + getCommandName() + "] " + side + ": set entity");
					worldObj.setTileEntity(blockPos, tileEntity);
					break;
				case 'C':
					WarpDrive.logger.info("[" + getCommandName() + "] " + side + ": update containing block info");
					if (tileEntity != null) {
						tileEntity.updateContainingBlockInfo();
					}
					break;
				default:
					WarpDrive.logger.info("[" + getCommandName() + "] " + side + ": invalid step '" + ch + "'");
					break;
				}
			}
		}
		else
		{
			WarpDrive.addChatMessage(player,  new TextComponentString(getCommandUsage(commandSender)));
		}
	}

}