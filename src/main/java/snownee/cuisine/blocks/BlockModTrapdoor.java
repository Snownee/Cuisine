package snownee.cuisine.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import snownee.kiwi.block.IModBlock;

public class BlockModTrapdoor extends BlockTrapDoor implements IModBlock
{
    private final String name;

    public BlockModTrapdoor(String name, Material materialIn)
    {
        super(materialIn);
        this.name = name;
        disableStats();
        setSoundType(SoundType.WOOD);
    }

    @Override
    public Block cast()
    {
        return this;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void register(String modid)
    {
        setRegistryName(modid, getName());
        setTranslationKey(modid + "." + getName());
    }

}
