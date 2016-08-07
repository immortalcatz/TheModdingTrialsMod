package com.trials.modsquad.block.machines;

import com.trials.modsquad.ModSquad;
import com.trials.modsquad.Ref;
import com.trials.modsquad.block.TileEntities.TileGrinder;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

import static com.trials.modsquad.Ref.GUI_ID_GRINDER;

public class BlockGrinder extends Block {

    private TileEntity grinder;

    public BlockGrinder(String s, String s1) {
        super(Material.IRON);
        setUnlocalizedName(s);
        setRegistryName(s1);
        setCreativeTab(Ref.tabModSquad);
        setHarvestLevel("pickaxe", 1);
        setResistance(30F);
        setHardness(5F);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(worldIn.getTileEntity(pos) == null || playerIn.isSneaking()) return false;
        playerIn.openGui(ModSquad.instance, GUI_ID_GRINDER, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileGrinder();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) { // Drop items when block breaks
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        TileEntity t = worldIn.getTileEntity(pos);
        if(!(t instanceof IInventory)) return;
        ItemStack s;
        for(int i = 0; i<((IInventory) t).getSizeInventory(); ++i)
            if((s = ((IInventory) t).getStackInSlot(i))!=null && s.stackSize > 0){
                EntityItem item = new EntityItem(worldIn, rand.nextFloat()*0.8f+0.1f, rand.nextFloat()*.8f+.1f, rand.nextFloat()*.8f+.1f,
                        new ItemStack(s.getItem(), s.stackSize, s.getItemDamage()));
                if(s.hasTagCompound() && s.getTagCompound()!=null) item.getEntityItem().setTagCompound(s.getTagCompound().copy());
                item.motionX = rand.nextGaussian() * .05f;
                item.motionY = rand.nextGaussian() * .05f + .2f;
                item.motionZ = rand.nextGaussian() * .05f;
                worldIn.spawnEntityInWorld(item);
                s.stackSize = 0;
            }
        super.breakBlock(worldIn, pos, state);
    }

    public static final PropertyDirection PROPERTYFACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[]{PROPERTYFACING});
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        EnumFacing enumfacing = (placer == null) ? EnumFacing.NORTH : EnumFacing.fromAngle(placer.rotationYaw);

        return this.getDefaultState().withProperty(PROPERTYFACING, enumfacing);
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return false;
    }
}
