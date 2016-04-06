package com.golems.entity;

import java.util.List;

import com.golems.content.ContainerPortableWorkbench;
import com.golems.main.ContentInit;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class EntityCraftingGolem extends GolemBase
{
	public EntityCraftingGolem(World world) 
	{
		super(world, 2.0F, Blocks.crafting_table);
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(24.0D);
	  	this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.29D);
	}

	@Override
	protected void applyTexture() 
	{
		this.setTextureType(this.getGolemTexture("crafting"));
	}
	
	@Override
	protected boolean interact(EntityPlayer player)
	{
		ItemStack itemstack = player.inventory.getCurrentItem();

		if(!player.worldObj.isRemote && itemstack == null)
		{
			// display crafting grid for player
			player.displayGui(new EntityCraftingGolem.InterfaceCraftingGrid(player.worldObj, player.playerLocation));
			player.triggerAchievement(StatList.field_181742_Z);
			player.swingItem();
		}
		
		return super.interact(player);
	}

	@Override
	public void addGolemDrops(List<WeightedRandomChestContent> dropList, boolean recentlyHit, int lootingLevel) 
	{
		GolemBase.addGuaranteedDropEntry(dropList, new ItemStack(Blocks.crafting_table, 1 + rand.nextInt(2)));
		GolemBase.addDropEntry(dropList, Blocks.planks, 0, 1, 6, 70 + lootingLevel * 10);
	}

	@Override
	public String getGolemSound() 
	{
		return Block.soundTypeStone.soundName;
	}

	public static class InterfaceCraftingGrid extends net.minecraft.block.BlockWorkbench.InterfaceCraftingTable
    {
        private final World world2;
        private final BlockPos position2;

        public InterfaceCraftingGrid(World worldIn, BlockPos pos)
        {
            super(worldIn, pos);
            this.world2 = worldIn;
            this.position2 = pos;
        }

        public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
        {
            return new ContainerPortableWorkbench(playerInventory, this.world2, this.position2);
        }
    }
}
