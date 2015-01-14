/**---------------------------------------------------------------
*	Class Name: LineChartGenerator.java
*
*	Author:     Saurabh Gokhale
*	Desc:       Charting Solutions (CS) is a Charting/ Graphing library for Bar, Pie and Line Charts.
*				Its purely developed using core java 2D object without any external dependency or library.
*
*				This class is used to generate a LINE chart by accepting the values in the format given below	
*			
*				CS accept required values by HTTP GET method as follows:
*				
*				eg:
*				Chart?cht=line&ct=Total+Amount+Due&ccn=Jan09+Feb09+Mar09+Apr09+May09&ccv=99.0+224.0+680.+39.0+171.4&cxt=Students&cyt=Total+Marks"
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
*				graphTitleOffset - 	Empty space between image top and Title text
*				chart - 			Actual graphic2D object which is used to generate Graph
*				YAxisScalingFactor -factor by which the entire image is squeezed.
*									For Example: value 200 from request is multiplied by 
*									YAxisScalingFactor to fit into the image.
*				barHeight -			Array holding modified Y axis values to be plotted on screen 
*				lineChartColumnNameArr - Array holding X axis values to be plotted on screen 
*				lineChartColumnValueArr - Array holding Y axis values to be plotted on screen
*				barChartTitle - 	Line Chart Title text
*				barChartXAxisTitle- X Axis title text	
*				barChartYAxisTitle- Y Axis title text
*				yAxisValueMaxLength-Y Axis value max character length.
*				nextRoundOffNum - 	Max number to be plotted on the Y Axis
*				yAxisMaxVal - 		int max value for Y Axis.	
*				factor - 			Number of Zeros in the Y Max Value		
*				pointSeperation -	Space between 2 X Axis points
*				yAxisMaxValue - 	Temp variable to find Y Max value	
*				yAxisValueMaxPixLength - Y Axis max value length in pixel 
*				sansSarifFont12 - 	sans-serif font size 12
*				sansSarifFont16 - 	sans-serif font size 16
*				Color[] - 			Color array		
*				dashStroke - 		Object for writing dashed lines in the chart
*				resetStroke - 		Object for writing continuous lines in the chart
*									
*---------------------------------------------------------------
*/


package com.graphs;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.BasicStroke;


public class LineChartGenerator {
	
	public static int imageWidth = 600;
	public static int imageHeight = 460;

	public static int graphWidth = 450;
	public static int graphHeight = 280;
	
	public static int xOffset = 110;
	public static int yOffset = 70;
	public static int yAxisOffset = 50;
	
	public static int graphTitleOffset = 30;

	private Graphics2D chart;

	private double YAxisScalingFactor = 0;
	
	int legendXOffSet = 0;
	ArrayList barHeight = new ArrayList();
	ArrayList lineChartColumnNameArr = null;
	ArrayList lineChartColumnValueArr = null;
	String barChartTitle="";
	String barChartXAxisTitle = "";
	String barChartYAxisTitle = "";
	int yAxisValueMaxLength = 0;
	double nextRoundOffNum =  0;
	long yAxisMaxVal = 0;
	int factor = 0;
	double pointSeperation = 0;
	double yAxisMaxValue = 0;
	int yAxisValueMaxPixLength = 0;
	
	public static Font sansSarifFont12 = new Font("SansSerif",Font.PLAIN,12);
	public static Font sansSarifFont16 = new Font("SansSerif",Font.PLAIN,16);
	
	/*
	 * Stroke object is use to create dash lines on the line graph.
	 */
	final static float dash[] = {3.002f};
    final static BasicStroke dashStroke = new BasicStroke(1.002f, 
            BasicStroke.CAP_BUTT, 
            BasicStroke.JOIN_MITER, 
            10.002f, dash, 0.0f);
    final static BasicStroke resetStroke = new BasicStroke(1.002f);
	
	//public void createImage(OutputStream stream, HashMap barChartInputMap) throws IOException {
	public BufferedImage createImage(OutputStream stream, HashMap barChartInputMap) throws IOException {
		
		long startTime = System.currentTimeMillis();
		
		BufferedImage bi =
			new BufferedImage(
				imageWidth,
				imageHeight,
				BufferedImage.TYPE_INT_RGB);

		chart = bi.createGraphics();
		/*
		 * translate method added for known java awt bug 6215380
		 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6215380
		 */
		chart.translate(0.0001, 0.0001);
		chart.setColor(Color.white);	
		chart.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		
		chart.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		//Now draw our chart in the BufferedImage
		initGraph(barChartInputMap); 	//Initialize graph framework
		System.out.println("CS Line createImage: Time taken for initGraph = " + (System.currentTimeMillis() - startTime) + " msec.");
		drawGraphFramework(); 			//Draw the framework
		System.out.println("CS Line createImage: Total Time taken for drawGraphFramework = " + (System.currentTimeMillis() - startTime) + " msec.");
		drawGraph(); 					//Draw the actual graph
		System.out.println("CS Line createImage: Total Time taken for drawGraph = " + (System.currentTimeMillis() - startTime)  + " msec.");
		drawTitle(); 					//Draw the title and the Legend information
		System.out.println("CS Line createImage: Total Time taken for drawTitle = " + (System.currentTimeMillis() - startTime)  + " msec.");

		/*
		 * Following JPEGImageEncoder code is only required if final image is to be created in the JPEG format. 
		 * Currently the image is generated in the PNG format. 
		 */
		
		//Finally encode the image to a jpg
		/*JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(stream);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
		param.setQuality(1f, false);
		encoder.encode(bi, param);
		*/
		long endTime = System.currentTimeMillis();
		System.out.println("CS Line createImage: Total Time taken to generate line graph = " + (endTime - startTime) /1000 + " sec.");
		return bi;

	}
	
	/**
	 * InitGrpah is the method to initialize all graph input parameters.
	 * @author sgokhale
	 * @param barChartInputMap --> HashMap containing all the chart data
	 * @return void --> as corresponding instance variables are updated with required information.  
	 */
	
	void initGraph(HashMap barChartInputMap){
		
		//Setup all the input information for BarChart
		
		lineChartColumnNameArr = (ArrayList)barChartInputMap.get("ccn");
		lineChartColumnValueArr = (ArrayList)barChartInputMap.get("ccv");
		
		//Populating barHeight arraylist
		if (lineChartColumnValueArr != null){ 
			for (int i=0; i <lineChartColumnValueArr.size(); i++){
				barHeight.add(Float.valueOf((String)lineChartColumnValueArr.get(i)));
			}
		}
		
		float yAxisMaxValueTemp = 0;
		for (int i=0; i<barHeight.size(); i++){
			yAxisMaxValueTemp = ((Float)barHeight.get(i)).floatValue();
			if(yAxisMaxValueTemp>yAxisMaxValue){
				yAxisMaxValue = yAxisMaxValueTemp;
			}
		}
		
		yAxisMaxVal = Math.round(yAxisMaxValue);
		yAxisValueMaxLength = (yAxisMaxVal+"").length();
		factor = yAxisValueMaxLength-1;
		nextRoundOffNum =  (yAxisMaxVal + ((Math.pow(10, factor)) - (yAxisMaxVal%(Math.pow(10, factor)))));
		
		YAxisScalingFactor = graphHeight/nextRoundOffNum;
		
		barChartTitle = (String) barChartInputMap.get("ct");
		barChartXAxisTitle = (String) barChartInputMap.get("cxt");
		barChartYAxisTitle = (String) barChartInputMap.get("cyt");
		
		if(barHeight.size()-1 <= 0)
			pointSeperation = 0;
		else
			pointSeperation = graphWidth /(barHeight.size()-1);
		
	}
	
	/**
	 * drawGraphFramework is the method to draw graph framework (like X and Y Axis, 
	 * plot X and Y axis values, draw dotted lines) on the bufferedImage.
	 * @author sgokhale
	 * @param none
	 * @return void --> as corresponding instance variables are updated with required information.  
	 */
	void drawGraphFramework(){
		
		chart.setFont(sansSarifFont12);
		chart.drawRect(0, 0, imageWidth - 1, imageHeight - 1);

		yAxisValueMaxPixLength = chart.getFontMetrics().stringWidth(((Math.round(nextRoundOffNum)))+"");

		int raise = 0;
		int x =0;
		int j= 0;
		if(lineChartColumnNameArr.size() > 1){
			//Draw X Axis Values
			
				j=xOffset;
			
			int xAxisMaxPixLength = 0;
			chart.setColor(Color.black);
			
			for (int i = 0; i < lineChartColumnNameArr.size(); i++) {
				xAxisMaxPixLength = chart.getFontMetrics().stringWidth((String)lineChartColumnNameArr.get(i));
				chart.drawLine(
						j,
						graphHeight+yOffset,
						j,
						graphHeight+yOffset+5);
				chart.drawString(
						(String)lineChartColumnNameArr.get(i),
						j-(xAxisMaxPixLength/2),
						graphHeight+yOffset+20);
				
				j+=pointSeperation;
			}
			
			chart.setStroke(dashStroke);
			int lastXValue = (int)(j-pointSeperation);
			
			//Draw Y Axis Values
			for (int i = 0; i <= nextRoundOffNum*YAxisScalingFactor; ) {
				chart.setColor(Color.gray);
				chart.drawLine(
						xOffset-5,
						graphHeight+yOffset-(i),
						lastXValue,
						graphHeight+yOffset-(i));
				
				raise = (int) (x * Math.pow(10, factor));
				chart.setColor(Color.black);
				chart.drawString(
							raise+"",
							xOffset-yAxisValueMaxPixLength-5+paddedValue(raise, factor),
							graphHeight+yOffset-(i)+3);
					
				i+= YAxisScalingFactor * Math.pow(10, factor);
				x++;
			}
		
			/*
			 * when totalHorizontalLines <4 that means only 3 or less than 3 horizontal line were drawn in the graph.
			 * For the graph of this height 3 lines does not look enough and good. In this case, more horizontal lines
			 * will be drawn.
			 */
			if(x <4){
				x=0;
				for (int i = 0; i <= nextRoundOffNum*YAxisScalingFactor; ) {
					chart.setColor(Color.gray);
					chart.drawLine(
							xOffset-5,
							graphHeight+yOffset-(i),
							lastXValue,
							graphHeight+yOffset-(i));
					
					raise = (int) (x * Math.pow(10, factor))/2;
					chart.setColor(Color.black);
						if(raise != 0)
							chart.drawString(
								raise+"",
								xOffset-yAxisValueMaxPixLength-5+paddedValue(raise, factor),
								graphHeight+yOffset-(i)+3);
						else
							chart.drawString(
									"",
									xOffset-yAxisValueMaxPixLength-5+paddedValue(raise, factor),
									graphHeight+yOffset-(i)+3);
						i+= (YAxisScalingFactor * Math.pow(10, factor)/2);
						x++;
				}
			}
			chart.setStroke(resetStroke);
			
			//Draw Y Axis
			chart.drawLine(xOffset , (int)(graphHeight+yOffset-(YAxisScalingFactor*nextRoundOffNum)) , xOffset, graphHeight+yOffset);
			chart.drawLine((int)(j-pointSeperation) , graphHeight+yOffset , (int)(j-pointSeperation), (int)(graphHeight+yOffset-(YAxisScalingFactor*nextRoundOffNum)));
			
			//Draw X Axis
			chart.drawLine(xOffset , graphHeight+yOffset, (int)(j-pointSeperation), graphHeight+yOffset);
			
			
		}else if(lineChartColumnNameArr.size() == 1){
			
			//Draw X Axis Values
			j=(graphWidth/2)+xOffset;
			int xAxisMaxPixLength = 0;
			chart.setColor(Color.black);
				xAxisMaxPixLength = chart.getFontMetrics().stringWidth((String)lineChartColumnNameArr.get(0));
				chart.drawLine(
						j,
						graphHeight+yOffset,
						j,
						graphHeight+yOffset+5);
				chart.drawString(
						(String)lineChartColumnNameArr.get(0),
						j -(xAxisMaxPixLength/2),
						graphHeight+yOffset+20);
				
				chart.setStroke(dashStroke);
				
				//Draw Y Axis Values
				for (int i = 0; i <= nextRoundOffNum*YAxisScalingFactor; ) {
					chart.setColor(Color.gray);
					chart.drawLine(
							xOffset-5,
							graphHeight+yOffset-(i),
							graphWidth+xOffset,
							graphHeight+yOffset-(i));
					
					raise = (int) (x * Math.pow(10, factor));
					chart.setColor(Color.black);
					chart.drawString(
							raise+"",
							xOffset-yAxisValueMaxPixLength-5+paddedValue(raise, factor),
							graphHeight+yOffset-(i)+3);
					
					i+= YAxisScalingFactor * Math.pow(10, factor);
					x++;
				}
			
		
			
			if(x <4){
				x=0;
				for (int i = 0; i <= nextRoundOffNum*YAxisScalingFactor; ) {
					chart.setColor(Color.gray);
					chart.drawLine(
							xOffset-5,
							graphHeight+yOffset-(i),
							graphWidth+xOffset,
							graphHeight+yOffset-(i));
					
					raise = (int) (x * Math.pow(10, factor))/2;
					chart.setColor(Color.black);
					if(raise != 0)
						chart.drawString(
							raise+"",
							xOffset-yAxisValueMaxPixLength-5+paddedValue(raise, factor),
							graphHeight+yOffset-(i)+3);
					else
						chart.drawString(
								"",
								xOffset-yAxisValueMaxPixLength-5+paddedValue(raise, factor),
								graphHeight+yOffset-(i)+3);
					
					i+= (YAxisScalingFactor * Math.pow(10, factor)/2);
					x++;
				}
			}
			chart.setStroke(resetStroke);
			
			//Draw Y Axis
			chart.drawLine(xOffset , (int)(graphHeight+yOffset-(YAxisScalingFactor*nextRoundOffNum)) , xOffset, graphHeight+yOffset);
			chart.drawLine(graphWidth+xOffset , graphHeight+yOffset , graphWidth+xOffset, (int)(graphHeight+yOffset-(YAxisScalingFactor*nextRoundOffNum)));
			
			//Draw X Axis
			chart.drawLine(xOffset , graphHeight+yOffset, graphWidth+xOffset, graphHeight+yOffset);
			
		}
		
		
	}
	
	
	/**
	 * drawGraphFramework is the method to draw graph framework (like X and Y Axis, 
	 * plot X and Y axis values, draw dotted lines) on the bufferedImage.
	 * @author sgokhale
	 * @param none
	 * @return void --> as corresponding instance variables are updated with required information.  
	 */
	
	void drawGraph(){

		// Draw actual bars 
		int j= 0;
		
		if(barHeight.size() >1)
			j = xOffset;
		else
			j = (graphWidth/2) + xOffset;
			
		int [] xPoints = new int[barHeight.size()];
		int [] yPoints = new int[barHeight.size()];
		
		//Calculate the X and Y values for the points
		for (int i=0; i<barHeight.size(); i++)
		{
			
			//Left Top Bar Point
			xPoints[i] = j;
			yPoints[i] = graphHeight+yOffset -(int)((((Float)barHeight.get(i)).floatValue())* YAxisScalingFactor);

			j += pointSeperation;
			
		}
		
		//Draw Rectangles on the X and Y values
		chart.setColor(Color.blue);
		for(int i=0; i<xPoints.length; i++)
		{
			chart.fillRect(xPoints[i]-3, yPoints[i]-3, 6, 6);
		}
		
		//Draw lines between two X, Y points 
		for(int i=1; i<xPoints.length; i++)
		{
			chart.drawLine(xPoints[i-1], yPoints[i-1], xPoints[i], yPoints[i]);
		}


		
		//Draw the values for the points in the box
				
		chart.setFont(sansSarifFont12);
		chart.setColor(Color.BLACK);
		int valueWidth = 0;
		
		for(int i=1; i<(xPoints.length)-1; i++)
		{
			valueWidth = chart.getFontMetrics().stringWidth((Float)barHeight.get(i)+"");
			chart.setColor(Color.WHITE);
			chart.fillRect(xPoints[i]-(valueWidth/2)-2, yPoints[i]-25, valueWidth+5, 17);
			chart.setColor(Color.BLACK);
			chart.drawRect(xPoints[i]-(valueWidth/2)-3, yPoints[i]-24, valueWidth+6, 16);
			chart.drawString(barHeight.get(i)+"", xPoints[i]-(valueWidth/2), yPoints[i]-12);
		}

		
		if(barHeight.size() >1) {
			//Draw the first value to the right of the Y Axis
			valueWidth = chart.getFontMetrics().stringWidth((Float)barHeight.get(0)+"");
			chart.setColor(Color.WHITE);
			chart.fillRect(xPoints[0]+4, yPoints[0]-25, valueWidth+5, 17);
			chart.setColor(Color.BLACK);
			chart.drawRect(xPoints[0]+3, yPoints[0]-24, valueWidth+6, 16);
			chart.drawString(barHeight.get(0)+"", xPoints[0]+6, yPoints[0]-12);
		
			//Draw the last value to the left of the right most Y Axis
			valueWidth = chart.getFontMetrics().stringWidth((Float)barHeight.get(xPoints.length-1)+"");
			chart.setColor(Color.WHITE);
			chart.fillRect(xPoints[xPoints.length-1]-(valueWidth)-7, yPoints[xPoints.length-1]-24, valueWidth+5, 17);
			chart.setColor(Color.BLACK);
			chart.drawRect(xPoints[xPoints.length-1]-(valueWidth)-8, yPoints[xPoints.length-1]-24, valueWidth+5, 16);
			chart.drawString(barHeight.get(xPoints.length-1)+"", xPoints[xPoints.length-1]-(valueWidth)-5, yPoints[xPoints.length-1]-12);
		}
		else if(barHeight.size() == 1){
			valueWidth = chart.getFontMetrics().stringWidth((Float)barHeight.get(0)+"");
			chart.setColor(Color.WHITE);
			chart.fillRect(xPoints[0]-(valueWidth/2)-2, yPoints[0]-25, valueWidth+5, 17);
			chart.setColor(Color.BLACK);
			chart.drawRect(xPoints[0]-(valueWidth/2)-3, yPoints[0]-24, valueWidth+6, 16);
			chart.drawString(barHeight.get(0)+"", xPoints[0]-(valueWidth/2), yPoints[0]-12);
		}
	}
	
	
	/**
	 * drawTitle is the method to draw graph label. This method also draws X and Y axis labels.
	 * @author sgokhale
	 * @param none
	 * @return void --> as corresponding instance variables are updated with required information.  
	 */
	void drawTitle(){
		
		//Set Graph Title
		chart.setFont(sansSarifFont16);
		
		//draw the chart title and the box around it only if the title is passed from the input.
		if(barChartTitle != null && ! "".equals(barChartTitle)){
		int valueWidth = chart.getFontMetrics().stringWidth(barChartTitle);
		chart.setColor(Color.lightGray);
		chart.fillRect((xOffset + (graphWidth/2) - (valueWidth /2))-7, graphTitleOffset-17, valueWidth+15, 22);
		chart.setColor(Color.BLACK);
		chart.drawRect((xOffset + (graphWidth/2) - (valueWidth /2))-7, graphTitleOffset-17, valueWidth+15, 22);
		
		chart.drawString(
				barChartTitle,
				xOffset + (graphWidth/2) - (valueWidth /2)
				, graphTitleOffset);
		}
		
		// Set X Axis Label
		int barChartXAxisTitleLength = chart.getFontMetrics().stringWidth(barChartXAxisTitle);
		int xAxisNameStartPosition = xOffset + graphWidth - (graphWidth/2) - (barChartXAxisTitleLength/2);
		chart.drawString(
				barChartXAxisTitle,
				xAxisNameStartPosition, graphHeight+yOffset+50);
		
		// Set Y Axis Label
		int barChartYAxisTitleLength = chart.getFontMetrics().stringWidth(barChartYAxisTitle);
		int yAxisNameStartPosition =graphHeight + yOffset - (graphHeight/2) + (barChartYAxisTitleLength/2);
		AffineTransform at = new AffineTransform();
		
		//following code is used to write Y Axis name in vertical direction
		at.rotate(Math.toRadians(-90),xOffset-yAxisValueMaxPixLength-15, yAxisNameStartPosition);
		chart.transform(at);
		chart.drawString(barChartYAxisTitle, xOffset-yAxisValueMaxPixLength-15, yAxisNameStartPosition);

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


