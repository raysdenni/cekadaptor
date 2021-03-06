#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <SoftwareSerial.h>

//konfigurasi firebase
#define FIREBASE_HOST "adaptorcheckeriot-default-rtdb.asia-southeast1.firebasedatabase.app"
#define FIREBASE_AUTH "r2Yd7srXZn1rSDlyiEvgWPBRsi7WDzanrryH6fPZ"

//konfigurasi WiFi
#define WIFI_SSID "diot.asia"
#define WIFI_PASS "12345678"

//LED untuk tanda terkoneksi wifi
#define PIN_LED 5

//variable untuk Software Serial (RX,TX)
SoftwareSerial DataSerial(12, 13); //Inisialisasi PIN yang digunakan, disini menggunakan PIN D6=12 dan D7=13

//Variable millis, menggunakan millis sebagai pengganti delay
unsigned long previousMillis = 0;
const long interval = 2000;

//variable array untuk data parsing
String arrData[3];

void setup() {
  Serial.begin(9600);
  DataSerial.begin(9600);

  //koneksi ke wifi
  WiFi.begin(WIFI_SSID, WIFI_PASS);
  Serial.print("Connecting...");
  //selama tidak terkoneksi
  while(WiFi.status() != WL_CONNECTED){
    Serial.print(".");
    delay(500);
  }
  //bila terkoneksi wifi
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());

  //koneksi ke firebase
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

  //setting mode LED
  pinMode(PIN_LED, OUTPUT);
}

void loop() {
  //konfigurasi millis
  unsigned long currentMillis = millis(); //baca nilai millis saat ini
  if(currentMillis - previousMillis >= interval){
    //perbaharui previousMillis
    previousMillis = currentMillis;

    //baca data kiriman dari Arduino Uno
    //baca data serial
    String data = "";
    while(DataSerial.available()>0){
      data += char(DataSerial.read());
    }

    //membuang spasi data yang diterima dari ArduinoUno
    data.trim();

    //uji data
    if(data != ""){
      //parsing data
      int index = 0;
      for(int i=0; i<= data.length(); i++){
        char pemisah = '#';
        if(data[i] != pemisah)
          arrData[index] += data[i];
        else
          index++; //variable index akan bertambah
      }

      //memastikan data yang diterima dari ArduinoUno lengkap (tegangan, arus, daya)
      //urutan array nya 0=tegangan, 1=arus, 2=daya
      if(index == 2){
        //tampilkan nilai sensor dari firebase ke serial monitor
//        Serial.println("Tegangan : " + arrData[0] + " Volt"); //nilai tegangan
//        Serial.println("Arus     : " + arrData[1] + " mAmp"); //nilai arus
//        Serial.println("Daya     : " + arrData[2] + " Watt"); //nilai daya
        kirimfirebase();
        Serial.println();
      }

      arrData[0] = "";
      arrData[1] = "";
      arrData[2] = "";
    }
    //minta data ke ArduinoUno
    DataSerial.println("y");
  }
}

void kirimfirebase(){
  //kirim nilai sensor dari arduino
  Firebase.setString("voltase", arrData[0]);
  Firebase.setString("ampere", arrData[1]);
  Firebase.setString("daya", arrData[2]);
  
  //handle
  if(Firebase.failed()){
    Serial.println("Gagal kirim data");
    Serial.println(Firebase.error());
    digitalWrite(PIN_LED, LOW);
    return;
  }
  //apabila berhasil kirim data
  digitalWrite(PIN_LED, HIGH);

  //ambil nilai dari firebase
  //Serial.println("Tegangan : " + Firebase.getString("voltase") + " Volt");
  //Serial.println("Arus     : " + Firebase.getString("ampere") + " mAmp");
  //Serial.println("Daya     : " + Firebase.getString("daya") + " Watt");
}
