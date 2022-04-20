package virtuoel.pehkui.util;

import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class I18nUtils
{
	public static final Object[] EMPTY_VARARGS = new Object[0];
	
	private static final boolean RESOURCE_LOADER_LOADED = ModLoaderUtils.isModLoaded("fabric-resource-loader-v0");
	
	public static Text translate(final String unlocalized, final String defaultLocalized)
	{
		return translate(unlocalized, defaultLocalized, EMPTY_VARARGS);
	}
	
	private static final Function<String, Object> LITERAL = LiteralText::new;
	private static final BiFunction<String, Object[], Object> TRANSLATABLE = TranslatableText::new;
	
	public static Text translate(final String unlocalized, final String defaultLocalized, final Object... args)
	{
		if (RESOURCE_LOADER_LOADED)
		{
			if (VersionUtils.MINOR < 19)
			{
				return (Text) TRANSLATABLE.apply(unlocalized, args);
			}
			
			return Text.translatable(unlocalized, args);
		}
		
		if (VersionUtils.MINOR < 19)
		{
			return (Text) LITERAL.apply(String.format(defaultLocalized, args));
		}
		
		return Text.literal(String.format(defaultLocalized, args));
	}
}
