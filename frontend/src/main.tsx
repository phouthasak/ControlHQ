import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";
import { EnvironmentSettingsProvider } from "./context/EnvironmentContext.tsx";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <EnvironmentSettingsProvider>
      <App />
    </EnvironmentSettingsProvider>
  </StrictMode>,
);
