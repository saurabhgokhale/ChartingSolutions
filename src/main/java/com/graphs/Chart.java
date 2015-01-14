//---------------------------------------------------------------
//	Class Name: Chart.java (Charting Solutions)
//
//	Author:     Saurabh Gokhale
//	Desc:       CS Chart is a initial servlet which receives GET Response from the front 
//				end (browser) and generates an graph image.
//				3 Different types of Graphs are supported
//				1> Bar Graph
//				2> Pie Graph
//				3> Line Graph
//				Image can be generated in
//				1> PNG format (loosely coupled format)
//				2> JPEG format. --> currently commented code.
//
//---------------------------------------------------------------
package com.graphs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class Chart extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2711005040845331712L;

	/**
	 * Constructor of the object.
	 */
	public Chart() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy();
	}

	/**
	 * doGet
	 * doGet method receive all the data from the queryString in the following format
	 * Chart?cht=__&ct=__+__&ccn=__+__&ccv=__+__&cxt=__+__&cyt=__+__
	 * where
	 * @param request 
	 * cht = Chart Type (bar/pie/line)
	 * ct =  Chart Title
	 * ccn = Chart Column Name
	 * ccv = Chart Column Value 
	 * cxt = Chart X Axis Title
	 * cyt = Chart Y Axis Title
	 * 
	 * @param response 
	 * BufferedImage
	 * 
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/*Runtime runtime = Runtime.getRuntime();
		long heapSize = runtime.totalMemory();
		long heapMaxSize = runtime.maxMemory();
		long heapFreeSize = runtime.freeMemory();
		int mb = 1024*1024;
		
		System.out.println("CS Line ChartServlet: before Graph heapSize = " + heapSize/mb + " heapMaxSize = " + heapMaxSize/mb + " heapFreeSize = " + heapFreeSize/mb);
		*/
		long startTime = System.currentTimeMillis();
	
		BufferedImage bi = null;
		
		try {
		
			String barChartType = request.getParameter("cht"); // Chart Type
			String barChartTitle = request.getParameter("ct"); // chart Title
			String barChartColumnNameString = request.getParameter("ccn"); // Chart Column Name
			String barChartColumnNameValue = request.getParameter("ccv"); // Chart Column Values
			String barChartXAxisTitleValue = request.getParameter("cxt"); // chart X Axis title
			String barChartYAxisTitleValue = request.getParameter("cyt"); // Chart Y Axis title
			String strImageWidth = request.getParameter("iw"); //image width
			String strImageHeight = request.getParameter("ih"); //image height
			String strChartWidth = request.getParameter("cw"); //chart width
			String strChartHeight = request.getParameter("ch"); //chart height
			String drawPercentageForPieChart = request.getParameter("percent");
			/*
			 * if chart column names or chart column values are passed null or empty
			 * CS will return empty image of size 50*50.
			 * Else it will call the corresponding chart generators and generate Chart image.
			 */
			if((barChartColumnNameString == null || "".equals(barChartColumnNameString))
				|| (barChartColumnNameValue == null || "".equals(barChartColumnNameValue)))
			{
				System.out.println("CS ChartServlet: ccn or ccv null or empty");
				
				bi = new BufferedImage(50,50,BufferedImage.TYPE_INT_RGB);
				Graphics2D chart = bi.createGraphics();
				chart.setColor(Color.white);	
				chart.fillRect(0, 0, bi.getWidth(), bi.getHeight());
			}
			else{
				
				/* Since values for column name and column values are present, read all the values
				 * and populate it in the appropriate arraylists. Also check if column values 
				 * and column names sent are more than 10. If yes, ignore any values after max 10 values.
				 */
				int i = 0; // i is used to drop more than 10 column name and values. 
				
				ArrayList barChartColumnNameArr = new ArrayList();
				if (barChartColumnNameString != null)
				{
					if(barChartColumnNameString.indexOf(" ") > -1)
					{
						StringTokenizer st = new StringTokenizer(barChartColumnNameString, " ");
						while(st.hasMoreTokens() && i < 10){
							barChartColumnNameArr.add(st.nextToken());
							i++;
						}
					}else{
						barChartColumnNameArr.add(barChartColumnNameString);
					}
				}
				
				i=0;
				ArrayList barChartColumnValueArr = new ArrayList();
				if (barChartColumnNameValue != null)
				{
					if(barChartColumnNameValue.indexOf(" ") > -1)
					{
						StringTokenizer st = new StringTokenizer(barChartColumnNameValue, " ");
						while(st.hasMoreTokens() &&  i < 10){
							barChartColumnValueArr.add(st.nextToken());
							i++;
						}
					}else{
						barChartColumnValueArr.add(barChartColumnNameValue);
					}
				}
				
				
				String barChartXAxisTitle = "";
				if (barChartXAxisTitleValue != null && ! "".equals(barChartXAxisTitleValue))
				{
					barChartXAxisTitle = barChartXAxisTitleValue;
				}else{
					barChartXAxisTitle = "X Axis";
				}
					
				
				String barChartYAxisTitle = "";
				if (barChartYAxisTitleValue != null && ! "".equals(barChartYAxisTitleValue))
				{
					barChartYAxisTitle = barChartYAxisTitleValue;
				}else{
					barChartYAxisTitle = "Y Axis";
				}
				
				//Create a HashMap of all the values to pass to Chart Generators
				HashMap chartInputMap = new HashMap();
				chartInputMap.put("ct", barChartTitle);
				chartInputMap.put("ccn", barChartColumnNameArr);
				chartInputMap.put("ccv", barChartColumnValueArr);
				chartInputMap.put("cxt", barChartXAxisTitle);
				chartInputMap.put("cyt", barChartYAxisTitle);
				
				
				try{//check if graph and image width and height are passed
					if(strImageWidth != null && strImageWidth.trim() != ""){
						chartInputMap.put("iw", new Integer(Integer.parseInt(strImageWidth)));
					}
				}catch(Exception e){
					System.out.println("CS: imageWidth parameter passed is not a number");
				}
				
				try{//check if graph and image width and height are passed
					if(strImageHeight != null && strImageHeight.trim() != ""){
						chartInputMap.put("ih", new Integer(Integer.parseInt(strImageHeight)));
					}
				}catch(Exception e){
						System.out.println("CS: imageHeight parameter passed is not a number");
				}
				
				try{//check if graph and image width and height are passed
					if(strChartWidth != null && strChartWidth.trim() != ""){
						chartInputMap.put("cw", new Integer(Integer.parseInt(strChartWidth)));
					}
				}catch(Exception e){
						System.out.println("CS: chartWidth parameter passed is not a number");
				}
				
				try{//check if graph and image width and height are passed
					if(strChartHeight != null && strChartHeight.trim() != ""){
						chartInputMap.put("ch", new Integer(Integer.parseInt(strChartHeight)));
					}
				}catch(Exception e){
						System.out.println("CS: chartHeight parameter passed is not a number");
				}	
			
				try{//check if draw percentage tag is sent in the parameter
					if(drawPercentageForPieChart != null && drawPercentageForPieChart.trim() != ""){
						chartInputMap.put("percent", Boolean.valueOf(drawPercentageForPieChart));
					}
				}catch(Exception e){
						System.out.println("CS: percent parameter passed is not a boolean");
				}	
				
	
				try{
					if (barChartType.equalsIgnoreCase("bar")){
						BarChartGenerator barGenerator = new BarChartGenerator();
						bi = barGenerator.createImage(response.getOutputStream(), chartInputMap);
					}
					else if (barChartType.equalsIgnoreCase("pie")){
						PieChartGenerator pieGenerator = new PieChartGenerator();
						bi = pieGenerator.createImage(response.getOutputStream(), chartInputMap);
					}
					else if (barChartType.equalsIgnoreCase("line")){
						LineChartGenerator lineGenerator = new LineChartGenerator();
						bi = lineGenerator.createImage(response.getOutputStream(), chartInputMap);
					}
				}catch(Exception e){
					/*
					 * in case of any exception returned
					 * CS will return empty image of size 50*50.
					 */
					e.printStackTrace();
					System.out.println("CS ChartServlet: Exception returned from graph generator = " + e.getMessage());
					bi = new BufferedImage(50,50,BufferedImage.TYPE_INT_RGB);
					Graphics2D chart = bi.createGraphics();
					chart.setColor(Color.white);	
					chart.fillRect(0, 0, bi.getWidth(), bi.getHeight());
				}
			}
				response.setContentType("image/png");
				OutputStream os = response.getOutputStream();
				ImageIO.write(bi, "png", os);
				os.close();
				
				/*heapSize = runtime.totalMemory();
				heapMaxSize = runtime.maxMemory();
				heapFreeSize = runtime.freeMemory();
				System.out.println("CS Line ChartServlet: before Graph heapSize = " + heapSize/mb + " heapMaxSize = " + heapMaxSize/mb + " heapFreeSize = " + heapFreeSize/mb);
				*/
				long endTime = System.currentTimeMillis();
				System.out.println("CS ChartServlet: Total Time taken to generate line graph = " + (endTime - startTime) /1000 + " sec.");
				
				
			} catch (Exception e) {
				System.out.println("CS ChartServlet: Exception returned from chart servlet= " + e.getMessage());
				//e.printStackTrace();
			}
		
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * If POST request is received, forward the request to GET. Currently only GET request is supported by CS. 
		 * After redirecting request to get since its missing mandatory fields, Library will generate a small white image
		 * of 50 * 50 in size which will be transparent on the white background. This was done for the use case for which
		 * I developed this library. There getting an empty image was acceptable on some error than getting a big stack trace
		 * of error. 
		 */

		doGet(request, response);
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occure
	 */
	public void init() throws ServletException {
		//TODO - add code here.
	}

}
