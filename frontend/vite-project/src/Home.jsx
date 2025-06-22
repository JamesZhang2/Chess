import { useState } from "react";
import "./Home.css";
import Login from "./Login";

function Home({ username }) {
    const [loggedOut, setLoggedOut] = useState(false);
    const [vsHuman, setVsHuman] = useState(true);  // toggles vs human or vs AI
    const [aiType, setAIType] = useState("random");
    const [side, setSide] = useState("random");
    if (loggedOut) {
        return <Login />;
    }
    return (
        <div className="home-body">
            <header>
                <h1>Hello, {username}!</h1>
                <button id="log-out-btn" onClick={() => setLoggedOut(true)}>Log out</button>
            </header>
            <div className="home-container">
                <div className="toggle" id="human-ai-toggle">
                    <button id="vs-human-btn" className={vsHuman ? "pressed" : ""} onClick={() => setVsHuman(true)}>Play vs. Human</button>
                    <button id="vs-human-btn" className={vsHuman ? "" : "pressed"} onClick={() => setVsHuman(false)}>Play vs. AI</button>
                </div>

                <div className="toggle" id="side-toggle">
                    <p>I play as:</p>
                    <button id="play-white-btn" className={side === "white" ? "pressed" : ""} onClick={() => setSide("white")}>White</button>
                    <button id="play-random-btn" className={side === "random" ? "pressed" : ""} onClick={() => setSide("random")}>Random</button>
                    <button id="play-black-btn" className={side === "black" ? "pressed" : ""} onClick={() => setSide("black")}>Black</button>
                </div>

                <div className="handicap-container">
                    <label htmlFor="handicap-dropdown">Handicap: </label>
                    <select name="handicap-dropdown" id="handicap-dropdown">
                        <option value="none" selected>None</option>
                        <option value="selfQueen">I play without the queen</option>
                        <option value="selfARook">I play without the a-rook</option>
                        <option value="selfHRook">I play without the h-rook</option>
                        <option value="selfBKnight">I play without the b-knight</option>
                        <option value="selfGKnight">I play without the g-knight</option>
                        <option value="selfCBishop">I play without the c-bishop</option>
                        <option value="selfFBishop">I play without the f-bishop</option>
                        <option value="selfAHRook">I play without both rooks</option>
                        <option value="selfAHRookQueen">I play without both rooks and the queen</option>
                        <option value="opQueen">Opponent plays without the queen</option>
                        <option value="opARook">Opponent plays without the a-rook</option>
                        <option value="opHRook">Opponent plays without the h-rook</option>
                        <option value="opBKnight">Opponent plays without the b-knight</option>
                        <option value="opGKnight">Opponent plays without the g-knight</option>
                        <option value="opCBishop">Opponent plays without the c-bishop</option>
                        <option value="opFBishop">Opponent plays without the f-bishop</option>
                        <option value="opAHRook">Opponent plays without both rooks</option>
                        <option value="opAHRookQueen">Opponent plays without both rooks and the queen</option>
                    </select>
                </div>

                <div id="ai-menu" className={vsHuman ? "hide" : "show"}>
                    <div className="toggle" id="ai-type-toggle">
                        <p>AI type:</p>
                        <button id="random-ai-btn" className={aiType === "random" ? "pressed" : ""} onClick={() => setAIType("random")}>Random</button>
                        <button id="minimax-1-ai-btn" className={aiType === "minimax1" ? "pressed" : ""} onClick={() => setAIType("minimax1")}>Minimax Depth 1</button>
                        <button id="minimax-3-ai-btn" className={aiType === "minimax3" ? "pressed" : ""} onClick={() => setAIType("minimax3")}>Minimax Depth 3</button>
                    </div>
                </div>

                <button id="play-btn">{vsHuman ? "Create Challenge" : "Play"}</button>

                <div className="current-challenges">
                    Current challenges:
                    <table id="challenges-table">

                    </table>
                </div>
            </div>
        </div>
    );
}

export default Home;