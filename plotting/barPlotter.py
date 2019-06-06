import sys
import plotly.graph_objs as go
from plotly.offline import download_plotlyjs, init_notebook_mode, plot, iplot

saX = raw_input()
saY = raw_input()
#gaX = raw_input()
#gaY = raw_input()

#saX = sys.stin.readline()
#saY = sys.stin.readlint()
#gaX = sys.stin.readlint()
#gaY = sys.stin.readlint()


y1 = saX.split(' ')
y2 = saY.split(' ')
#y3 = gaX.split(' ')
#y4 = gaY.split(' ')



trace1 = go.Bar(
    x=['Small', 'Medium', 'Large'],
    y=y1,
    name = 'SA',
)
trace2 = go.Bar(
    x=['Small', 'Medium', 'Large'],
    y=y2,
    name = 'GA',
)


data = [trace1, trace2]

layout = go.Layout(
    barmode='group',
    yaxis = dict(title = 'Fitness', zeroline=True, showline=True),
    xaxis = dict(title = 'Data set', zeroline=True, showline=True),
)


fig = go.Figure(data=data, layout=layout)


plot(fig)
