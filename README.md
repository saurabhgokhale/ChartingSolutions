3D Charting Solutions
============================

This is a Charting Library that I developed for my previous employer.

Bar Chart
----------
On the JSP/HTML page, I make call to the "Chart" Servlet with following parameters, which generates an image and returns a bufferedImage Object. Being inside the image tag, the response is displayed by the browsers as an image on the page.

```html
<img src="Chart?cht=bar&ct=Call+Usage+Analysis&ccn=06Mar+07Mar+08Mar+09Mar+10Mar&ccv=150+79+23+55+44&cxt=Dates&cyt=Total+Amount+Due+($)&iw=700&ih=350&cw=400&ch=300&percent=false"/>

where
cht = Chart Type
ct  = Chart Title
ccn = Chart Column Name
ccv = Chart Column Value
cxt = Chart X Axis Title
cyt = Chart Y Axis Title
iw  = Image Width in pixel [Optional Value]
ih  = Image Height in pixel [Optional Value]
cw  = Chart Width in pixel [Optional Value]
ch  = Chart Height in pixel [Optional Value]
percent = Display % value or hide [Optional Value]
```


![Bar Chart](http://i.imgur.com/o5dVjzc.png)

With a single parameter change, the same chart can be displayed in another format like Pie or Line... pass cht = pie or line instead of bar

Pie Chart
----------
```html
<img src="Chart?cht=pie&ct=Call+Usage+Analysis&ccn=06Mar+07Mar+08Mar+09Mar+10Mar&ccv=150+79+23+55+44&cxt=Dates&cyt=Total+Amount+Due+($)&percent=true"/>
```
In a case where a parameter sent is not required, it is ignored. In Pie chart case X and Y Axis title parameters are ignored.

![Pie Chart](http://i.imgur.com/66kVELs.png)


Line Chart
----------
```html
<img src="Chart?cht=line&ct=Call+Usage+Analysis&ccn=06Mar+07Mar+08Mar+09Mar+10Mar&ccv=150+79+23+55+44&cxt=Dates&cyt=Total+Amount+Due+($)&percent=false"/>
```

![Line Chart](http://i.imgur.com/U1Ugot2.png)


Please Note
----------
1. For the simplicity purposes, I removed log4j dependency and replaced log statements with few system outs. In production sysouts can be removed.
2. Currently code accepts only first 10 arguments to the graph. This is done to make sure that the bar / line graph inputs are plotted with image size limit.
3. I need to add more comments in the code to make this code more redable. 

Lastly, THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND. Anyone is free to copy, modify, publish, use, compile, sell, or distribute this code, for any purpose, commercial or non-commercial, and by any means.