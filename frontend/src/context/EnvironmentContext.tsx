import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import type { EnvironmentSettings } from "../types/environmentSetting";
import { BASE_URL } from "../util/endpoints";
import { DARK_THEME, LIGHT_THEME } from "../util/themes";

const EnvironmentContext = createContext<EnvironmentSettings | undefined>(undefined);

export const EnvironmentSettingsProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [theme, setTheme] = useState<string>(() => {
        const saveTheme = localStorage.getItem('app-theme');
        return (saveTheme as string) || LIGHT_THEME;
    });

    const [envEndpoint, setEnvEndpoint] = useState<string>(() => {
        const savedEndpoint = localStorage.getItem('app-endpoint');
        return savedEndpoint || BASE_URL;
    });

    useEffect(() => {
        localStorage.setItem('app-theme', theme);
        document.body.className = theme;
    }, [theme]);

    const toggleTheme = () => {
        setTheme((prev) => (prev === LIGHT_THEME ? DARK_THEME : LIGHT_THEME));
    };

    return (
        <EnvironmentContext.Provider value={{ theme, toggleTheme, envEndpoint }}>
            {children}
        </EnvironmentContext.Provider>
    );
};

export const useSettings = () => {
    const context = useContext(EnvironmentContext);
    if (context === undefined) {
        throw new Error('useSettings must be used within a SettingsProvider');
    }

    return context;
};