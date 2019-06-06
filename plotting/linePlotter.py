import sys
import plotly.graph_objs as go
from plotly.offline import download_plotlyjs, init_notebook_mode, plot, iplot



saX = raw_input()
saY = raw_input()
gaX = raw_input()
gaY = raw_input()



saX = saX.split(' ')
saY = saY.split(' ')
gaX = gaX.split(' ')
gaY = gaY.split(' ')

saXValue = saX[len(saX)-2]
saYValue = saY[len(saY)-2]

gaXValue = gaX[len(gaX)-2]
gaYValue = gaY[len(gaY)-2]

firstSaX = saX[0]
firstSaY = saY[0]
firstGaX = gaX[0]
firstGaY = gaY[0]


if int(saXValue)>int(gaXValue):
    gaX.insert(len(gaX)-1, saXValue)
    gaY.insert(len(gaY)-1, gaYValue)

else:
    saX.insert(len(saX)-1, gaXValue)
    saY.insert(len(saY)-1, saYValue)

saX.insert(0, 0)
saY.insert(0, firstSaY)

gaX.insert(0, 0)
gaY.insert(0, firstGaY)



trace1 = go.Scatter(
    x=saX,
    y=saY,
    name = 'SA',
)
trace2 = go.Scatter(
    x=gaX,
    y=gaY,
    name = 'GA',
)

data = [trace1, trace2]

layout = dict(
    xaxis = dict(title = 'Time (ms)', zeroline=True, showline=True,rangemode='tozero'),
    yaxis = dict(title = 'Fitness', zeroline=True, showline=True),

    title = ""
)

# cat ../results/bestSolutionsTimesSA-4-50.txt  ../results/bestSolutionsTimesGA-4-50.txt | python linePlotter.py
#plot([go.Scatter(x=[1, 2, 3], y=[3, 1, 6])])
fig = dict(data=data, layout = layout)

plot(fig,filename='testplot.html')

