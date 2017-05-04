package ladysnake.tartaros.common.items;

import ladysnake.tartaros.common.Reference;
import ladysnake.tartaros.common.capabilities.IncorporealDataHandler;
import ladysnake.tartaros.common.handlers.CustomTartarosTeleporter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemDebug extends Item {

	public ItemDebug() {
		super();
		this.setUnlocalizedName(Reference.Items.DEBUG.getUnlocalizedName());
        this.setRegistryName(Reference.Items.DEBUG.getRegistryName());
        this.setMaxStackSize(1);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		IncorporealDataHandler.getHandler(playerIn).setIncorporeal(true, playerIn);
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
}