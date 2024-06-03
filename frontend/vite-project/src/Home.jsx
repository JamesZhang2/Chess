import "./Home.css";

function Home() {
  return (
    <>
      <div className="home-container">
        <div className="vs-human-container">
          <h2>Play vs. Human</h2>
          <div className="side-dropdown">
            <label htmlFor="vs-human-side">I play as:</label>
            <select name="vs-human-side" id="vs-human-side">
              <option value="white">White</option>
              <option value="random" selected>Random</option>
              <option value="black">Black</option>
            </select>
          </div>
          <div className="time-dropdown">
            <label htmlFor="vs-human-time">Time control:</label>
            <select name="vs-human-time" id="vs-human-time">
              <option value="untimed" selected>Untimed</option>
              <option value="1-min">1 minutes</option>
              <option value="3-min">3 minutes</option>
              <option value="5-min">5 minutes</option>
              <option value="10-min">10 minutes</option>
            </select>
          </div>
          <button>Create Challenge</button>
        </div>

        <div className="vs-ai-container">
          <h2>Play vs. AI</h2>
          <div className="side-dropdown">
            <label htmlFor="vs-ai-side">I play as:</label>
            <select name="vs-ai-side" id="vs-ai-side">
              <option value="white">White</option>
              <option value="random" selected>Random</option>
              <option value="black">Black</option>
            </select>
          </div>
          <div className="time-dropdown">
            <label htmlFor="vs-ai-time">Time control:</label>
            <select name="vs-ai-time" id="vs-ai-time">
              <option value="untimed" selected>Untimed</option>
              <option value="1-min">1 minutes</option>
              <option value="3-min">3 minutes</option>
              <option value="5-min">5 minutes</option>
              <option value="10-min">10 minutes</option>
            </select>
          </div>
          <div className="ai-level-dropdown">
            <label htmlFor="ai-level">AI Level:</label>
            <select name="ai-level" id="ai-level">
              <option value="1" selected>1</option>
              <option value="2">2</option>
              <option value="3">3</option>
            </select>
          </div>
          <button>Play</button>
        </div>
        <div className="current-challenges">
          Current challenges
          <table id="challenges-table">

          </table>
        </div>
      </div>
    </>
  );
}

export default Home;