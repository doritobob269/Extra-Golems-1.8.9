package com.golems.content;

import java.util.List;

import com.golems.entity.EntityBedrockGolem;
import com.golems.entity.GolemBase;
import com.golems.main.Config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBedrockGolem extends Item 
{
	public ItemBedrockGolem()
	{
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		// creative players can use this item to spawn a bedrock golem
		if(Config.ALLOW_BEDROCK_GOLEM && !worldIn.isRemote && player.capabilities.isCreativeMode) 
		{
			GolemBase golem = new EntityBedrockGolem(worldIn);

			if (side == EnumFacing.DOWN)
	        {
	            return false;
	        }
	        else
	        {
	            boolean flag = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);
	            BlockPos blockpos1 = flag ? pos : pos.offset(side);
	        }
			
			golem.setPlayerCreated(true);
			golem.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.up(1).getY(), (double)pos.getZ() + 0.5D, 0.0F, 0.0F);
			worldIn.spawnEntityInWorld(golem);

			return true;
		}
		return false;
	}
	
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		String loreCreativeOnly = EnumChatFormatting.RED + trans("tooltip.creative_only_item"); 
		par3List.add(loreCreativeOnly);

		if(GuiScreen.isShiftKeyDown())
		{
			par3List.add(StatCollector.translateToLocalFormatted("tooltip.use_to_spawn", new Object[] {trans("entity.golems.golem_bedrock.name")}));
			par3List.add(StatCollector.translateToLocalFormatted("tooltip.use_on_existing", new Object[] {trans("entity.golems.golem_bedrock.name")}));
			par3List.add(trans("tooltip.to_remove_it") + ".");
		}
		else
		{	
			String lorePressShift =
				EnumChatFormatting.GRAY + trans("tooltip.press") + " " + 
				EnumChatFormatting.YELLOW + trans("tooltip.shift") + " " + 
				EnumChatFormatting.GRAY + trans("tooltip.for_more_details");
			par3List.add(lorePressShift);
		}
	}
	
	private String trans(String s)
	{
		return StatCollector.translateToLocal(s);
	}
}
