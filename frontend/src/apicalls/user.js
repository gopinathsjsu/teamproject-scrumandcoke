import { axiosInstance } from ".";

export const Login = async (payload) => {
  try {
    const response = await axiosInstance.post("v0/iam/login", payload);
    return response;
  } catch (error) {
    return error.response;
  }
};

export const SignUp = async (payload) => {
  try {
    const response = await axiosInstance.post("v0/iam/signup", payload);
    return response;
  } catch (error) {
    return error.response;
  }
};

export const Logout = async () => {
  try {
    const response = await axiosInstance.post("v0/iam/logout");
    return response;
  } catch (error) {
    return error.response;
  }
};

export const UpgradeMembership = async () => {
  try {
    const response = await axiosInstance.post("v0/users/me/upgradeMembership");
    return response;
  } catch (error) {
    return error.response;
  }
};

export const GetUpcomingBookings = async (id) => {
  try {
    const response = await axiosInstance.get(`booking/user/${id}/upcoming`);
    return response;
  } catch (error) {
    return error.response;
  }
};

export const GetPastBookings = async (id) => {
  try {
    const response = await axiosInstance.get(`booking/user/${id}/past`);
    return response;
  } catch (error) {
    return error.response;
  }
};

export const CancelBooking = async (id) => {
  try {
    const response = await axiosInstance.post(`booking/${id}`);
    return response;
  } catch (error) {
    return error.response;
  }
};

export const CreateBooking = async (payload) => {
  try {
    const response = await axiosInstance.post(`booking`, payload);
    return response;
  } catch (error) {
    return error.response;
  }
};

//admin
export const GetAnalytics1 = async () => {
  try {
    const response = await axiosInstance.get(
      `v0/analytics/theaterOccupanciesByMovie`
    );
    return response;
  } catch (error) {
    return error.response;
  }
};
