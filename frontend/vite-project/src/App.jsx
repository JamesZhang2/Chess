import { useState } from "react";
import "./App.css";
import axios from "axios";
import Board from "./Board.jsx";
import Login from "./Login.jsx";
import Home from "./Home.jsx";
import { BrowserRouter, Routes, Route } from "react-router-dom";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/home" element={<Home />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
