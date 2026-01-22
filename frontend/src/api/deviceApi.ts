import apiClient from "./client";
import type { Device } from '../types/device';

export const getDevices = async (): Promise<Device[]> => {
    const response = await apiClient.get<Device[]>('/devices');
    return response.data.devices;
};

export const toggleDevice = async (deviceId: string, state: boolean): Promise<Device> => {
    const response = await apiClient.post<Device>(`/devices/${deviceId}/relay`, {
        relayState: state
    });
    return response.data;
};