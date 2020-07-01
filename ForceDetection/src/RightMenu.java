import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class RightMenu extends GridPane {

	public RightMenu(int num_sensors, ArrayList<Plate> plates) {
		int num_rows = num_sensors / 4;
		setVgap(5);// one row is minumum for calibration
		setHgap(4);
		setPadding(new Insets(25, 25, 25, 25));
		ArrayList<SensorChart> sensor_charts = new ArrayList<>();

		for (Plate p : plates) {
			for (int i = 0; i < p.sensors.length; i++) {
				for (int j = 0; j < p.sensors[i].length; j++) {
					sensor_charts.add(new SensorChart(p.sensors[i][j]));
				}
			}
		}
		int row = 0;
		int column = 0;
		for (int i = 0; i < sensor_charts.size(); i++) {
			row = 1 + i % 4;
			this.add(sensor_charts.get(i), row, column);
			if (row % 4 == 0) {
				column++;
			}
		}

		Timeline timer = new Timeline(new KeyFrame(Duration.millis(Main.frequncy), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				for (SensorChart sensorChart : sensor_charts) {
					try {
						if (sensorChart.series.getData().size() > sensorChart.MAX_DATAPOINTS) {
							sensorChart.series.getData().remove(0);
							sensorChart.xAxis.setLowerBound(sensorChart.lcy - sensorChart.MAX_DATAPOINTS);
							sensorChart.xAxis.setUpperBound(sensorChart.lcy - 1);
						}
						XYChart.Data<Number, Number> data = new LineChart.Data<>(sensorChart.lcy,
								sensorChart.sensor.getMeasurement());
						sensorChart.series.getData().add(data);
						sensorChart.lc.getData().clear();
						sensorChart.lc.getData().add(sensorChart.series);
						sensorChart.lcy++;
					} catch (Exception e) {
						e.printStackTrace();
					}
					for (XYChart.Data<Number, Number> data : sensorChart.series.getData()) {
						// this node is StackPane
						StackPane stackPane = (StackPane) data.getNode();
						stackPane.setVisible(false);
					}

				}
			}
		}));
		timer.setCycleCount(Timeline.INDEFINITE);
		timer.play();
	}
}
