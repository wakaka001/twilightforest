package twilightforest.client.model.entity;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import twilightforest.entity.boss.EntityTFHydraNeck;

public class ModelTFHydraNeck<T extends EntityTFHydraNeck> extends EntityModel<T> {

	RendererModel neck;

	public ModelTFHydraNeck() {
		textureWidth = 512;
		textureHeight = 256;

		setTextureOffset("neck.box", 128, 136);
		setTextureOffset("neck.fin", 128, 200);
		neck = new RendererModel(this, "neck");
		neck.addBox("box", -16F, -16F, -16F, 32, 32, 32);
		neck.addBox("fin", -2F, -23F, 0F, 4, 24, 24);
		neck.setRotationPoint(0F, 0F, 0F);

	}

	@Override
	public void render(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		neck.render(scale);
	}

	@Override
	public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		neck.rotateAngleY = netHeadYaw / 57.29578F;
		neck.rotateAngleX = headPitch / 57.29578F;
	}


}
