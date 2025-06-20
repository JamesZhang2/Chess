import { useState } from "react";
import "./App.css";
import Login from "./Login.jsx";
import Home from "./Home.jsx";
import Game from "./Game.jsx";
import { BrowserRouter, Routes, Route } from "react-router-dom";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Game whitePlayerType={"HumanGUIPlayer"} blackPlayerType={"HumanGUIPlayer"} />} />
        <Route path="/login" element={<Login />} />
        <Route path="/home" element={<Home />} />
        <Route path="/game" element={<Game />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
