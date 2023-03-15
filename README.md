# Autonomous tangible microrobots for app based (board) games

[Allgemein](#allgemein) <br>
[Tests](#tests) <br>
[Architekturüberlegung](#architekturüberlegung) <br>
[Implementierung](#implementierung) <br>
[Löten](#löten) <br>
[Steuerung](#steuerung) <br>
[Modellierung](#modellierung) <br>
[Display](#display) <br>
[Kamera](#kamera) <br>
[Nachrichten](#nachrichten) <br>
[App](#app) <br>
[Aruco Generation](#arucogeneration) <br>
[Ausblick](#ausblick) <br>

<a name="allgemein"></a>
**Roboter:**
besteht aus: 
- ESP32(Wifi und Bluetooth) mit Kamera
- OLED Display
- H Brücke
- 2 Motoren N20 (2-4V 145RPM)
- Akku 3,7V 800mAh lipo
- 4 3D gedruckten Rädern
- 3D gedrucktes Getriebe
- 3D gedrucktes Gehäuse

**Gearbeitet wurde mit:**
- Für die **App**: Android Studio -> **Java**
- Für den **Roboter**: **PlatformIO** -> Arduino -> CLion -> **C++**
- Für die **Bildverarbeitung**: Computervision Bibliothek für ArUco Marker -> **OpenCV**
- Für das **Spielfeld**: um ein ArUco Board zu generieren -> OpenCV und **Python**
- Für die **Konstruktion** des Roboters: um die 3D Teile für den Roboter zu drucken -> **Fusion360** und **3D Drucker**

![image of robot](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/PXL_20210516_085552411.jpg)

App Icon (ganz oben links sieht man den Icon der App):
<img src="https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/Screenshot_20210606-151528.jpg" alt="image of app icon" width="200">

<a name="tests"></a>
## Erste Tests des ESP32

Nach Ankunft des **ESP32** mit Kamera testeten wir ihn, indem wir in **CLion** das Plugin für **PlatformIO** installierten. Um in CLion mit **Arduino** und dem esp32cam board arbeiten zu können, muss es unter  AI Thinker ausgewählt werden.

Das erste kleine Testprojekt besteht aus drei Teilen.
1. Ausgabe von "Hello" auf der Commandozeile
2. Anzeigen der verfügbaren WLAN Netzwerke
3. Möglichkeit des verbindens mit Bluetooth

![esp32test code sample](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/esp32test.png)

<a name="architekturüberlegung"></a>
## Architekturüberlegung

Sehr abstrakt gesehen:

![architecturAbstrakt](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/architectureAbstract.png)

Lose Kopplung -> hoher innerer Zusammenhalt und trennen der Zuständigkeiten

alles spezielle wegkapseln, damit es leicht austauschbar ist!

Erste Überlegungen zur "genauen" Architektur:

![softwareProject](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/softwareProject.png)

Auf der Seite der App war die Architekturüberlegung die im Diagramm zu sehen ist noch nicht genau genug und wurde deshalb nicht komplett übernommen. Auf Seite des Roboters hat es bis zum Ende hin gepasst und die Architektur vom Anfang wurde übernommen.

Für die Klasse dispatcher auf dem Roboter:

| Komponente | Feld 0 | Feld 1 |
| ------ | ------ | ------ |
| Format | Header 1 Byte Adresse für HAL Komponenten | Nachricht |
| Kamera | '0' | ‘0‘ für senden der graustufen Bilder ‘1‘ für stop |
| Motor1 | '1' | message[1]'1' oder '0' für Vorwärts oder Rückwärts, oder '2' für stop, als String, alles andere um nichts zu tun |
| Motor2 | '1' | message[2]'1' oder '0' für Vorwärts oder Rückwärts, oder '2' für stop, als String, alles andere um nichts zu tun |
| Display | '3' | Text als String |


<a name="implementierung"></a>
## Implementierung für den Roboter

Es wurde sich als Konvention für "snake case" entschieden. Also alles wird mit Unterstrichen (_) getrennt.

Die Klassen sind, wie unter [Architekturüberlegung](#architekturüberlegung) festgehalten, eingeteilt. Zu jeder .cpp File gibt es eine passende .h File. Die Header liegen alle im "include" Ordner. In diesem befindet sich der Unterordner "hal", dies steht für HardwareAbstraktionsLayer. Die Source Files liegen alle im "src" Ordner und auch hier gibt es den Unterordner "hal".
"i" am Anfang eines Klassennamen deutet auf ein Interface hin.

Struktur:
![struktur](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/struktur.png)

1. Zuerst wurde die WLAN und Bluetooth Funktionalität aus den ersten Tests des ESPs hier in die communication eingebunden.
2. Als nächstes wurde start und stop für den Motor implementiert. Siehe unter [Steuerung](#steuerung)
3. Dann wurde der dispatcher für das senden und empfangen von Nachrichten implementiert.
4. Implementierung für das Display zum Anzeigen von Nachrichten. Siehe unter [Display](#display)
5. Die Kamera kann nun ein Foto aufnehmen. Siehe unter [Kamera](#kamera)
6. Nachrichten über BLE (Bluetooth Low Energy) senden. Problem: wir bekommen mit einem normalen Netzteil 4,7 V und das reicht nicht. Also der Brownout detector springt an, da es einen Spannungsabfall gibt. Lösung: Kondensator reicht nicht, also stärkeres Netzteil. Selbst 9,6 V haben nicht funktioniert. Also haben wir einfach einen neuen ESP32 benutzt. Es ging sofort, nur mit dem Strom des Laptops ohne Probleme. Siehe unter [Nachrichten](#nachrichten)
![Bildschirmfoto_2021-04-13_um_08.47.06](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/Bildschirmfoto_2021-04-13_um_08.47.06.png)

7. über BLE ein Bild vom ESP32 an die inzwischen selbst geschriebene App schicken. Problem: Bild ist 9,216KB groß, ESP sendet nur bis 0,6KB. Lösung: Paket in Packages aufteilen und rüberschicken.
8. das Bild enthält einen Marker der mittels OpenCV erkannt wird. So weiß der Roboter wo er ist und wo er hin muss.



<a name="löten"></a>
## Löten von Motoren und H Brücke

Zuerst braucht man den Lötkolben, eine dritte Hand, Lötzinn, Kabel, Motoren und die H-Brücke.
<img src="https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/PXL_20210404_084705929.jpg" width="300">


Dann nimmt man sich zwei Motoren und 2 Kabel (female auf beiden Seiten).
<img src="https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/PXL_20210404_085400757.jpg" width="300">

Nun trennt man beide Kabel genau in der Mitte und entfernt die Isolierung vorsichtig.
<img src="https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/PXL_20210404_091227462.jpg" width="300">

Wir haben uns für rot als Plus- und schwarz als Minus-Kabel entschieden. Die Kabel können nun an die Motoren gelötet werden.
<img src="https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/PXL_20210404_091640709.jpg" width="300">

Man kann den Motor nun mit rot an 3,3V und mit schwarz an G für Ground des ESP stecken. Dies haben wir zum testen gemacht. Wenn man den ESP nun mit der Steckdose verbindet, läuft der Motor.
<img src="https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/PXL_20210404_091820984.jpg" width="300">

Dann trennt man immer zwei Kontaktstifte von der Stiftleiste ab. 5 Paare werden für eine H-Brücke gebraucht.
<img src="https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/PXL_20210404_092322977.jpg" width="300">

Diese lötet man nun fest.
<img src="https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/PXL_20210404_092836325.jpg" width="300">

Hier sind alle festgelötet.
<img src="https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/PXL_20210404_093255988.jpg" width="300">

Man kann die Motoren nun an Motor A und Motor B der H-Brücke befestigen.
<img src="https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/PXL_20210404_094103377.jpg" width="300">


<a name="steuerung"></a>
## Motoren ansteuern

![ESP32-CAM-pinout-guide-gpios-pins-explained](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/ESP32-CAM-pinout-guide-gpios-pins-explained.jpg)

Um zu gucken, welche Pins des ESP32 auf welche in Arduino gemappt sind.

https://randomnerdtutorials.com/esp32-cam-ai-thinker-pinout/

Aufbau zum Testen:

![PXL_20210405_132735670](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/PXL_20210405_132735670.jpg)

Die beiden Motoren werden an die H-Brücke angeschlossen.
Die H-Brücke wird mit Motor A an IO13 und IO12 angeschlossen. Motor B wird an IO4 und IO2 angeschlossen. Plus der H-Brücke wird an 3,3V und Minus der H-Brücke an GRN (Ground).
5V des ESP32 wird an 5V des Adapters für den Strom angeschlossen und GND (Ground) des ESP32 wird an GND (Ground) des Adapters für den Strom angeschlossen.

Zusätzlich habe ich noch IO0, UOR, UOT, GND/R an die jeweiligen Pins des Adapter angeschlossen, um den Code uploaden zu können, ohne des ESP32 von der Steckplatte nehmen zu müssen.

IO4 musste gegen IO14 ersetzt werden, da die LED an IO4 hängt und somit immer an geht, wenn man den Motor anspricht. Da wir nun PVM für die Motoren benutzen, um die Geschwindigkeit zu steuern, flackert die LED, so können die Marker nicht mehr erkannt werden. Deswegen musste der Pin gewechselt werden, das bedeutet aber auch, das nicht genug freie Pins für das Display vorhanden ist und dies wegfallen muss.


<a name="modellierung"></a>
## Modellierung in Fusion360

Der Roboter wird in Fusion360 modelliert, um die Basis und die Räder 3D Drucken zu können.

![Bildschirmfoto_2021-04-20_um_11.05.00](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/Bildschirmfoto_2021-04-20_um_11.05.00.png)

![Bildschirmfoto_2021-04-20_um_11.05.11](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/Bildschirmfoto_2021-04-20_um_11.05.11.png)

Hier sieht man erste Prototypen.

![Bildschirmfoto_2021-04-20_um_11.04.31](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/Bildschirmfoto_2021-04-20_um_11.04.31.png)

Dies ist der erste Testdruck für die Halterung des ESP32 und der Motoren, die rechts und links daneben kommen.

![PXL_20210420_090033723](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/PXL_20210420_090033723.jpg)

Hier der erste fertige Testdruck. Die Toleranzen werden nun weiter überarbeitet, bis es perfekt passt.

![PXL_20210420_094524118](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/PXL_20210420_094524118.jpg)

Der Wendepunkt liegt genau in der Mitte, damit ist der **Roboter** im Gegensatz zu einem Auto **Holonom** und kann sich **auf der Stelle drehen**, indem ein Motor vorwärts, der andere rückwärts fährt.

![Bildschirmfoto_2021-04-20_um_12.41.43](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/Bildschirmfoto_2021-04-20_um_12.41.43.png)

Hier sind hinter den Motoren nun Wände, zwischen diesen müssen die Kabel langgehen und in den Wänden ist Platz für eine M2,5 4Kant Mutter. In die kommt eine Schraube M2,5 x 12, an diese soll dann das Rad.

![PXL_20210427_142530724](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/PXL_20210427_142530724.jpg)

Hier sind die zwei Motoren und der Esp32 cam verbaut. Es fehlt also oben noch der Akku, das Display und die H-Brücke muss auch noch drauf.

![PXL_20210503_154659117](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/PXL_20210503_154659117.jpg)
![PXL_20210503_154702976](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/PXL_20210503_154702976.jpg)
Auf dem folgenden Bild sieht man den Robo von unten. Hier sieht man die Kamera, die die ArUco Marker auf den Feldern erkennt. Wir benutzen sie, damit wir wissen, auf welchem Feld wir stehen (Marker ID -> x und y Koordinate) und wie wir allgemein stehen (Pose estimation -> Translation und Rotation zum Marker):
![PXL_20210503_154708345](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/PXL_20210503_154708345.jpg)

Der Roboter zusammengebaut:
![PXL_20210516_085552411](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/PXL_20210516_085552411.jpg)

Drucken größerer Reifen:
![Bildschirmfoto_2021-05-20_um_09.43.05](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/Bildschirmfoto_2021-05-20_um_09.43.05.png)

Um den Roboter zusammenzubauen braucht man die .stl Files aus dem Repo. Sie liegen auf dem Master Branch unter docs/CAD.


<a name="display"></a>
## Display testen

Bibliothek für das Display: 
https://platformio.org/lib/show/135/Adafruit%20SSD1306/examples
https://platformio.org/lib/show/6214/Adafruit%20BusIO/installation

Das Display hat GND, das muss an GND vom ESP32 angeschlossen werden. 
VCC vom Display wird an 3,3V vom ESP32 angeschlossen. 
SCL wird an IO15 und SDA an IO14 angeschlossen.

![PXL_20210408_140750821](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/PXL_20210408_140750821.jpg)

Das Display konnte im fertigen Projekt nicht mehr integriert werden, da der ESP zu wenige Pins besitzt. Dem Motor mussten leider ein Display Pin geben werden, wegen der LED des ESP. Mehr dazu siehe [steuerung](#steuerung).


<a name="kamera"></a>
## Kamera testen


![Bildschirmfoto_2021-04-08_um_18.46.21](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/Bildschirmfoto_2021-04-08_um_18.46.21.png)

Unter dem Punkt die App kann man sehen, das die aufgenommenen Bilder 9,216 KB groß sind. Diese werden in 19 Paketen vom ESP32 zur App gesendet.

Mit der Nachricht "00" fängt er an Bilder zu senden. So schnell er kann. Es sieht aus, wie ein flüssiges Video. Mit "01" stoppt man das ganze.



<a name="nachrichten"></a>
## Nachrichten senden

Man kann auf dem Handy eine App namens BLE Scanner installieren. Dort kann man sich mit dem ESP32 verbinden und eine Nachricht senden. Diese kann man dann im Terminal von C Lion sehen, wenn man "pio device monitor -b 115200" eingibt.

![Screenshot_20210413-095543](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/Screenshot_20210413-095543.jpg)

![Screenshot_20210413-095548](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/Screenshot_20210413-095548.jpg)

![Bildschirmfoto_2021-04-13_um_09.55.57](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/Bildschirmfoto_2021-04-13_um_09.55.57.png)


<a name="app"></a>
## Die App

OpenCV 4.5.2 mit contribs!!! für Android Studio: 

https://pullrequest.opencv.org/buildbot/export/opencv_releases/master-contrib_pack-contrib-android/20210425-042121--11332/

https://stackoverflow.com/questions/17767557/how-to-use-opencv-in-using-gradle

https://docs.opencv.org/2.4/doc/tutorials/introduction/android_binary_package/O4A_SDK.html

Wichtig:
https://stackoverflow.com/questions/57698796/cannot-detect-opencv-libs-after-update-form-3-4-3-to-4-1-1

Anfänglich:
1. Momentan kann man mit der App (auf dem Dev Branch Unterordner phone) über BLE mit dem Robo (ESP32) kommunizieren.
2. OpenCV ist über die oberen beiden Links mit eingebunden. Mit OpenCV können wir die ArUco Marker per Kamera erkennen.

https://docs.opencv.org/3.4.3/d5/dae/tutorial_aruco_detection.html

Hier wurde auf Papier der ArUco Marker mit der Id 7 ausgedruckt und vor die Kamera gehalten. Der Marker wird erkannt!

![Screenshot_20210429-161143](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/Screenshot_20210429-161143.jpg)

_Stand Ende des Projekts:_

-Redesign der App
-Button zum Verbinden des Roboters über BLE
-Button zum senden einzelner Strings zum testen
-DropDown Menü um auszuwählen, zu welchem Feld der Roboter fahren soll.

Beim Fahren korrigiert er sich nach jedem Schritt durch die **Pose Estimation** selbst. Translation und Rotation werden berechnet. So weiß der Roboter auf welchem Feld er steht und mit welchem Winkel er zu den Markern steht. Durch diesen Winkel korrigiert er sich nach jedem Schritt selbst um immer "geradeaus" zu fahren oder sich zu drehen.

**ArUco Marker Erkennung**:
wenn kein Marker erkannt wird kann dies
- an dem Winkel in dem der Roboter zu ihm steht,
- oder dem **Rauschen der Kamera**,
- oder **Interferenzen der Motoren** (hierdurch evtl. verfälschte Bilddaten und verlorene Pakete),
- oder der geringen Auflösung der Kamera,
liegen.

Screenshot direkt nach dem Verbinden:
![Screenshot_20210704-135847](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/Screenshot_20210704-135847.jpg)

Screenshot nach dem Klick auf eine ID im DropDown Menü:
![Screenshot_20210704-135916](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/Screenshot_20210704-135916.jpg)


<a name="arucogeneration"></a>
## ArUco Board Generation (Python)

PyCharm -> pip install opencv-contrib-python
 ->pip install matplotlib

Das erste Ergebnis ist ein Spielbrett zum Ausdrucken:
![Bildschirmfoto_2021-05-02_um_11.08.06](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/Bildschirmfoto_2021-05-02_um_11.08.06.png)

Am Ende wurde sich für größere Felder (ein Spielfeld ist eine DinA4 Seite groß) und mehr Markern auf den Feldern (50*50 = 2500) entschieden, da die Kamera so nah am Boden ist, dass sie die Marker so am besten erkennt.

Als Beispiel ein 1x2 großes Feld (siehe Bild). Das Feld kann durch ausdrucken weiter Marker bis auf 10x10 vergrößert werden.
![IMG-20210606-WA0000](https://github.com/Lea-Fu/ESP32-robot/blob/main/docs/images/IMG-20210606-WA0000.jpg)


<a name="ausblick"></a>
## Ausblick

Ausblick für die Weiterführung des Projekts!

Low Cost für Bürstenmotoren. Diese sind schwerer zu steuern, da man nicht wie bei Schrittmotoren genau weiß, wie viel sie sich bewegen. Das nächste mal also die etwas teureren Schrittmotoren nehmen.

Inferenzen: die Motoren beeinträchtigen die Kamera, dies war vorher auch nicht klar. Es konnte in diesem Projekt gelöst werden, aber man könnte es sich bestimmt noch einfacher machen.

Spielfigur: Der Roboter hat einen ESP32. Eine Spielfigur ist einfach nur eine Figur/Blatt. Diese könnte auch aus einem ESP32 bestehen, ohne Räder, aber mit Kamera, so dass diese auch Marker erkennt und man somit in der App nicht angeben muss, wo die eigene Spielfigur sich befindet, sondern man sie einfach nur setzt. Durch OpenCV und die ArUco Marker Erkennung die wir auch beim Roboter nutzen könnte es somit verbessert werden.

Reifen: Hier benutzen wir Gummibänder, diese springen gelegentlich ab, hier könnte man sich eine andere Konstruktion überlegen, z.B. 3D gedruckte Ketten, wie bei einem Panzer etc..

In der Mitte des Feldes landen: Dies könnte man mit dem Gradientenabstiegsverfahren (Künstliche Intelligenz) machen, indem man ein Feld in der Mitte aus einem ArUco Marker baut, und außen immer in Kreisen drum herum andere Marker setzt, so kennt man die Mitte und kann das Machine Learning Verfahren bzw. den Algorithmus anwenden.
