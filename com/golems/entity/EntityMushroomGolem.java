package com.golems.entity;

import java.util.List;

import com.golems.entity.ai.EntityAIPlaceBlocksRandomly;
import com.golems.main.Config;
import com.golems.main.ExtraGolems;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class EntityMushroomGolem extends GolemMultiTextured
{			
	private static final String[] shroomTypes = {"red","brown"};
	private final IBlockState[] mushrooms = {Blocks.brown_mushroom.getDefaultState(), Blocks.red_mushroom.getDefaultState()};
	private final Block[] soils = {Blocks.dirt, Blocks.grass, Blocks.mycelium};

	public EntityMushroomGolem(World world) 
	{
		super(world, 3.0F, Blocks.red_mushroom_block, shroomTypes);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIPlaceBlocksRandomly(this, Config.TWEAK_MUSHROOM, mushrooms, soils, Config.ALLOW_MUSHROOM_SPECIAL));
	}	

	@Override
	public String getTexturePrefix() 
	{
		return "shroom";
	}
	
	@Override
	public String getModId() 
	{
		return ExtraGolems.MODID;
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30D);
	}

	@Override
	public void addGolemDrops(List<WeightedRandomChestContent> dropList, boolean recentlyHit, int lootingLevel)	
	{
		int size = 4 + this.rand.nextInt(6 + lootingLevel * 2);
		Block shroom = rand.nextBoolean() ? Blocks.red_mushroom : Blocks.brown_mushroom;
		GolemBase.addGuaranteedDropEntry(dropList, new ItemStack(shroom, size));
	}

	@Override
	public String getGolemSound() 
	{
		return Block.soundTypeGrass.soundName;
	}
}
