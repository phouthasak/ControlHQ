export interface Device {
    id: string;
    externalId: string;
    model: string;
    name: string;
    type: string;
    latititude: number;
    longitude: number;
    relayState: boolean;
    errorCode: number;
}