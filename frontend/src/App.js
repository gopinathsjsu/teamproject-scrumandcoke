import React from "react";
import "./App.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { useSelector } from "react-redux";
import Login from "./pages/Login";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import Register from "./pages/Register";
import Home from "./pages/Home";
import Sorry from "./pages/Sorry";
import Admin from "./pages/Admin-Home";
import Movie from "./pages/Movie";
import Booking from "./pages/Booking";
import Checkout from "./pages/Checkout";
import MembershipPage from "./pages/Membership";
import Tickets from "./pages/Tickets";
import AnalyticsDashboard from "./pages/Admin-Stats";
import MoviesForTheater from "./pages/Movies-For-Theater";
import TheatersForLocation from "./pages/Theaters-For-Location";
import MembershipCheckout from "./pages/Membership-Checkout";
import { Navigate } from "react-router-dom";

const App = () => {
  const { userInfo } = useSelector((state) => state.auth);

  const Menu = () => {
    return (
      <Routes>
        <Route path="login" element={<Login />} />
        <Route path="register" element={<Register />} />
        <Route path="*" element={<Sorry />} />
        <Route path="/" element={<Home />} />
        <Route path="/movie/:id" element={<Movie />} />
        <Route path="/booking" element={<Booking />} />
        <Route path="/checkout" element={<Checkout />} />
        <Route path="/buy-membership" element={<MembershipPage />} />
        <Route
          path="/buy-membership/checkout"
          element={<MembershipCheckout />}
        />
        <Route path="/tickets" element={<Tickets />} />
        <Route path="/theater/:id" element={<MoviesForTheater />} />
        <Route path="/location/:id" element={<TheatersForLocation />} />

        <Route
          path="/admin"
          element={
            userInfo?.userType === "THEATER_EMPLOYEE" ? (
              <Admin />
            ) : (
              <Navigate to="/" />
            )
          }
        />
        <Route
          path="/analytics"
          element={
            userInfo?.userType === "THEATER_EMPLOYEE" ? (
              <AnalyticsDashboard />
            ) : (
              <Navigate to="/" />
            )
          }
        />
      </Routes>
    );
  };

  return (
    <div className="main-app">
      <BrowserRouter>
        <Navbar />
        <Menu />
        <Footer />
      </BrowserRouter>
    </div>
  );
};

export default App;
