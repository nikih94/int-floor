import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.text.DecimalFormat;

public class Sensor extends Rectangle {
	int id;
	public double measurement;
	public double upper_bound;
	public double lower_bound;
	Color default_color;
	Text label;

	public Sensor(int id, Text text) {
		super(200, 200);
		default_color = Color.web("0x71e05e");
		this.setDisable(true);
		this.measurement = 0;
		this.id = id;
		label = text;
		this.setStyle("-fx-arc-height: 30; -fx-arc-width: 30;");
		label.setText(id + " : 0");
		this.setFill(default_color);
		this.setStroke(default_color);
	}

	public void applyReadings() {
		double previous = measurement;
		this.measurement = Main.sensor_data[0][id];

		DecimalFormat df = new DecimalFormat("#.##");
		label.setText(id + " : " + df.format(this.measurement));
		if (this.measurement > 0.01) {
			DropShadow shadow = new DropShadow();
			shadow.setColor(getColor(measurement));
			shadow.setRadius(1000);
			Color c = getColor(this.measurement);
			Timeline shadowAnimation = new Timeline(
					new KeyFrame(Duration.ZERO, new KeyValue(shadow.colorProperty(), getColor(previous))),
					new KeyFrame(Duration.millis(Main.frequncy),
							new KeyValue(shadow.colorProperty(), getColor(measurement))));

			this.setEffect(shadow);
			shadowAnimation.play();
			FillTransition ft = new FillTransition(Duration.millis(Main.frequncy), this, getColor(previous),
					getColor(measurement));
			ft.setCycleCount(1);
			ft.setAutoReverse(true);
			ft.play();
			this.setEffect(shadow);
		}else {
			DropShadow shadow = new DropShadow();
			shadow.setColor(default_color);
			shadow.setRadius(1000);
			this.setFill(default_color);
			this.setStroke(default_color);
			this.setEffect(shadow);
		}
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getMeasurement() {
		return measurement;
	}

	public void setMeasurement(int measurement) {
		this.measurement = measurement;
	}

	public Color getColor(double power) {
		power = 1 - power;
		double H = power * 0.2 * 360; // Hue (note 0.4 = Green)
		double S = 0.9; // Saturation
		double B = 0.9; // Brightness
		return Color.hsb(H, S, B, 1);
	}
}
