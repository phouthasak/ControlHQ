import { useCallback, useEffect, useState } from "react";
import { getDevices } from "../api/deviceApi";
import type { Device } from "../types/device";
import axios from "axios";

export const useDevices = () => {
  const [devices, setDevices] = useState<Device[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchDevices = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getDevices();
      setDevices(data);
    } catch (err) {
      let errorMessage = "An unexpected error occurred";

      // Best Practice: Check if it's an Axios error to get the backend message
      if (axios.isAxiosError(err)) {
        // If backend sends { message: "Camera offline" }
        errorMessage = err.response?.data?.message || err.message;
      } else if (err instanceof Error) {
        errorMessage = err.message;
      }
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchDevices();
  }, [fetchDevices]);

  return { devices, loading, error, refreshDevices: fetchDevices };
};
