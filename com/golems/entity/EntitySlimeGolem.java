package com.golems.entity;

import java.util.List;

import com.golems.main.Config;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class EntitySlimeGolem extends GolemBase 
{			
	public EntitySlimeGolem(World world) 
	{
		super(world, 2.5F, Blocks.slime_block);
		this.tasks.addTask(0, new EntityAISwimming(this));		
		((PathNavigateGround)this.getNavigator()).setAvoidsWater(false);
	}

	@Override
	protected void applyTexture()
	{
		this.setTextureType(this.getGolemTexture("slime"));
	}

	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		if(super.attackEntityAsMob(entity))
		{
			if(Config.ALLOW_SLIME_SPECIAL)
			{
				knockbackTarget(entity, Config.TWEAK_SLIME);
			}
			return true;
		}
		return false;
	}

	@Override
	protected void damageEntity(DamageSource source, float amount) 
	{
		if (!this.isEntityInvulnerable(source))
		{
			super.damageEntity(source, amount);
			if(source.getEntity() != null && Config.ALLOW_SLIME_SPECIAL)
			{
				knockbackTarget(source.getEntity(), Config.TWEAK_SLIME * 3 / 5);
			}
		}
	}
	
	private void knockbackTarget(Entity entity, final double KNOCKBACK_FACTOR)
	{
		// debug:
		//System.out.println("applying knockback: vX=" + entity.motionX + "; vY=" + entity.motionY + "; vZ=" + entity.motionZ);
		double dX = (entity.posX - this.posX) * KNOCKBACK_FACTOR;
		double dZ = (entity.posZ - this.posZ) * KNOCKBACK_FACTOR;
		entity.addVelocity(dX, KNOCKBACK_FACTOR / 4, dZ);
		entity.attackEntityFrom(DamageSource.causeMobDamage(this), 0.1F);
		// debug:
		//System.out.println("finished knockback: vX=" + entity.motionX + "; vY=" + entity.motionY + "; vZ=" + entity.motionZ);
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(85.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.29D);
		this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.5D);
	}

	@Override
	public void addGolemDrops(List<WeightedRandomChestContent> dropList, boolean recentlyHit, int lootingLevel)	
	{
		int size = 11 + this.rand.nextInt(16 + lootingLevel * 4);
		GolemBase.addGuaranteedDropEntry(dropList, new ItemStack(Items.slime_ball, size));
	}

	@Override
	public String getGolemSound() 
	{
		return Block.soundTypeGrass.soundName;
	}
}
