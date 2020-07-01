
static const uint8_t analog_pins[] = {A0,A1,A2,A3,A4,A5,A6,A7,A8,A9,A10,A11,A12,A13,A14,A15};
int sensors[16];

// Measure the voltage at 5V and resistance of your 3.3k resistor, and enter
// their value's below:
const float VCC = 4.98; // Measured voltage of Ardunio 5V line
const float R_DIV = 3230.0; // Measured resistance of 3.3k resistor

void setup() 
{
  Serial.begin(9600);
  for (int i = 0; i < 16; i++) {
    pinMode(analog_pins[i], INPUT);
  }
  
}

void loop() 
{
  for (int i = 0; i < 16; i++) {
    int fsrADC = analogRead(analog_pins[i]);
    if(fsrADC>0){
    // If the FSR has no pressure, the resistance will be
    // near infinite. So the voltage should be near 0.
  
      // Use ADC reading to calculate voltage:
      float fsrV = fsrADC * VCC / 1023.0;
      // Use voltage and static resistor value to 
      // calculate FSR resistance:
      float fsrR = R_DIV * (VCC / fsrV - 1.0);
      // Guesstimate force based on slopes in figure 3 of
      // FSR datasheet:
      float force;
      float fsrG = 1.0 / fsrR; // Calculate conductance
      // Break parabolic curve down into two linear slopes:
      if (fsrR <= 600) 
        force = (fsrG - 0.00075) / 0.00000032639;  
      else{
        force =  fsrG / 0.000000642857;
      }
      sensors[i] = (int)force;
      // Serial.println(String(i) + " : " +String((int)force));
       //Serial.println();
    }else{
      sensors[i] =0;
    }
  }
  String out ="";
  for (int i =0; i<16;i++){
    out += String((int)sensors[i]) + ",";
  }
  Serial.println(out);
      delay(200);
}

