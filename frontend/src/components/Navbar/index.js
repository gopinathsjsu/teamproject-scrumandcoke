import React from "react";
import { NavLink } from "react-router-dom";
import "./navbar.css";
import { useSelector, useDispatch } from "react-redux";
import { logout } from "../../redux/authSlice";
import { useLogoutMutation } from "../../redux/userApiSlice";
import { useNavigate } from "react-router-dom";

const Navbar = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const { userInfo } = useSelector((state) => state.auth);

  const [logoutApiCall] = useLogoutMutation();

  const logoutHandler = async () => {
    try {
      await logoutApiCall();
      dispatch(logout());
      navigate("/login");
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <nav className="navbar navbar-expand-lg">
      <a className="navbar-brand" href="/">
        MovieWeb
      </a>
      <button
        className="navbar-toggler"
        type="button"
        data-toggle="collapse"
        data-target="#navbarNavDropdown"
        aria-controls="navbarNavDropdown"
        aria-expanded="false"
        aria-label="Toggle navigation"
      ></button>
      <div className="collapse navbar-collapse" id="navbarNavDropdown">
        <ul className="navbar-nav main-nav">
          <li className="nav-item">
            <NavLink className="nav-link" to="/">
              Home
            </NavLink>
          </li>
          <li className="nav-item">
            <NavLink className="nav-link" to="/buy-membership">
              Buy Membership
            </NavLink>
          </li>
        </ul>

        {userInfo ? (
          <ul className="navbar-nav2 ml-auto">
            <li className="nav-item">
              <p className="username">Welcome {userInfo.data.firstName}!</p>
            </li>
            <li className="nav-item">
              <NavLink>
                <button
                  className="btn btn-outline-success"
                  onClick={logoutHandler}
                >
                  Logout
                </button>
              </NavLink>
            </li>
          </ul>
        ) : (
          <ul className="navbar-nav ml-auto">
            <li className="nav-item">
              <NavLink to="/login">
                <button className="btn btn-outline-success">Login</button>
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink to="/register">
                <button className="btn btn-outline-success">Register</button>
              </NavLink>
            </li>
          </ul>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
