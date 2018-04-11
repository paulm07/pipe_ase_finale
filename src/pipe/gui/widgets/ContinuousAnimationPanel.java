package pipe.gui.widgets;

import formula.GlobalClock;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler;
import pipe.client.api.model.AnimationHistoryItem;
import pipe.client.api.model.AnimationType;
import pipe.dataLayer.DataLayer;
import pipe.dataLayer.Place;
import pipe.dataLayer.Transition;
import pipe.gui.Animator;
import pipe.gui.CreateGui;


import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 *
 * Continuous animation panel
 *
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public class ContinuousAnimationPanel extends JPanel {

    private final DataLayer mDataLayer;
    private final Animator mAnimator;
    List<Function<Void, Void>> mCharUpdaters = new ArrayList<>();
    private boolean mContinuousRunning = false;

    public ContinuousAnimationPanel(final Animator pAnimator, final DataLayer pDataLayer) {
        mDataLayer = pDataLayer;
        mAnimator = pAnimator;

        initComponents();
    }

    private void initComponents() {
        JPanel animationConfigPanel = initializeAnimationConfigPanel();
        JPanel chartConfigPanel = initiaizeChartConfigPanel();
        if (animationConfigPanel == null || chartConfigPanel == null) {
//            do nothing
        }

        JPanel configWrapper = new JPanel();
        configWrapper.setLayout(new BoxLayout(configWrapper, BoxLayout.X_AXIS));
        configWrapper.add(animationConfigPanel);
        configWrapper.add(chartConfigPanel);

        setPreferredSize(new Dimension(630, 300));
        add(configWrapper);
    }

    private JPanel initiaizeChartConfigPanel() {
        JPanel chartConfigPanel = new JPanel();
        chartConfigPanel.setLayout(new BoxLayout(chartConfigPanel, BoxLayout.Y_AXIS));

        JPanel xConfigPanel = new JPanel();
        xConfigPanel.setLayout(new BoxLayout(xConfigPanel, BoxLayout.Y_AXIS));

        JPanel xPlaceWrapperPanel = new JPanel();
        xPlaceWrapperPanel.setLayout(new BoxLayout(xPlaceWrapperPanel, BoxLayout.X_AXIS));
        xPlaceWrapperPanel.add(new JLabel("x-Axis place:"));

        //final JComboBox<Place> xPlaceCombobox = new JComboBox<>(mDataLayer.getPlaces());
        //xPlaceWrapperPanel.add(xPlaceCombobox);

        JPanel xValueIndexWrapperPanel = new JPanel();
        xValueIndexWrapperPanel.setLayout(new BoxLayout(xValueIndexWrapperPanel, BoxLayout.X_AXIS));
        xValueIndexWrapperPanel.add(new JLabel("X-Axis Value Index: "));
        JTextField xValueIndexTextField = new JTextField("0");
        xValueIndexWrapperPanel.setToolTipText("Index (0 based) of the field in the datatype that you want to show in the chart");
        xValueIndexWrapperPanel.add(xValueIndexTextField);
        xConfigPanel.add(xPlaceWrapperPanel);

        JCheckBox xAxisCheckBox = new JCheckBox("Use Time as X-Axis reference instead");
        xConfigPanel.add(xAxisCheckBox);
        xConfigPanel.add(xValueIndexWrapperPanel);


        JPanel yConfigPanel = new JPanel();
        yConfigPanel.setLayout(new BoxLayout(yConfigPanel, BoxLayout.Y_AXIS));

        JPanel yPlaceWrapperPanel = new JPanel();
        yPlaceWrapperPanel.setLayout(new BoxLayout(yPlaceWrapperPanel, BoxLayout.X_AXIS));
        yPlaceWrapperPanel.add(new JLabel("Y-Axis place:"));
        //final JComboBox<Place> yPlaceCombobox = new JComboBox<>(mDataLayer.getPlaces());
       // yPlaceWrapperPanel.add(yPlaceCombobox);

        JPanel yValueIndexWrapperPanel = new JPanel();
        yValueIndexWrapperPanel.setLayout(new BoxLayout(yValueIndexWrapperPanel, BoxLayout.X_AXIS));
        yValueIndexWrapperPanel.add(new JLabel("Y-Axis Value index: "));
        yValueIndexWrapperPanel.setToolTipText("Index (0 based) of the field in the datatype that you want to show in the chart");
        JTextField yvalueIndexTextField = new JTextField();
        yValueIndexWrapperPanel.add(yvalueIndexTextField);
        yConfigPanel.add(yPlaceWrapperPanel);
        yConfigPanel.add(yValueIndexWrapperPanel);

        JButton addChartButton = new JButton("Add chart");
        addChartButton.addActionListener(l -> {
            int yIndex = Integer.parseInt(yvalueIndexTextField.getText().trim());
            if (xAxisCheckBox.isSelected()) {
            //    addTimedChart("Time", (Place) yPlaceCombobox.getSelectedItem(), yIndex);
            }
            else {
                int xIndex = Integer.parseInt(xValueIndexTextField.getText().trim());
          //      addChart((Place)xPlaceCombobox.getSelectedItem(), xIndex, (Place) yPlaceCombobox.getSelectedItem(), yIndex);
            }


        });

        chartConfigPanel.add(xConfigPanel);
        chartConfigPanel.add(yConfigPanel);
        chartConfigPanel.add(addChartButton);

        chartConfigPanel.setBorder(BorderFactory.createTitledBorder("Chart config"));
        return chartConfigPanel;
    }

    private void addTimedChart(final String pXOption, final Place pYPlace, final int pYIndex) {
        String yTitle = pYPlace.getName();
        String chartTitle = String.format("%s-%s XY Chart", pXOption, yTitle);

        final List<Double> xData = new ArrayList<>();
        final List<Double> yData = new ArrayList<>();

        xData.add((double) GlobalClock.getInstance().getClockState().next);
        yData.add(pYPlace.getToken().getListToken().get(0).Tlist.get(pYIndex).getValueAsDouble());

        addChart(pXOption, yTitle, chartTitle, new ChartUpdater() {
            @Override
            public String addSeries(XYChart pChart) {
                String series = "series1";
                pChart.addSeries(series, xData, yData);
                return series;
            }

            @Override
            public void updateSeries(XYChart pChart, String pSeries) {
                xData.add((double) GlobalClock.getInstance().getClockState().next);
                yData.add(pYPlace.getToken().getListToken().get(0).Tlist.get(pYIndex).getValueAsDouble());
                pChart.updateXYSeries(pSeries, xData, yData, null);
            }
        });
    }

    private void displayChart(final String pTitle, final JPanel pChartPanel) {
        final JFrame frame = new JFrame(pTitle);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.getContentPane().add(pChartPanel);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private void addChart(final Place xPlace, final int xIndex, final Place yPlace, final int yIndex) {
        String xTitle = xPlace.getName();
        String yTitle = yPlace.getName();
        String chartTitle = String.format("%s-%s XY Chart", xTitle, yTitle);

        final List<Double> xData = new ArrayList<>();
        final List<Double> yData = new ArrayList<>();

        xData.add(xPlace.getToken().getListToken().get(0).Tlist.get(xIndex).getValueAsDouble());
        yData.add(yPlace.getToken().getListToken().get(0).Tlist.get(yIndex).getValueAsDouble());

        addChart(xTitle, yTitle, chartTitle, new ChartUpdater() {
            @Override
            public String addSeries(XYChart pChart) {
                String series = "series1";
                pChart.addSeries(series, xData, yData);
                return series;
            }

            @Override
            public void updateSeries(XYChart pChart, String pSeries) {
                xData.add(xPlace.getToken().getListToken().get(0).Tlist.get(xIndex).getValueAsDouble());
                yData.add(yPlace.getToken().getListToken().get(0).Tlist.get(yIndex).getValueAsDouble());
                pChart.updateXYSeries(pSeries, xData, yData, null);
            }
        });
    }

    private void addChart(final String pTitleX, final String pTitleY, final String pChartTitle, final ChartUpdater pChartUpdater) {
        XYChart xyChart = new XYChartBuilder().width(500)
                .height(400)
                .xAxisTitle(pTitleX)
                .yAxisTitle(pTitleY)
                .theme(Styler.ChartTheme.Matlab)
                .title(pChartTitle).build();

        final String series = pChartUpdater.addSeries(xyChart);
        final XChartPanel<XYChart> chartPanel = new XChartPanel(xyChart);
        mCharUpdaters.add(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                pChartUpdater.updateSeries(xyChart, series);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        chartPanel.revalidate();
                        chartPanel.repaint();
                    }
                });
                return null;
            }
        });

        displayChart(xyChart.getTitle(), chartPanel);
    }


    private JPanel initializeAnimationConfigPanel() {
        JPanel buttonPannel = new JPanel();
        buttonPannel.setLayout(new BoxLayout(buttonPannel, BoxLayout.Y_AXIS));

        JPanel delayConfigPanel = new JPanel();
        delayConfigPanel.add(new JLabel("Delay in Seconds: "));
        final JTextField delayConfigTextField = new JTextField("0", 10);
        delayConfigTextField.setEditable(true);
        delayConfigPanel.add(delayConfigTextField);

        JPanel stepsConfigPanel = new JPanel();
        stepsConfigPanel.add(new JLabel("Maximum number of steps: "));
        final JTextField maxStepsConfigField = new JTextField("100", 10);
        maxStepsConfigField.setEditable(true);
        stepsConfigPanel.add(maxStepsConfigField);

        JPanel buttonHolder = new JPanel();
        JButton singleStep = new JButton("SingleStep");
        JButton continuous = new JButton("Continuous");
        JButton stop = new JButton("Stop");

        singleStep.addActionListener(l -> {
            doSingleStepAnimation();
        });
        continuous.addActionListener(l -> {
            int delay = Integer.valueOf(delayConfigTextField.getText());
            int maxSteps = Integer.valueOf(maxStepsConfigField.getText());
            buttonHolder.setEnabled(false);
            doContinuousAnimation(maxSteps, delay);
            buttonHolder.setEnabled(true);
//            JOptionPane.showMessageDialog(ContinuousAnimationPanel.this, "Not implemented yet.");
        });
        stop.addActionListener(l -> {
            mContinuousRunning = false;
//            JOptionPane.showMessageDialog(ContinuousAnimationPanel.this, "Not implemented yet.");
        });

        buttonHolder.add(singleStep);
        buttonHolder.add(continuous);
        buttonHolder.add(stop);

        buttonPannel.add(delayConfigPanel);
        buttonPannel.add(stepsConfigPanel);
        buttonPannel.add(buttonHolder);

        buttonPannel.setBorder(BorderFactory.createTitledBorder("Animation config"));

        return buttonPannel;
    }

    private void doContinuousAnimation(final int pMaxSteps, final int pDelay) {
        int count = 0;
        
        
        // MODIFIED CODE TO REMOVE CONTINUOUS FIRING ERROR
        
        if(mDataLayer.unknown.isEmpty())
        {
        	JOptionPane.showMessageDialog(CreateGui.getApp (), "Transitions are missing or are all disabled. Please check your work.");
        	return;
        }
        
        // END MODIFIED CODE TO REMOVE CONTINUOUS FIRING ERROR
        
        if (mContinuousRunning == false) {
        	
            mContinuousRunning = true;
            while (count < pMaxSteps && mContinuousRunning) {
                doSingleStepAnimation();
                count++;
                if (pDelay > 0) {
                    try {
                        Thread.sleep(TimeUnit.SECONDS.toMillis(pDelay));
                    } catch (InterruptedException e) {
                        mContinuousRunning = false;
                        throw new RuntimeException(e);
                    }
                }
            }
            
            
        }

    }

    private void doSingleStepAnimation() {
        AnimationHistoryItem animationHistoryItem = CreateGui.getAnimationHistory(CreateGui.getTab().getSelectedIndex()).addNewItem(AnimationType.FixedStepAnimation, "");
        Transition fired = mAnimator.doHighLevelRandomFiring();
        boolean isContinuousTransitionFired = false;
        if (fired != null) {
            addFiredTransitionHistory(animationHistoryItem, fired);
            isContinuousTransitionFired = fired.isContinuous();
        }
        for (Transition transition : mDataLayer.continuous) {
            if (transition != fired) {
                if (transition.checkStatusAndFireWhenEnabled()) {
                    mAnimator.fireHighLevelTransitionInGUI(transition);
                    addFiredTransitionHistory(animationHistoryItem, transition);
                    isContinuousTransitionFired = true;

                    Iterator<Transition> iDepTrans = transition.getDependentTrans().iterator();
                    while (iDepTrans.hasNext()) {
                        Transition thisDepTrans = iDepTrans.next();
                        if (!thisDepTrans.isContinuous() && mDataLayer.disabled.remove(thisDepTrans)) {
                            mDataLayer.unknown.add(thisDepTrans);
                        }
                    }
                }
            }
        }

        if (isContinuousTransitionFired) {
            mCharUpdaters.forEach(f -> f.apply(null));
        }

        try(FileOutputStream fos = new FileOutputStream("continuous.txt", true)) {
            for (Place place : CreateGui.currentPNMLData().getPlaces()) {
                String token = "0";
                if (!place.token.getListToken().isEmpty()) {
                    token = place.token.getListToken().get(0).displayToken(false);
                }
                fos.write(String.format("%12s", token).getBytes());
            }
            fos.write(String.format("%n").getBytes());
            fos.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        GlobalClock.getInstance().increment();

    }

    private void addFiredTransitionHistory(final AnimationHistoryItem pHistoryItem, final Transition pTransition) {
        AnimationHistoryItem.TransitionItem item = new AnimationHistoryItem.TransitionItem(pTransition.getName(), 0L);
        pHistoryItem.addFiredTransition(item);
    }


    private interface ChartUpdater {
        String addSeries(final XYChart pChart);
        void updateSeries(final XYChart pChart, String series);
    }
}
