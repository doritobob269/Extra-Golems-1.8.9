package com.golems.entity;

import java.util.List;

import com.golems.main.Config;
import com.golems.main.ContentInit;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class EntityEndstoneGolem extends GolemBase 
{			
	/** countdown timer for next teleport **/
	protected int teleportDelay;
	/** Max distance for one teleport; range is 32.0 for endstone golem **/
	protected double range;
	protected boolean canTeleport;
	protected boolean hasParticles;
	protected boolean avoidsArrows;

	protected int ticksBetweenIdleTeleports;
	/** Percent chance to teleport away when hurt by non-projectile **/
	protected int chanceToTeleportWhenHurt;

	/** Default constructor **/
	public EntityEndstoneGolem(World world) 
	{
		this(world, 8.0F, Blocks.end_stone, 32.0D, Config.ALLOW_ENDSTONE_SPECIAL, true);
	}

	/**
	 * Flexible constructor to allow child classes to customize.
	 * 
	 * @param world the worldObj
	 * @param attack base attack damage
	 * @param pick Creative pick-block return
	 * @param teleportRange 64.0 for enderman, 32.0 for endstone golem
	 * @param teleportingAllowed usually set by the config, checked here
	 * @param particles whether to display "portal" particles 
	 **/
	public EntityEndstoneGolem(World world, float attack, Block pick, double teleportRange, boolean teleportingAllowed, boolean particles)
	{
		super(world, attack, pick);
		this.ticksBetweenIdleTeleports = 200;
		this.chanceToTeleportWhenHurt = 75;
		this.range = teleportRange;
		this.canTeleport = teleportingAllowed;
		this.hasParticles = particles;
		this.avoidsArrows = true;
	}

	/**
	 * Flexible contructor to allow child classes to customize.
	 * 
	 * @param world the worldObj
	 * @param attack base attack damage
	 * @param teleportRange 64.0 for enderman, 32.0 for endstone golem
	 * @param teleportingAllowed usually set by the config, checked here
	 * @param particles whether to display "portal" particles 
	 **/
	public EntityEndstoneGolem(World world, float attack, double teleportRange, boolean teleportingAllowed, boolean particles) 
	{
		this(world, attack, ContentInit.golemHead, teleportRange, teleportingAllowed, particles);
	}

	@Override
	protected void applyTexture()
	{
		this.setTextureType(this.getGolemTexture("end_stone"));
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(50.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.26D);
	}

	@Override
	public void addGolemDrops(List<WeightedRandomChestContent> dropList, boolean recentlyHit, int lootingLevel)
	{
		GolemBase.addDropEntry(dropList, Blocks.end_stone, 0, 2, 2 + lootingLevel, 90);
		GolemBase.addDropEntry(dropList, Items.ender_pearl, 0, 2, 4 + lootingLevel, 40 + lootingLevel * 10);
		GolemBase.addDropEntry(dropList, Items.ender_eye, 0, 1, 1 + lootingLevel, 6 + lootingLevel * 10);
	}

	/**
	 * Teleport the golem
	 */
	protected boolean teleportTo(double p_70825_1_, double p_70825_3_, double p_70825_5_)
	{
		net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(this, p_70825_1_, p_70825_3_, p_70825_5_, 0);
		if(!this.canTeleport || net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) 
		{
			return false;
		}
		double d3 = this.posX;
		double d4 = this.posY;
		double d5 = this.posZ;
		this.posX = event.targetX;
		this.posY = event.targetY;
		this.posZ = event.targetZ;
		boolean flag = false;
		BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);

		if (this.worldObj.isBlockLoaded(blockpos))
		{
			boolean flag1 = false;

			while (!flag1 && blockpos.getY() > 0)
			{
				BlockPos blockpos1 = blockpos.down();
				Block block = this.worldObj.getBlockState(blockpos1).getBlock();

				if (block.getMaterial().blocksMovement())
				{
					flag1 = true;
				}
				else
				{
					--this.posY;
					blockpos = blockpos1;
				}
			}

			if (flag1)
			{
				super.setPositionAndUpdate(this.posX, this.posY, this.posZ);

				if (this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty() && !this.worldObj.isAnyLiquid(this.getEntityBoundingBox()))
				{
					flag = true;
				}
			}
		}

		if (!flag)
		{
			this.setPosition(d3, d4, d5);
			return false;
		}
		else
		{
			short short1 = 128;

			for (int i = 0; this.hasParticles && i < short1; ++i)
			{
				double d9 = (double)i / ((double)short1 - 1.0D);
				float f = (this.rand.nextFloat() - 0.5F) * 0.2F;
				float f1 = (this.rand.nextFloat() - 0.5F) * 0.2F;
				float f2 = (this.rand.nextFloat() - 0.5F) * 0.2F;
				double d6 = d3 + (this.posX - d3) * d9 + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D;
				double d7 = d4 + (this.posY - d4) * d9 + this.rand.nextDouble() * (double)this.height;
				double d8 = d5 + (this.posZ - d5) * d9 + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D;
				this.worldObj.spawnParticle(EnumParticleTypes.PORTAL, d6, d7, d8, (double)f, (double)f1, (double)f2, new int[0]);
			}

			this.worldObj.playSoundEffect(d3, d4, d5, "mob.endermen.portal", 1.0F, 1.0F);
			this.playSound("mob.endermen.portal", 1.0F, 1.0F);
			return true;
		}
	}

	protected boolean teleportRandomly()
	{
		double d0 = this.posX + (this.rand.nextDouble() - 0.5D) * range;
		double d1 = this.posY + (this.rand.nextDouble() - 0.5D) * range * 0.5D;
		double d2 = this.posZ + (this.rand.nextDouble() - 0.5D) * range;
		return this.teleportTo(d0, d1, d2);
	}
	
	@Override
	protected void updateAITasks()
	{
		super.updateAITasks();
		
		if (Config.ALLOW_ENDSTONE_WATER_HURT && this.isWet())
        {
            this.attackEntityFrom(DamageSource.drown, 1.0F);
            for(int i = 0; i < 16; ++i)
            {
            	if(this.teleportRandomly()) break;
            }
        }
		
		if(this.getAITarget() != null)
		{
			this.faceEntity(this.getAITarget(), 100.0F, 100.0F);
			if(rand.nextInt(5) == 0)
			{
				this.teleportToEntity(this.getAITarget());
			}
		}
		else if(rand.nextInt(this.ticksBetweenIdleTeleports) == 0)
		{
			this.teleportRandomly();
		}
	}

	@Override
	public void onLivingUpdate()
	{		
		if(this.worldObj.isRemote && this.hasParticles)
		{
			for (int i = 0; i < 2; ++i)
			{
				this.worldObj.spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height - 0.25D, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D, new int[0]);
			}
		}

		if (!this.worldObj.isRemote && this.isEntityAlive())
		{
			if (this.getAITarget() != null)
			{
				if (this.getAITarget() instanceof EntityPlayer)
				{
					if (this.getAITarget().getDistanceSqToEntity(this) < 64.0D)
					{
						this.teleportRandomly();
					}

					this.teleportDelay = 0;
				}
				else if (this.getAITarget().getDistanceSqToEntity(this) > 16.0D && this.teleportDelay++ >= 30 && this.teleportToEntity(this.getAITarget()))
				{
					this.teleportDelay = 0;
				}
			}
			else
			{           
				this.teleportDelay = 0;
			}
		}
		
		this.isJumping = false;
		super.onLivingUpdate();
	}

	@Override
	public boolean attackEntityFrom(DamageSource src, float amnt)
	{
		if (this.isEntityInvulnerable(src))
		{
			return false;
		}
		else
		{
			if (this.avoidsArrows && src instanceof EntityDamageSourceIndirect)
			{
				for (int i = 0; i < 16; ++i)
				{
					if (this.teleportRandomly())
					{
						return true;
					}
				}

				return super.attackEntityFrom(src, amnt);
			}
			else
			{
				if(rand.nextInt(100) < this.chanceToTeleportWhenHurt || (this.getAITarget() != null && rand.nextBoolean()))
				{
					this.teleportRandomly();
				}

				return super.attackEntityFrom(src, amnt);
			}
		}
	}

	/**
	 * Teleport the golem to another entity
	 */
	protected boolean teleportToEntity(Entity p_70816_1_)
	{
		Vec3 vec3 = new Vec3(this.posX - p_70816_1_.posX, this.getEntityBoundingBox().minY + (double)(this.height / 2.0F) - p_70816_1_.posY + (double)p_70816_1_.getEyeHeight(), this.posZ - p_70816_1_.posZ);
		vec3 = vec3.normalize();
		double d0 = 16.0D;
		double d1 = this.posX + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3.xCoord * d0;
		double d2 = this.posY + (double)(this.rand.nextInt(16) - 8) - vec3.yCoord * d0;
		double d3 = this.posZ + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3.zCoord * d0;
		return this.teleportTo(d1, d2, d3);
	}	

	@Override
	public String getGolemSound() 
	{
		return Block.soundTypeStone.soundName;
	}
}
