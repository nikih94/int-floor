
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class Plate extends GridPane {
	public Sensor[][] sensors;
	int num_sensors;

	public Plate(int num_sensors, int id) {
		
		this.num_sensors = num_sensors;
		sensors = new Sensor[num_sensors / 2][num_sensors / 2];
		this.setVgap(num_sensors / 2);
		this.setHgap(num_sensors / 2);
		this.setPadding(new Insets(20, 20, 20, 20));
		int k=0;
		for (int i = 0; i < num_sensors / 2; i++) {
			for (int j = 0; j < num_sensors / 2; j++) {
				StackPane p = new StackPane();
				Text l = new Text();
				Sensor s = new Sensor(Main.id, l);
				p.getChildren().addAll(s,l);
				Main.id++;
				sensors[i][j] = s;
				s.getStyleClass().add("sensor");
				this.add(p, j, i);
			}
		}
		
		if(id ==0) {
			sensors[0][0].id = 15;
			sensors[0][1].id = 14;
			sensors[1][0].id = 11;
			sensors[1][1].id = 10;
		}
		if(id==1) {
			sensors[0][0].id = 13;
			sensors[0][1].id = 12;
			sensors[1][0].id = 9;
			sensors[1][1].id = 8;
		}
		if(id==2) {
			sensors[0][0].id = 7;
			sensors[0][1].id = 6;
			sensors[1][0].id = 3;
			sensors[1][1].id = 2;
		}
		if(id==3) {
			sensors[0][0].id = 5;
			sensors[0][1].id = 4;
			sensors[1][0].id = 1;
			sensors[1][1].id = 0;
		
		}
	}

	public void update() {
		for (int i = 0; i < num_sensors / 2; i++) {
			for (int j = 0; j < num_sensors / 2; j++) {
				sensors[i][j].applyReadings();
			}
		}
	}
}
