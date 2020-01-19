/*
 * Author:      Kamil Kacprzak
 * Date:        14.01.2020
 * Description: Following script will read data from analog inputs and IMU sensor and pass the data 
 *              to Android App via bluetooth module.
 */
#include <ArduinoBLE.h>
BLEService imuService("1101");
BLECharacteristic imuAccChar("2101", BLERead | BLENotify,12);
BLECharacteristic imuGyroChar("2102", BLERead | BLENotify, 12);
#include <Arduino_LSM9DS1.h>
/*
 * The library takes care of the sensor initialisation and sets its values as follows:

Accelerometer range is set at [-4,+4]g -/+0.122 mg
Gyroscope range is set at [-2000, +2000] dps +/-70 mdps
Magnetometer range is set at [-400, +400] uT +/-0.014 uT
Accelerometer Output data rate is fixed at 104 Hz
Gyroscope Output data rate is fixed at 104 Hz
Magnetometer Output data rate is fixed at 20 Hz
 */


void setup() {
  Serial.begin(9600);
  while(!Serial);
  
  if (!IMU.begin()) {
    Serial.println("Failed to initialize IMU!");
    while (1);
  }

  if (!BLE.begin()) {
    Serial.println("Failed to start Bluetooth!");
    while (1);
  }
  
  BLE.setLocalName("VrGlove");
  
  BLE.setAdvertisedService(imuService);
  imuService.addCharacteristic(imuAccChar);
  imuService.addCharacteristic(imuGyroChar);
  BLE.addService(imuService);
  imuAccChar.writeValue((byte)0x01); 
  imuGyroChar.writeValue((byte)0x01);
  BLE.advertise();
  Serial.println("Setup accomplished"); 
}

void loop() {
  BLEDevice central = BLE.central();
  if (central) {
    Serial.print("Connected to central: ");
    Serial.println(central.address());
  }

  //If available read gyroscope and accelometer data
  float gyro[3] , acc[3];
  float x[2], y[2], z[2];
  
  while( central.connected()){    
    if (IMU.accelerationAvailable() && IMU.gyroscopeAvailable()) {
         //Initialize tables 
         for (int i = 0;i<3;i++){
          gyro[i] = 0.0;
          acc[i]  = 0.0;
          }
         
         // Take the average of 10 readings - removes natural body shaking
         for(int i = 0; i<10; i++) {
            IMU.readGyroscope(x[0], y[0], z[0]);
            IMU.readAcceleration(x[1], y[1], z[1]);  
            
            gyro[0] +=x[0];        
            gyro[1] +=y[0];
            gyro[2] +=z[0];
  
            acc[0] +=x[1];        
            acc[1] +=y[1];
            acc[2] +=z[1];
         }
         for (int i = 0;i<3;i++){
            gyro[i] = gyro[i]/10;
            acc[i]  = acc[i]/10;
         }
         
         //Convert arrays into pointers to pass them through BLE characteristics
         byte *accChar = (byte*)&acc;
         byte *gyroChar = (byte*)&gyro;
         imuAccChar.setValue(accChar,12);
         imuGyroChar.setValue(gyroChar,12);
         
         //Print data to serial monitor
         Serial.print("Gyroscope   x    y    z\t\t\t\t\t\t");
         Serial.println("Accelometer x    y    z");
         Serial.print("\t"+ String(gyro[0]) + "\t" + String(gyro[1]) + "\t" + String(gyro[2]));               
         Serial.println("\t\t\t\t\t"+ String(acc[0]) + "\t" + String(acc[1]) + "\t" + String(acc[2]));
         
         // TODO: Delete this part
         delay(5000);
    }       
  }
}
