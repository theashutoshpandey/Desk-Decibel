
<p style="display: flex; align-items: center; gap: 10px;">
  <img src="static/logo.png" alt="Desk Decibel Logo" width="50" />
  <span style="color: green; font-size: 2em; font-weight: bold;">Desk </span><span style="color: white; font-size: 2em; font-weight: bold;">Decibel</span>
</p>

**Silently informed. Effortlessly productive.**

**Desk Decibel** is a lightweight desktop monitoring interface built for discreet, high-efficiency supervision within secure network environments. It streams live video feeds from authorized systems over a private network using a simple, responsive browser interface. Designed for smooth, silent performance, it provides intuitive access without disrupting workflow.

---

## 🛠️ Technologies Used

- **Java** — Backend service for streaming and host authentication
- **HTML / CSS / JavaScript** — Lightweight and responsive user interface
- **HLS (HTTP Live Streaming)** & **WebSockets** — Real-time media delivery
- **Browser Storage (localStorage)** — Address book persistence on client-side

---

## 🚀 Key Features

- 🔒 **Password-Protected Host Authentication**
- 🌐 **Connect via Hostname / IP / Address Book**
- 🗂️ **Local Address Book Management** (Add/Remove Entries)
- 🎥 **Autoplay Inline Video Playback** (Minimal UI)
- ⚠️ **User Alerts** on Failed Connections or Invalid Credentials
- 📱 **Responsive UI** for Desktop and Mobile Browsers
- ✳️ **Custom Branding Support** (Favicon, Title, Styles)

---

## ▶️ How to Use

1. **Start the Java Streaming Server**  
   Ensure the backend server is active on the target machine.

2. **Launch the Interface**  
   Open your browser and navigate to:  
   `http://127.0.0.1:8080/decibel`

3. **Connect to a Host**
   - Enter or select a hostname/IP from the address book.
   - Provide the associated password.
   - On successful authentication, the video feed will begin streaming inline.

---

## 📦 Creating and Installing the Java Executable (.exe)

> Ensure you have [JDK](https://www.oracle.com/java/technologies/javase-downloads.html) and [Launch4j](http://launch4j.sourceforge.net/) installed.

### ✅ Step-by-Step Guide

1. **Compile Java Code**
   ```bash
   javac -d out src/com/desk/decibel/*.java

### Convert JAR to EXE using Launch4j

1. **Open Launch4j**.

2. Set the following configuration:
   - **Output file**: `DeskDecibel.exe`
   - **Jar**: Path to `DeskDecibel.jar`
   - **Main Class**: `com.deskdecibel.Main`

3. Configure JVM options if needed.

4. Click **Build** to generate the `.exe` file.

---

### Distribute/Install

- Package `DeskDecibel.exe` along with any required dependencies and a `README.md` file.
- *(Optional)* Use an installer generator such as with autostart:
   - [Inno Setup](https://jrsoftware.org/isinfo.php)
   - [NSIS](https://nsis.sourceforge.io/)

## 📊 System Architecture

> [Click to view the System Architecture Diagram (PDF)](Assets/Desk_Decibel.pdf)

---

## 🎥 Demo Video
> 📥 [Download Desk Decibel Demo (MP4)](Assets/Desk_Decibel_Demo.mp4)

---

---

## 🔐 Security Notice

- **Desk Decibel** is intended strictly for **trusted, local network environments**.
- No external or cloud-based data transmission occurs.
- Host addresses and login attempts are stored **only in the browser's local storage**.
- **Do not deploy** in untrusted or public environments.

---

## 👤 Author
**Ashutosh Pandey**
> Feel free to reach out for collaboration, feedback, or enhancements.
---

## 📄 License
This project is licensed under the **MIT License**, allowing open use, modification, and distribution **with attribution**.  
However, if you intend to use this project commercially or make substantial modifications, please reach out to the author for permission.