import streamlit as st
import requests
import time
from logo import render_logo

render_logo()

# Initialize chat history
if "messages" not in st.session_state:
    st.session_state.messages = []

# Display chat messages from history on app rerun
for message in st.session_state.messages:
    with st.chat_message(message["role"]):
        st.markdown(message["content"])

# Backend URL (Replace with your Java Spring Boot backend URL)
BASE_URL = "http://localhost:8080/chat"

# Accept user input
if prompt := st.chat_input("What is up?"):
    # Add user message to chat history
    st.session_state.messages.append({"role": "user", "content": prompt})
    # Display user message in chat message container
    with st.chat_message("user"):
        st.markdown(prompt)

    # Send user input to Java Spring Boot backend and get response
    try:
        response = requests.post(BASE_URL, json={"user_input": prompt})
        response.raise_for_status()

        # Check if the response is in JSON format
        if response.headers.get('Content-Type') == 'application/json':
            assistant_response = response.json().get("text", "Sorry, I couldn't understand that.")
        else:
            # If not JSON, handle as plain text or other format
            assistant_response = response.text
    
    except requests.RequestException as e:
        assistant_response = f"An error occurred: {str(e)}"

    # Display assistant response in chat message container with streaming effect
    with st.chat_message("assistant"):
        message_placeholder = st.empty()
        full_response = ""
        # Simulate stream of response with milliseconds delay
        for chunk in assistant_response.split():
            full_response += chunk + " "
            time.sleep(0.05)  # Adjust delay time as needed
            # Add a blinking cursor to simulate typing
            message_placeholder.markdown(full_response + "▌")
        message_placeholder.markdown(full_response)
    
    # Add assistant response to chat history
    st.session_state.messages.append({"role": "assistant", "content": assistant_response})