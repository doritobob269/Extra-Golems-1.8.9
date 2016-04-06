package com.golems.content;

import java.util.List;

import com.golems.events.GolemPaperAddInfoEvent;
import com.golems.main.ContentInit;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemGolemPaper extends Item 
{
	public ItemGolemPaper()
	{
		super();
		this.setUnlocalizedName(ContentInit.N_PAPER);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}
	
	/**
     * allows items to add custom lines of information to the mouseover description
     */
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		if(GuiScreen.isShiftKeyDown())
		{
			String loreListIntro = EnumChatFormatting.WHITE + trans("tooltip.in_order_of_attack") + ":";
			par3List.add(loreListIntro);
			par3List.add(trans("tile.blockDiamond.name") + ", " + trans("tile.blockEmerald.name") + ",");
			par3List.add(trans("tile.obsidian.name") + ", " + trans("material.glass") + ", " + trans("tile.stainedGlass.name") + ",");
			par3List.add(trans("tile.lightgem.name") + ", " + trans("tile.whiteStone.name") + ", " + trans("tile.quartzBlock.default.name") + ",");
			par3List.add(trans("tile.blockGold.name") + ", " + trans("tile.prismarine.rough.name") + ", " + trans("tile.icePacked.name") + ",");
			par3List.add(trans("tile.netherBrick.name") + ", " + trans("tile.blockIron.name") + ", " + trans("tile.seaLantern.name") + ",");
			par3List.add(trans("tile.sandStone.name") + ", " + trans("tile.redSandStone.default.name") + ", " + trans("tile.clayHardened.name") + ",");
			par3List.add(trans("tile.clayHardenedStained.name") + ", " + trans("material.shroom_block") + ", " + trans("material.log") + ",");
			par3List.add(trans("tile.tnt.name") + ", " + trans("tile.blockCoal.name") + ", " + trans("tile.blockRedstone.name") + ",");
			par3List.add(trans("tile.blockLapis.name") + ", " + trans("tile.slime.name") + ", " + trans("tile.clay.name") + ",");
			par3List.add(trans("tile.bookshelf.name") + ", " + trans("tile.sponge.dry.name") + ", " + trans("tile.melon.name") + ",");
			par3List.add(trans("tile.cloth.name") + ", " + trans("tile.hayBlock.name") + ", " + trans("material.leaf_block"));		
			GolemPaperAddInfoEvent event = new GolemPaperAddInfoEvent(par1ItemStack, par2EntityPlayer, par3List);
			MinecraftForge.EVENT_BUS.post(event);
		}
		else
		{		
			String lorePressShift = 
					EnumChatFormatting.GRAY + trans("tooltip.press") + " " + 
					EnumChatFormatting.YELLOW + trans("tooltip.shift").toUpperCase() + " " + 
					EnumChatFormatting.GRAY + trans("tooltip.for_golem_materials");
			par3List.add(lorePressShift);
		}
	}

	private String trans(String s)
	{
		return StatCollector.translateToLocal(s);
	}
}
