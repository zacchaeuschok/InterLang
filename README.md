# InterLang App

## Introduction
Welcome to the InterLang App! This application allows you to interact with a healthcare system using natural language commands. You can perform various operations like adding patients, adding goal data for patients, fetching goal data, generating care plans, and more, all through a user-friendly interface.

## How It Works
The InterLang App is built on the Spring Boot framework and uses Streamlit for the user interface. It communicates with a FHIR (Fast Healthcare Interoperability Resources) server to manage patient data and a powerful AI model from OpenAI for natural language understanding.

Here's how it works:
1. You select an operation from the available options: "Add Patient," "Add Goal Data," "Fetch Goal Data," "Generate Care Plan," or "Other Operations."
2. Depending on your choice, you'll be presented with a form to fill out the necessary information for that operation.
3. You enter the required details in the form fields.
4. Once you've entered the information, you click the "Submit" button.

The app then sends your input to the backend, where it is processed, and a response is generated based on your command. You will receive the response on the user interface.

## Setting Up the App
To run the InterLang App on your local environment using Visual Studio Code with a .devcontainer, please follow these steps:

1. Clone this repository to your local machine.
2. Open the project in Visual Studio Code.
3. Make sure you have the required extensions for Visual Studio Code to support Java development and Docker.
4. Ensure that you have Docker installed and running on your system.
5. Open the project in a DevContainer by clicking on the green "Open a remote window" button in the bottom-left corner of Visual Studio Code and selecting "Reopen in Container." This will set up a development environment with all the necessary dependencies.
6. In the project directory, locate the `application.properties` file and update it with the following properties:

```properties
fhir.server.url=<FHIR_SERVER_URL>
fhir.api.key=<FHIR_API_URL>
openai.api.key=<OPENAI_API_KEY>
```

Make sure you replace the placeholders with the actual values.

7. Save the `application.properties` file.

8. Now, you are ready to run the InterLang App. In the Visual Studio Code terminal, run the following command:

```bash
./mvnw spring-boot:run
```

This will start the Spring Boot application.

9. Then run the following command to start the Streamlit interface:

```bash
pipenv install
pipenv run streamlit run frontend/streamlit_app.py
```

10. Now you can go to `localhost:8501` to access the InterLang App.

You are now all set up and ready to use the InterLang App on your local environment!

Enjoy using the InterLang App to interact with the healthcare system and manage patient data with ease. If you have any questions or encounter any issues, please refer to the documentation or reach out to the project maintainers for assistance.
