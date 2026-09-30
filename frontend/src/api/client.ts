import axios from 'axios';
import {BACKEND_URL} from "../constants.ts";

const apiClient = axios.create({
    baseURL: BACKEND_URL,
    timeout: 10000,
    headers: {
        "Content-Type": "application/json",
    }
});

apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        if([401,409,422,429].includes(error.response?.status)) {
            return Promise.reject(error);
        }

        switch (error.response?.status) {
            default: console.error("handler")
        }
        return Promise.reject(error);
    }
)



export default apiClient;