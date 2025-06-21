import { useRef, useState } from "react";
import "./Login.css";
import axios from "axios";
import Home from "./Home.jsx";

function Login() {
    const usernameRef = useRef(null);  // username that the user typed in the input
    const passwordRef = useRef(null);
    const [isRegister, setIsRegister] = useState(null);
    const [msgState, setMsgState] = useState("hidden");
    const [message, setMessage] = useState("");
    const [showHome, setShowHome] = useState(false);
    const [homeUsername, setHomeUsername] = useState(null);  // username for logging in

    if (showHome) {
        return <Home username={homeUsername} />
    } else {
        return (
            <main>
                <div className="login-container">
                    <h2>Login/Register</h2>
                    <form id="login-form" onSubmit={handleSubmit}>
                        <div>
                            <label htmlFor="username">Username: </label>
                            <input type="text" id="username" ref={usernameRef} required pattern="[A-Za-z]\w*" />
                        </div>
                        <div>
                            <label htmlFor="password">Password: </label>
                            <input type="password" id="password" ref={passwordRef} required />
                        </div>
                        <div className="login-btns-container">
                            <button id="login-btn" onClick={() => setIsRegister(false)}>Login</button>
                            <button id="register-btn" onClick={() => setIsRegister(true)}>Register</button>
                        </div>
                    </form>
                    <div className="guest-btn-container">
                        <button id="guest-btn" onClick={handleGuestClick}>Play as guest</button>
                    </div>
                    <div className={"login-msg-container " + msgState}>
                        <p className="login-msg">{message}</p>
                    </div>
                </div>
            </main>
        );
    }

    function handleSubmit(e) {
        e.preventDefault();
        const username = usernameRef.current.value;
        const password = passwordRef.current.value;
        console.log("Username: " + username);
        console.log("Password: " + password);
        if (isRegister) {
            axios.post("/api/register", { "username": username, "password": password })
                .then((response) => {
                    const res = response.data;
                    const success = res.success;
                    const message = res.message;
                    console.log(`success: ${success}, message: ${message}`);
                    if (success) {
                        setMsgState("success");
                    } else {
                        setMsgState("error");
                    }
                    setMessage(message);
                })
                .catch((error) => {
                    console.log(error);
                });
        } else {
            axios.post("/api/login", { "username": username, "password": password })
                .then((response) => {
                    const res = response.data;
                    const success = res.success;
                    const message = res.message;
                    console.log(`success: ${success}, message: ${message}`);
                    if (success) {
                        console.log("logging in");
                        setShowHome(true);
                        setHomeUsername(username);
                    } else {
                        setMsgState("error");
                        setMessage(message);
                    }
                })
                .catch((error) => {
                    console.log(error);
                });
        }
    }

    function handleGuestClick(e) {
        axios.get("/api/playAsGuest")
            .then((response) => {
                const res = response.data;
                const success = res.success;
                const username = res.message;
                console.log(`success: ${success}, username: ${message}`);
                if (success) {
                    setShowHome(true);
                    setHomeUsername(username);
                } else {
                    throw new Error("playAsGuest should always return true in the success field, but got false");
                }
            })
            .catch((error) => {
                console.log(error);
            });
    }
}

export default Login;