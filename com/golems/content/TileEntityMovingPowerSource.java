package com.golems.content;

import java.util.List;

import com.golems.entity.EntityRedstoneGolem;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

public class TileEntityMovingPowerSource extends TileEntity implements ITickable
{    
	private AxisAlignedBB aabb = (AxisAlignedBB)null;
	
	public TileEntityMovingPowerSource() {}

	/**
	 * This controls whether the tile entity gets replaced whenever the block state 
	 * is changed. Normally only want this when block actually is replaced.
	 */
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		return (oldState.getBlock() != newSate.getBlock());
	}

	@Override
	public void update()
	{
		List<EntityRedstoneGolem> entityList = worldObj.getEntitiesWithinAABB(EntityRedstoneGolem.class, this.getAABBToCheck(this.worldObj, this.getPos()));

		// if no golem was found, delete this tile entity and block
		if(entityList.isEmpty())
		{
			if(worldObj.getBlockState(getPos()).getBlock() instanceof BlockPowerProvider)
			{
				worldObj.removeTileEntity(getPos());
				worldObj.setBlockToAir(getPos());
			}
		}
	} 

	private AxisAlignedBB getAABBToCheck(World worldIn, BlockPos pos)
	{
		if(this.aabb == (AxisAlignedBB)null)
    	{
    		this.aabb = new AxisAlignedBB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)pos.getX() + 1D, (double)pos.getY() + 1D, (double)pos.getZ() + 1D);
    	}
    	return this.aabb;	
    }
}
