package treemek.mesky.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ColorUtils {
	
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

    public static int[] findClosestColor(int targetColor, BufferedImage image, boolean useFullBright) {
        int color = (useFullBright)?getFullBrightnessColor(targetColor):targetColor;
        double minDifference = Double.MAX_VALUE;
        int[] closestCoords = new int[2];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int imageColor = image.getRGB(x, y) & 0x00FFFFFF; // Mask out alpha
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
			Color c = Color.decode("#" + color.replace("#", ""));
			return c.getRGB();
		} catch (Exception e) {
			return 0xFFFFFFFF;
		}
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
	        return (invertedR << 16) | (invertedG << 8) | invertedB;
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
			Color.decode("#" + color);
			return color;
		} catch (Exception e) {
			System.out.println(e);
			color = "ffffff";
			return color;
		}
    }
    
}
