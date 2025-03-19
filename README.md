# 🛡️ PhishAlert: AI-Enhanced URL Protection (Gmail Extension) | BACKEND

## 📌 Overview
The backend service for **PhishAlert**, our thesis project, processes URL safety checks and delivers results to the 
frontend (browser extension) and Arduino devices. It uses a REST API and WebClient to integrate with the Google Safe Browsing API and OpenAI API for phishing detection.


---

### 🚀 Features
- **🔗 REST API Communication**: Supports request-response communication between the frontend and backend.
- **🌐 API Integration**:
    - **Google Safe Browsing API**: Identifies malicious URLs.
    - **OpenAI API**: Provides an additional layer of URL safety evaluation.
- **🔄 WebClient Communication**: Uses WebClient to send URL safety analysis results to Arduino for hardware-based 
  notifications.
- **📢 Simultaneous Feedback**: Sends URL safety analysis results to the frontend for user alerts while updating 
  Arduino via WebClient.


---

  
### 🔧 Prerequisites
1. **☕ Java Development Kit (JDK)**: Version 23.
2. **🔨 Maven**: For dependency management.
3. **🔑 Google Safe Browsing API Key**: Obtain one from Google Cloud Console.
4. **🤖 OpenAI API Key**: Obtain one from OpenAI.
5. **🌍 Internet Connection**: Required for API communication and online services
6. **💻 IDE**: IntelliJ IDEA or any Java-supporting Integrated Development Environment (IDE).
7. **🛠️ Arduino Device (Optional)**: For physical alert handling based on the URL safety evaluation.

---


### 🏗️ Installation
**📥 Clone the Repository**
1. ```git clone https://github.com/JonArbell/phish-alert.git```


2. ``` cd phishing-detector ```


#### ⚙️ Set Up Environment Variables
- Before running the backend, you need to configure your environment variables for API keys.
Create a .env file in the root directory of the project and add the following:
```
SAFE_BROWSING_KEY = your_google_api_key_here
OPENAI_SECRET_KEY = your_openai_api_key_here
```
Replace your_google_api_key_here and your_openai_api_key_here with the actual API keys you obtained from Google and OpenAI.


#### 🛠️ Set Up the Arduino Device
In this step, we're using Arduino Uno R4 Wi-Fi for physical alerts. Follow these steps to receive WebClient notifications:
- 🔌 Connect your Arduino Uno R4 Wi-Fi to your computer via USB.
- 📝 Write and upload the following Arduino script to handle incoming WebClient requests and trigger hardware-based notifications:


```cpp
#include <WiFiS3.h>
#include <WiFiUdp.h>
#include <ArduinoMDNS.h>

const char* ssid = "SK Telecom";
const char* password = "wifinamint0";

const int redPin = 11;
const int greenPin = 12;

WiFiServer server(80);

WiFiUDP udp;
MDNS mdns(udp);

void nameFound(const char* name, IPAddress ip);

void setup() {
  pinMode(greenPin, OUTPUT);
  pinMode(redPin, OUTPUT);

  int status = WL_NO_SHIELD;

  while(status != WL_CONNECTED){
    digitalWrite(redPin, HIGH);
    delay(300);

    digitalWrite(redPin, LOW);
    delay(300);

    status = WiFi.begin(ssid, password);
  }
  
  mdns.begin(WiFi.localIP(), "arduino");

  mdns.setNameResolvedCallback(nameFound);

  digitalWrite(redPin, LOW);  

  server.begin();
}

void loop() {
  WiFiClient client = server.available();

  if(!mdns.isResolvingName()){
    if (client && client.connected()) {
      String request = "";

      while (client.available()) {
        char c = client.read();
        request += c;
      }

      String messageReceived = extractSendValue(request);
      if (request.startsWith("POST /send-response")) {
        if (messageReceived == "Safe") {
          digitalWrite(greenPin, HIGH);
        } else if (messageReceived == "Suspicious") {
          digitalWrite(redPin, HIGH);
          
        }
        response(client,messageReceived);
        delay(5000);

        digitalWrite(redPin, LOW);
        digitalWrite(greenPin, LOW);
      }
    }
  }

  mdns.run();
  
}

String extractSendValue(String body) {
  int sendIndex = body.indexOf("\"send\":\"");
  if (sendIndex == -1) return ""; // Key not found

  sendIndex += 8; // Move to start of value
  int endIndex = body.indexOf("\"", sendIndex);
  if (endIndex == -1) return ""; // Malformed JSON

  return body.substring(sendIndex, endIndex);
}


void response(WiFiClient client, String messageReceived){
  client.println("HTTP/1.1 200 OK");
  client.println("Content-Type: application/json");
  client.println("Connection: close");
  client.println();

  client.println("{\"status\":\"success\", \"message\":\"Response processed\", \"message-received\":\"" + messageReceived + "\"}");

  client.stop();

}

void nameFound(const char* name, IPAddress ip){
  if (ip != INADDR_NONE) {
    Serial.print("The IP address for '");
    Serial.print("' is ");
    Serial.println(ip);

  } else {
    Serial.print("Resolving '");
    Serial.print(name);
    Serial.println("' timed out.");
  }

}
```
- ✅ Upload this script to the Arduino device.


#### ▶️ Run the Backend Service

**🔎 Test the Setup**
- **🖥️ Frontend (Browser Extension)**: The extension should now interact with the backend and receive notifications 
  based 
  on URL safety.
- **💡 Arduino Device**: The device will alert users with an LED or another physical indicator depending on whether the URL is safe or malicious.

---

## 📌 Additional Notes
- **⚡ Testing**: Test different types of URLs (safe, suspicious, and malicious) to ensure the system is accurately 
identifying threats.
- **📜 Logs**: If there are any issues, check the backend logs for errors related to API calls or WebClient 
  communication.
- **📊 API Limits**: The Google Safe Browsing API and OpenAI API may have usage limits depending on the API key you 
  are using. Make sure to check their documentation for any rate limiting or quota restrictions.

