# STDISCM-S14-Random-P3P4
### EXCONDE, Isiah Reuben
### GOMEZ, Zachary
### MARISTELA, Joseph Miguel 
### REJANO, Hans Martin

## P3
### Build/Compilation Steps
#### Using Visual Studio Code:
- Extract the javafx-sdk-21.0.06.zip and place in your preferred directory (then update the module-path below)
- To compile all the .java files:
```javac --module-path "D:\javafx-sdk-21.0.6\lib" --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.graphics  consumer\*.java producer\*.java```

Execute in different terminal

[Consumer]

```java --module-path "D:\javafx-sdk-21.0.6\lib" --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.graphics consumer.Consumer```

[Producer]

```java producer.Producer```

## P4 - Distributed Enrollment System
### Overview
P4 is a distributed enrollment system that demonstrates fault tolerance in microservices architecture. The system allows students to enroll in courses, view grades, and faculty members to upload grades and manage courses. Each feature is implemented as a separate microservice, ensuring that when one service is down, only that specific feature becomes unavailable.

### Services Architecture
The system is composed of 10 microservices:
1. **jwtlogin** (port 8080): Authentication and JWT token generation
2. **viewcourses** (port 8081): Lists available courses
3. **enroll** (port 8082): Student enrollment functionality
4. **viewgrades** (port 8083): Student grade viewing
5. **faculty-service** (port 8084): Core faculty functionalities
6. **createAccount** (port 8085): User registration
7. **viewenrolledcourses** (port 8086): Student's enrolled course list
8. **createcourse** (port 8087): Course creation by faculty
9. **editcourses** (port 8088): Course modification by faculty
10. **faculty-dropcourses** (port 8090): Course removal by faculty

### System Requirements
- Java 17+
- Node.js 14+ and npm
- Docker and Docker Compose
- Maven

### Setup & Installation
#### Clone the Repository
```
git clone https://github.com/your-repo/STDISCM-S14-Random-P3P4.git
cd STDISCM-S14-Random-P3P4/P4
```

#### Running Docker Containers:

1. Make sure docker is installed and running
2. Make sure maven is installed on the system
2. navigate to P4 project directory (root)
3. Run "mvn clean install -DskipTests"
4. Run "docker-compose up --build"

#### Running frontend:
1. navigate to P4/frontend
2. run "npm i"
3. run "npm start App.js"

Server should start on localhost:5000 with all backend services running
Use docker to turn on/off backend services if needed

```

When a service is down, only the specific feature it supports becomes unavailable, while the rest of the system continues to function. The frontend provides clear error messages indicating which service is unavailable.