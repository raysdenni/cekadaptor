#include <Wire.h>
#include <Adafruit_SSD1306.h>
#include <Adafruit_INA219.h>
#include <Adafruit_GFX.h>
#include <Image\dennisplash.h> //include untuk splash image custom

#define SCREEN_WIDTH 128 // lebar OLED dalam pixel (x)
#define SCREEN_HEIGHT 64 // tinggi OLED dalam pixel (y)
#define OLED_RESET -1
const int Alamat_INA = 0x40; //alamat I2C INA219
const int Alamat_SSD1306 = 0x3C; //alamat I2C LCD Oled 0.96" 128x64

Adafruit_SSD1306 lcd(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);
Adafruit_INA219 ina219(Alamat_INA);

//Deklarasi Variable untuk menampung nilai
float tegangan;
float arus;
float daya;
char message[]="DATABASE DISCONNECTED!"; //untuk sebagai dummy status database
int x, minX; //untuk posisi cursor scroll status database

void setup()   {
  Serial.begin(9600);
  ina219.begin();
  lcd.begin(SSD1306_SWITCHCAPVCC, Alamat_SSD1306);
  lcd.clearDisplay();
  lcd.drawBitmap(0, 0, dennisplashsplash, 128, 64, WHITE); //memanggil fungsi custom splash
  lcd.display();
  delay(5000);
  lcd.setTextColor(WHITE);
  lcd.setTextWrap(false);
  x = lcd.width(); //panjang x yang akan discroll sepanjang lebar lcd
  minX = -6 * strlen(message);  // panjang karakter * ukuran fontnya
}

void loop() {
  bacanilaisensor();
  tampilkandilcd();
  
  //Membaca permintaan dari nodeMCU
  String minta = "";
  while(Serial.available()>0){
    minta += char(Serial.read());
  }

  //membuang spasi data yang diterima dari NodeMCU
  minta.trim();
  
  //Menguji variable minta
  if(minta == "y"){
    //lakukan perintah kirim data
    kirimdata();
    strcpy(message, "DATABASE CONNECTED!");
  }
  
  //mengosongkan variable minta
  minta = "";
  delay(100);
}

void bacanilaisensor(){
  //baca data dari modul ina219
  tegangan = ina219.getBusVoltage_V(); //baca tegangan
  arus = ina219.getCurrent_mA(); //baca arus
  daya = tegangan * (arus / 1000); //rumus mendapatkan nilai watt

}

void kirimdata(){
  //variable untuk menampung data sensor INA219 yang akan dikirim ke NodeMCU
  String datasensor = String(tegangan) + "#" + String(arus) + "#" + String(daya);

  //fungsi kirim data ke NodeMCU
  Serial.println(datasensor);
}

void tampilkandilcd(){
  //membersihkan layar
  lcd.clearDisplay();
  
  //text display di LCD untuk header
  lcd.setTextSize(2);
  lcd.setCursor(0, 0);
  lcd.print("CEK");
  lcd.setTextSize(1);
  lcd.setCursor(39, 0);
  lcd.print(" ");
  lcd.setTextSize(2);
  lcd.setCursor(41, 0);
  lcd.print("ADAPTOR");

  //cek dan tampilkan status koneksi database di LCD
  lcd.setTextSize(1);
  lcd.setCursor(x, 22);
  lcd.print(message);

  //tampilkan tegangan di LCD
  lcd.setCursor(3, 31);
  lcd.print("Tegangan :");
  lcd.print(tegangan);
  lcd.setCursor(103, 31);
  lcd.print("Volt");
  
  //tampilkan arus di LCD
  lcd.setCursor(3, 41);
  lcd.print("Arus     :");
  lcd.print(arus);
  lcd.setCursor(103, 41);
  lcd.print("mAmp");

  //tampilkan daya di LCD
  lcd.setCursor(3, 51);
  lcd.print("Daya     :");
  lcd.print(daya);
  lcd.setCursor(103, 51);
  lcd.print("Watt");

  //tampilkan border untuk isinya
  lcd.drawRect(0, 19, 128, 42, WHITE);
  lcd.display();

   x=x-4; // scroll speed
   if(x < minX) x= lcd.width();
}
