NexusEnroll - Java Web PoC

This PoC adds a minimal web frontend (Spark Java) and simple file-based persistence
(using Java serialization) so you don't need an external DB server. It's a small,
self-contained demo to support the assignment requirements.

How to run:
1. mvn clean package
2. java -jar target/nexus-enroll-web-0.2.0-SNAPSHOT.jar
3. Open http://localhost:4567

Data files are stored under ./data/*.bin
