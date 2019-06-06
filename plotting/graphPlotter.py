import plotly.graph_objs as go
from plotly.offline import download_plotlyjs, init_notebook_mode, plot, iplot

trace1 = go.Bar(
    x=['Order Amount 10', 'Order Amount 50', 'Order Amount 100', 'Order Amount 250', 'Order Amount 500'],
    y=[206, 894, 1781, 4447, 8943],
    name='SA Batch Size 3'
)
trace2 = go.Bar(
    x=['Order Amount 10', 'Order Amount 50', 'Order Amount 100', 'Order Amount 250', 'Order Amount 500'],
    y=[152, 596, 1135, 2907, 5960],
    name='SA Batch Size 4'
)
trace3 = go.Bar(
    x=['Order Amount 10', 'Order Amount 50', 'Order Amount 100', 'Order Amount 250', 'Order Amount 500'],
    y=[92, 430, 841, 2120, 4481],
    name='SA Batch Size 5'
)


#GA-SA

data = [trace1, trace2, trace3]
layout = go.Layout(
    barmode='group'

)


#plot([go.Scatter(x=[1, 2, 3], y=[3, 1, 6])])
fig = go.Figure(data=data, layout=layout)
plot(fig)
