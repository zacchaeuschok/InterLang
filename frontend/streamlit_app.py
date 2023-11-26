import streamlit as st
import requests

st.title("Chat with Assistant")

user_input = st.text_input("Type your message here:")

if st.button("Send"):
    if user_input:
        response = requests.post("http://localhost:8080/chat", json=user_input)
        if response.status_code == 200:
            st.write("Response:", response.text)
        else:
            st.error("Error from server")
    else:
        st.warning("Please enter a message.")
