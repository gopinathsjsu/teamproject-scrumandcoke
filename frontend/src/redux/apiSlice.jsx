// import { fetchBaseQuery, createApi } from "@reduxjs/toolkit/query/react";

// import { logout } from "./authSlice";

// const baseQuery = fetchBaseQuery({
//   baseUrl: "http://movietheaterclub-1444714557.us-east-2.elb.amazonaws.com/v0/",
//   credentials: "include",
//   withCredentials: true,
// });

// async function baseQueryWithAuth(args, api, extra) {
//   const result = await baseQuery(args, api, extra);

//   if (result.error && result.error.status === 401) {
//     api.dispatch(logout());
//   }
//   return result;
// }

// export const apiSlice = createApi({
//   baseQuery: baseQueryWithAuth,
//   tagTypes: ["User"],
//   endpoints: (builder) => ({}),
// });

import { fetchBaseQuery, createApi } from "@reduxjs/toolkit/query/react";
import { logout } from "./authSlice";

const baseQuery = fetchBaseQuery({
  baseUrl: "http://movietheaterclub-1444714557.us-east-2.elb.amazonaws.com/v0/",
  credentials: "include",
  prepareHeaders: (headers, { getState }) => {
    // Use getState to access the Redux state
    const state = getState();
    const userInfo = state.auth.userInfo; // Adjust this according to your Redux state structure

    if (userInfo && userInfo.sessionId) {
      // Set the Cookie header with the session ID
      headers.set("x-session-id", `${userInfo.sessionId}`);
    }
    return headers;
  },
});

async function baseQueryWithAuth(args, api, extra) {
  const result = await baseQuery(args, api, extra);

  if (result.error && result.error.status === 401) {
    api.dispatch(logout());
  }
  return result;
}

export const apiSlice = createApi({
  baseQuery: baseQueryWithAuth,
  tagTypes: ["User"],
  endpoints: (builder) => ({}),
});
