package com.golems.entity.ai;

import com.golems.entity.GolemBase;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIPlaceBlocksRandomly extends EntityAIBase
{
	public final GolemBase theGolem;
	public final int tickDelay;
	public final IBlockState[] plantables;
	public final Block[] plantSupports;
	public final boolean canExecute;
	
	public EntityAIPlaceBlocksRandomly(GolemBase golem, int ticksBetweenPlanting, IBlockState[] plants, Block[] soils, boolean configAllows)
	{
		this.setMutexBits(8);
		this.theGolem = golem;
		this.tickDelay = ticksBetweenPlanting;
		this.plantables = plants;
		this.plantSupports = soils;
		this.canExecute = configAllows;
	}
	
	public EntityAIPlaceBlocksRandomly(GolemBase golem, int ticksBetweenPlanting, IBlockState[] plants, Block[] soils)
	{
		this(golem, ticksBetweenPlanting, plants, soils, true);
	}
	
	@Override
	public boolean shouldExecute() 
	{
		return canExecute && theGolem.worldObj.rand.nextInt(tickDelay) == 0;
	}
	
	@Override
	public void startExecuting()
	{
		int x = MathHelper.floor_double(theGolem.posX);
		int y = MathHelper.floor_double(theGolem.posY - 0.20000000298023224D - (double)theGolem.getYOffset());
		int z = MathHelper.floor_double(theGolem.posZ);
		BlockPos below = new BlockPos(x, y, z);
		Block blockBelow = theGolem.worldObj.getBlockState(below).getBlock();

		if(theGolem.worldObj.isAirBlock(below.up(1)))
		{
			for(Block b : this.plantSupports)
			{
				if(blockBelow == b)
				{
					// debug:
					// System.out.println("Planting a flower!");
					setToPlant(theGolem.worldObj, below.up(1));
					return;
				}
			}
		}
	}
	
	@Override
	public boolean continueExecuting()
	{
		return false;
	}
	
	public boolean setToPlant(World world, BlockPos pos)
	{
		IBlockState state = this.plantables[world.rand.nextInt(this.plantables.length)];
		return world.setBlockState(pos, state, 2);	
	}
}