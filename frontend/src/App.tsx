import "./App.css";
import DeviceList from "./components/DeviceList";
import { useSettings } from "./context/EnvironmentContext";
import { LIGHT_THEME } from "./util/themes";

function App() {
  const { theme, toggleTheme, envEndpoint } = useSettings();

  return (
    <>
      <div className={theme === "dark" ? "" : ""}>
        <div className="p-4 flex justify-content-between align-items-center">
          <button onClick={toggleTheme} className="p-2 border-round">
            Switch to {theme === LIGHT_THEME ? "Dark" : "Light"} Mode
          </button>

          <div className="p-4">
            <small>Connected to: {envEndpoint}</small>
          </div>
        </div>

        <DeviceList />
      </div>
    </>
  );
}

export default App;
