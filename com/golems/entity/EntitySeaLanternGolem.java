package com.golems.entity;

import java.util.List;

import com.golems.main.ContentInit;
import com.golems.main.ExtraGolems;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntitySeaLanternGolem extends GolemLightProvider
{			
	public EntitySeaLanternGolem(World world) 
	{
		super(world, 4.0F, Blocks.sea_lantern, EnumLightLevel.FULL);
	}
	
	protected void applyTexture()
	{
		this.setTextureType(this.getGolemTexture("sea_lantern"));
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(26.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.26D);
	}

	@Override
	public void addGolemDrops(List<WeightedRandomChestContent> dropList, boolean recentlyHit, int lootingLevel)	
	{
		int size = 1 + this.rand.nextInt(2 + lootingLevel);
		GolemBase.addGuaranteedDropEntry(dropList, new ItemStack(Blocks.sea_lantern, size > 4 ? 4 : size));
		GolemBase.addDropEntry(dropList, Items.prismarine_shard, 0, 1, 3, 6 + lootingLevel * 5);
		GolemBase.addDropEntry(dropList, Items.prismarine_crystals, 0, 1, 3, 6 + lootingLevel * 5);
	}
 
	@Override
	public String getGolemSound() 
	{
		return Block.soundTypeGlass.soundName;
	}
}
