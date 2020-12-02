import ipywidgets as widgets
from ipywidgets import interact
import matplotlib.pyplot as plt
from matplotlib.patches import Rectangle
import matplotlib.image as mpimg
from matplotlib.offsetbox import OffsetImage, AnnotationBbox
import math
from src.throttle import *

class TwoProb(widgets.HBox):

    def __init__(self):
        super().__init__()
        #{font-size: 14px;line-height: 24px;color: black;}
        #print(self.style.keys)
        
        self.slider_a = widgets.FloatSlider(value=0.5, min=0, max=1.0, step=0.01,
                                            description='$P(A)$', readout_format='.2f')
        self.slider_b = widgets.FloatSlider(value=0.5, min=0, max=1.0, step=0.01,
                                            description='$P(B)$', readout_format='.2f')
        self.slider_ab = widgets.FloatSlider(value=0.25, min=0, max=0.5, step=0.01,
                                             description='$P(A \cap B ) $', readout_format='.2f')
        self.highlight = widgets.Dropdown(
            options=[
                ('all', 0),
                ('P(A)', 1),
                ('P(¬A)', 2),
                ('P(B)', 3),
                ('P(A ∩ B)', 4),
                ('P(A ∪ B)', 5),
                ('P(A | B)', 6),
                ('P(B | A)', 7)
            ],
            value=0,
            description='Show:'
        )
        self.slider_a.observe(self.handle_slider_change, names='value')
        self.slider_b.observe(self.handle_slider_change, names='value')
        self.slider_ab.observe(self.update2, names='value')
        self.highlight.observe(self.update2, names='value')
        
        self.probs = tuple(map(lambda x: widgets.HTMLMath(x), [
            "$P(A)$", "$=$", "0.50",
            "$P(B)$", "$=$", "0.50",
            "$P(\\neg A)$", "$=$", "0.50",
            "$P(\\neg B)$", "$=$", "0.50",
            "$P(A \\cap B)$", "$=$", "0.25",
            "$P(A \\cup B)$", "$=$", "0.75",
            "$P(A | B)$", "$=$", "0.50",
            "$P(B | A)$", "$=$", "0.50"
        ]))
        #prob_lst = widgets.GridBox(
        #    children=self.probs,
        #    layout=widgets.Layout(
        #        width='60%',
        #        grid_template_columns='45% 20% 30%',
        #        grid_template_rows='25px 25px 25px 25px 25px 25px 25px 25px')
        #)
        prob_lst = widgets.VBox([ widgets.HBox([self.probs[i] for i in range(3*j,3*(j+1))]) for j in range(0,8)])
        
        self.output = widgets.Output()  
        with self.output:
            plt.rc('font', size=18)
            fig, ax = plt.subplots()
            self.fig = fig
            self.ax = ax
            self.fig.canvas.toolbar_visible = False
            self.fig.canvas.header_visible = False
            self.fig.canvas.footer_visible = False
            self.fig.tight_layout()
            self.ax.set_aspect(1.)
            #ax.axes.xaxis.tick_top()
            legend = mpimg.imread('data/legend.png')
            imagebox = OffsetImage(legend, zoom=0.2)
            self.ab = AnnotationBbox(imagebox, (1.0, 0.8))
            self.drawFig(0.25, 0.25, 0.25, 0.25)
        
        box_layout = widgets.Layout(
            display='flex',
            flex_flow='column',
            align_items='flex-start',
            justify_content='space-around')

        self.children = [
            widgets.Box(
                [self.output],
                layout = widgets.Layout(width='68%', height='500px')
            ),
            widgets.VBox([
                    widgets.VBox([
                        self.slider_a,
                        self.slider_b,
                        self.slider_ab,
                        self.highlight]),
                    prob_lst],
                layout = box_layout)
             ]
        
    def drawFig(self, pp, pn, np, nn):
        self.ax.clear()
        self.ax.set_xlim(-1, 1)
        self.ax.axes.xaxis.set_ticks([-0.5,0.5])
        self.ax.axes.xaxis.set_ticklabels(["$B$","$\\neg B$"])
        self.ax.set_ylim(-1, 1)
        self.ax.axes.yaxis.set_ticks([-0.5,0.5])
        self.ax.axes.yaxis.set_ticklabels(["$\\neg A$","$A$"])
        self.ax.axline((-1,0),(1,0),color='k')
        self.ax.axline((0,-1),(0,1),color='k')
        #self.ax.add_patch(Rectangle((-1, -1), 2, 2, fc='w'))
        cpp = 'b'
        cpn = 'b'
        cnp = 'b'
        cnn = 'b'
        hl = self.highlight.value
        if hl == 1:
            cnp = 'c'
            cnn = 'c'
        elif hl == 2:
            cpp = 'c'
            cpn = 'c'
        elif hl == 3:
            cpn = 'c'
            cnn = 'c'
        elif hl == 4:
            cnp = 'c'
            cpn = 'c'
            cnn = 'c'
        elif hl == 5:
            cnn = 'c'
        elif hl == 6:
            cnp = 'c'
            cpn = 'lightgrey'
            cnn = 'lightgrey'
        elif hl == 7:
            cpn = 'c'
            cnp = 'lightgrey'
            cnn = 'lightgrey'
        spp = math.sqrt(max(0, pp))
        self.ax.add_patch(Rectangle((-spp,0), spp, spp, fc=cpp))
        spn = math.sqrt(max(0, pn))
        self.ax.add_patch(Rectangle((0,0), spn, spn, fc=cpn))
        snp = math.sqrt(max(0, np))
        self.ax.add_patch(Rectangle((-snp,-snp), snp, snp, fc=cnp))
        snn = math.sqrt(max(0, nn))
        self.ax.add_patch(Rectangle((0,-snn), snn, snn, fc=cnn))
        self.ax.add_artist(self.ab)

    def updateProbList(self, a, b, ab):
        self.probs[ 2].value = "%.2f" % a
        self.probs[ 5].value = "%.2f" % b
        self.probs[ 8].value = "%.2f" % (1 - a)
        self.probs[11].value = "%.2f" % (1 - b)
        self.probs[14].value = "%.2f" % (ab)
        self.probs[17].value = "%.2f" % (a + b - ab)
        self.probs[20].value = ("%.2f" % (ab / b)) if b > 0 else "$-$"
        self.probs[23].value = ("%.2f" % (ab / a)) if a > 0 else "$-$"

    @throttle(0.8)
    def updateGraph(self, a, b, ab, change):
        pp = ab
        pn = a - ab
        np = b - ab
        nn = 1 - a - b + ab
        with self.output:
            #self.output.clear_output(wait=True)
            self.drawFig(pp, pn, np, nn)
            self.fig.canvas.draw()

    @throttle(0.2)
    def handle_slider_change(self, change):
        old = self.slider_ab.value
        a = self.slider_a.value
        b = self.slider_b.value
        self.slider_ab.min = max(0, a + b - 1)
        self.slider_ab.max = max(a + b - 1, min(a, b))
        if self.slider_ab.value == old:
            self.update2(change)
            
    def update2(self, change):
        a = self.slider_a.value
        b = self.slider_b.value
        ab = self.slider_ab.value
        self.updateProbList(a, b, ab)
        self.updateGraph(a, b, ab, change)
