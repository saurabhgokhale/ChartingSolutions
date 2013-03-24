3D Charting Solutions
============================

This is a Charting Library that I developed for Amdocs Inc. I am in the process of getting approval from the company to make the code open source.

I am hoping to get the approval in few days. Until then, here is the charts that I developed.

Bar Chart
----------
On the JSP/HTML page, I make call to the "Chart" Servlet with following parameters, which generates an image and returned as a bufferedImage. Being inside the image tag, the response is displayed as image on the page

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
