3D Charting Solutions
=====================

This charting library generates BAR, PIE and LINE graphs. This library does not use any other 3rd party library and is purely based on Java 2D classes.


Bar Chart
----------
On the JSP/HTML page, I make call to the "Chart" Servlet with following parameters, which generates an image and returns a bufferedImage Object. Being inside the image tag, the response is displayed by the browsers as an image on the page.

```html
<img src="Chart?cht=bar&ct=Call+Usage+Analysis&ccn=06Mar+07Mar+08Mar+09Mar+10Mar&ccv=150+79+23+55+44&cxt=Dates&cyt=Total+Amount+Due+($)&iw=700&ih=350&cw=400&ch=300&percent=false"/>
```

where   
1. cht = Chart Type   
2. ct  = Chart Title   
3. ccn = Chart Column Name   
4. ccv = Chart Column Value   
5. cxt = Chart X Axis Title   
6. cyt = Chart Y Axis Title   
7. iw  = Image Width in pixel [Optional Value]   
8. ih  = Image Height in pixel [Optional Value]   
9. cw  = Chart Width in pixel [Optional Value]   
10. ch  = Chart Height in pixel [Optional Value]   
11. percent = Display % value or hide [Optional Value]   



![Bar Chart](docs/images/bar.png?raw=true)

With a single parameter change, the same chart can be displayed in another format like Pie or Line... pass cht = pie or line instead of bar

Pie Chart
----------
```html
<img src="Chart?cht=pie&ct=Call+Usage+Analysis&ccn=06Mar+07Mar+08Mar+09Mar+10Mar&ccv=150+79+23+55+44&cxt=Dates&cyt=Total+Amount+Due+($)&percent=true"/>
```
   
In a case where a parameter sent is not required, it is ignored. In Pie chart case X and Y Axis title parameters are ignored.

![Pie Chart](docs/images/pie.png?raw=true)


Line Chart
----------
```html
<img src="Chart?cht=line&ct=Call+Usage+Analysis&ccn=06Mar+07Mar+08Mar+09Mar+10Mar&ccv=150+79+23+55+44&cxt=Dates&cyt=Total+Amount+Due+($)&percent=false"/>
```

![Line Chart](docs/images/line.png?raw=true)


How to build
----------
1. Clone or download the project.
2. run **$ mvn clean install**
3. It generates chartingSolutions.war in target folder.
4. Copy this war file in tomcat/webapps directory.

   
   
Please Note
----------
1. For the simplicity purposes, I removed log4j dependency and replaced log statements with few system outs. In production sysouts can be removed.
2. Currently code accepts only first 10 arguments to the graph. This is done to make sure that the bar / line graph inputs are plotted within image size limit.
3. At some places, some additional comments in the code may be needed to make this code more readable. 

Lastly, THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND. Anyone is free to copy, modify, publish, use, compile, sell, or distribute this code, for any purpose, commercial or non-commercial, and by any means.