# PhishAlert: AI-Enhanced URL Protection (Gmail Extension) | BACKEND

## Overview
The backend service for PhishAlert processes URL safety checks and distributes results to the frontend (browser extension) and Arduino devices. The backend uses WebSockets for bidirectional communication and integrates with the Google Safe Browsing API and OpenAI API to provide robust phishing detection.
### Features
- WebSocket Communication: Supports bidirectional messaging with frontend and Arduino.
- API Integration:
    - Google Safe Browsing API: Identifies malicious URLs.
    - OpenAI API: Provides an additional layer of URL safety evaluation.
- Broadcast to Multiple Clients: Sends analysis results simultaneously to frontend and Arduino.


### System Flow
1. Frontend: A user submits a URL to the backend via WebSocket.
2. Backend:
    - Validates the incoming URL.
    - Sends the URL to Google Safe Browsing and OpenAI APIs for analysis.
    - Processes and combines the results into a final verdict.
3. Broadcast:
    - Sends the results to the frontend for display.
    - Sends the results to the Arduino device for additional alert handling.

### Prerequisites
1. Java Development Kit (JDK): Version 23.
2. Maven: For dependency management.
3. Google Safe Browsing API Key: Obtain one from Google Cloud Console.
4. OpenAI API Key: Obtain one from OpenAI.

### Installation
**Clone the Repository**
1. git clone https://github.com/JonArbell/phish-alert.git
2. cd phishing-detector



  
