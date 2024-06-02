import "./Login.css";

function Login() {
  return (
    <>
      <main>
        <div className="login-container">
          <div className="login-card">
            <h2>Login</h2>
            <form id="login-form" action="http://localhost:8080/login" method="post">
              <div>
                <label htmlFor="login_username">Username:</label>
                <input type="text" id="login_username" />
              </div>
              <div>
                <label htmlFor="login_password">Password:</label>
                <input type="text" id="login_password" />
              </div>
              <button id="login-btn">Login</button>
            </form>
          </div>
          <div className="login-card">
            <h2>Register</h2>
            <form id="register-form" action="http://localhost:8080/register" method="post">
              <div>
                <label htmlFor="login_username">Username:</label>
                <input type="text" id="login_username" />
              </div>
              <div>
                <label htmlFor="login_password">Password:</label>
                <input type="text" id="login_password" />
              </div>
              <button id="register-btn">Register</button>
            </form>
          </div>
          <div className="login-card" id="guest-card">
            <button id="guest-btn">Play as guest</button>
          </div>
        </div>
      </main>
    </>
  );
}

export default Login;