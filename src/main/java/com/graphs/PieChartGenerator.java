
/**---------------------------------------------------------------
*	Class Name: PieChartGenerator.java
*
*	Author:     Saurabh Gokhale
*	Desc:       Charting Solutions (CS) is a Charting/ Graphing library for Bar, Pie and Line Charts.
*				Its purely developed using core java 2D object without any external dependency or library.
*
*				This class is used to generate a PIE chart by accepting the values in the format given below
*
*				CS accept required values by HTTP GET method as follows:
*				
*				eg:
*				Chart?cht=pie&ct=Total+Amount+Due&ccn=Jan09+Feb09+Mar09+Apr09+May09&ccv=99.0+224.0+680.+39.0+171.4&cxt=Students&cyt=Total+Marks"
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
*				pieImageWidth - 	total image width
*				pieImageHeight -	total image Height
*				graphWidth - 		width of the actual graph
*				graphHeight - 		Height of the actual graph
*				xOffset - 			Empty space between image (0,0) and X Axis
*				yOffset - 			Empty space between image (0,0) and Y Axis
*				pieSliceValues - 	values sent by HTTP GET/POST request to draw 
*				chart - 			Actual graphic2D object which is used to generate Graph
*				pieChartSliceValuesArr - Array holding pie chart values to be plotted on screen 
*				pieChartsliceNamesArr - Array holding pie chart value names to be plotted on screen
*				pieChartTitle - 	Chart Title text
*				sansSarifFont12 - 	sans-serif font size 12
*				sansSarifFont16 - 	sans-serif font size 16
*				Color[] - 			Color array
*				numOfSlices - 		Total Number of Slices / values sent to the generator.
*				divisionFactor -    divisionFactor is the divisor by which I divide radius of the
*									pie chart to generate height. Therefore 
*									width = radius * 2 
*									Height = radius / divisionFactor;
*				legendYOffSet - 	Y Axis space between image (0,0) and legend.
*				startAngle - 		Starting angle to draw the first slice. Always -45.
* 				sweepAngle - 		Sweep angle is calculated as follows 
* 									360*(pieSliceValues[i]/totalPieSliceValue).
* 									Its the actual angle by which the curve will be drawn. 
*				totalPieSliceValue- Total of all the values sent by the client.
*				radius - 			Radius of the Pie chart
*
*---------------------------------------------------------------
*/

package com.graphs;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

//Following imports are required if graph image is required in the JPEG format
//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGEncodeParam;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;


public class PieChartGenerator {
	
	public int graphWidth = 420;
	public int graphHeight = 300;
	
	public int imageWidth = 600;
	public int imageHeight = 350;
	
	public static int xOffset = 10;
	public static int yOffset = 70;
	public static int graphTitleOffset = 50;
	public static int xAxisTitleOffset = 30;
	
	public boolean drawPercentageOnChart= true;
	private Graphics2D chart;
	int legendYOffSet = yOffset+40;
	
	ArrayList pieChartSliceValuesArr = new ArrayList();
	ArrayList pieChartsliceNamesArr = new ArrayList();
	double[] pieSliceValues = null;
	
	String pieChartTitle="";
	int numOfSlices = 0;
	
	double pieImageWidth = 400;
	double pieImageHeight = 250;
	double divisionFactor = 1.5;
	double depth=0;
	double pieXCord =0, pieYCord=0;
	int chartTitleYOffset=110;
	int startAngle = -45;
	int sweepAngle = 0;

	double totalPieSliceValue=0;
	double radius;
	int maxLegendLength=0;
	
	public static Font sansSarifFont12 = new Font("SansSerif",Font.PLAIN,12);
	public static Font sansSarifFont16 = new Font("SansSerif",Font.PLAIN,16);
	
	private static Color[] colors = {

		new Color(103, 105, 168),
	  	new Color(99, 156, 255),
	  	new Color(140, 140, 140),
	  	new Color(255, 202, 122),
	  	new Color(255, 153, 000),
	  	new Color(153, 153, 000),
	  	new Color(129, 135, 181),
	  	new Color(52, 184, 222),
	  	new Color(79, 237, 224),
	  	new Color(202, 134, 177),
	  	new Color(198, 99, 165),
	  	new Color(99, 90, 255),
	  	new Color(199, 174, 145),
	  	new Color(219, 208, 165),
	  	new Color(173, 198, 148),
	  	new Color(137, 238, 151),
	  	new Color(247, 189, 132),
	  	new Color(206, 173, 156),
	  	new Color(219, 208, 165),
	  	new Color(99, 165, 156),
	  	new Color(99, 204, 213),
	  	new Color(244, 129, 114)
	};
	
	//public void createImage(OutputStream stream, HashMap pieChartInputMap) throws IOException {
	public BufferedImage createImage(OutputStream stream, HashMap pieChartInputMap) throws IOException {	
	
		long startTime = System.currentTimeMillis();
		float transparency=0.8f;
		//if imageWidth and imageHeight is specified in from the calling application, use 
		// that else use the one specified at the time of variable declaration.
		initializeGraphHeightAndWidth(pieChartInputMap);
		
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
		chart.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,transparency));
		
		//Call All graph generation methods
		initGraph(pieChartInputMap);
		System.out.println("CS Pie createImage: Time taken for initGraph = " + (System.currentTimeMillis() - startTime) + " msec.");
		drawPieChart(); //Draw the actual pie chart
		System.out.println("CS Pie createImage: Time taken for drawPieChart = " + (System.currentTimeMillis() - startTime) + " msec.");
		DrawValuesOnSlices(); // Draw the values on pie slices
		System.out.println("CS Pie createImage: Time taken for DrawValuesOnSlices = " + (System.currentTimeMillis() - startTime) + " msec.");
		if(drawPercentageOnChart)
			DrawPercentsBesideSlices(); //Draw percentages besides slices
		System.out.println("CS Pie createImage: Time taken for DrawPercentsBesideSlices = " + (System.currentTimeMillis() - startTime) + " msec.");
		drawTitleAndLegend(); //Draw the legend information on the right of the chart
		System.out.println("CS Pie createImage: Time taken for drawTitleAndLegend = " + (System.currentTimeMillis() - startTime) + " msec.");
		
		//DrawPercentsOnSlices(); // this method is optional method to draw percent info on pie slice.
		
		//Finally encode the image to a jpg
		/*
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(stream);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
		param.setQuality(1f, false);
 		encoder.encode(bi, param);
 		*/
		long endTime = System.currentTimeMillis();
		System.out.println("CS Pie createImage: Total Time taken to generate line graph = " + (endTime - startTime) /1000 + " sec.");
		
		return bi;
	}
	
		void initializeGraphHeightAndWidth(HashMap pieChartInputMap){
	
		int iWidth = 0;
		int iHeight = 0;
		int cWidth = 0;
		int cHeight = 0;
		
		if(pieChartInputMap.get("iw") != null)
			iWidth = ((Integer)pieChartInputMap.get("iw")).intValue();
		
		if(pieChartInputMap.get("ih") != null)
			iHeight = ((Integer)pieChartInputMap.get("ih")).intValue();
		
		if(pieChartInputMap.get("cw") != null)
			cWidth = ((Integer)pieChartInputMap.get("cw")).intValue();
		
		if(pieChartInputMap.get("ch") != null)
			cHeight = ((Integer)pieChartInputMap.get("ch")).intValue();
		
		if(iWidth > imageWidth)
			imageWidth = iWidth;
		
		if(iHeight > imageHeight)
			imageHeight = iHeight;
		
		if(cWidth > graphWidth)
			graphWidth = cWidth;
		
		if(cHeight > graphHeight)
			graphHeight = cHeight;
	}
		
		
	void drawPieChart(){
		
		//if all the values in the pie chart are 0 then consider the total for all the angles as 1 
		if (totalPieSliceValue == 0)
			totalPieSliceValue=1;
		
		//First for loop is to draw the base of the pie chart.
		for(int i = 0; i < numOfSlices; i++) 
		{
			chart.setPaint(colors[i]);
		    sweepAngle = (int)Math.round(360*(pieSliceValues[i]/totalPieSliceValue));
		    chart.fill(new Arc2D.Double (pieXCord, pieYCord+depth, radius, (int)(radius/divisionFactor),
		    		startAngle, (int)sweepAngle,Arc2D.PIE));
		    startAngle += sweepAngle;
		}
		
		chart.fill(new Arc2D.Double (pieXCord, pieYCord+depth, radius, (int)(radius/divisionFactor),
	    		startAngle, (315-startAngle),Arc2D.PIE));
		
		//This for loop is to draw the depth of the pie chart
		 for(int x = (int)depth-1; x >= 1; x--) 
		 {
		      startAngle = -45;
		      for(int i=0; i<numOfSlices; i++) 
		      {
		    	chart.setPaint(colors[i].darker());
		        sweepAngle = (int)Math.round(360 * (pieSliceValues[i] / totalPieSliceValue));
		        chart.draw(new Arc2D.Double (pieXCord, pieYCord+x, radius, (int)(radius/divisionFactor),
		        		startAngle, (int)sweepAngle,Arc2D.PIE));
		        chart.draw(new Arc2D.Double (pieXCord+1, pieYCord+x+1, radius-2, (int)((radius-2)/divisionFactor),
		        		startAngle, (int)sweepAngle,Arc2D.PIE));
		        startAngle += sweepAngle;
		      }
		      chart.fill(new Arc2D.Double (pieXCord, pieYCord+x, radius, (int)(radius/divisionFactor),
	        		startAngle, (315-startAngle),Arc2D.PIE));
		 }
		 
		//This last for loop is to draw the last layer .. that is the top most layer of the image
		//because of this image layer, the graph looks good.
	     startAngle = -45;
	     for(int i = 0; i < numOfSlices; i++) 
		 {
	    	 chart.setPaint(colors[i]);
	    	 sweepAngle = (int)Math.round(360 * (pieSliceValues[i] / totalPieSliceValue));
	    	 chart.fill(new Arc2D.Double (pieXCord, pieYCord, radius, (int)(radius/divisionFactor),
		    		startAngle, (int)sweepAngle,Arc2D.PIE));
	    	 startAngle += sweepAngle;
		  }
	     chart.fill(new Arc2D.Double (pieXCord, pieYCord, radius, (int)(radius/divisionFactor),
	    		startAngle, (315-startAngle),Arc2D.PIE));
	    
	     chart.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.f));
		
	}
	
	public void drawTitleAndLegend(){
		double percent;
		int legendReverseOffset = 20;
		
		if(pieChartTitle != null && ! "".equals(pieChartTitle)){
			//Draw Title
			chart.setFont(sansSarifFont16);
			int pieChartTitleLength = chart.getFontMetrics().stringWidth(pieChartTitle);
			
			chart.setColor(Color.lightGray);
			chart.fillRect(((imageWidth/2) - (pieChartTitleLength /2))-7, graphTitleOffset-17, pieChartTitleLength+15, 22);
			chart.setColor(Color.BLACK);
			chart.drawRect(((imageWidth/2) - (pieChartTitleLength /2))-7, graphTitleOffset-17, pieChartTitleLength+15, 22);
			
			chart.drawString(
					pieChartTitle,
					(imageWidth/2) -(pieChartTitleLength/2),
					yOffset-20);
		}
		
		for(int i=0; i<numOfSlices; i++) 
		{
	    	chart.setPaint(colors[i]);
        
	        chart.fill3DRect(
					graphWidth+xOffset-legendReverseOffset,
					legendYOffSet,
					15,
					15, true);
			
			//Draw wordings for the Legend
			chart.setFont(sansSarifFont12);
			chart.setColor(new Color(0,0,0));
			percent = pieSliceValues[i]/totalPieSliceValue*100;
		    DecimalFormat fmt =  new DecimalFormat("0.##");
		    String str = ""+ fmt.format(percent)+"%";
			chart.drawString(
					(String)pieChartsliceNamesArr.get(i)+" "+"["+str+"]",
					graphWidth+xOffset-legendReverseOffset+20,
					legendYOffSet+15);
			
			legendYOffSet += 20;
		}
		
		//Draw box to enclose legend information
		int maxLegendNameLength=0;
		int pixLength = 0;
		for(int i = 0; i < numOfSlices;i++){
			pixLength = chart.getFontMetrics().stringWidth((String) pieChartsliceNamesArr.get(i)+ " [100.00%]");
			if(pixLength > maxLegendNameLength)
				maxLegendNameLength = pixLength;
		}
		
		chart.draw3DRect(
				graphWidth+xOffset -legendReverseOffset -5,
				yOffset+40-5, // this is same as initial value of legendYOffSet-5
				maxLegendNameLength+20,
				20*numOfSlices+5, true);
		
	}
	
	
	public void DrawPercentsOnSlices()
	{
		chart.setFont(sansSarifFont12);
		for(int i = 0; i < numOfSlices; i++) 
	  {
			chart.setPaint(Color.black);
		    sweepAngle = (int)(Math.round(360 * (pieSliceValues[i] / totalPieSliceValue)));
		    Arc2D arc = new Arc2D.Double (pieXCord, pieYCord, radius, (int)(radius/divisionFactor),
		    		startAngle, (int)(sweepAngle/2),Arc2D.PIE);
		    Point2D p = arc.getEndPoint();
		    Point2D c = new Point2D.Double();
		    c.setLocation(arc.getCenterX(),arc.getCenterY()); 
		    int x = (int)(p.getX()+(c.getX()-p.getX())/3-10); 
		    int y = (int)(p.getY()+(c.getY()-p.getY())/3)+7;
		    double percent;
		    percent = pieSliceValues[i]/totalPieSliceValue*100;
		    DecimalFormat fmt =  new DecimalFormat("0.##");
		    String str = ""+ fmt.format(percent)+"%";
		    chart.drawString(str,x ,y);
		    startAngle += sweepAngle;
	  }
	}
	
	
	public void DrawValuesOnSlices()
	{
		chart.setFont(sansSarifFont12);
		for(int i = 0; i < numOfSlices; i++) 
	    {
			chart.setPaint(Color.black);
		    sweepAngle = (int)(Math.round(360 * (pieSliceValues[i] / totalPieSliceValue)));
		    Arc2D arc = new Arc2D.Double (pieXCord, pieYCord, radius, (int)(radius/divisionFactor),
		    		startAngle, (int)(sweepAngle/2),Arc2D.PIE);
		    Point2D p = arc.getEndPoint();
		    Point2D c = new Point2D.Double();
		    c.setLocation(arc.getCenterX(),arc.getCenterY()); 
		    int x = (int)(p.getX()+(c.getX()-p.getX())/3-10); 
		    int y = (int)(p.getY()+(c.getY()-p.getY())/3)+7;
		    String str = ""+ pieSliceValues[i];
		    chart.drawString(str,x ,y);
		    startAngle += sweepAngle;
	    }
	}
	
	
	public void DrawPercentsBesideSlices()
	{
		chart.setFont(sansSarifFont12);
		for(int i = 0; i < numOfSlices; i++) 
	    {
			chart.setPaint(Color.black);
		    sweepAngle = (int)Math.round(360 * (pieSliceValues[i] / totalPieSliceValue));
		    Arc2D arc = new Arc2D.Double (pieXCord, pieYCord, radius, (int)(radius/divisionFactor),
		    		startAngle, (int)(sweepAngle/2),Arc2D.PIE);
		    Point2D p = arc.getEndPoint();
		    Point2D c = new Point2D.Double();
		    c.setLocation(arc.getCenterX(),arc.getCenterY()); 
		    double percent;
		    percent = pieSliceValues[i]/totalPieSliceValue*100;
		    DecimalFormat fmt =  new DecimalFormat("0.##");
		    String str = ""+ fmt.format(percent)+"%";
		        
		    double dist1=20;
		    double dist2=5;
		    double x1,y1;
		    double px, py, cx, cy;
		    double x_str, y_str;
		    px=p.getX();
		    py=p.getY();
		    cx=c.getX();
		    cy=c.getY();
		    if(px > cx) {x1=px+dist1;x_str=x1;}
		    else {x1=px-dist1;x_str=x1-40;}
		    if(py > cy) {y1=py+dist1+depth/2;y_str=y1+10;}
		    else {y1=py-dist2;y_str=y1;}
	
		    if(py>cy)
		    	chart.drawLine((int)x1,(int)y1,(int)px,(int)(py+depth/2));
		    else
		    	chart.drawLine((int)x1,(int)y1,(int)px,(int)py);
		  
		    chart.drawString(str,(int)x_str,(int)y_str);
		    
		    startAngle += sweepAngle;
	    }
	}

	void initGraph(HashMap pieChartInputMap){
		
		//Setup all the input information for pieChart

		pieChartsliceNamesArr = (ArrayList)pieChartInputMap.get("ccn");
		pieChartSliceValuesArr = (ArrayList)pieChartInputMap.get("ccv");
		
		try{
			if(pieChartInputMap.get("percent") != null)
				drawPercentageOnChart = ((Boolean) pieChartInputMap.get("percent")).booleanValue();
		}catch(Exception e){
		}

		
		if (pieChartSliceValuesArr != null){
			pieSliceValues = new double[pieChartSliceValuesArr.size()];
			for (int i=0; i <pieChartSliceValuesArr.size(); i++){
				pieSliceValues[i] = Math.abs(Double.parseDouble((String)pieChartSliceValuesArr.get(i)));
				totalPieSliceValue += pieSliceValues[i];
			}
		}
		
	
		//Populate the number of pie chart slices
		if (pieSliceValues == null)
			numOfSlices = 0;
		else
			numOfSlices = pieSliceValues.length;
	
		//Populate pie chart title
		pieChartTitle = (String) pieChartInputMap.get("ct");
		
		//initialize all the values
		radius=(int)(4*pieImageWidth/6);
		pieXCord=(pieImageWidth-radius)/2;
		pieYCord=chartTitleYOffset+(pieImageHeight-radius/divisionFactor-depth-chartTitleYOffset)/2;
		depth=(int)(radius/8);
		
	}
	
	void drawGraphFramework(){
		
		chart.setFont(new Font("Arial", 1, 10));
		chart.drawRect(0, 0, imageWidth - 1, imageHeight - 1);
		chart.setColor(Color.black);
		
		//Drow Y Axis
		chart.drawLine(xOffset , yOffset , xOffset, graphHeight+yOffset);
		
		//Drow X Axis
		chart.drawLine(xOffset , graphHeight+yOffset, graphWidth+xOffset, graphHeight+yOffset);
		
	}
	
	/*
	void drawTitleAndLegend(){
		System.out.println("drawTitleAndLegend Start");
		//Set Graph Title
		chart.setFont(new Font("Arial", 1, 20));
		chart.drawString(
				pieChartTitle,
				(imageWidth/2)-10, graphTitleOffset);
		
		//Set Legend Label
		chart.setFont(new Font("Arial", 1, 15));
		chart.drawString(
				"Legend:",
				graphWidth+xOffset+5, yOffset);
		
	}
	*/
}



