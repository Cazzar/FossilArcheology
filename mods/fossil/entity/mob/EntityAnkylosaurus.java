package mods.fossil.entity.mob;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import mods.fossil.Fossil;
import mods.fossil.client.DinoSoundHandler;
import mods.fossil.fossilAI.DinoAIAttackOnCollide;
import mods.fossil.fossilAI.DinoAIControlledByPlayer;
import mods.fossil.fossilAI.DinoAIEat;
import mods.fossil.fossilAI.DinoAIFollowOwner;
import mods.fossil.fossilAI.DinoAIGrowup;
import mods.fossil.fossilAI.DinoAIStarvation;
import mods.fossil.fossilAI.DinoAIWander;
import mods.fossil.fossilEnums.EnumDinoFoodBlock;
import mods.fossil.fossilEnums.EnumDinoFoodItem;
import mods.fossil.fossilEnums.EnumDinoType;
import mods.fossil.fossilEnums.EnumOrderType;
import mods.fossil.guiBlocks.GuiPedia;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class EntityAnkylosaurus extends EntityDinosaur
{
    private boolean looksWithInterest;
    //public final float HuntLimit = (float)(this.getHungerLimit() * 4 / 5);
    /*private float field_25048_b;
    private float field_25054_c;
    private boolean field_25052_g;*/
    //public int RushTick = 0;
    public boolean Running = false;

    public EntityAnkylosaurus(World var1)
    {
        super(var1,EnumDinoType.Ankylosaurus);
        this.OrderStatus = EnumOrderType.FreeMove;
        this.looksWithInterest = false;
        //this.health = 8;
        //this.experienceValue=3;
        
        /*this.Width0=1.2F;
        this.WidthInc=0.4F;
        this.Length0=1.1F;
        this.LengthInc=0.7F;
        this.Height0=1.2F;
        this.HeightInc=0.36F;
        
        /*this.HitboxXfactor=5.0F;
        this.HitboxYfactor=5.0F;
        this.HitboxZfactor=5.0F;*
        
        this.BaseattackStrength=3;
        //this.AttackStrengthIncrease=;
        //this.BreedingTime=;
        //this.BaseSpeed=;
        this.SpeedIncrease=0.016F;
        this.MaxAge=13;
        this.BaseHealth=21;
        this.HealthIncrease=1;
        //this.AdultAge=;
        //this.AgingTicks=;
        this.MaxHunger=500;
        //this.Hungrylevel=;*/
        this.updateSize();
        
        this.setSubSpecies((new Random()).nextInt(3) + 1);
        this.getNavigator().setAvoidsWater(true);
        //this.tasks.addTask(0, new DinoAIGrowup(this));
        //this.tasks.addTask(0, new DinoAIStarvation(this));
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.ridingHandler = new DinoAIControlledByPlayer(this));//, 0.34F));
//        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
        this.tasks.addTask(4, new DinoAIAttackOnCollide(this, true));
        this.tasks.addTask(5, new DinoAIFollowOwner(this, 5.0F, 2.0F));
        //this.tasks.addTask(6, new DinoAIEatFerns(this));
        //this.tasks.addTask(6, new DinoAIUseFeeder(this, 24, EnumDinoEating.Herbivorous));
        this.tasks.addTask(7, new DinoAIEat(this, 24));
        this.tasks.addTask(8, new DinoAIWander(this));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(10, new EntityAILookIdle(this));
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    public boolean isAIEnabled()
    {
        return !this.isModelized();
    }

    /**
     * Returns the texture's file path as a String.
     */
    public String getTexture()
    {
        if (this.isModelized())
            return super.getTexture();

            return "/mods/fossil/textures/mob/Ankylosaurus.png";

    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    /*public void writeEntityToNBT(NBTTagCompound var1)
    {
        super.writeEntityToNBT(var1);
        //var1.setInteger("SubSpecies", this.getSubSpecies());
        //var1.setBoolean("Angry", this.isSelfAngry());
    }*/

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    /*public void readEntityFromNBT(NBTTagCompound var1)
    {
        super.readEntityFromNBT(var1);
        //this.setSubSpecies(var1.getInteger("SubSpecies"));
        //this.CheckSkin();
        //this.setSelfAngry(var1.getBoolean("Angry"));
        //this.InitSize();
    }*/
    /**
     * Causes this entity to do an upwards motion (jumping).
     */
    protected void jump()
    {
        this.motionY = 0.5;
        this.isAirBorne = true;
        ForgeHooks.onLivingJump(this);
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return DinoSoundHandler.Anky_living;
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
    	return DinoSoundHandler.Anky_hurt;
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return DinoSoundHandler.Anky_death;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();
        /*this.field_25054_c = this.field_25048_b;

        if (this.looksWithInterest)
        {
            this.field_25048_b += (1.0F - this.field_25048_b) * 0.4F;
        }
        else
        {
            this.field_25048_b += (0.0F - this.field_25048_b) * 0.4F;
        }*/

        if (this.looksWithInterest)
        {
            this.numTicksToChaseTarget = 10;
        }
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
    }

    /*public float getInterestedAngle(float var1)
    {
        return (this.field_25054_c + (this.field_25048_b - this.field_25054_c) * var1) * 0.15F * (float)Math.PI;
    }*/

    public float getEyeHeight()
    {
        return this.height * 0.8F;
    }

    /**
     * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
     * use in wolves.
     */
    public int getVerticalFaceSpeed()
    {
        return this.isSitting() ? 20 : super.getVerticalFaceSpeed();
    }

    /**
     * Disables a mob's ability to move on its own while true.
     */
    protected boolean isMovementCeased()
    {
        return this.isSitting();// || this.field_25052_g;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource var1, int var2)
    {
        if (this.modelizedDrop())
        {
            return true;
        }
        else
        {
            Entity var3 = var1.getEntity();

            if (var3 != null && !(var3 instanceof EntityPlayer) && !(var3 instanceof EntityArrow))
            {
                var2 = (var2 + 1) / 2;
            }

            if (!super.attackEntityFrom(var1, var2))
            {
                return false;
            }
            else
            {
                if (!this.isTamed() && !this.isSelfAngry())
                {
                    if (var3 instanceof EntityPlayer)
                    {
                        this.setSelfAngry(true);
                        this.entityToAttack = var3;
                    }

                    if (var3 instanceof EntityArrow && ((EntityArrow)var3).shootingEntity != null)
                    {
                        var3 = ((EntityArrow)var3).shootingEntity;
                    }
                }
                else if (var3 != this && var3 != null)
                {
                    if (this.isTamed() && var3 instanceof EntityPlayer && ((EntityPlayer)var3).username.equalsIgnoreCase(this.getOwnerName()))
                    {
                        return true;
                    }

                    this.entityToAttack = var3;
                }

                return true;
            }
        }
    }

    /**
     * Finds the closest player within 16 blocks to attack, or null if this Entity isn't interested in attacking
     * (Animals, Spiders at day, peaceful PigZombies).
     */
    protected Entity findPlayerToAttack()
    {
        return this.isSelfAngry() ? this.worldObj.getClosestPlayerToEntity(this, 16.0D) : null;
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer var1)
    {
    	//Add special item interaction code here
        return super.interact(var1);
    }

    public boolean isSelfAngry()
    {
        return (this.dataWatcher.getWatchableObjectByte(16) & 2) != 0;
    }

    /*public boolean isSelfSitting()
    {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }*/

    public void setSelfAngry(boolean var1)
    {
        byte var2 = this.dataWatcher.getWatchableObjectByte(16);

        if (var1)
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 | 2)));
            this.moveSpeed = 2.0F;
        }
        else
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 & -3)));
            this.moveSpeed = 0.5F;
        }
    }

    /*public void setSelfSitting(boolean var1)
    {
        byte var2 = this.dataWatcher.getWatchableObjectByte(16);

        if (var1)
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 | 1)));
        }
        else
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 & -2)));
        }
    }

    public void setTamed(boolean var1)
    {
        byte var2 = this.dataWatcher.getWatchableObjectByte(16);

        if (var1)
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 | 4)));
        }
        else
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 & -5)));
        }
    }*/

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        return this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).size() == 0 && !this.worldObj.isAnyLiquid(this.boundingBox);
    }

   /* private void InitSize()
    {
        this.updateSize();
        this.setPosition(this.posX, this.posY, this.posZ);
        this.moveSpeed = this.getSpeed();//0.5F + (float)(this.getDinoAge() * 3);
    }*/

    /*private void ChangeTexture()
    {
        this.CheckSkin();
    }*/

    public void updateRiderPosition()
    {
        if (this.riddenByEntity != null)
        {
            this.riddenByEntity.setPosition(this.posX, this.posY + (double)this.getDinoHeight() * 0.65D + 0.07D * (double)(12 - this.getDinoAge()), this.posZ);
        }
    }
    
    /**
     * Applies a velocity to each of the entities pushing them away from each other. Args: entity
     */
    public void applyEntityCollision(Entity var1)
    {
        if (var1 instanceof EntityLiving && this.riddenByEntity != null && this.onGround && this.RiderSneak==true)
        {//you can hurt others with the dino while you ride it
            //this.onKillEntity((EntityLiving)var1);
            ((EntityLiving)var1).attackEntityFrom(DamageSource.causeMobDamage(this), 10);
        }
        else
        {
            super.applyEntityCollision(var1);
        }
    }


    public EntityAnkylosaurus spawnBabyAnimal(EntityAgeable var1)
    {
        return new EntityAnkylosaurus(this.worldObj);
    }

    private boolean FindFren(int var1)
    {
        float var2 = (float)(var1 * 2);
        int var3 = 0;
        int var4 = 0;
        int var5 = 0;
        int var6;
        int var7;

        for (var6 = -var1; var6 <= var1; ++var6)
        {
            for (var7 = -2; var7 <= 2; ++var7)
            {
                for (int var8 = -var1; var8 <= var1; ++var8)
                {
                    if (this.worldObj.getBlockId((int)Math.round(this.posX + (double)var6), (int)Math.round(this.posY + (double)var7), (int)Math.round(this.posZ + (double)var8)) == Fossil.ferns.blockID && var2 > this.GetDistanceWithXYZ(this.posX + (double)var6, this.posY + (double)var7, this.posZ + (double)var8))
                    {
                        var2 = this.GetDistanceWithXYZ(this.posX + (double)var6, this.posY + (double)var7, this.posZ + (double)var8);
                        var3 = var6;
                        var4 = var7;
                        var5 = var8;
                    }
                }
            }
        }

        if (var2 == (float)(var1 * 2))
        {
            return false;
        }
        else if (Math.sqrt((double)(var3 ^ 2 + var4 ^ 2 + var5 ^ 2)) >= 2.0D)
        {
            this.setPathToEntity(this.worldObj.getEntityPathToXYZ(this, (int)Math.round(this.posX + (double)var3), (int)Math.round(this.posY + (double)var4), (int)Math.round(this.posZ + (double)var5), 10.0F, true, false, true, false));
            return true;
        }
        else
        {
            this.FaceToCoord((int)(-(this.posX + (double)var3)), (int)(this.posY + (double)var4), (int)(-(this.posZ + (double)var5)));
            this.increaseHunger(10);

            for (var6 = -1; var6 <= 1; ++var6)
            {
                for (var7 = -1; var7 <= 1; ++var7)
                {
                    if (this.worldObj.getBlockId((int)Math.round(this.posX + (double)var3 + (double)var6), (int)Math.round(this.posY + (double)var4), (int)Math.round(this.posZ + (double)var5 + (double)var7)) == Fossil.ferns.blockID)
                    {
                        this.worldObj.playAuxSFX(2001, (int)Math.round(this.posX + (double)var3 + (double)var6), (int)Math.round(this.posY + (double)var4), (int)Math.round(this.posZ + (double)var5 + (double)var7), Block.tallGrass.blockID);
                        this.worldObj.setBlock((int)Math.round(this.posX + (double)var3 + (double)var6), (int)Math.round(this.posY + (double)var4), (int)Math.round(this.posZ + (double)var5 + (double)var7), 0);

                        if (this.worldObj.getBlockId((int)Math.round(this.posX + (double)var3 + (double)var6), (int)Math.round(this.posY + (double)var4) + 1, (int)Math.round(this.posZ + (double)var5 + (double)var7)) == Fossil.ferns.blockID)//fernUpper
                        {
                            this.worldObj.setBlock((int)Math.round(this.posX + (double)var3 + (double)var6), (int)Math.round(this.posY + (double)var4) + 1, (int)Math.round(this.posZ + (double)var5 + (double)var7), 0);
                        }

                        if (this.worldObj.getBlockId((int)Math.round(this.posX + (double)var3 + (double)var6), (int)Math.round(this.posY + (double)var4) - 1, (int)Math.round(this.posZ + (double)var5 + (double)var7)) == Block.grass.blockID)
                        {
                            this.worldObj.setBlock((int)Math.round(this.posX + (double)var3 + (double)var6), (int)Math.round(this.posY + (double)var4) - 1, (int)Math.round(this.posZ + (double)var5 + (double)var7), Block.dirt.blockID);
                        }
                    }
                }

                this.heal(3);
                this.setPathToEntity((PathEntity)null);
            }

            return true;
        }
    }

    /*public void updateSize()
    {
        this.setSize((float)(1.5D + 0.3D * (double)((float)this.getDinoAge())), (float)(1.5D + 0.3D * (double)((float)this.getDinoAge())));
    }*/
    
    /*protected int foodValue(Item var1)
    {
        return var1 == Item.wheat ? 10 : (var1 == Item.appleRed ? 30 : 0);
    }

    public void HoldItem(Item var1) {}*/

    /*public float getGLX()
    {
        return (float)(1.5D + 0.3D * (double)((float)this.getDinoAge()));
    }

    public float getGLY()
    {
        return (float)(1.5D + 0.3D * (double)((float)this.getDinoAge()));
    }*/

    /*public String[] additionalPediaMessage()
    {
        String[] var1 = null;

        if (!this.isTamed())
        {
            var1 = new String[] {UntamedText};
        }
        else
        {
            ArrayList var2 = new ArrayList();

            if (this.isTamed() && this.getDinoAge() > 4 && this.riddenByEntity == null)
            {
                var2.add(RidiableText);
            }

            if (!var2.isEmpty())
            {
                var1 = new String[1];
                var1 = (String[])var2.toArray(var1);
            }
        }

        return var1;
    }*/

    /*public EntityAgeable func_90011_a(EntityAgeable var1)
    {
        return this.spawnBabyAnimal(var1);
    }*/

	@Override
	public EntityAgeable createChild(EntityAgeable var1) 
	{
		return null;
	}
}
