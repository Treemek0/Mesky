package treemek.mesky;

import java.util.regex.Pattern;

public class Reference {
	public static final String MODID = "mesky";
	public static final String NAME = "Mesky";
	public static final String VERSION = "0.3.6";
	public static final Pattern COLOR_PATTERN = Pattern.compile("<&([0-9a-fkmnr])>");
	public static final Pattern HEX_COLOR_PATTERN = Pattern.compile("<#([0-9a-fA-F]{6})>");
}
