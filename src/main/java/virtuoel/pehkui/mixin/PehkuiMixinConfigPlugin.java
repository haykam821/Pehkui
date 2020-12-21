package virtuoel.pehkui.mixin;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.fabricmc.loader.api.FabricLoader;
import virtuoel.pehkui.util.VersionUtils;

public class PehkuiMixinConfigPlugin implements IMixinConfigPlugin
{
	private static final String MIXIN_PACKAGE = "virtuoel.pehkui.mixin";
	
	@Override
	public void onLoad(String mixinPackage)
	{
		if (!mixinPackage.startsWith(MIXIN_PACKAGE))
		{
			throw new IllegalArgumentException(
				String.format("Invalid package: Expected \"%s\", but found \"%s\".", MIXIN_PACKAGE, mixinPackage)
			);
		}
	}
	
	@Override
	public String getRefMapperConfig()
	{
		return null;
	}
	
	private static final boolean REACH_ATTRIBUTES_LOADED = FabricLoader.getInstance().isModLoaded("reach-entity-attributes");
	private static final boolean STEP_HEIGHT_ATTRIBUTES_LOADED = FabricLoader.getInstance().isModLoaded("step-height-entity-attribute");
	private static final boolean IDENTITY_LOADED = FabricLoader.getInstance().isModLoaded("identity");
	private static final boolean OPTIFABRIC_LOADED = FabricLoader.getInstance().isModLoaded("optifabric");
	
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
	{
		if (!mixinClassName.startsWith(MIXIN_PACKAGE))
		{
			throw new IllegalArgumentException(
				String.format("Invalid package for class \"%s\": Expected \"%s\", but found \"%s\".", targetClassName, MIXIN_PACKAGE, mixinClassName)
			);
		}
		
		if (!VersionUtils.shouldApplyCompatibilityMixin(mixinClassName))
		{
			return false;
		}
		
		if (mixinClassName.endsWith("InGameOverlayRendererMixin"))
		{
			return OPTIFABRIC_LOADED == mixinClassName.contains(".optifine.compat.");
		}
		
		if (mixinClassName.startsWith(MIXIN_PACKAGE + ".reach"))
		{
			return REACH_ATTRIBUTES_LOADED == mixinClassName.equals(MIXIN_PACKAGE + ".reach.compat.ReachEntityAttributesMixin");
		}
		else if (mixinClassName.startsWith(MIXIN_PACKAGE + ".step_height"))
		{
			return STEP_HEIGHT_ATTRIBUTES_LOADED == mixinClassName.equals(MIXIN_PACKAGE + ".step_height.compat.StepHeightEntityAttributeMainMixin");
		}
		else if (mixinClassName.startsWith(MIXIN_PACKAGE + ".identity.compat"))
		{
			return IDENTITY_LOADED;
		}
		
		return true;
	}
	
	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
	{
		
	}
	
	@Override
	public List<String> getMixins()
	{
		return null;
	}
	
	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
	{
		
	}
	
	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
	{
		
	}
}
