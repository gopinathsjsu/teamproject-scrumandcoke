// import axios from "axios";

// export const axiosInstance = axios.create({
//   // baseURL: "http://movietheaterclub-1444714557.us-east-2.elb.amazonaws.com/",
//   baseURL: "https://fc31-2601-646-a100-cbf0-e164-ed22-95cd-68aa.ngrok-free.app",
//   headers: {
//     "Content-Type": "application/json",
//   },
//   withCredentials: true,
// });

import axios from "axios";
import store from "../redux/store"; // Import your Redux store

// Create an Axios instance
export const axiosInstance = axios.create({
  baseURL: "http://movietheaterclub-1444714557.us-east-2.elb.amazonaws.com/",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});

// Request Interceptor for Headers
axiosInstance.interceptors.request.use(
  (config) => {
    // Get the user info from Redux store
    const state = store.getState();
    const userInfo = state.auth.userInfo; // Adjust this according to your Redux state structure

    if (userInfo && userInfo.sessionId) {
      // Set the session ID in headers
      config.headers["x-session-id"] = userInfo.sessionId;
    }

    return config;
  },
  (error) => {
    // Handle request error
    return Promise.reject(error);
  }
);
