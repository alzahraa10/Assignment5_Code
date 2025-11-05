Student: Alzahraa Hussein
Overview
This project contains Java code with unit tests, integration tests, and a GitHub Actions workflow.
The workflow runs automatically when changes are pushed to the main branch.
How to Run Tests
Use Maven to run all tests:
mvn test
GitHub Actions
The CI workflow file is located here:
.github/workflows/SE333_CI.yml
It performs the following:
Checks the code
Runs unit and integration tests
Generates reports if supported
Project Structure
src/main/java contains the main source code
src/test/java contains unit and integration tests
pom.xml contains project dependencies and plugins
Repository Link
https://github.com/alzahraa10/Assignment5_Code
Notes
The CI workflow is set up and runs on push.
Unit and integration tests are included.