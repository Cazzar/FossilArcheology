package mod.fossil.common.entity;

import com.google.common.io.ByteArrayDataInput;


import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import java.util.List;

import mod.fossil.common.Fossil;
import mod.fossil.common.entity.mob.EntityBrachiosaurus;
import mod.fossil.common.entity.mob.EntityDinosaurce;
import mod.fossil.common.entity.mob.EntityMosasaurus;
import mod.fossil.common.entity.mob.EntityPlesiosaur;
import mod.fossil.common.entity.mob.EntityPterosaur;
import mod.fossil.common.entity.mob.EntityRaptor;
import mod.fossil.common.entity.mob.EntityStegosaurus;
import mod.fossil.common.entity.mob.EntityTRex;
import mod.fossil.common.entity.mob.EntityTriceratops;
import mod.fossil.common.entity.mob.Entitydil;
import mod.fossil.common.fossilEnums.EnumDinoType;
import mod.fossil.common.fossilEnums.EnumOrderType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.src.ModLoader;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenForest;
import net.minecraft.world.biome.BiomeGenSnow;
import net.minecraft.world.biome.BiomeGenTaiga;

public class EntityDinoEgg extends Entity implements IEntityAdditionalSpawnData
{
    private static final String HEAD = "Dinoegg.";
    private static final String MSGHEAD = "Dinoegg.msghead";
    private static final String MSGTAIL = ".msgtail";
    private static final String COLD = "cold";
    private static final String DRY = "dry";
    private static final String PEDIA = "PediaText.egg.";
    public int damageTaken;
    public int timeSinceHit;
    public EnumDinoType DinoInside;
    public int BirthTick;
    public String ParentOwner;
    private int HatchTime;
    public final int HatchingNeedTime;

    public EntityDinoEgg(World var1, EnumDinoType var2)
    {
        super(var1);
        this.BirthTick = 0;
        this.ParentOwner = "";
        this.HatchingNeedTime = this.HatchTime;
        this.damageTaken = 0;
        this.timeSinceHit = 0;
        this.preventEntitySpawning = true;
        this.setSize(0.5F, 1.5F);
        this.yOffset = this.height;
        this.DinoInside = var2;
    }

    /**
     * Returns the texture's file path as a String.
     */
    public String getTexture()
    {
        int var1 = this.DinoInside.ordinal();
        return var1 < 4 ? "/mod/fossil/common/textures/eggTexture" + (var1 + 1) + ".png" : "/mod/fossil/common/textures/eggTexture" + var1 + ".png";
    }

    public EntityDinoEgg(World var1)
    {
        this(var1, (EnumDinoType)null);
    }

    public EntityDinoEgg(World var1, EnumDinoType var2, EntityDinosaurce var3)
    {
        this(var1, var2);
        this.ParentOwner = var3.getOwnerName();
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void entityInit()
    {
        if (Fossil.DebugMode)
        {
            this.HatchTime = 1;
        }
        else
        {
            this.HatchTime = 3000;
        }
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats or minecarts.
     */
    public AxisAlignedBB getCollisionBox(Entity var1)
    {
        return var1.boundingBox;
    }

    /**
     * returns the bounding box for this entity
     */
    public AxisAlignedBB getBoundingBox()
    {
        return this.boundingBox;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return true;
    }

    public EntityDinoEgg(World var1, double var2, double var4, double var6, EnumDinoType var8)
    {
        this(var1, var8);
        this.setPosition(var2, var4 + (double)this.yOffset, var6);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = var2;
        this.prevPosY = var4;
        this.prevPosZ = var6;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return (double)this.height * 0.0D - 0.30000001192092896D;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource var1, int var2)
    {
        if (!this.worldObj.isRemote && !this.isDead)
        {
            this.timeSinceHit = 10;
            this.damageTaken += var2 * 10;
            this.setBeenAttacked();

            if (this.damageTaken > 40)
            {
                if (this.riddenByEntity != null)
                {
                    this.riddenByEntity.mountEntity(this);
                }

                this.setDead();
            }

            return true;
        }
        else
        {
            return true;
        }
    }

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    public void performHurtAnimation()
    {
        this.timeSinceHit = 10;
        this.damageTaken += this.damageTaken * 10;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();
        this.HandleHatching();
        super.onUpdate();

        if (this.timeSinceHit > 0)
        {
            --this.timeSinceHit;
        }

        if (this.damageTaken > 0)
        {
            --this.damageTaken;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        byte var1 = 5;
        double var2 = 0.0D;

        for (int var4 = 0; var4 < var1; ++var4)
        {
            double var5 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(var4 + 0) / (double)var1 - 0.125D;
            double var7 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(var4 + 1) / (double)var1 - 0.125D;
            AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(this.boundingBox.minX, var5, this.boundingBox.minZ, this.boundingBox.maxX, var7, this.boundingBox.maxZ);
        }

        double var21;

        if (var2 < 1.0D)
        {
            var21 = var2 * 2.0D - 1.0D;
            this.motionY += 0.03999999910593033D * var21;
        }
        else
        {
            if (this.motionY < 0.0D)
            {
                this.motionY /= 2.0D;
            }

            this.motionY += 0.007000000216066837D;
        }

        if (this.riddenByEntity != null)
        {
            this.motionX += this.riddenByEntity.motionX * 0.2D;
            this.motionZ += this.riddenByEntity.motionZ * 0.2D;
        }

        var21 = 0.4D;

        if (this.motionX < -var21)
        {
            this.motionX = -var21;
        }

        if (this.motionX > var21)
        {
            this.motionX = var21;
        }

        if (this.motionZ < -var21)
        {
            this.motionZ = -var21;
        }

        if (this.motionZ > var21)
        {
            this.motionZ = var21;
        }

        if (this.onGround)
        {
            this.motionX *= 0.5D;
            this.motionY *= 0.5D;
            this.motionZ *= 0.5D;
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        double var6 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        double var8;
        double var10;

        if (var6 > 0.15D)
        {
            var8 = Math.cos((double)this.rotationYaw * Math.PI / 180.0D);
            var10 = Math.sin((double)this.rotationYaw * Math.PI / 180.0D);

            for (int var12 = 0; (double)var12 < 1.0D + var6 * 60.0D; ++var12)
            {
                double var13 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
                double var15 = (double)(this.rand.nextInt(2) * 2 - 1) * 0.7D;
                double var17;
                double var19;

                if (this.rand.nextBoolean())
                {
                    var17 = this.posX - var8 * var13 * 0.8D + var10 * var15;
                    var19 = this.posZ - var10 * var13 * 0.8D - var8 * var15;
                }
                else
                {
                    var17 = this.posX + var8 + var10 * var13 * 0.7D;
                    var19 = this.posZ + var10 - var8 * var13 * 0.7D;
                }
            }
        }

        if (this.isCollidedHorizontally && var6 > 0.15D)
        {
            if (!this.worldObj.isRemote)
            {
                this.setDead();
            }
        }
        else
        {
            this.motionX *= 0.9900000095367432D;
            this.motionY *= 0.949999988079071D;
            this.motionZ *= 0.9900000095367432D;
        }

        this.rotationPitch = 0.0F;
        var8 = (double)this.rotationYaw;
        var10 = this.prevPosX - this.posX;
        double var22 = this.prevPosZ - this.posZ;

        if (var10 * var10 + var22 * var22 > 0.001D)
        {
            var8 = (double)((float)(Math.atan2(var22, var10) * 180.0D / Math.PI));
        }

        double var14;

        for (var14 = var8 - (double)this.rotationYaw; var14 >= 180.0D; var14 -= 360.0D)
        {
            ;
        }

        while (var14 < -180.0D)
        {
            var14 += 360.0D;
        }

        if (var14 > 20.0D)
        {
            var14 = 20.0D;
        }

        if (var14 < -20.0D)
        {
            var14 = -20.0D;
        }

        this.rotationYaw = (float)((double)this.rotationYaw + var14);
        this.setRotation(this.rotationYaw, this.rotationPitch);
        List var16 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.0D, 0.0D, 0.0D));
        int var23;

        if (var16 != null && var16.size() > 0)
        {
            for (var23 = 0; var23 < var16.size(); ++var23)
            {
                Entity var18 = (Entity)var16.get(var23);

                if (var18 != this.riddenByEntity && var18.canBePushed() && var18 instanceof EntityBoat)
                {
                    var18.applyEntityCollision(this);
                }
            }
        }

        for (var23 = 0; var23 < 4; ++var23)
        {
            int var24 = MathHelper.floor_double(this.posX + ((double)(var23 % 2) - 0.5D) * 0.8D);
            int var25 = MathHelper.floor_double(this.posY);
            int var20 = MathHelper.floor_double(this.posZ + ((double)(var23 / 2) - 0.5D) * 0.8D);

            if (this.worldObj.getBlockId(var24, var25, var20) == Block.snow.blockID)
            {
                this.worldObj.setBlockWithNotify(var24, var25, var20, 0);
            }
        }

        if (this.riddenByEntity != null && this.riddenByEntity.isDead)
        {
            this.riddenByEntity = null;
        }
    }

    public void updateRiderPosition()
    {
        if (this.riddenByEntity != null)
        {
            double var1 = Math.cos((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
            double var3 = Math.sin((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
            this.riddenByEntity.setPosition(this.posX + var1, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + var3);
        }
    }

    private void HandleHatching()
    {
        float var2 = this.getBrightness(1.0F);
        EntityPlayer var4 = null;

        if ((this.ParentOwner == "" || this.worldObj.getPlayerEntityByName(this.ParentOwner) == null) && this.worldObj.getClosestPlayerToEntity(this, 16.0D) != null)
        {
            var4 = this.worldObj.getClosestPlayerToEntity(this, 16.0D);
        }

        if (this.DinoInside == EnumDinoType.Mosasaurus)
        {
            if (this.inWater)
                ++this.BirthTick;
            else
                --this.BirthTick;
        }
        else if ((double)var2 >= 0.5D)
            ++this.BirthTick;
        else if (!this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)))
            --this.BirthTick;

        if (this.BirthTick <= -this.HatchingNeedTime)
        {
            String var6;

            if (this.DinoInside == EnumDinoType.Mosasaurus)
            {
                var6 = Fossil.GetLangTextByKey("Dinoegg.dry.msgtail");
            }
            else
            {
                var6 = Fossil.GetLangTextByKey("Dinoegg.cold.msgtail");
            }

            String var1 = Fossil.GetLangTextByKey("Dinoegg.msghead");
            Fossil.ShowMessage(var1 + EntityDinosaurce.GetNameByEnum(this.DinoInside, false) + var6, var4);
            this.setDead();
        }
        else
        {
            if (this.BirthTick >= this.HatchingNeedTime)
            {
                if (this.worldObj.isRemote)
                {
                    return;
                }

                BiomeGenBase var3 = this.worldObj.provider.worldChunkMgr.getBiomeGenAt((int)Math.floor(this.posX), (int)Math.floor(this.posZ));
                Object var5 = null;
                switch (this.DinoInside)
                {
                    case Triceratops:var5 = new EntityTriceratops(this.worldObj);break;
                    case Raptor:var5 = new EntityRaptor(this.worldObj);
                        				if (var3 instanceof BiomeGenForest)
                        					((EntityRaptor)var5).setSubSpecies(2);
                        				else if (var3 instanceof BiomeGenSnow || var3 instanceof BiomeGenTaiga)
                        					((EntityRaptor)var5).setSubSpecies(1);
                        				else
                        					((EntityRaptor)var5).setSubSpecies(3);break;
                    case TRex:var5 = new EntityTRex(this.worldObj);break;
                    case Pterosaur:var5 = new EntityPterosaur(this.worldObj);break;
                    case Plesiosaur:var5 = new EntityPlesiosaur(this.worldObj);break;
                    case Mosasaurus:var5 = new EntityMosasaurus(this.worldObj);break;
                    case Stegosaurus:var5 = new EntityStegosaurus(this.worldObj);break;
                    case Utahraptor:var5 = new Entitydil(this.worldObj);break;
                    case Brachiosaurus:var5 = new EntityBrachiosaurus(this.worldObj);break;

                    default:
                        Fossil.ShowMessage("Bug:Impossible result.", var4);
                        this.setDead();
                        return;
                }
                if(((EntityDinosaurce)var5).SelfType.isTameable() && var4 != null)
                {// Tameable and player next to it
                        ((EntityDinosaurce)var5).setOwner(var4.username);
                        ((EntityDinosaurce)var5).setTamed(true);
                }

                ((EntityLiving)var5).setLocationAndAngles((double)((int)Math.floor(this.posX)), (double)((int)Math.floor(this.posY) + 1), (double)((int)Math.floor(this.posZ)), this.worldObj.rand.nextFloat() * 360.0F, 0.0F);

                if (this.worldObj.checkIfAABBIsClear(((EntityLiving)var5).boundingBox) && this.worldObj.getCollidingBoundingBoxes((Entity)var5, ((EntityLiving)var5).boundingBox).size() == 0 && (!this.worldObj.isAnyLiquid(((EntityLiving)var5).boundingBox) || this.DinoInside == EnumDinoType.Mosasaurus))
                {
                    if (!this.worldObj.isRemote)
                    {
                        this.worldObj.spawnEntityInWorld((Entity)var5);
                    }

                    if (var4 != null)
                    {
                        Fossil.ShowMessage(Fossil.GetLangTextByKey("Dinoegg.Hatched"), var4);
                    }

                    this.setDead();
                }
                else
                {
                    Fossil.ShowMessage(Fossil.GetLangTextByKey("Dinoegg.NoSpace"), var4);
                    this.BirthTick -= 500;
                }
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound var1)
    {
        var1.setInteger("BirthTick", this.BirthTick);
        var1.setInteger("DinoType", this.EnumToInt(this.DinoInside));
        var1.setString("ParentOwner", this.ParentOwner);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound var1)
    {
        EnumDinoType[] var2 = EnumDinoType.values();
        this.BirthTick = var1.getInteger("BirthTick");
        this.DinoInside = var2[var1.getInteger("DinoType")];
        this.ParentOwner = var1.getString("ParentOwner");
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer var1)
    {
        ItemStack var2 = var1.inventory.getCurrentItem();

        if (var2 == null)
        {
            ItemStack var3 = new ItemStack(Fossil.ancientegg, 1, this.EnumToInt(this.DinoInside));

            if (var1.inventory.addItemStackToInventory(var3))
            {
                this.worldObj.playSoundAtEntity(var1, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                this.setDead();
            }
            return false;
        }
        else if (FMLCommonHandler.instance().getSide().isClient() && var2.getItem().itemID == Fossil.dinoPedia.itemID)
        {
            this.showpedia(var1);
            return true;
        }
        else
        {
            return false;
        }
    }

    private int EnumToInt(EnumDinoType var1)
    {
        return this.DinoInside.ordinal();
    }

    private void showpedia(EntityPlayer var1)
    {
        String var2 = "";
        String var3 = Fossil.GetLangTextByKey("PediaText.egg.selfHead") + EntityDinosaurce.GetNameByEnum(this.DinoInside, false) + Fossil.GetLangTextByKey("PediaText.egg.selfTail");
        int var4 = (int)Math.floor((double)((float)this.BirthTick / (float)this.HatchingNeedTime * 100.0F));
        Fossil.ShowMessage(var3, var1);

        if (this.DinoInside == EnumDinoType.Mosasaurus)
        {
            if (this.BirthTick >= 0)
            {
                var2 = Fossil.GetLangTextByKey("PediaText.egg.wet");
            }
            else
            {
                var2 = Fossil.GetLangTextByKey("PediaText.egg.dry");
            }
        }
        else if (this.BirthTick >= 0)
        {
            var2 = Fossil.GetLangTextByKey("PediaText.egg.warm");
        }
        else
        {
            var2 = Fossil.GetLangTextByKey("PediaText.egg.cold");
        }

        String var5 = Fossil.GetLangTextByKey("PediaText.egg.Status");
        String var6 = Fossil.GetLangTextByKey("PediaText.egg.Progress");
        Fossil.ShowMessage(var5 + var2, var1);
        Fossil.ShowMessage(var6 + var4 + "/100", var1);
    }

    public void writeSpawnData(ByteArrayDataOutput var1)
    {
        var1.writeInt(this.BirthTick);
        var1.writeInt(this.EnumToInt(this.DinoInside));
    }

    public void readSpawnData(ByteArrayDataInput var1)
    {
        this.BirthTick = var1.readInt();
        this.DinoInside = EnumDinoType.values()[var1.readInt()];
    }
}
