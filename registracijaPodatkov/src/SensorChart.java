
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;

public class SensorChart extends Pane {
	Sensor sensor;
	LineChart<Number, Number> lc;
	XYChart.Series<Number, Number> series;
	NumberAxis xAxis;
	int lcy = 0;
	int MAX_DATAPOINTS = 20;

	public SensorChart(Sensor sensor) {
		this.sensor = sensor;
		series = new LineChart.Series<>();
		xAxis = new NumberAxis(0, MAX_DATAPOINTS, MAX_DATAPOINTS / 2);
		xAxis.setForceZeroInRange(false);
		xAxis.setAutoRanging(false);
		xAxis.setTickLabelsVisible(false);
		xAxis.setTickMarkVisible(false);
		xAxis.setMinorTickVisible(false);

		lc = createLineChart(xAxis);
		lc.setAnimated(false);
		this.getChildren().add(lc);
	}
	private LineChart<Number, Number> createLineChart(NumberAxis x) {
		// defining the axes
		NumberAxis yAxis = new NumberAxis();
		yAxis.setUpperBound(1);
		yAxis.setAutoRanging(false);
		xAxis.setLabel("Tick");
		// creating the chart
		LineChart<Number, Number> lineChart = new LineChart<>(x, yAxis);
		lineChart.setPrefSize(200, 80);

		lineChart.setTitle("Sensor " + sensor.id);
		// defining a series
		XYChart.Series<Number, Number> series = new LineChart.Series<>();
		series.setName("Tick");
		lineChart.getData().add(series);
		
		return lineChart;
	}
}
