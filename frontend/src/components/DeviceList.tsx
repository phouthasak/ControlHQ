import { toggleDevice } from "../api/deviceApi";
import { DEFAULT_ITEMS_PER_PAGE } from "../util/endpoints";
import { useDevices } from "../hooks/useDevices";
import type { Device } from "../types/device";
import { classNames } from "primereact/utils";
import { DataView } from "primereact/dataview";

export default function DeviceList() {
    const { devices, loading, error, refreshDevices } = useDevices();

    const handleToggle = async (id: string, currentState: boolean) => {
        try {
            await toggleDevice(id, !currentState);
            refreshDevices();
        } catch (e) {
            console.log(e);
            alert("Failed to toggle device");
        }
    };

    const itemTemplate = (device: Device, index: number) => {
        return (
            <div className="col-12" key={device.id}>
                <div className={classNames('flex flex-column xl:flex-row xl:align-items-start p-4 gap-4', { 'border-top-1 surface-border': index !== 0 })}>
                    {/* <img className="w-9 sm:w-16rem xl:w-10rem shadow-2 block xl:block mx-auto border-round" src={`https://primefaces.org/cdn/primereact/images/product/${product.image}`} alt={device.name} /> */}
                    <div className="flex flex-column sm:flex-row justify-content-between align-items-center xl:align-items-start flex-1 gap-4">
                        <div className="flex flex-column align-items-center sm:align-items-start gap-3">
                            <div className="text-2xl font-bold text-900">{device.name}</div>
                            <div className="flex align-items-center gap-3">
                                <span className="flex align-items-center gap-2">
                                    <i className="pi pi-tag"></i>
                                    <span className="font-semibold">{device.type}</span>
                                </span>
                            </div>
                        </div>
                        <div className="flex sm:flex-column align-items-center sm:align-items-end gap-3 sm:gap-2">
                            <span className="text-2xl font-semibold">{device.externalId}</span>
                        </div>
                    </div>
                </div>
            </div>
        );
    };

    const listTemplate = (items: Device[]) => {
        if (!items || items.length === 0) return null;
        
        const list = items.map((device, index) => {
            return itemTemplate(device, index);
        });
        console.log(list);
        return <div className="grid grid-nogutter">{list}</div>;
    };

    if (loading) {
        return <div className="p-4">Loading devices...</div>;
    }

    if (error) {
        return <div className="p-4 text-red-500">Error: {error}</div>;
    }

    return (
        <div className="card">
            <DataView value={devices} paginator rows={DEFAULT_ITEMS_PER_PAGE} listTemplate={listTemplate} />
        </div>
        // <div className="p-4">
        //     <h2 className="text-2xl font-bold mb-4">Connected Devices</h2>
            
        //     {devices.length === 0 ? (
        //         <p>No devices found.</p>
        //     ) : (
        //         <div className="grid gap-4 grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
        //             {devices.map((device) => (
        //                 <div key={device.id} className="border rounded-lg p-4 shadow-sm bg-white">
        //                     <div className="flex justify-between items-center mb-2">
        //                         <h3 className="font-semibold text-lg">{device.name}</h3>
        //                         <span className={`px-2 py-1 rounded text-xs ${device.relayState ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'}`}>
        //                             {device.relayState ? 'ON' : 'OFF'}
        //                         </span>
        //                     </div>
                            
        //                     <p className="text-gray-600 text-sm mb-4">
        //                         Model: {device.model} <br/>
        //                         Type: {device.type}
        //                     </p>

        //                     <button 
        //                         onClick={() => handleToggle(device.id, device.relayState)}
        //                         className="w-full bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded transition-colors"
        //                     >
        //                         Turn {device.relayState ? 'Off' : 'On'}
        //                     </button>
        //                 </div>
        //             ))}
        //         </div>
        //     )}
        // </div>
    );
};