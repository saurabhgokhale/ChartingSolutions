/**---------------------------------------------------------------
*	Class Name: BarChartGenerator.java
*
*	Author:     Saurabh Gokhale
*	Desc:       Charting Solutions (CS) is a Charting/ Graphing library for Bar, Pie and Line Charts.
*				Its purely developed using core java 2D object without any external dependency or library.
*
*				This class is used to generate a BAR chart by accepting the values in the format given below
*
*				CS accept required values by HTTP GET method as follows:
*				
*				eg:
*				Chart?cht=bar&ct=Total+Amount+Due&ccn=Jan09+Feb09+Mar09+Apr09+May09&ccv=99.0+224.0+680.+39.0+171.4&cxt=Students&cyt=Total+Marks"
*				
*				Chart = chart application servlet listening to the incoming requests in the HTTP GET format
*				cht = Chart Type
*				ct = Chart Title
*				ccn = Chart Column Names
*				ccv = Chart Column Values
*				cxt = Chart X Axis Title
*				cyt = Chart Y Axis Title
*
*				Following are the variables used in the Graph generation
*				imageWidth - 		total image width
*				imageHeight - 		total image Height
*				graphWidth - 		width of the actual graph
*				graphHeight - 		Height of the actual graph
*				xOffset - 			Empty space between image (0,0) and X Axis
*				yOffset - 			Empty space between image (0,0) and Y Axis
*				yAxisOffset - 		Empty space between image (0,0) and Y Axis. This additional variable is
*									used for drawing yAxis top left endpoints.
*				yAxisLimit - 		Is the max point the Y Axis should ideally go. This value is 
*									raise + Y round of Value. For example:
*									bar height = 880
*									Round of Value = 900.
*									Y Axis raise = per 100
*									yAxisLimit = 100+ 900 = 1000.  
*				yAxisLeftTopPoint - This is the actual value in real number like graph column name value
*									which will give the max yValue to be plotted on yAxis.
*									This value is then multiplied by YAxisScalingFactor to calculate 
*				graphTitleYOffset - Empty space between image top and Title text
*				chart - 			Actual graphic2D object which is used to generate Graph
*				YAxisScalingFactor -factor by which the entire image is squeezed.
*									For Example: value 200 from request is multiplied by 
*									YAxisScalingFactor to fit into the image.
*				barHeight -			Array holding modified Y axis values to be plotted on screen 
*				barChartColumnNameArr - Array holding X axis values to be plotted on screen 
*				barChartColumnValueArr - Array holding Y axis values to be plotted on screen
*				barChartTitle - 	Chart Title text
*				barChartXAxisTitle- X Axis title text	
*				barChartYAxisTitle- Y Axis title text
*				yAxisValueMaxLength-Y Axis value max character length.
*				nextRoundOffNum - 	Max number to be plotted on the Y Axis
*				yAxisMaxVal - 		int max value for Y Axis.	
*				factor - 			Number of Zeros in the Y Max Value		
*				yAxisMaxValue - 	Temp variable to find Y Max value
*				yAxisMaxNegativeValue -Temp variable to find Y Max Negative value to handle Nagative plotting		
*				yAxisValueMaxPixLength - Y Axis max value length in pixel 
*				sansSarifFont12 - 	sans-serif font size 12
*				sansSarifFont16 - 	sans-serif font size 16
*				Color[] - 			Color array		
*				colMargin - 		distance between 2 bars
*		
*---------------------------------------------------------------
*/

package com.graphs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

//Following imports are required if graph image is required in the JPEG format
//import java.text.DecimalFormat;
//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGEncodeParam;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;


public class BarChartGenerator {
	
	public int imageWidth = 500;
	public int imageHeight = 400;
	public static int graphTitleYOffset = 30;
	public static Font sansSarifFont12 = new Font("SansSerif",Font.PLAIN,12);
	public static Font sansSarifFont16 = new Font("SansSerif",Font.PLAIN,16);
	
	private static Color[] colors = {
		new Color(204,204,204), new Color(153,153,102),
		new Color(255,255,204), new Color(204,153,255), 
		new Color(153,153,153), new Color(204,153,102),
		new Color(153,153,204), new Color(204,204,204),
		new Color(153,153,102), new Color(255,255,204),
		new Color(204,000,255), new Color(204,153,102),
		new Color(153,153,204)
	};
	
	public int graphWidth = 400;
	public int graphHeight = 250;
	
	public int xOffset = 80;
	public int yOffset = 70;
	public int yAxisOffset = 50;

	public  int colMargin = 20;
	public  int barWidth = 50;
	
	public float yMin = 0;
	public float yMax = 0;
	
	private Graphics2D chart;
	private double YAxisScalingFactor = 0;
	int legendXOffSet = 0;
	int legendYOffSet = 0;
	double nextRoundOffNum =  0;
	ArrayList barHeight = new ArrayList();
	ArrayList barChartColumnNameArr = null;
	ArrayList barChartColumnValueArr = null;
	String barChartTitle="";
	String barChartXAxisTitle = "";
	String barChartYAxisTitle = "";
	int xTilt = 20;
	int yTilt = 15;
	int distanceBetweenBars = 30;
	long yAxisValue = 0;
	int yAxisValueMaxLength = 0;
	double yAxisMaxValue = 0;
	double yAxisMaxNegativeValue = 0;
	int yAxisValueMaxPixLength = 0;

	int factor = 1;
	double yAxisLimit = 0;
	int yAxisLeftTopPoint=0;
	
	
	//public void createImage(OutputStream stream, HashMap barChartInputMap) throws IOException {
	public BufferedImage createImage(OutputStream stream, HashMap barChartInputMap) throws IOException {
		
		long startTime = System.currentTimeMillis();
		
		initGraphInputs(barChartInputMap);
		System.out.println("CS Bar createImage: Time taken for initGraphInputs = " + (System.currentTimeMillis() - startTime) + " msec.");
		
		BufferedImage bi =
			new BufferedImage(
				imageWidth,
				imageHeight,
				BufferedImage.TYPE_INT_RGB);

		chart = bi.createGraphics();
		chart.setColor(Color.white);
		chart.translate(0.0001, 0.0001);
		chart.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		chart.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		//Now draw our chart in the BufferedImage
		
		if(yAxisMaxNegativeValue < 0)
			drawGraph(0); //Draw the graph only for negative values by passing FLAG = 0;
		
		drawGraphFramework(); //Draw the Graph framework like X and Y Axis	
		System.out.println("CS Bar createImage: Time taken for drawGraphFramework = " + (System.currentTimeMillis() - startTime) + " msec.");
		drawGraph(1); //Draw the actual graph like bars for all positive values by passing FLAG = 1;
		System.out.println("CS Bar createImage: Time taken for drawGraph = " + (System.currentTimeMillis() - startTime) + " msec.");
		drawTitleAndLegend(); //Draw the title and the Legend information
		System.out.println("CS Bar createImage: Time taken for drawTitleAndLegend = " + (System.currentTimeMillis() - startTime) + " msec.");

		/*
		 * Following code is commented out. This code is required if the graph image is
		 * required to be produced in JPEG format. Current the graph image is generated
		 * in PNG (looseless) format.
		 */
		//Finally encode the image to a jpg
		/*JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(stream);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
		param.setQuality(1f, false);
		encoder.encode(bi, param);
		*/
		long endTime = System.currentTimeMillis();
		System.out.println("CS Bar createImage: Total Time taken to generate line graph = " + (endTime - startTime) /1000 + " sec.");
		
		return bi;

	}
	
	
	void initGraphInputs(HashMap barChartInputMap){
		
		//Setup all the input information for BarChart
		barChartColumnNameArr = (ArrayList)barChartInputMap.get("ccn");
		barChartColumnValueArr = (ArrayList)barChartInputMap.get("ccv");
		
		//Re-set graph parameters depending upon number if input values 
		initGraphParameters();
		
		//Populating barHeight arraylist
		if (barChartColumnValueArr != null){ 
			for (int i=0; i <barChartColumnValueArr.size(); i++){
				barHeight.add(Float.valueOf((String)barChartColumnValueArr.get(i)));
			}
		}
		
		//Populate Bar chart title, X Axis and Y Axis Title
		barChartTitle = (String) barChartInputMap.get("ct");
		barChartXAxisTitle = (String) barChartInputMap.get("cxt");
		barChartYAxisTitle = (String) barChartInputMap.get("cyt");
		
		float yAxisMaxValueTemp = 0;
		for (int i=0; i<barHeight.size(); i++){
			yAxisMaxValueTemp = ((Float)barHeight.get(i)).floatValue();
			if(yAxisMaxValueTemp <yAxisMaxNegativeValue){
				yAxisMaxNegativeValue =yAxisMaxValueTemp;
			}
			if((Math.abs(yAxisMaxValueTemp))>yAxisMaxValue){
				yAxisMaxValue = Math.abs(yAxisMaxValueTemp);
			}
		}
		long yAxisMaxVal = Math.round(yAxisMaxValue);

		yAxisValueMaxLength = (yAxisMaxVal+"").length();
		factor = yAxisValueMaxLength-1;
		nextRoundOffNum =  (yAxisMaxVal + ((Math.pow(10, factor)) - (yAxisMaxVal%(Math.pow(10, factor)))));
		YAxisScalingFactor = graphHeight/nextRoundOffNum;
		yAxisLimit = nextRoundOffNum + (Math.pow(10, factor));
		
		//yAxisLeftTopPoint is the topmost y 
		yAxisLeftTopPoint = yAxisOffset;
		if(graphHeight+yOffset-((int)(yAxisLimit*YAxisScalingFactor))>=yAxisOffset){
			yAxisLeftTopPoint = graphHeight+yOffset-((int)(yAxisLimit*YAxisScalingFactor));
		}
		
		/*
		 * After getting all the graph values assigned, if a graph contains any negative value then image height has to 
		 * be adjusted so that the legend information can be drawn correct below the graph which has a negative bar.
		 */
		System.out.println("imageHeight = " +imageHeight + " yAxisMaxNegativeValue " + yAxisMaxNegativeValue + " YAxisScalingFactor " + YAxisScalingFactor);
		if(yAxisMaxNegativeValue < 0){
			imageHeight= imageHeight + (int)((Math.abs(yAxisMaxNegativeValue)) * YAxisScalingFactor);
		}
		System.out.println("imageHeight = " +imageHeight + " (Math.abs(yAxisMaxNegativeValue)) * YAxisScalingFactor = " + (Math.abs(yAxisMaxNegativeValue)) * YAxisScalingFactor);

	}
	
	void initGraphParameters(){
		// depending upon the number of input values decide upon the values of the graph
		
		//Set the parameters for only 1 value in the graph 
		if(barChartColumnValueArr.size() ==1){
			imageWidth = 500;
			imageHeight = 400;
			graphWidth = 280;
			graphHeight = 250;
			
			xOffset = 80;
			yOffset = 60;
			yAxisOffset = 50;
			distanceBetweenBars = 25;
			
			xTilt = 20;
			yTilt = 15;
			
			barWidth = 40;
			colMargin = 120;
		}
		
		//Set the parameters for 2 values in the graph 
		if(barChartColumnValueArr.size() ==2){
			imageWidth = 500;
			imageHeight = 400;
			graphWidth = 280;
			graphHeight = 250;
			
			xOffset = 80;
			yOffset = 60;
			yAxisOffset = 50;
			distanceBetweenBars = 50;
			
			xTilt = 20;
			yTilt = 15;
			
			barWidth = 40;
			colMargin = 60;
		}
		
		//Set the parameters for 3 values in the graph
		if(barChartColumnValueArr.size() ==3){
			imageWidth = 530;
			imageHeight = 420;
			graphWidth = 310;
			graphHeight = 250;
			
			xOffset = 80;
			yOffset = 60;
			yAxisOffset = 50;
			distanceBetweenBars = 45;
			
			xTilt = 20;
			yTilt = 15;
			
			barWidth = 40;
			colMargin = 60;
		}
		
		if(barChartColumnValueArr.size() >=4 && barChartColumnValueArr.size() <6){
			//imageHeight = 420;
			//graphWidth = 350;
			graphHeight = 190;
			
			xOffset = 80;
			yOffset = 80;
			yAxisOffset = 50;
			distanceBetweenBars = 35;
			
			xTilt = 20;
			yTilt = 15;
			
			barWidth = 40;
			colMargin = 40;
		}
		
		if(barChartColumnValueArr.size() >5 && barChartColumnValueArr.size() <=8 ){
			
			graphWidth = 380;
			graphHeight = 180;
			
			xOffset = 80;
			yOffset = 60;
			yAxisOffset = 50;
			distanceBetweenBars = 19;
			
			xTilt = 15;
			yTilt = 10;
			
			barWidth = 28;
		}
		if(barChartColumnValueArr.size() >8){
			
			graphWidth = 400;
			graphHeight = 180;
			
			xOffset = 60;
			yOffset = 40;
			yAxisOffset = 40;
			distanceBetweenBars = 15;
			
			xTilt = 12;
			yTilt = 8;
			
			barWidth = 25;
		}
	}
	
	void drawGraphFramework(){
		
		//System.out.println("drawGraphFramework Start");
		chart.setFont(sansSarifFont12);
		chart.setColor(Color.black);
		
		//Draw X Axis
		
		if(yAxisMaxNegativeValue < 0){
			/*
			 * Now draw a white X axis box so that any negative bar graph edges are overlapped 
			 * with while color bar.
			 */
			chart.setColor(Color.white);
			chart.fillRect(xOffset+xTilt , graphHeight+yOffset-yTilt, graphWidth-xTilt+4, yTilt);
			chart.setColor(Color.black);
		}
		chart.drawLine(xOffset , graphHeight+yOffset, graphWidth+xOffset, graphHeight+yOffset);
		chart.drawLine(xOffset+xTilt , graphHeight+yOffset-yTilt, graphWidth+xOffset+xTilt , graphHeight+yOffset-yTilt);
		chart.drawLine(graphWidth+xOffset, graphHeight+yOffset, graphWidth+xOffset+xTilt , graphHeight+yOffset-yTilt);
		
		//Drow Y Axis
		chart.drawLine(xOffset , yAxisLeftTopPoint , xOffset, graphHeight+yOffset);
		//chart.drawLine(xOffset , (int)(graphHeight+yOffset - ((nextRoundOffNum+Math.pow(10, factor))*YAxisScalingFactor)) , xOffset, graphHeight+yOffset);
		
		chart.drawLine(xOffset+xTilt , yAxisLeftTopPoint-yTilt , xOffset+xTilt, graphHeight+yOffset-yTilt);
		
		chart.drawLine(xOffset , yAxisLeftTopPoint , xOffset+xTilt , yAxisLeftTopPoint-yTilt);
		chart.drawLine(xOffset, graphHeight+yOffset , xOffset+xTilt, graphHeight+yOffset-yTilt);
		
		
	}
	
	void drawGraph(int flag){

		//System.out.println("drawGraph Start");
		// Drow actual bars 
		int j = xOffset + colMargin + 1 - 10;
		ArrayList xPointsList = new ArrayList();
		ArrayList yPointsList = new ArrayList();
		
		
		for (int i=0; i<barHeight.size(); i++)
		{
			int[] xPoints = new int[4];
			int[] yPoints = new int[4];
			
			//Left Top Bar Point
			xPoints[0] = j;
			yPoints[0] = (int) (graphHeight+yOffset-((((Float)barHeight.get(i)).floatValue())* YAxisScalingFactor));

			//left bottom point 
			xPoints[1] = j;
			yPoints[1] = graphHeight + yOffset;
			
			//right bottom point
			xPoints[2] = j+ barWidth - 1;
			yPoints[2] = graphHeight+yOffset;
			
			//right top point
			xPoints[3] = j+ barWidth - 1;
			yPoints[3] = (int) (graphHeight+yOffset-((((Float)barHeight.get(i)).floatValue())* YAxisScalingFactor));
			
			xPointsList.add(xPoints);
			yPointsList.add(yPoints);
			
			/////////////////////////////////////////////////////////////////////////////////
			int[] xPoints1 = new int[4];
			int[] yPoints1 = new int[4];
			
			xPoints1[0] = j+ barWidth - 1;
			yPoints1[0] = graphHeight+yOffset;
			
			xPoints1[1] = j+ barWidth - 1;
			yPoints1[1] = (int)(graphHeight+yOffset-((((Float)barHeight.get(i)).floatValue())* YAxisScalingFactor));
			
			xPoints1[2] = j+ barWidth +xTilt;
			yPoints1[2] = (int)( graphHeight+yOffset - yTilt -((((Float)barHeight.get(i)).floatValue())* YAxisScalingFactor));
			
			xPoints1[3] = j+ barWidth +xTilt;
			yPoints1[3] = graphHeight+yOffset - yTilt;
			
			xPointsList.add(xPoints1);
			yPointsList.add(yPoints1);
			
			///////////////////////////////////////////////////////////////////////////////////
			
			int[] xPoints2 = new int[4];
			int[] yPoints2 = new int[4];
			
			xPoints2[0] = j;
			yPoints2[0] = (int)(graphHeight+yOffset-((((Float)barHeight.get(i)).floatValue())* YAxisScalingFactor));
			
			xPoints2[1] = j+ barWidth - 1;
			yPoints2[1] = (int)(graphHeight+yOffset-((((Float)barHeight.get(i)).floatValue())* YAxisScalingFactor));
			
			xPoints2[2] = j+ barWidth + xTilt;
			yPoints2[2] = (int)(graphHeight+yOffset - yTilt -((((Float)barHeight.get(i)).floatValue())* YAxisScalingFactor));
			
			xPoints2[3] = j +xTilt;
			yPoints2[3] = (int)(graphHeight+yOffset- yTilt -((((Float)barHeight.get(i)).floatValue())* YAxisScalingFactor));
			
			xPointsList.add(xPoints2);
			yPointsList.add(yPoints2);
			
			j = j + barWidth+distanceBetweenBars;
			/////////////////////////////////////////////////////////////////////////////////
		}

		int colorIndex = 0;
		int barHeightValueIndex = 0;
		
		for(int i=0; i<xPointsList.size(); i++)
		{
			int[] xPoints = (int[])xPointsList.get(i);
			int[] yPoints = (int[])yPointsList.get(i);
			
			if(i%3==0){
				chart.setColor(colors[colorIndex]);
				colorIndex++;
			}
			
			/*
			 * Draw the bars depending upon flag value being passed as 0 or 1.
			 * 0 = Draw only negative bars
			 * 1 = Draw only positive bars and other graph details like bar values. 
			 */
			if(((((Float)barHeight.get(barHeightValueIndex)).floatValue() >= 0) && flag == 1)
			|| ((((Float)barHeight.get(barHeightValueIndex)).floatValue() < 0) && flag == 0))
					chart.fillPolygon(xPoints, yPoints, 4);
			
			/*
			 * Every 3 xPointsList and yPointsList array values corresponds to 1 value in barHeight array
			 * So every after 3 iterations of xPointsList loop, increase the barHeight counter value by 1.
			 */
			if(i%3==2){
				barHeightValueIndex++;
			}
		}
		
		chart.setColor(Color.BLACK);
		barHeightValueIndex = 0;
		for(int i=0; i<xPointsList.size(); i++)
		{
			
			int[] xPoints = (int[])xPointsList.get(i);
			int[] yPoints = (int[])yPointsList.get(i);
			
			if(((((Float)barHeight.get(barHeightValueIndex)).floatValue() >= 0) && flag == 1)
			|| ((((Float)barHeight.get(barHeightValueIndex)).floatValue() < 0) && flag == 0))
					chart.drawPolygon(xPoints, yPoints, 4);
			
			if(i%3==2){
				barHeightValueIndex++;
			}
		}
		
		
		//Draw the values on the bars
		
		if(flag ==1){		
			//Draw the Y axis labels 0, 100, 200 etc
			chart.setColor(new Color(0,0,0));
			chart.setFont(sansSarifFont12);
			System.out.println("!! YAxisScalingFactor = " + YAxisScalingFactor);
				
			yAxisValueMaxLength = (((Math.round(yAxisMaxValue)))+"").length();
			int factor = yAxisValueMaxLength-1;
			yAxisValueMaxPixLength = chart.getFontMetrics().stringWidth(((Math.round(yAxisMaxValue)))+"0");

			int raise = 0;
			int x =0;
			
			/*
			 * logic for power: when yAxisLimit length is 6 (eg: 110000) and yAxisMaxValue length is 5
			 * (eg: 57000) then set power to 5 (yAxisLimit length - 1)
			 * else if yAxisLimit and yAxisMaxValue have same length 5 (eg: yAxisLimit = 80000 and 
			 * yAxisMaxValue = 57000) then set power = 5 ((yAxisLimit length)
			 * This is done to adjust the plotting of the Y Axis values close to the bar.
			 */
			int power = 0;
			if((((int)yAxisLimit)+"").length() > ((((int)yAxisMaxValue)+"").length())){
				power = ((int)yAxisLimit+"").length()-1;
			}else{
				power = ((int)yAxisLimit+"").length();
			}
			
			for (int i = graphHeight+yOffset; i >= yAxisLeftTopPoint; ) {
				System.out.println("@@ i =" + i); 
				chart.drawLine(
						xOffset,
						i,
						xOffset-5,
						i);
				
				raise = (int) (x * Math.pow(10, factor));
				System.out.println("raise = " + raise);
	
				
				chart.drawString(
						raise+"",
						xOffset-yAxisValueMaxPixLength-5+paddedValue(raise,power), //factor),
						i+3);
				
				i-=(YAxisScalingFactor * ((double)Math.pow(10, factor)));
				x++;
				
			}
			
			//Draw the  x axis 
			chart.setFont(sansSarifFont12);
			int barHeightWidth = 0;
			int xAxisNamesWidth = 0;
			int barLeftPoint = xOffset + colMargin + 1 - 10;
			String s = "";
			String xAxisNames = "";
			for (int i=0; i<barHeight.size(); i++){
				barHeightWidth = chart.getFontMetrics().stringWidth((Float)barHeight.get(i)+"");
				xAxisNamesWidth = chart.getFontMetrics().stringWidth((String)barChartColumnNameArr.get(i));
				
				
				s = new String(String.valueOf(((Float)barHeight.get(i)).floatValue()));
				xAxisNames = (String) barChartColumnNameArr.get(i);
		
				chart.drawString(
						xAxisNames,
						barLeftPoint+(barWidth/2)-(xAxisNamesWidth/2),
						graphHeight+yOffset+15);
				
				chart.setColor(Color.WHITE);
				chart.fillRect(barLeftPoint+(barWidth/2)-(barHeightWidth/2)-4,
						graphHeight+yOffset -(int)(((((Float)barHeight.get(i)).floatValue())* YAxisScalingFactor))/2 - 7,
						barHeightWidth+6, 15);
				chart.setColor(Color.BLACK);
				chart.drawRect(barLeftPoint+(barWidth/2)-(barHeightWidth/2)-4,
						graphHeight+yOffset -(int)(((((Float)barHeight.get(i)).floatValue())* YAxisScalingFactor))/2 - 7,
						barHeightWidth+6, 15);
				
				chart.drawString(
						s,
						barLeftPoint+(barWidth/2)-(barHeightWidth/2)-1,
						graphHeight+yOffset -(int)((((Float)barHeight.get(i)).floatValue())* YAxisScalingFactor)/2 + 5);
				
				barLeftPoint += barWidth+distanceBetweenBars;
			}
		}
	}
	
	void drawTitleAndLegend(){
		boolean belowImage =false;
		//Draw Legend information
		
		
		//calculate the max name and max bar height
		int maxLegendNameLength=0;
		int pixLength = 0;
		for(int i = 0; i < barHeight.size();i++){
			pixLength = chart.getFontMetrics().stringWidth((String) barChartColumnNameArr.get(i)+" ");
			if(pixLength > maxLegendNameLength){
				maxLegendNameLength = pixLength;
			}
		}
		
		int maxBarHeightLength=0;
		pixLength = 0;
		for(int i = 0; i < barHeight.size();i++){
			pixLength = chart.getFontMetrics().stringWidth((String)((barHeight.get(i))+"")+"  ["+"]  ");;
			if(pixLength > maxBarHeightLength){
				maxBarHeightLength = pixLength;
			}
		}
		
		if (barHeight.size() > 3){
			//If there are more than 4 bars We will draw legend information below the graph
			belowImage = true;
			legendXOffSet = xOffset + graphWidth/2 - (maxLegendNameLength+maxBarHeightLength);
			
			/*
			 * Add an if condition to check if there is any negative value. If there is any -ve value in the 
			 * input, move the legend information below the max negative value graph * yAxisScalingFactor
			 * 
			 */
			if(yAxisMaxNegativeValue < 0)
			{
				legendYOffSet = yOffset+graphHeight+(int)((Math.abs(yAxisMaxNegativeValue))*YAxisScalingFactor) + 20;
			}else{
				legendYOffSet = yOffset+graphHeight+65; // This addition is to go below X Axis title
			}

		}else{
			//We will draw the legend information besides graph
			legendXOffSet = graphWidth+xOffset;
			legendYOffSet = yOffset+5;
		}
		
		int legendYOffSetStartValue = legendYOffSet;
		//Draw the legend information
		for (int i=0; i<barHeight.size(); i++)
		{
			
			chart.setColor(colors[i]);
		
			//Draw Legend information
			chart.fill3DRect(
					legendXOffSet,
					legendYOffSet,
					15,
					15, true);
			
			//Draw wordings for the Legend
			chart.setFont(sansSarifFont12);
			chart.setColor(new Color(0,0,0));
			chart.drawString(
					(String) barChartColumnNameArr.get(i)+ " [" + barHeight.get(i) + "]",
					legendXOffSet+20,
					legendYOffSet+15);
			
			if(belowImage){
				//if belowImage is true then draw the legend information below the graph
				if(i%2!=1){
					legendXOffSet += maxLegendNameLength+ maxBarHeightLength + 10;
				}else{
					legendXOffSet = xOffset + graphWidth/2 - (maxLegendNameLength+maxBarHeightLength);
					legendYOffSet += 20;
				}
			}else{
				legendYOffSet += 20;
			}
		}
		
		if(belowImage){
			//This code is to draw box besides legend information below the image
			chart.draw3DRect(
					xOffset + graphWidth/2 - (maxLegendNameLength+maxBarHeightLength)-5,
					legendYOffSetStartValue-5,
					(maxLegendNameLength+maxBarHeightLength)*2+28,
					20*(barHeight.size())/2+15, true);
			
		}else{
			//This code is to draw box besides legend information on the side of the image
			chart.draw3DRect(
					legendXOffSet -5,
					yOffset,
					maxLegendNameLength+maxBarHeightLength+20,
					20*barHeight.size()+8, true);
		}
		
		
		
		//Set Graph Title
		int titleLength =0;
		chart.setFont(sansSarifFont16);
		
		// If chart title is sent in ct parameter then display the title else do not display
		if(barChartTitle != null && ! "".equalsIgnoreCase(barChartTitle)){
			titleLength = chart.getFontMetrics().stringWidth(barChartTitle);
			chart.setColor(Color.lightGray);
			chart.fillRect(((imageWidth/2) - (titleLength /2))-7, graphTitleYOffset-17, titleLength+15, 22);
			chart.setColor(Color.BLACK);
			chart.drawRect(((imageWidth/2) - (titleLength /2))-7, graphTitleYOffset-17, titleLength+15, 22);
			
			chart.drawString(
					barChartTitle,
					(imageWidth/2)-(titleLength/2), graphTitleYOffset);
		}
		
		// Set X Axis Label
		titleLength = chart.getFontMetrics().stringWidth(barChartXAxisTitle);
		int xAxisNameStartPosition = xOffset + graphWidth - (graphWidth/2) - (titleLength/2);
		chart.drawString(
				barChartXAxisTitle,
				xAxisNameStartPosition, graphHeight+yOffset+45);
		
		// Set Y Axis Label
		titleLength = chart.getFontMetrics().stringWidth(barChartYAxisTitle);
		int yAxisNameStartPosition =graphHeight + yOffset - (graphHeight/2) + (titleLength/2);
		AffineTransform at = new AffineTransform();
		
		//following code is used to write Y Axis name in vertical direction
		at.rotate(Math.toRadians(-90),xOffset-yAxisValueMaxPixLength-10, yAxisNameStartPosition);
		chart.transform(at);
		chart.drawString(barChartYAxisTitle, xOffset-yAxisValueMaxPixLength-10, yAxisNameStartPosition);
		
		//Set Legend Label
		chart.setFont(sansSarifFont12);
		chart.drawString(
				"Legend:",
				legendXOffSet+5, yOffset);
		
	}
	
	/**
	 * This method is used to pad number of digit spaces to the value.
	 * This method is required to fix the digit positions to the correct value.
	 * 
	 * @param value
	 * @param power
	 * @return
	 */
	private int paddedValue(int value, int power){
		
		if(power ==0){
			return 0;
		}
		
		long MaxValue = (long) Math.pow(10, power);
		int MaxValueLength = (MaxValue+"").length();
		int valueLength = (value+"").length();
		int diff =0;
		int pad = chart.getFontMetrics().stringWidth("0");
		if(valueLength < MaxValueLength){
			diff = MaxValueLength - valueLength;
		}
		return 	diff*pad;
	}
}


