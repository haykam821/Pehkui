package virtuoel.pehkui.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import virtuoel.pehkui.util.ScaleUtils;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin
{
	@ModifyReturnValue(method = "getDimensions", at = @At("RETURN"))
	private EntityDimensions pehkui$getDimensions(EntityDimensions original)
	{
		final float widthScale = ScaleUtils.getBoundingBoxWidthScale((Entity) (Object) this);
		final float heightScale = ScaleUtils.getBoundingBoxHeightScale((Entity) (Object) this);
		
		if (widthScale != 1.0F || heightScale != 1.0F)
		{
			return original.scaled(widthScale, heightScale);
		}
		
		return original;
	}
	
	@ModifyArg(method = "tickMovement", index = 1, at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(FF)F"))
	private float pehkui$tickMovement$minVelocity(float value)
	{
		final float scale = ScaleUtils.getMotionScale((Entity) (Object) this);
		return scale != 1.0F ? ScaleUtils.divideClamped(value, scale) : value;
	}
	
	@Inject(at = @At("RETURN"), method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;")
	private void pehkui$dropItem(ItemStack stack, boolean spread, boolean thrown, CallbackInfoReturnable<ItemEntity> info)
	{
		final ItemEntity entity = info.getReturnValue();
		
		if (entity != null)
		{
			ScaleUtils.setScaleOfDrop(entity, (Entity) (Object) this);
			
			final float scale = ScaleUtils.getEyeHeightScale((Entity) (Object) this);
			
			if (scale != 1.0F)
			{
				final Vec3d pos = entity.getPos();
				
				entity.setPosition(pos.x, pos.y + ((1.0F - scale) * 0.3D), pos.z);
			}
		}
	}
	
	@WrapOperation(method = "tickMovement()V", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"))
	private Box pehkui$tickMovement$expand(double x, double y, double z, Operation<Box> original)
	{
		final float widthScale = ScaleUtils.getBoundingBoxWidthScale((Entity) (Object) this);
		final float heightScale = ScaleUtils.getBoundingBoxHeightScale((Entity) (Object) this);
		
		if (widthScale != 1.0F)
		{
			x *= widthScale;
			z *= widthScale;
		}
		
		if (heightScale != 1.0F)
		{
			y *= heightScale;
		}
		
		return original.call(x, y, z);
	}
	
	@ModifyExpressionValue(method = "attack(Lnet/minecraft/entity/Entity;)V", at = { @At(value = "CONSTANT", args = "floatValue=0.5F", ordinal = 1), @At(value = "CONSTANT", args = "floatValue=0.5F", ordinal = 2), @At(value = "CONSTANT", args = "floatValue=0.5F", ordinal = 3) })
	private float pehkui$attack$knockback(float value)
	{
		final float scale = ScaleUtils.getKnockbackScale((Entity) (Object) this);
		
		return scale != 1.0F ? scale * value : value;
	}
	
	@ModifyExpressionValue(method = "getAttackCooldownProgressPerTick", at = @At(value = "CONSTANT", args = "doubleValue=20.0D"))
	private double pehkui$getAttackCooldownProgressPerTick$multiplier(double value)
	{
		final float scale = ScaleUtils.getAttackSpeedScale((Entity) (Object) this);
		
		return scale != 1.0F ? value / scale : value;
	}
	
	@ModifyReturnValue(method = "getBlockBreakingSpeed", at = @At("RETURN"))
	private float pehkui$getBlockBreakingSpeed(float original)
	{
		final float scale = ScaleUtils.getMiningSpeedScale((Entity) (Object) this);
		
		return scale != 1.0F ? original * scale : original;
	}
	
	@ModifyExpressionValue(method = "updateCapeAngles", at = { @At(value = "CONSTANT", args = "doubleValue=10.0D"), @At(value = "CONSTANT", args = "doubleValue=-10.0D") })
	private double pehkui$updateCapeAngles$limits(double value)
	{
		final float scale = ScaleUtils.getMotionScale((Entity) (Object) this);
		
		return scale != 1.0F ? scale * value : value;
	}
	
	@Unique private static final ThreadLocal<Float> pehkui$WIDTH_SCALE = ThreadLocal.withInitial(() -> 1.0F);
	@Unique private static final ThreadLocal<Float> pehkui$HEIGHT_SCALE = ThreadLocal.withInitial(() -> 1.0F);
	
	@Inject(method = "attack", at = @At("HEAD"))
	private void pehkui$attack(Entity target, CallbackInfo info)
	{
		pehkui$WIDTH_SCALE.set(ScaleUtils.getBoundingBoxWidthScale(target));
		pehkui$HEIGHT_SCALE.set(ScaleUtils.getBoundingBoxHeightScale(target));
	}
	
	@ModifyArg(method = "attack(Lnet/minecraft/entity/Entity;)V", index = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"))
	private double pehkui$attack$expand$x(double value)
	{
		return value * pehkui$WIDTH_SCALE.get();
	}
	
	@ModifyArg(method = "attack(Lnet/minecraft/entity/Entity;)V", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"))
	private double pehkui$attack$expand$y(double value)
	{
		return value * pehkui$HEIGHT_SCALE.get();
	}
	
	@ModifyArg(method = "attack(Lnet/minecraft/entity/Entity;)V", index = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"))
	private double pehkui$attack$expand$z(double value)
	{
		return value * pehkui$WIDTH_SCALE.get();
	}
}
