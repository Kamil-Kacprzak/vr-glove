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
BLECharacteristic fingersChar("2103", BLERead | BLENotify, 20);

BLEDescriptor     imuAccDescriptor("3101",(byte*)"Acc Descriptor",15);
BLEDescriptor     imuGyroDescriptor("3102",(byte*)"Gyr Descriptor",15);
BLEDescriptor     fingersDescriptor("3103",(byte*)"Fin Descriptor",15);


#include <Arduino_LSM9DS1.h>
/*
The library takes care of the sensor initialisation and sets its values as follows:

Accelerometer range is set at [-4,+4]g -/+0.122 mg
Gyroscope range is set at [-2000, +2000] dps +/-70 mdps
Magnetometer range is set at [-400, +400] uT +/-0.014 uT
Accelerometer Output data rate is fixed at 104 Hz
Gyroscope Output data rate is fixed at 104 Hz
Magnetometer Output data rate is fixed at 20 Hz
 */

float gyro[3] , acc[3];
float x[2], y[2], z[2];
float fingers[5]; //thumb,point,mid,heart,pinky;

float oldFingersData[5], oldGyroData[3], oldAccData[3];
float elapsedTime, currentTime, previousTime;

float gyroAngleX, gyroAngleY, gyroAngleZ;

void setup() {
  Serial.begin(9600);
  
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
  imuService.addCharacteristic(fingersChar);   
  
  imuAccChar.addDescriptor(imuAccDescriptor);
  imuGyroChar.addDescriptor(imuGyroDescriptor);
  fingersChar.addDescriptor(fingersDescriptor);
  
  BLE.addService(imuService);
  
  imuAccChar.writeValue((byte)0x01); 
  imuGyroChar.writeValue((byte)0x01);
  fingersChar.writeValue((byte)0x01);
  
  BLE.advertise();
  
  Serial.println("Setup accomplished"); 
}

void loop() {
  /*
   * At the moment (05.2020) there is a bug in nano 33 ble that regards disconnecting from central. Sometimes after
   * doing so board can act as in it's still connected or it doesn't remove connection which will not allow for new 
   * connection to happen
   */
  BLEDevice central = BLE.central();
  if (central) {
    Serial.print("Connected to central: ");
    Serial.println(central.address());
  }  
    
  while( central.connected()){    
    if (IMU.accelerationAvailable() && IMU.gyroscopeAvailable()) {
        //Read from IMU
        previousTime = currentTime;
        currentTime = millis();
        elapsedTime = (currentTime - previousTime) / 1000;   

        IMU.readGyroscope(gyro[0],gyro[1], gyro[2]);
        //Remove approx. error from gyroscope
        gyro[0] -= 2.80;
        gyro[1] -= 0.18;
        gyro[2] -= 0.18;
        
        ///Calibration is not needed since it is working without bias
        IMU.readAcceleration(acc[0],acc[1], acc[2]);  
         
        //Read flex sesnroes - thumb,point,mid,heart,pinky;
        fingers[0] = analogRead(A3);
        fingers[1] = analogRead(A0);
        fingers[2] = analogRead(A1);
        fingers[3] = analogRead(A6);
        fingers[4] = analogRead(A7);

         for (int i = 0;i<3;i++){
            //convert gyro degree/s to degrees by multiplying through sample rate
            gyro[i] *= elapsedTime;
            
            //Take data above noise, otherwise set old value or 0 for minor values
            if(!(gyro[i] < oldGyroData[i]-0.2 || gyro[i] > oldGyroData[i]+0.2)){
              if( gyro[i] < -0.1 ||  gyro[i] > 0.1){
                gyro[i] = oldGyroData[i];
              }else{
                 gyro[i] = 0.0;
              }
            }
            
            if(!(acc[i] < oldAccData[i]-0.02 || acc[i] > oldAccData[i]+0.02)){
              acc[i] = oldAccData[i];
            }
         }
         for (int i = 0;i<5;i++){
            if(!(fingers[i] < oldFingersData[i]-15.0 || fingers[i] > oldFingersData[i]+15.0)){
              fingers[i] = oldFingersData[i];
            }
         }         
         
         //Convert arrays into pointers to pass them through BLE characteristics
         byte *accChar = (byte*)&acc;
         byte *gyroChar = (byte*)&gyro;
         byte *flexChar = (byte*)&fingers;
         
         imuAccChar.setValue(accChar,12);
         imuGyroChar.setValue(gyroChar,12);
         fingersChar.setValue(flexChar,20);

         //Print data to serial monitor        
         Serial.println("Gyroscope   x    y    z\t\tAccelometer x    y    z\tthumb\tpoint\tmid\theart\tpinky\t");
         Serial.print("\t"+ String(gyro[0]) + "\t" + String(gyro[1]) + "\t" + String(gyro[2]));               
         Serial.print("\t"+ String(acc[0]) + "\t" + String(acc[1]) + "\t" + String(acc[2]));

         for (int i = 0;i<5;i++){
            Serial.print("\t");               
            Serial.print(fingers[i]);
            oldFingersData[i] = fingers[i];
         }
         for (int i = 0;i<3;i++){
            oldGyroData[i] = gyro[i];
            oldAccData[i]  = acc[i];
         }
         Serial.println(""); 
    }        
  }
   central.disconnect();
}
