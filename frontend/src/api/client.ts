import axios from 'axios';
import { API_VERSION, BASE_URL } from '../util/endpoints';

const apiClient = axios.create({
    baseURL: BASE_URL + API_VERSION,
    headers: {
        'Content-Type': 'application/json'
    },
});

apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error("API Error", error);

        if (error.response && error.response.status === 401) {
            // redirect to login
        }

        return Promise.reject(error);
    }
);

export default apiClient;