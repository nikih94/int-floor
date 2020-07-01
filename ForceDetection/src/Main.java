import com.fazecast.jSerialComm.SerialPort;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class Main extends Application {
	public static int num_plates_x = 2;
	public static int num_plates_y = 2;
	public static int num_sensors_per_plate = 4;
	public static int port_id;
	public static Plate[][] plates;
	public static int frequncy = 200;
	public static int id = 0;
	public static float sensor_data[][] = new float[3][16];

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Force detection");
		SplitPane split_pane = new SplitPane();
		split_pane.setPrefSize(1920, 1080);
		ImageView innorenew_logo = null;
		ImageView eu_logo = null;
		Image innorenew = new Image(getClass().getResourceAsStream("/logo_color.png"));
		innorenew_logo = new ImageView(innorenew);
		innorenew_logo.setFitHeight(250);
		innorenew_logo.setFitWidth(250);

		Image eu = new Image(getClass().getResourceAsStream("EU.png"));
		eu_logo = new ImageView(eu);
		eu_logo.setFitHeight(250);
		ProgressIndicator pi = new ProgressIndicator(0);
		pi.getStyleClass().add("progress");
		pi.setPrefSize(100, 100);
		pi.setVisible(false);
		Button calibrate = new Button("Calibrate sensors");
		calibrate.getStyleClass().add("calibrate");
		DataLogger dl = new DataLogger();
		dl.openSerialPort();
		dl.start();
		calibrate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				pi.setVisible(true);
				pi.setProgress(0);
				calibrate.setDisable(true);
				Timeline fiveSecondsCalibrate = new Timeline(
						new KeyFrame(Duration.millis(500), new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								pi.setProgress(pi.getProgress() + 0.1);
							}
						}));
				fiveSecondsCalibrate.setCycleCount(10);
				fiveSecondsCalibrate.play();
				fiveSecondsCalibrate.setOnFinished(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						System.out.println("Callibration ended");
						calibrate.setDisable(false);
						pi.setVisible(false);
					}
				});
			}
		});

		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(25, 25, 25, 25));
		gridPane.setVgap(num_plates_x);
		gridPane.setHgap(num_plates_y);
		plates = new Plate[num_plates_x][num_plates_y];
		gridPane.setAlignment(Pos.CENTER);
		ArrayList<Plate> chart_plates = new ArrayList<>();
		int k = 0;
		for (int i = 0; i < num_plates_x; i++) {
			for (int j = 0; j < num_plates_y; j++) {
				Plate p = new Plate(num_sensors_per_plate, k);
				k++;
				p.getStyleClass().add("plate");
				plates[i][j] = p;
				chart_plates.add(p);
				gridPane.add(p, j, i);
			}
		}
		// ugly hack

		RightMenu right_menu = new RightMenu(16, chart_plates);
		VBox vbox = new VBox(2);
		HBox hbox = new HBox(2);
		hbox.getChildren().addAll(innorenew_logo, eu_logo);
		hbox.setAlignment(Pos.CENTER);
		HBox.setHgrow(innorenew_logo, Priority.ALWAYS);
		HBox.setHgrow(eu_logo, Priority.ALWAYS);
		vbox.getChildren().addAll(right_menu, hbox);
		split_pane.getItems().addAll(gridPane, vbox);
		// StackPane.setAlignment(gridPane, Pos.CENTER);
		Scene grid_scene = new Scene(split_pane);
		grid_scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
		primaryStage.setScene(grid_scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Please supply the serial port id as first parameter:");
			SerialPort available_ports[] = SerialPort.getCommPorts();

			for (int i = 0; i < available_ports.length; i++) {
				System.out.println(i + " : " + available_ports[i]);
			}
			//return;
		} else {
			port_id = Integer.parseInt(args[0]);
		}
		Timeline fiveSecondsWonder = new Timeline(
				new KeyFrame(Duration.millis(frequncy), new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						for (int i = 0; i < plates.length; i++) {
							for (int j = 0; j < plates[i].length; j++) {
								plates[i][j].update();
							}
						}
					}
				}));
		fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
		fiveSecondsWonder.play();
		launch(args);
	}
}
