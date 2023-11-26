import streamlit as st
import requests

from logo import render_logo

render_logo()

BASE_URL = "http://localhost:8080/chat"

# Operation Selection
operation = st.selectbox("Select Operation", 
                         ["Add Patient", "Add Goal Data", "Fetch Goal Data", "Generate Care Plan", "Other Operations"])

# Form for different operations
with st.form(key='operations_form'):
    if operation == "Add Patient":
        st.subheader("Add a New Patient")
        family_name = st.text_input("Family Name:")
        given_name = st.text_input("Given Name:")
        user_input = f"Create a patient with the data Family Name {family_name}, Given Name {given_name}."

    elif operation == "Add Goal Data":
        st.subheader("Add Goal Data for a Patient")
        patient_id = st.text_input("Patient ID:")
        goal_key = st.selectbox("Goal Key:", ["Sleep", "Steps", "Heart Rate"])
        goal_value = st.text_input("Goal Value:")
        user_input = f"Add goal data for patient {patient_id} with goal key {goal_key} and goal value {goal_value}."

    elif operation == "Fetch Goal Data":
        st.subheader("Fetch Goal Data for a Patient")
        patient_id = st.text_input("Patient ID:")
        user_input = f"Fetch goal data for patient {patient_id}."

    elif operation == "Generate Care Plan":
        st.subheader("Generate Care Plan for Patient")

    else:
        st.subheader("Other Operations")
        user_input = st.text_area("Enter your command:")

    # Submit button
    submit_button = st.form_submit_button(label='Submit')

# Send the data to the backend if form is submitted
if submit_button and user_input:
    response = requests.post(BASE_URL, json={"user_input": user_input})
    if response.status_code == 200:
        st.write("Response:", response.text)
    else:
        st.error("Error from server")
