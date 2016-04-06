package com.golems.entity;

import java.util.List;

import com.golems.main.Config;
import com.golems.main.ContentInit;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class EntityRedstoneGolem extends GolemBase 
{			
	public EntityRedstoneGolem(World world) 
	{
		this(world, 2.0F, Blocks.redstone_block);
	}
	
	public EntityRedstoneGolem(World world, float attack, ItemStack pick)
	{
		super(world, attack, pick);
	}
	
	public EntityRedstoneGolem(World world, float attack, Block pick)
	{
		this(world, attack, new ItemStack(pick, 1));
	}

	@Override
	protected void applyTexture()
	{
		this.setTextureType(this.getGolemTexture("redstone"));
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		// calling every other tick reduces lag by 50%
		if(Config.ALLOW_REDSTONE_SPECIAL && this.ticksExisted % 2 == 0)
		{
			placePowerNearby(Config.TWEAK_REDSTONE);
		}
	}
	
	/** Finds air blocks nearby and replaces them with BlockMovingPowerSource **/
	protected boolean placePowerNearby(int power) 
	{
		int x = MathHelper.floor_double(this.posX);
		int y = MathHelper.floor_double(this.posY - 0.20000000298023224D); // y-pos of block below golem
		int z = MathHelper.floor_double(this.posZ);
		
		// power 3 layers at golem location
		for(int k = -1; k < 3; ++k)
		{	
			BlockPos at = new BlockPos(x, y + k, z);
			// if the block is air, make it a power block
			if(this.worldObj.isAirBlock(at))
			{
				return this.worldObj.setBlockState(at, ContentInit.blockPowerSourceFull.getStateFromMeta(power));
			}
		}
		return false;
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(18.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.26D);
	}

	@Override
	public void addGolemDrops(List<WeightedRandomChestContent> dropList, boolean recentlyHit, int lootingLevel)
	{
		int size = 8 + rand.nextInt(14 + lootingLevel * 4);
		GolemBase.addGuaranteedDropEntry(dropList, new ItemStack(Items.redstone, size > 36 ? 36 : size));
	}

	@Override
	public String getGolemSound() 
	{
		return Block.soundTypeStone.soundName;
	}
}
