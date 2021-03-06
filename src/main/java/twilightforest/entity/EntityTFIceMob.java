package twilightforest.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import twilightforest.TwilightForestMod;
import twilightforest.client.particle.TFParticleType;

public abstract class EntityTFIceMob extends MonsterEntity {
	public EntityTFIceMob(EntityType<? extends EntityTFIceMob> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	public void livingTick() {
		if (!this.onGround && this.getMotion().getY() < 0.0D) {
			this.motionY *= 0.6D;
		}
		super.livingTick();
		if (this.world.isRemote) {
			// make snow particles
			for (int i = 0; i < 3; i++) {
				float px = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.3F;
				float py = this.getEyeHeight() + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.5F;
				float pz = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.3F;

				//TODO: Use Particle registry now (yay)
				TwilightForestMod.proxy.spawnParticle(TFParticleType.SNOW_GUARDIAN, this.lastTickPosX + px, this.lastTickPosY + py, this.lastTickPosZ + pz, 0, 0, 0);
				if (this.world.getBiome(this.getPosition()).getTemperature(this.getPosition()) > 1.0F || this.isBurning()) {
					this.world.addParticle(ParticleTypes.CLOUD, this.lastTickPosX + px, this.lastTickPosY + py, this.lastTickPosZ + pz, 0, 0.1F, 0);
					this.world.addParticle(ParticleTypes.DRIPPING_WATER, this.lastTickPosX + px, this.lastTickPosY + py, this.lastTickPosZ + pz, 0, 0, 0);
				}
			}
		}
		if (this.world.getBiome(this.getPosition()).getTemperature(this.getPosition()) > 1.0F && this.ticksExisted % 20 == 0) {
			//BURN!!!
			this.attackEntityFrom(DamageSource.ON_FIRE, 1.0F);
		}
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}
}
