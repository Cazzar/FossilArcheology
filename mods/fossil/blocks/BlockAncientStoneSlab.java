package mods.fossil.blocks;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.fossil.Fossil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHalfSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockAncientStoneSlab extends BlockHalfSlab
{
	public static final String[] blockStepTypes = { "anicentStone" };
    private Icon theIcon;
    
	public BlockAncientStoneSlab(int par1, boolean par2)
	{
		super(par1, par2, Material.rock);
		setLightOpacity(0);
		useNeighborBrightness[this.blockID] = true;
	}
	
	@Override
	public void registerIcons(IconRegister par1IconRegister)
	{
        this.blockIcon = par1IconRegister.registerIcon("fossil:Ancient_Stonebricks");
        this.theIcon = par1IconRegister.registerIcon("fossil:Ancient_Stonebricks");
	}

	public int idDropped(int par1, Random par2Random, int par3)
	{
		return Fossil.ancientStoneSingleSlab.blockID;
	}
/*
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving)
	{
		if(par1World.getBlockId(par2, par3 - 1, par4) == Fossil.palaeSingleSlab.blockID)
		{
			par1World.setBlock(par2, par3, par4, 0);
			par1World.setBlock(par2, par3 - 1, par4, Fossil.palaeDoubleSlab.blockID);
		}
		if(par1World.getBlockId(par2, par3 + 1, par4) == Fossil.palaeSingleSlab.blockID)
		{
			par1World.setBlock(par2, par3, par4, 0);
			par1World.setBlock(par2, par3 + 1, par4, Fossil.palaeDoubleSlab.blockID);
		}
	}
	*/

    protected ItemStack createStackedBlock(int par1)
    {
        return new ItemStack(Fossil.ancientStoneSingleSlab.blockID, 2, par1 & 7);
    }


    /**
     * Returns the slab block name with step type.
     */
    public String getFullSlabName(int par1)
    {
        if (par1 < 0 || par1 >= blockStepTypes.length)
        {
            par1 = 0;
        }

        return super.getUnlocalizedName() + "." + blockStepTypes[par1];
    }

    @SideOnly(Side.CLIENT)

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        if (par1 != Fossil.ancientStoneDoubleSlab.blockID)
        {
            par3List.add(new ItemStack(par1, 1, 0));
          }
    }

}
