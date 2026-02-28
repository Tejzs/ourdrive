# OurDrive

A web-based file storage and sharing application. Live at [ourdrive.app](https://ourdrive.app).

## Features

- **Chunked uploads** — large files are uploaded in chunks for reliability
- **Pause & resume uploads** — pick up where you left off if an upload is interrupted
- **Folder sharing** — share folders with other users directly

## Tech Stack

- **Backend:** Java (Tomcat)
- **Frontend:** HTML, CSS, JavaScript
- **Structure:** Standard Java web application (WEB-INF, Pages, Java source)

## Project Structure

```
ourdrive/
├── Java/          # Java source files (servlets, models, utilities)
├── Pages/         # HTML/JSP frontend pages
├── WEB-INF/       # Web config (web.xml, libraries)
└── index.html     # Entry point
```

## Getting Started

### Prerequisites

- Java JDK 11+
- A Java web server (e.g. Apache Tomcat)

### Running Locally

1. Clone the repo:
   ```bash
   git clone https://github.com/Tejzs/ourdrive.git
   ```
2. Deploy the project to your Tomcat `webapps` directory (or import into an IDE like IntelliJ/Eclipse as a web project).
3. Start Tomcat and navigate to `http://localhost:8080/ourdrive`.

## Contributing

Pull requests are welcome. For major changes, open an issue first to discuss what you'd like to change.
