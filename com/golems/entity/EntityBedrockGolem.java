package com.golems.entity;

import java.util.List;

import com.golems.content.ContainerPortableWorkbench;
import com.golems.main.ContentInit;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWorkbench.InterfaceCraftingTable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBedrockGolem extends GolemBase 
{

	public EntityBedrockGolem(World world) 
	{
		super(world, 32.0F, Blocks.bedrock);
	}

	@Override
	protected void applyTexture()
	{
		this.setTextureType(this.getGolemTexture("bedrock"));
	}

	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		return super.attackEntityAsMob(entity);
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource p_180431_1_)
    {
        return true;
    }
	
	@SideOnly(Side.CLIENT)
    public boolean canRenderOnFire()
    {
        return false;
    }

	@Override
	protected boolean interact(EntityPlayer player)
	{
		ItemStack itemstack = player.inventory.getCurrentItem();

		if (itemstack != null)
		{	
			// creative players can "despawn" by using spawnBedrockGolem on this entity
			if(itemstack.getItem() == ContentInit.spawnBedrockGolem && player.capabilities.isCreativeMode)
			{
				player.swingItem();
				if(!this.worldObj.isRemote)
				{
					this.setDead();
				}
				return true;
			}
		}
		/* this was a test for crafting table interaction
		else if(!player.worldObj.isRemote)
		{
			// test: can an entity display crafting grid?
			player.displayGui(new EntityBedrockGolem.InterfaceCraftingGrid(player.worldObj, player.playerLocation));
			player.triggerAchievement(StatList.field_181742_Z);
		}
		*/
		return super.interact(player);
	}

	@Override
	protected void damageEntity(DamageSource source, float amount) {}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(999.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.24D);
		this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D);
	}

	@Override
	public void addGolemDrops(List<WeightedRandomChestContent> dropList, boolean recentlyHit, int lootingLevel) {}

	@Override
	public String getGolemSound() 
	{
		return Block.soundTypeStone.soundName;
	}
}