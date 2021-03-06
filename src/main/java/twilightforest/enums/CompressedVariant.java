package twilightforest.enums;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.Material;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum CompressedVariant implements IStringSerializable {

    IRONWOOD(Material.WOOD, SoundType.WOOD),
    FIERY(Material.IRON, SoundType.METAL, MaterialColor.BLACK_TERRACOTTA),
    STEELLEAF(Material.LEAVES, SoundType.PLANT),
    ARCTIC_FUR(Material.WOOL, SoundType.CLOTH),
    CARMINITE(Material.CLAY, SoundType.SLIME, MaterialColor.RED);

    public final Material material;
    public final SoundType soundType;
    public final MaterialColor mapColor;

    CompressedVariant(Material material, SoundType soundType) {
        this(material, soundType, material.getColor());
    }

    CompressedVariant(Material material, SoundType soundType, MaterialColor mapColor) {
        this.material = material;
        this.soundType = soundType;
        this.mapColor = mapColor;
    }

    @Override
    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
