import axios from 'axios';
import {BACKEND_URL} from "../constants.ts";
import {getToken, setToken} from "./auth.ts";
import {useNavigate} from "react-router";

const apiClient = axios.create({
    baseURL: BACKEND_URL,
    timeout: 10000,
    headers: {
        "Content-Type": "application/json",
    }
});

const authorizedApiClient = axios.create({
    baseURL: BACKEND_URL,
    timeout: 10000,
    headers: {
        "Content-Type": "application/json"
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

authorizedApiClient.interceptors.request.use((config) => {
    const token = getToken();
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    } else{
        console.log("no token provided");
    }
    return config;
});

authorizedApiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        switch (error.response?.status) {
            case 401:{
                setToken("");
                const navigate = useNavigate();
                navigate("/login");
                break;
            }
            default: console.error("other error "+error.response?.status);
        }
    }
)



export {apiClient, authorizedApiClient}