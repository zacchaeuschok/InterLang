import streamlit as st


def render_logo():
    st.markdown(
        """
    <style>
    .big-font {
        font-size:40px !important;
        color: #003594;
        font-weight: bold;
        margin-right: 5px;
    }
    .small-font {
        font-size:15px !important;
        color: #575B5A;
        font-weight: bold;
    }
    </style>
    """,
        unsafe_allow_html=True,
    )

    st.markdown(
        '<p><span class="big-font">🏥 Mobile Munchkins</span> <span class="small-font">by Fifteen Labs</span></p>',
        unsafe_allow_html=True,
    )

    st.markdown("")