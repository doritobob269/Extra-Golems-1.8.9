package com.golems.content;

import com.golems.entity.GolemBase;
import com.golems.events.GolemBuildEvent;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockGolemHead extends BlockDirectional 
{
	public BlockGolemHead() 
	{
		super(Material.ground);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		this.setCreativeTab(CreativeTabs.tabMisc);
		this.setStepSound(soundTypeWood);
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {FACING});
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		super.onBlockAdded(world, pos, state);
		Block blockBelow1 = world.getBlockState(pos.down(1)).getBlock();
		Block blockBelow2 = world.getBlockState(pos.down(2)).getBlock();
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		if(blockBelow1 == blockBelow2)
		{
			boolean flagX = isGolemXAligned(world, pos);
			boolean flagZ = isGolemZAligned(world, pos);
			IBlockState meta = world.getBlockState(pos.down(1));

			// hard-coded support for Snow Golem
			if(blockBelow1 == Blocks.snow)
			{
				if(!world.isRemote)
				{
					removeGolemBody(world, pos);
					EntitySnowman entitysnowman = new EntitySnowman(world);
					System.out.print("[Extra Golems]: Building regular boring Snow Golem\n");
					entitysnowman.setLocationAndAngles((double)x + 0.5D, (double)y - 1.95D, (double)z + 0.5D, 0.0F, 0.0F);
					world.spawnEntityInWorld(entitysnowman);
				}

				spawnParticles(world, x, y - 2, z);
			}

			if(flagX || flagZ)
			{
				if(!world.isRemote)
				{
					// hard-coded support for Iron Golem
					if(blockBelow1 == Blocks.iron_block)
					{
						removeAllGolemBlocks(world, pos, flagX);
						// spawn the golem
						EntityIronGolem golem = new EntityIronGolem(world);
						System.out.print("[Extra Golems]: Building regular boring Iron Golem\n");
						golem.setPlayerCreated(true);
						golem.setLocationAndAngles((double)x + 0.5D, (double)y - 1.95D, (double)z + 0.5D, 0.0F, 0.0F);
						world.spawnEntityInWorld(golem);
						return;
					}				
					
					GolemBuildEvent event = new GolemBuildEvent(world, pos, flagX);
					MinecraftForge.EVENT_BUS.post(event);
					if(event.isGolemNull() || event.isGolemBanned())
					{
						return;
					}

					// clear the area where the golem blocks were
					removeAllGolemBlocks(world, pos, flagX);

					// spawn the golem
					GolemBase golem = event.getGolem();
					System.out.print("[Extra Golems]: Building golem " + golem.toString() + "\n");
					golem.setPlayerCreated(true);
					golem.setLocationAndAngles((double)x + 0.5D, (double)y - 1.95D, (double)z + 0.5D, 0.0F, 0.0F);
					world.spawnEntityInWorld(golem);
				}

				spawnParticles(world, x, y - 2, z);
			}			
		}
	}

	protected void spawnParticles(World world, int x, int y, int z)
	{
		for (int i1 = 0; i1 < 120; ++i1)
		{
			world.spawnParticle(EnumParticleTypes.SNOW_SHOVEL, (double)x + world.rand.nextDouble(), (double)(y - 2) + world.rand.nextDouble() * 2.5D, (double)z + world.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
		}
	}
	
	/** @return {@code true} if the blocks at x-1 and x+1 match the block at x **/
	public static boolean isGolemXAligned(World world, BlockPos headPos)
	{
		BlockPos[] armsX = {headPos.down(1).west(1), headPos.down(1).east(1)};
		Block below = world.getBlockState(headPos.down(1)).getBlock();
		return world.getBlockState(armsX[0]).getBlock() == below && world.getBlockState(armsX[1]).getBlock() == below;
	}
	
	/** @return {@code true} if the blocks at z-1 and z+1 match the block at z **/
	public static boolean isGolemZAligned(World world, BlockPos headPos)
	{
		BlockPos[] armsZ = {headPos.down(1).north(1), headPos.down(1).south(1)};
		Block below = world.getBlockState(headPos.down(1)).getBlock();
		return world.getBlockState(armsZ[0]).getBlock() == below && world.getBlockState(armsZ[1]).getBlock() == below;
	}
	
	/** Replaces this block and the four construction blocks with air **/
	public static void removeAllGolemBlocks(World world, BlockPos pos, boolean isXAligned)
	{
		removeGolemBody(world, pos);
		removeGolemArms(world, pos, isXAligned);
	}
	
	/** Replaces this block and the two below it with air **/
	public static void removeGolemBody(World world, BlockPos head)
	{
		world.setBlockToAir(head);
		world.setBlockToAir(head.down(1));
		world.setBlockToAir(head.down(2));
	}
	
	/** Replaces blocks at arm positions with air **/
	public static void removeGolemArms(World world, BlockPos pos, boolean isXAligned)
	{
		if(isXAligned)
		{
			world.setBlockToAir(pos.down(1).west(1));
			world.setBlockToAir(pos.down(1).east(1));
		}
		else
		{
			world.setBlockToAir(pos.down(1).north(1));
			world.setBlockToAir(pos.down(1).south(1));
		}
	}
}
