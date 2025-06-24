import "./App.css";
import Login from "./Login.jsx";
import Home from "./Home.jsx";
import Game from "./Game.jsx";
import { BrowserRouter, Routes, Route } from "react-router-dom";

function App() {
  return (
    <Login />
    // <Home username={"James"} />
    // <Game whiteName={"White"} whitePlayerType={"MinimaxAIPlayer"} blackName={"Black"} blackPlayerType={"HumanGUIPlayer"} />
  );
  // return (
  //   <BrowserRouter>
  //     <Routes>
  //       <Route path="/" element={<Game whiteName={"White"} whitePlayerType={"HumanGUIPlayer"} blackName={"Black"} blackPlayerType={"HumanGUIPlayer"} />} />
  //       <Route path="/login" element={<Login />} />
  //       <Route path="/home" element={<Home />} />
  //       <Route path="/game" element={<Game />} />
  //     </Routes>
  //   </BrowserRouter>
  // );
}

export default App;
