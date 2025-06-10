package treemek.mesky.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import treemek.mesky.Reference;

public class ColorUtils {
	
	public enum MeskyColor {
	    BLACK("<&0>"),
	    DARK_BLUE("<&1>"),
	    DARK_GREEN("<&2>"),
	    DARK_AQUA("<&3>"),
	    DARK_RED("<&4>"),
	    DARK_PURPLE("<&5>"),
	    GOLD("<&6>"),
	    GRAY("<&7>"),
	    DARK_GRAY("<&8>"),
	    BLUE("<&9>"),
	    GREEN("<&a>"),
	    AQUA("<&b>"),
	    RED("<&c>"),
	    LIGHT_PURPLE("<&d>"),
	    YELLOW("<&e>"),
	    WHITE("<&f>");
		
		
	    private final String code;
		MeskyColor(String code) {
	        this.code = code;
	    }
	}
	
	public static int getFullBrightnessColor(int color) {
        // Extract RGB components
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        
        // Create Color object
        Color c = new Color(r, g, b);
        
        // Convert to HSB (Hue, Saturation, Brightness)
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        
        // Set brightness to full (1.0)
        hsb[2] = 1.0f;
        
        // Convert back to RGB
        int fullBrightnessRgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
        
        // Return the RGB value without alpha
        return fullBrightnessRgb & 0x00FFFFFF;
    }

    public static double colorDifference(int color1, int color2) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        // Calculate Euclidean distance in RGB color space
        return Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));
    }

    public static double[] findClosestColor(int targetColor, BufferedImage image, boolean useFullBright) {
        int color = (useFullBright) ? getFullBrightnessColor(targetColor) : targetColor;
        double minDifference = Double.MAX_VALUE;
        double[] closestCoords = new double[2];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int pixel = image.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xFF;

                if (alpha != 255) {
                    continue;
                }

                int imageColor = pixel & 0x00FFFFFF; // Mask out the alpha component
                double difference = colorDifference(color, imageColor);
                if (difference < minDifference) {
                    minDifference = difference;
                    closestCoords[0] = x;
                    closestCoords[1] = y;
                }
            }
        }

        return closestCoords;
    }
    
    public static int adjustBrightness(int color, float brightnessFactor) {
	    int r = (color >> 16) & 0xFF;
	    int g = (color >> 8) & 0xFF;
	    int b = color & 0xFF;

	    // Apply brightness factor
	    r = Math.min((int) (r * brightnessFactor), 255);
	    g = Math.min((int) (g * brightnessFactor), 255);
	    b = Math.min((int) (b * brightnessFactor), 255);

	    // Combine back into an integer
	    return (r << 16) | (g << 8) | b;
	}
    
    private static float getBrightnessAdjustmentFactor(int originalColor, int adjustedColor) {
        // Extract RGB components
        int rOrig = (originalColor >> 16) & 0xFF;
        int gOrig = (originalColor >> 8) & 0xFF;
        int bOrig = originalColor & 0xFF;

        int rAdj = (adjustedColor >> 16) & 0xFF;
        int gAdj = (adjustedColor >> 8) & 0xFF;
        int bAdj = adjustedColor & 0xFF;

        // Calculate the brightness factor for each component
        float brightnessR = (rOrig != 0)?(float) rAdj / rOrig:rAdj;
        float brightnessG = (gOrig != 0)?(float) gAdj / gOrig:gAdj;
        float brightnessB = (bOrig != 0)?(float) bAdj / bOrig:bAdj;
        
        // Return the average brightness factor
        return (brightnessR + brightnessG + brightnessB) / 3.0f;
    }

    public static float findBrightnessAdjustmentFactor(int targetColor, int mouseX, int mouseY, BufferedImage image) {
        // Get the original color from the image at the specified coordinates
        int imageColor = image.getRGB(mouseX, mouseY) & 0x00FFFFFF; // Mask out alpha
        
        // Get the full brightness version of the image color
        int fullBrightnessImageColor = getFullBrightnessColor(imageColor);
        
        // Calculate the brightness adjustment factor
        return getBrightnessAdjustmentFactor(fullBrightnessImageColor, targetColor);
    }
    
    public static int getColorInt(String color) {
		 color = color.replace("#", "");
		 
		 if(color.length() > 6) {
   		color = color.substring(0, 6);
		 }
		 
		 try {
			 int c = Integer.parseInt(color.replace("#", ""), 16);
			return c;
		} catch (Exception e) {
			return 0xFFFFFF;
		}
	 }
    
    public static int rgbToArgb(int rgb, int alpha) {
        // Ensure the alpha value is in the range [0, 255]
        alpha = Math.max(0, Math.min(255, alpha));
        
        // Extract the RGB components
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        
        // Combine alpha, red, green, and blue into an ARGB value
        int argb = (alpha << 24) | (red << 16) | (green << 8) | blue;
        
        return argb;
    }
    
    public static int InvertColor(int color) {
		 int r = (color >> 16) & 0xFF;
	        int g = (color >> 8) & 0xFF;
	        int b = color & 0xFF;
	        
	        // Invert each component
	        int invertedR = 255 - r;
	        int invertedG = 255 - g;
	        int invertedB = 255 - b;
	        
	        // Reassemble the color
	        return (255 << 24) | (invertedR << 16) | (invertedG << 8) | invertedB;
	 }
    
    public static String fixColor(String color) {
    	if(color == null || color.length() == 0) {
    		return "ffffff";
    	}
    	
    	color = color.replace("#", "");
    	
    	if(color.length() > 8) {
    		color = color.substring(0, 8);
    	}
    	
    	try {
    		int c = Integer.parseInt(color, 16);
			color = getColorString(c); // its because colors are not always in the same format as when theyre saved
			return color;
		} catch (Exception e) {
			System.out.println(e);
			color = "FFFFFF";
			return color;
		}
    }
    
    public static String getColorString(int color) {
		return String.format("%06X", color);
	}
    
    public static int ensureAlpha(int color) {
        if ((color >> 24 & 0xFF) == 0) {
            // Add the alpha component (255) by shifting 24 bits to the left and OR'ing with the original color.
            color |= 0xFF000000;
        }
        return color;
    }
    
    public static int averageColor(List<Integer> colors, boolean fullAlpha) {
        int totalAlpha = 0, totalRed = 0, totalGreen = 0, totalBlue = 0;
        int count = colors.size();

        for (int color : colors) {
            totalAlpha += (color >> 24) & 0xFF; // Extract alpha
            totalRed += (color >> 16) & 0xFF;   // Extract red
            totalGreen += (color >> 8) & 0xFF;  // Extract green
            totalBlue += color & 0xFF;          // Extract blue
        }

        int avgAlpha = (fullAlpha)? 255 : Math.min(totalAlpha / count, 255);
        int avgRed = Math.min(totalRed / count, 255);
        int avgGreen = Math.min(totalGreen / count, 255);
        int avgBlue = Math.min(totalBlue / count, 255);

        return (avgAlpha << 24) | (avgRed << 16) | (avgGreen << 8) | avgBlue;
    }
    
    public static EnumChatFormatting getColorAtIndex(String text, int index) {
        String sub = text.substring(0, index);
        
        // Define a pattern to match valid color codes
        Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-F]");
        Matcher matcher = patternControlCode.matcher(sub);
        
        EnumChatFormatting lastColor = EnumChatFormatting.WHITE; 

        while (matcher.find()) {
            String code = matcher.group().substring(1); // Extract the color code (strip '§')
            lastColor = getColorFromCode(code);
        }

        return lastColor;
    }
    
    public static EnumChatFormatting getColorFromCode(String code) {
        switch (code.charAt(0)) {
            case '<': return getColorFromCode(code.substring(2, 3)); // Handle custom color codes
            
            case '0': return EnumChatFormatting.BLACK; // Black
            case '1': return EnumChatFormatting.DARK_BLUE; // Dark Blue
            case '2': return EnumChatFormatting.DARK_GREEN; // Dark Green
            case '3': return EnumChatFormatting.DARK_AQUA; // Dark Aqua
            case '4': return EnumChatFormatting.DARK_RED; // Dark Red
            case '5': return EnumChatFormatting.DARK_PURPLE; // Dark Purple
            case '6': return EnumChatFormatting.GOLD; // Gold
            case '7': return EnumChatFormatting.GRAY; // Gray
            case '8': return EnumChatFormatting.DARK_GRAY; // Dark Gray
            case '9': return EnumChatFormatting.BLUE; // Blue
            case 'a': return EnumChatFormatting.GREEN; // Green
            case 'b': return EnumChatFormatting.AQUA; // Aqua
            case 'c': return EnumChatFormatting.RED; // Red
            case 'd': return EnumChatFormatting.LIGHT_PURPLE; // Light Purple
            case 'e': return EnumChatFormatting.YELLOW; // Yellow
            case 'f': return EnumChatFormatting.WHITE; // White
            
            // Additional formatting options
            case 'k': return EnumChatFormatting.OBFUSCATED; // Obfuscated
            case 'm': return EnumChatFormatting.STRIKETHROUGH; // Strikethrough
            case 'n': return EnumChatFormatting.UNDERLINE; // Underline
            
            case 'r': return EnumChatFormatting.RESET; // Reset to default
            default: return EnumChatFormatting.WHITE; // Default color
        }
    }
    
    public static boolean isEnumColor(EnumChatFormatting format) {
        // Check if the format is one of the color codes
        return format == EnumChatFormatting.BLACK ||
               format == EnumChatFormatting.DARK_BLUE ||
               format == EnumChatFormatting.DARK_GREEN ||
               format == EnumChatFormatting.DARK_AQUA ||
               format == EnumChatFormatting.DARK_RED ||
               format == EnumChatFormatting.DARK_PURPLE ||
               format == EnumChatFormatting.GOLD ||
               format == EnumChatFormatting.GRAY ||
               format == EnumChatFormatting.DARK_GRAY ||
               format == EnumChatFormatting.BLUE ||
               format == EnumChatFormatting.GREEN ||
               format == EnumChatFormatting.AQUA ||
               format == EnumChatFormatting.RED ||
               format == EnumChatFormatting.LIGHT_PURPLE ||
               format == EnumChatFormatting.YELLOW ||
               format == EnumChatFormatting.WHITE;
    }
    
    public static int getColorIntFromEnumChatFormatting(EnumChatFormatting color) {
        switch (color) {
            case BLACK: return 0x000000;
            case DARK_BLUE: return 0x0000AA;
            case DARK_GREEN: return 0x00AA00;
            case DARK_AQUA: return 0x00AAAA;
            case DARK_RED: return 0xAA0000;
            case DARK_PURPLE: return 0xAA00AA;
            case GOLD: return 0xFFAA00;
            case GRAY: return 0xAAAAAA;
            case DARK_GRAY: return 0x555555;
            case BLUE: return 0x5555FF;
            case GREEN: return 0x55FF55;
            case AQUA: return 0x55FFFF;
            case RED: return 0xFF5555;
            case LIGHT_PURPLE: return 0xFF55FF;
            case YELLOW: return 0xFFFF55;
            case WHITE: return 0xFFFFFF;
            default: return 0xFFFFFF;  // Default to white
        }
    }
    
    public static Color getColorFromEnumChatFormatting(EnumChatFormatting color) {
        switch (color) {
            case BLACK: return new Color(0, 0, 0);
            case DARK_BLUE: return new Color(0, 0, 170);
            case DARK_GREEN: return new Color(0, 170, 0);
            case DARK_AQUA: return new Color(0, 170, 170);
            case DARK_RED: return new Color(170, 0, 0);
            case DARK_PURPLE: return new Color(170, 0, 170);
            case GOLD: return new Color(255, 170, 0);
            case GRAY: return new Color(170, 170, 170);
            case DARK_GRAY: return new Color(85, 85, 85);
            case BLUE: return new Color(85, 85, 255);
            case GREEN: return new Color(85, 255, 85);
            case AQUA: return new Color(85, 255, 255);
            case RED: return new Color(255, 85, 85);
            case LIGHT_PURPLE: return new Color(255, 85, 255);
            case YELLOW: return new Color(255, 255, 85);
            case WHITE: return new Color(255, 255, 255);
            default: return null;
        }
    }
    
    public static String getColoredText(String text) {
        Matcher matcher = Reference.COLOR_PATTERN.matcher(text);
        String t = "";
        int lastEnd = 0;
        while(matcher.find()) {
        	EnumChatFormatting color = ColorUtils.getColorFromCode(matcher.group());
			t += text.substring(lastEnd, matcher.start()) + color ;
        	lastEnd = matcher.start() + 4;
        }
        
        t += text.substring(lastEnd);
        
        return t;
    }
    
    public static String removeTextFormatting(String text) {
        // Remove basic color codes (e.g., &a, &b)
        String basicColorPattern = "&[0-9a-fk-or]";
        text = text.replaceAll(basicColorPattern, "");

        // Remove hex color codes (e.g., #FF0000)
        String hexColorPattern = "#[0-9a-fA-F]{6}";
        text = text.replaceAll(hexColorPattern, "");

        text = StringUtils.stripControlCodes(text);
        
        return text;
    }
    
    public static String removeMinecraftTextColor(String text) {
    	Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-F]");
    	
    	 return patternControlCode.matcher(text).replaceAll("");
    }
    
    public static String replaceMinecraftTextColorWith(String text, EnumChatFormatting replacer) {
    	Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-F]");
    	
    	 return patternControlCode.matcher(text).replaceAll(replacer.toString());
    }
    
    public static String replaceEnum(String text, EnumChatFormatting e, EnumChatFormatting to) {
    	return text.replaceAll(e.toString(), to.toString());
    }
    
    public static EnumChatFormatting getRandomMinecraftColor() {
        EnumChatFormatting[] colors = {
            EnumChatFormatting.BLACK, // Black
            EnumChatFormatting.DARK_BLUE, // Dark Blue
            EnumChatFormatting.DARK_GREEN, // Dark Green
            EnumChatFormatting.DARK_AQUA, // Dark Aqua
            EnumChatFormatting.DARK_RED, // Dark Red
            EnumChatFormatting.DARK_PURPLE, // Dark Purple
            EnumChatFormatting.GOLD, // Gold
            EnumChatFormatting.GRAY, // Gray
            EnumChatFormatting.DARK_GRAY, // Dark Gray
            EnumChatFormatting.BLUE, // Blue
            EnumChatFormatting.GREEN, // Green
            EnumChatFormatting.AQUA, // Aqua
            EnumChatFormatting.RED, // Red
            EnumChatFormatting.LIGHT_PURPLE, // Light Purple
            EnumChatFormatting.YELLOW, // Yellow
            EnumChatFormatting.WHITE  // White
        };

        Random random = new Random();
        return colors[random.nextInt(colors.length)];
    }
    
    public static String getRandomColor() {
        String[] colors = {
            "<&0>", // Black
            "<&1>", // Dark Blue
            "<&2>", // Dark Green
            "<&3>", // Dark Aqua
            "<&4>", // Dark Red
            "<&5>", // Dark Purple
            "<&6>", // Gold
            "<&7>", // Gray
            "<&8>", // Dark Gray
            "<&9>", // Blue
            "<&a>", // Green
            "<&b>", // Aqua
            "<&c>", // Red
            "<&d>", // Light Purple
            "<&e>", // Yellow
            "<&f>"  // White
        };
        return colors[new java.util.Random().nextInt(colors.length)];
    }

    public static String convertMinecraftColorsToMeskyCodes(String input) {
        // Replace '§' with '&' if needed
        input = input.replace("§", "&");

        StringBuilder output = new StringBuilder();
        boolean inColor = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (inColor) {
                output.append("<&").append(c).append(">");
                inColor = false;
            } else if (c == '&') {
                inColor = true;
            } else {
                output.append(c);
            }
        }

        return output.toString();
    }

    
    public static EnumChatFormatting getClosestMinecraftColor(String hex) {
    	return getClosestMinecraftColor(getColorInt(hex));
    }
    
    public static EnumChatFormatting getClosestMinecraftColor(int hex) {
        Color target = new Color(hex);
        EnumChatFormatting closestColor = EnumChatFormatting.WHITE;
        double minDistance = Double.MAX_VALUE;

        for (EnumChatFormatting color : EnumChatFormatting.values()) {
            Color mcColor = getColorFromEnumChatFormatting(color);
            if (mcColor == null) continue;

            double distance = getColorDistance(target, mcColor);
            if (distance < minDistance) {
                minDistance = distance;
                closestColor = color;
            }
        }

        return closestColor;
    }
    
    private static double getColorDistance(Color c1, Color c2) {
        int rDiff = c1.getRed() - c2.getRed();
        int gDiff = c1.getGreen() - c2.getGreen();
        int bDiff = c1.getBlue() - c2.getBlue();
        return Math.sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff);
    }

    public static EnumChatFormatting getMostUsedColor(String message) {
        Map<EnumChatFormatting, Integer> colorCount = new HashMap<>();
        char colorChar = '\u00A7'; // Minecraft color code character
        EnumChatFormatting currentColor = EnumChatFormatting.WHITE; // Default color

        int i = 0;
        while (i < message.length()) {
            if (message.charAt(i) == colorChar && i + 1 < message.length()) {
                char code = message.charAt(i + 1);
                EnumChatFormatting color = getColorFromCode("" + code);

                if (color != null && color.isColor()) {
                    currentColor = color; // Set the new active color
                }
                i += 2; // Skip color code
            } else {
                // Count the character under the current color
                colorCount.put(currentColor, colorCount.getOrDefault(currentColor, 0) + 1);
                i++;
            }
        }

        // Find the most used color (most characters written in that color)
        EnumChatFormatting mostUsedColor = EnumChatFormatting.WHITE;
        int maxCount = 0;

        for (Map.Entry<EnumChatFormatting, Integer> entry : colorCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostUsedColor = entry.getKey();
            }
        }

        return mostUsedColor;
    }
    
}
