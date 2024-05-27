import { useState } from 'react';
import './App.css'
import axios from 'axios';
import Board from './Board.jsx'

function App() {
  const [msg, setMsg] = useState("initial");
  hello(setMsg);
  return <>
    <h1>{msg}</h1>
    <button onClick={buttonClick}>Click Me!</button><br />
    <input type="text" id="textbox" /><br />
    <button onClick={send}>Send</button>
  </>
  // return <Board />;
}

function hello(setMsg) {
  axios.get('http://localhost:8080/',)
    .then((response) => {
      setMsg(response.data);
    })
    .catch((error) => {
      console.log(error);
    });
}

function buttonClick() {
  axios.get('http://localhost:8080/button',)
    .then((response) => {
      console.log(response.data);
    })
    .catch((error) => {
      console.log(error);
    });
}

function send() {
  const textbox = document.getElementById("textbox");
  console.log(textbox.value);
  axios.post('http://localhost:8080/text', {
    text: textbox.value
  }, {
    headers: {}  // Add headers here if needed
  })
    .then((response) => {
      console.log(response);
      console.log(response.data);
      // Handle data
    })
    .catch((error) => {
      console.log(error);
    })
}

export default App
