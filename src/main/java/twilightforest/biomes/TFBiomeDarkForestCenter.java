package twilightforest.biomes;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import twilightforest.TFFeature;
import twilightforest.TwilightForestMod;

public class TFBiomeDarkForestCenter extends TFBiomeDarkForest {

	public TFBiomeDarkForestCenter(Builder props) {
		super(props);
	}

	@Override
	public int getGrassColor(BlockPos pos) {
		double d0 = INFO_NOISE.getValue(pos.getX() * 0.0225D, pos.getZ() * 0.0225D);
		return d0 < -0.2D ? 0x667540 : 0x554114;
	}

	@Override
	public int getFoliageColor(BlockPos pos) {
		double d0 = INFO_NOISE.getValue(pos.getX() * 0.0225D, pos.getZ() * 0.0225D);
		return d0 < -0.1D ? 0xf9821e : 0xe94e14;
	}

	@Override
	protected ResourceLocation[] getRequiredAdvancements() {
		return new ResourceLocation[]{ TwilightForestMod.prefix("progress_knights") };
	}


	@Override
	protected TFFeature getContainedFeature() {
		return TFFeature.DARK_TOWER;
	}
}
