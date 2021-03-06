package mods.fossil.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.fossil.Fossil;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.common.IArmorTextureProvider;

public class ItemFeet extends ItemArmor implements IArmorTextureProvider
{
	public ItemFeet(int par1, EnumArmorMaterial par2EnumArmorMaterial, int par3, int par4) 
	{
		
         super(par1, par2EnumArmorMaterial, par3, par4);
         //this.setCreativeTab(Fossil.tabFArmor);
    }
 
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		itemIcon = iconRegister.registerIcon("fossil:Bone_Boots");
    }
	
	public String getArmorTextureFile(ItemStack par1)
    {
        if ( par1.itemID == Fossil.skullHelmet.itemID|| par1.itemID == Fossil.ribCage.itemID|| par1.itemID == Fossil.feet.itemID)
        {
              return "/mods/fossil/textures/armor/bone_1.png";
        }
        if(par1.itemID == Fossil.femurs.itemID)
		{
              return "/mods/fossil/textures/armor/bone_2.png";
        }
			  return "/mods/fossil/textures/armor/bone_2.png";
    }	
}