const { axiosInstance } = require(".");

//admin and user
export const GetAllMovies = async () => {
  try {
    const response = await axiosInstance.get("movie");
    return response;
  } catch (error) {
    return error.response;
  }
};

//admin
export const UpdateMovie = async (payload) => {
  try {
    const response = await axiosInstance.put("movie", payload);
    return response;
  } catch (error) {
    return error.response;
  }
};

//admin
export const DeleteMovie = async (id) => {
  try {
    const response = await axiosInstance.delete(`movie/${id}`);
    return response;
  } catch (error) {
    return error.response;
  }
};

//user
export const GetMovieById = async (id) => {
  try {
    const response = await axiosInstance.get(`movie/${id}`);
    return response;
  } catch (error) {
    return error;
  }
};

//user
export const GetCurrentlyPlaying = async () => {
  try {
    const response = await axiosInstance.get("showtime/movies/upcoming");
    return response;
  } catch (error) {
    return error.response;
  }
};

//user
export const GetUpcomingShows = async () => {
  try {
    const response = await axiosInstance.get("movie/upcoming");
    return response;
  } catch (error) {
    return error.response;
  }
};
