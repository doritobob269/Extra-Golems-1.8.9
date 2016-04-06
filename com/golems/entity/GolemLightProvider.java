package com.golems.entity;

import com.golems.content.BlockLightProvider;
import com.golems.main.ContentInit;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class GolemLightProvider extends GolemBase 
{	
	private EnumLightLevel lightLevel;
	
	public GolemLightProvider(World world, float attack, Block pick, EnumLightLevel light)
	{
		super(world, attack, pick);
		this.lightLevel = light;
	}
	
	public GolemLightProvider(World world, float attack, EnumLightLevel light)
	{
		this(world, attack, ContentInit.golemHead, light);
	}
	
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		// only try to place light blocks every other tick -- reduces lag by 50%
		if(this.ticksExisted % 2 == 0)
		{
			this.placeLightBlock();
		}
	}

	protected boolean placeLightBlock() 
	{
		int x = MathHelper.floor_double(this.posX);
		int y = MathHelper.floor_double(this.posY - 0.20000000298023224D);
		int z = MathHelper.floor_double(this.posZ);
		int[][] validPos = {{x,z},{x+1,z},{x-1,z},{x,z+1},{x,z-1},{x+1,z+1},{x-1,z+1},{x+1,z-1},{x-1,z-1}};
		for(int[] coord : validPos)
		{
			int xPos = coord[0];
			int zPos = coord[1];
			for(int k = 0; k < 3; ++k)
			{	
				int yPos = y + k + 1;
				BlockPos pos = new BlockPos(xPos, yPos, zPos);
				Block at = this.worldObj.getBlockState(pos).getBlock();
				if(at.getMaterial() == this.lightLevel.getMaterialToReplace())
				{
					return this.worldObj.setBlockState(pos, this.lightLevel.getLightBlock().getDefaultState());
				}
				else if(at instanceof BlockLightProvider)
				{
					return false;
				}
			}
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float f)
	{
		return 15728880;
	}

	/** Gets how bright this entity is **/
	@Override
	public float getBrightness(float f)
	{
		return this.lightLevel.getBrightness();
	}

	/** Allows the golem to emit different levels of light **/
	public static enum EnumLightLevel
	{
		HALF(0.5F, Material.air),
		FULL(1.0F, Material.air),
		WATER_HALF(0.5F, Material.water), // WIP
		WATER_FULL(1.0F, Material.water);
		
		private final float light;
		private final Material replaceable;
		
		private EnumLightLevel(float brightness, Material canReplace)
		{
			this.light = brightness;
			this.replaceable = canReplace;
		}
		
		public Block getLightBlock()
		{
			switch(this)
			{
			case FULL: 	return ContentInit.blockLightSourceFull;
			case HALF:	return ContentInit.blockLightSourceHalf;
			case WATER_FULL: 
			case WATER_HALF: return ContentInit.blockWaterLightFull;
			default:	return Blocks.air;			
			}
		}
		
		public float getBrightness()
		{
			return this.light;
		}
		
		public Material getMaterialToReplace()
		{
			return this.replaceable;
		}
	}
}
