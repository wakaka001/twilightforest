package twilightforest.structures;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.util.math.MutableBoundingBox;
import twilightforest.TFFeature;
import twilightforest.entity.*;
import twilightforest.loot.TFTreasure;

import java.util.Random;

public class ComponentTFHollowHill extends StructureTFComponentOld {

	int hillSize;
	int radius;

	public ComponentTFHollowHill() {
		super();
	}

	public ComponentTFHollowHill(TFFeature feature, World world, Random rand, int i, int size, int x, int y, int z) {
		super(feature, i);

		this.setCoordBaseMode(Direction.SOUTH);

		// get the size of this hill?
		this.hillSize = size;
		radius = ((hillSize * 2 + 1) * 8) - 6;

		// can we determine the size here?
		this.boundingBox = StructureTFComponentOld.getComponentToAddBoundingBox(x, y, z, -radius, -(3 + hillSize), -radius, radius * 2, radius / 2, radius * 2, Direction.SOUTH);
	}

	@Override
	protected void writeStructureToNBT(CompoundNBT tagCompound) {
		super.writeStructureToNBT(tagCompound);
		tagCompound.putInt("hillSize", this.hillSize);
	}

	@Override
	protected void readAdditional(CompoundNBT tagCompound) {
		super.readAdditional(tagCompound);
		this.hillSize = tagCompound.getInt("hillSize");
		this.radius = ((hillSize * 2 + 1) * 8) - 6;
	}

	/**
	 * Add in all the blocks we're adding.
	 */
	@Override
	public boolean addComponentParts(IWorld worldIn, Random rand, MutableBoundingBox sbb, ChunkPos chunkPosIn) {
		World world = worldIn.getWorld();
//		int area = (int)(Math.PI * radius * radius);
//		int sn = area / 16; // number of stalactites (there will actually be around twice this number)
		int[] sna = {0, 128, 256, 512};
		int sn = sna[hillSize]; // number of stalactites mga = {0, 3, 9, 18}
		int[] mga = {0, 1, 4, 9};
		int mg = mga[hillSize]; // number of monster generators mga = {0, 3, 9, 18} (reduced due to "natural" spawning)
		int[] tca = {0, 2, 6, 12};
		int tc = tca[hillSize];  // number of treasure chests tca = {0, 2, 6, 12};

		// fill in features

		// monster generators!
		for (int i = 0; i < mg; i++) {
			int[] dest = getCoordsInHill2D(rand);
			ResourceLocation mobID = getMobID(rand);

			setSpawner(world, dest[0], rand.nextInt(4), dest[1], sbb, mobID);
//			placeMobSpawner(dest[0], hy + rand.nextInt(4), dest[1]);
		}
		// treasure chests!!
		for (int i = 0; i < tc; i++) {
			int[] dest = getCoordsInHill2D(rand);
			generateTreasureChest(world, dest[0], 0, dest[1], sbb);
		}

		// ore or glowing stalactites! (smaller, less plentiful)
		for (int i = 0; i < sn; i++) {
			int[] dest = getCoordsInHill2D(rand);
			generateOreStalactite(world, dest[0], 1, dest[1], sbb);
		}
		// stone stalactites!
		for (int i = 0; i < sn; i++) {
			int[] dest = getCoordsInHill2D(rand);
			generateBlockStalactite(world, Blocks.STONE, 1.0F, true, dest[0], 1, dest[1], sbb);
		}
		// stone stalagmites!
		for (int i = 0; i < sn; i++) {
			int[] dest = getCoordsInHill2D(rand);
			generateBlockStalactite(world, Blocks.STONE, 0.9F, false, dest[0], 1, dest[1], sbb);
		}

		// level 3 hills get 2 mid-air wraith spawners
		if (hillSize == 3) {
//			int[] dest = getEmptysInHill(hy + 10, 20);
//			placeWraithSpawner(dest[0], hy + 10, dest[1]);
//			dest = getEmptysInHill(hy + 10, 20);
//			placeWraithSpawner(dest[0], hy + 10, dest[1]);
		}

		return true;
	}

	/**
	 * Make an RNG and attempt to use it to place a treasure chest
	 */
	protected void generateTreasureChest(World world, int x, int y, int z, MutableBoundingBox sbb) {
		// generate an RNG for this chest
		//TODO: MOAR RANDOM!
		Random chestRNG = new Random(world.getSeed() + x * z);

		// try placing it
		placeTreasureAtCurrentPosition(world, chestRNG, x, y, z, this.hillSize == 3 ? TFTreasure.hill3 : (this.hillSize == 2 ? TFTreasure.hill2 : TFTreasure.hill1), sbb);

		// make something for it to stand on, if necessary
		setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), x, y - 1, z, sbb);
	}

	/**
	 * Generate a random ore stalactite
	 */
	protected void generateOreStalactite(World world, int x, int y, int z, MutableBoundingBox sbb) {
		// are the coordinates in our bounding box?
		int dx = getXWithOffset(x, z);
		int dy = getYWithOffset(y);
		int dz = getZWithOffset(x, z);
		BlockPos pos = new BlockPos(dx, dy, dz);
		if (sbb.isVecInside(pos) && world.getBlockState(pos).getBlock() != Blocks.SPAWNER) {
			// generate an RNG for this stalactite
			//TODO: MOAR RANDOM!
			Random stalRNG = new Random(world.getSeed() + dx * dz);

			// make the actual stalactite
			TFGenCaveStalactite stalag = TFGenCaveStalactite.makeRandomOreStalactite(stalRNG, hillSize);
			stalag.generate(world, stalRNG, pos);
		}
	}

	/**
	 * Make a random stone stalactite
	 */
	protected void generateBlockStalactite(World world, Block blockToGenerate, float length, boolean up, int x, int y, int z, MutableBoundingBox sbb) {
		// are the coordinates in our bounding box?
		int dx = getXWithOffset(x, z);
		int dy = getYWithOffset(y);
		int dz = getZWithOffset(x, z);
		BlockPos pos = new BlockPos(dx, dy, dz);
		if (sbb.isVecInside(pos) && world.getBlockState(pos).getBlock() != Blocks.SPAWNER) {
			// generate an RNG for this stalactite
			//TODO: MOAR RANDOM!
			Random stalRNG = new Random(world.getSeed() + dx * dz);

			if (hillSize == 1) {
				length *= 1.9F;
			}

			// make the actual stalactite
			(new TFGenCaveStalactite(blockToGenerate, length, up)).generate(world, stalRNG, pos);
		}
	}

	/**
	 * @param cx
	 * @param cz
	 * @return true if the coordinates would be inside the hill on the "floor" of the hill
	 */
	boolean isInHill(int cx, int cz) {
		int dx = radius - cx;
		int dz = radius - cz;
		int dist = (int) Math.sqrt(dx * dx + dz * dz);

		return dist < radius;
	}

	/**
	 * @return true if the coordinates are inside the hill in 3D
	 */
	boolean isInHill(int mapX, int mapY, int mapZ) {
		int dx = boundingBox.minX + radius - mapX;
		int dy = (boundingBox.minY - mapY) * 2; // hill is half as high as it is wide, thus we just double y distance from center.  I *think* that math works!
		int dz = boundingBox.minZ + radius - mapZ;
		int dist = dx * dx + dy * dy + dz * dz;
		return dist < radius * radius;
	}

	int[] getCoordsInHill2D(Random rand) {
		return getCoordsInHill2D(rand, radius);
	}

	/**
	 * @return a two element array containing some coordinates in the hill
	 */
	int[] getCoordsInHill2D(Random rand, int rad) {
		int rx, rz;
		do {
			rx = rand.nextInt(2 * rad);
			rz = rand.nextInt(2 * rad);
		} while (!isInHill(rx, rz));

		int[] coords = {rx, rz};
		return coords;
	}

	/**
	 * Gets the id of a mob appropriate to the current hill size.
	 */
	protected ResourceLocation getMobID(Random rand) {
		return getMobID(rand, this.hillSize);
	}

	/**
	 * Gets the id of a mob appropriate to the specified hill size.
	 *
	 * @param level
	 * @return
	 */
	protected ResourceLocation getMobID(Random rand, int level) {
		if (level == 1) {
			return getLevel1Mob(rand);
		}
		if (level == 2) {
			return getLevel2Mob(rand);
		}
		if (level == 3) {
			return getLevel3Mob(rand);
		}

		return EntityType.getKey(EntityType.SPIDER);
	}

	/**
	 * Returns a mob string appropriate for a level 1 hill
	 */
	public ResourceLocation getLevel1Mob(Random rand) {
		switch (rand.nextInt(10)) {
			case 0:
			case 1:
			case 2:
				return EntityType.getKey(TFEntities.swarm_spider.get());
			case 3:
			case 4:
			case 5:
				return EntityType.getKey(EntityType.SPIDER);
			case 6:
			case 7:
				return EntityType.getKey(EntityType.ZOMBIE);
			case 8:
				return EntityType.getKey(EntityType.SILVERFISH);
			case 9:
				return EntityType.getKey(TFEntities.redcap.get());
			default:
				return EntityType.getKey(TFEntities.swarm_spider.get());
		}
	}

	/**
	 * Returns a mob string appropriate for a level 2 hill
	 */
	public ResourceLocation getLevel2Mob(Random rand) {
		switch (rand.nextInt(10)) {
			case 0:
			case 1:
			case 2:
				return EntityType.getKey(TFEntities.redcap.get());
			case 3:
			case 4:
			case 5:
				return EntityType.getKey(EntityType.ZOMBIE);
			case 6:
			case 7:
				return EntityType.getKey(EntityType.SKELETON);
			case 8:
				return EntityType.getKey(TFEntities.swarm_spider.get());
			case 9:
				return EntityType.getKey(EntityType.CAVE_SPIDER);
			default:
				return EntityType.getKey(TFEntities.redcap.get());
		}
	}

	/**
	 * Returns a mob string appropriate for a level 3 hill.  The level 3 also has 2 mid-air wraith spawners.
	 */
	public ResourceLocation getLevel3Mob(Random rand) {
		switch (rand.nextInt(11)) {
			case 0:
				return EntityType.getKey(TFEntities.slime_beetle.get());
			case 1:
				return EntityType.getKey(TFEntities.fire_beetle.get());
			case 2:
				return EntityType.getKey(TFEntities.pinch_beetle.get());
			case 3:
			case 4:
			case 5:
				return EntityType.getKey(EntityType.SKELETON);
			case 6:
			case 7:
			case 8:
				return EntityType.getKey(EntityType.CAVE_SPIDER);
			case 9:
				return EntityType.getKey(EntityType.CREEPER);
			case 10:
			default:
				return EntityType.getKey(TFEntities.wraith.get());
		}
	}

}
