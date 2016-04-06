package com.golems.entity;

import com.golems.main.ContentInit;

import net.minecraft.block.Block;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public abstract class GolemMultiTextured extends GolemBase 
{
	private static final int DATA_WATCHER_TEXTURE = 13;
	private static final String NBT_TEXTURE = "GolemTextureData"; 
	/** {@code textures} cannot exceed 256 in length **/
	private final String[] textures;
	
	public GolemMultiTextured(World world, float attack, Block pick, String[] textureNames)
	{
		super(world, attack, pick);
		this.textures = textureNames;
	}
	
	public GolemMultiTextured(World world, float attack, String[] textureNames) 
	{
		this(world, attack, ContentInit.golemHead, textureNames);		
	}
	
	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataWatcher().addObject(DATA_WATCHER_TEXTURE, new Byte((byte)0));
	}
	
	@Override
	protected void applyTexture()
	{
		this.setTextureType(this.getGolemTexture("clay"));
	}
	
	@Override
	public boolean interact(EntityPlayer player)
	{
		// only change texture when player has empty hand
		if(player.getHeldItem() != null)
		{
			return super.interact(player);
		}
		else
		{
			this.setTextureNum((byte)((this.getTextureNum() + 1) % this.textures.length));
			this.setTextureType(this.getSpecialGolemTexture());
			this.writeEntityToNBT(new NBTTagCompound());
			player.swingItem();
			return true;
		}
	}
	
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		// since textureNum is correct, update texture AFTER loading from NBT and init
		if(this.ticksExisted == 2)
		{
			this.setTextureType(this.getSpecialGolemTexture(this.getTextureStringFromArray()));
			// debug:
			//System.out.println("Set texture using textureNum = " + this.getTextureNum());
		}
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt)
    {
		super.writeEntityToNBT(nbt);
		nbt.setByte(NBT_TEXTURE, (byte)this.getTextureNum());
    }
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		this.setTextureNum(nbt.getByte(NBT_TEXTURE));
		this.setTextureType(this.getSpecialGolemTexture(getTextureStringFromArray()));
		// debug:
		//System.out.println("NBT textureNum = " + this.getTextureNum());
	}
	
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData data)
	{
		this.setTextureNum((byte)this.rand.nextInt(this.textures.length));
		this.setTextureType(this.getSpecialGolemTexture(getTextureStringFromArray()));
		return super.onInitialSpawn(difficulty, data);
	}
	
	/** Call getGolemTexture with specialized name concatenation **/
	public ResourceLocation getSpecialGolemTexture()
	{
		return getSpecialGolemTexture(this.getTextureStringFromArray());
	}
	
	/** Call getGolemTexture with specialized name concatenation **/
	public ResourceLocation getSpecialGolemTexture(String s)
	{
		return GolemBase.getGolemTexture(getModId(), this.getTexturePrefix() + "_" + s);
	}
	
	public void setTextureNum(byte toSet)
	{
		this.dataWatcher.updateObject(DATA_WATCHER_TEXTURE, new Byte(toSet));
	}

	public int getTextureNum() 
	{
		return (int)this.dataWatcher.getWatchableObjectByte(DATA_WATCHER_TEXTURE);
	}
	
	public String getTextureStringFromArray()
	{
		return this.textures[this.getTextureNum()];
	}
	
	public abstract String getTexturePrefix();
	
	public abstract String getModId();
}

