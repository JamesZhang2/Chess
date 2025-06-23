import { useState, useRef } from "react";
import "./Home.css";
import Login from "./Login.jsx";
import Game from "./Game.jsx";

function Home({ username }) {
    const [loggedOut, setLoggedOut] = useState(false);
    const [vsHuman, setVsHuman] = useState(true);  // toggles vs human or vs AI
    const [aiType, setAIType] = useState("RandomAIPlayer");
    const [side, setSide] = useState("random");
    const [gameParams, setGameParams] = useState(null);
    const handicapRef = useRef(null);
    if (loggedOut) {
        return <Login />;
    }
    if (gameParams !== null) {
        return <Game {...gameParams} />
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
                    <select name="handicap-dropdown" id="handicap-dropdown" ref={handicapRef} defaultValue="NONE">
                        {/* These names must match the Handicap enum type on the backend, except that SELF and OP will be replaced with WHITE and BLACK */}
                        <option value="NONE">None</option>
                        <option value="SELF_QUEEN">I play without the queen</option>
                        <option value="SELF_A_ROOK">I play without the a-rook</option>
                        <option value="SELF_H_ROOK">I play without the h-rook</option>
                        <option value="SELF_B_KNIGHT">I play without the b-knight</option>
                        <option value="SELF_G_KNIGHT">I play without the g-knight</option>
                        <option value="SELF_C_BISHOP">I play without the c-bishop</option>
                        <option value="SELF_F_BISHOP">I play without the f-bishop</option>
                        <option value="SELF_BOTH_ROOKS">I play without both rooks</option>
                        <option value="SELF_QUEEN_BOTH_ROOKS">I play without both rooks and the queen</option>
                        <option value="OP_QUEEN">Opponent plays without the queen</option>
                        <option value="OP_A_ROOK">Opponent plays without the a-rook</option>
                        <option value="OP_H_ROOK">Opponent plays without the h-rook</option>
                        <option value="OP_B_KNIGHT">Opponent plays without the b-knight</option>
                        <option value="OP_G_KNIGHT">Opponent plays without the g-knight</option>
                        <option value="OP_C_BISHOP">Opponent plays without the c-bishop</option>
                        <option value="OP_F_BISHOP">Opponent plays without the f-bishop</option>
                        <option value="OP_BOTH_ROOKS">Opponent plays without both rooks</option>
                        <option value="OP_QUEEN_BOTH_ROOKS">Opponent plays without both rooks and the queen</option>
                    </select>
                </div>

                <div id="ai-menu" className={vsHuman ? "hide" : "show"}>
                    <div className="toggle" id="ai-type-toggle">
                        <p>AI type:</p>
                        <button id="random-ai-btn" className={aiType === "RandomAIPlayer" ? "pressed" : ""} onClick={() => setAIType("RandomAIPlayer")}>Random</button>
                        <button id="minimax-1-ai-btn" className={aiType === "MinimaxAIPlayer-1" ? "pressed" : ""} onClick={() => setAIType("MinimaxAIPlayer-1")}>Minimax Depth 1</button>
                        <button id="minimax-3-ai-btn" className={aiType === "MinimaxAIPlayer-3" ? "pressed" : ""} onClick={() => setAIType("MinimaxAIPlayer-3")}>Minimax Depth 3</button>
                    </div>
                </div>

                <button id="play-btn" onClick={handlePlayBtnClick}>{vsHuman ? "Create Challenge" : "Play"}</button>

                {/* <div className="current-challenges">
                    Current challenges:
                    <table id="challenges-table">
                        <tr>
                            <th>Username</th>
                            <th>They play as</th>
                            <th>Handicap</th>
                        </tr>
                    </table>
                </div> */}
            </div>
        </div>
    );

    function handlePlayBtnClick(e) {
        let whiteName, whitePlayerType, blackName, blackPlayerType, handicapType;

        if (vsHuman) {
            throw new Error("Unimplemented");
        } else {
            if (side === "white" || (side === "random" && Math.random() > 0.5)) {
                whiteName = username;
                whitePlayerType = "HumanGUIPlayer";
                blackName = aiType;
                blackPlayerType = aiType;
                handicapType = handicapRef.current.value.replace("SELF", "WHITE").replace("OP", "BLACK");
            } else {
                whiteName = aiType;
                whitePlayerType = aiType;
                blackName = username;
                blackPlayerType = "HumanGUIPlayer";
                handicapType = handicapRef.current.value.replace("SELF", "BLACK").replace("OP", "WHITE");
            }
        }

        setGameParams({
            "username": username,
            "whiteName": whiteName,
            "whitePlayerType": whitePlayerType,
            "blackName": blackName,
            "blackPlayerType": blackPlayerType,
            "handicapType": handicapType
        });
    }
}

export default Home;