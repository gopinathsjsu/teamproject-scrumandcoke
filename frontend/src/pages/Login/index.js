import React, { useState, useEffect } from "react";
import "./login.css";
import { Form, Button, Row, Col } from "react-bootstrap";
import movieGIF from "../../assets/movie.gif";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { useLoginMutation } from "../../redux/userApiSlice";
import { setCredentials } from "../../redux/authSlice";
import { message } from "antd";

const Login = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [login] = useLoginMutation();

  const { userInfo } = useSelector((state) => state.auth);

  const submitHandler = async (e) => {
    e.preventDefault();
    try {
      const res = await login({ email, password });
      console.log(res);

      if (res.data) {
        dispatch(setCredentials({ ...res }));

        if (res.data.userType === "THEATER_EMPLOYEE") {
          navigate("/admin");
        } else {
          navigate("/");
        }
      } else {
        message.error(res?.error?.data?.errorMessage);
      }
    } catch (err) {
      console.log(err);
      message.error(err?.data?.errorMessage);
    }
  };

  useEffect(() => {
    if (userInfo && userInfo.userType != "THEATER_EMPLOYEE") {
      navigate("/");
    }
  }, [userInfo]);

  return (
    <div className="container-fluid login">
      <div className="row login-row">
        <div className="col-lg-6 login-image-container">
          <img
            src={movieGIF}
            alt="Img not found"
            className="login-img"
            draggable="false"
          />
        </div>
        <div className="col-lg-6 login-form">
          <h2>Login</h2>
          <hr />
          <Form onSubmit={submitHandler}>
            <Form.Group className="mb-3" controlId="email">
              <Form.Label>Email Address</Form.Label>
              <Form.Control
                required
                type="email"
                placeholder="Enter email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              ></Form.Control>
            </Form.Group>

            <Form.Group className="mb-3" controlId="password">
              <Form.Label>Password</Form.Label>
              <Form.Control
                type="password"
                placeholder="Enter password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              ></Form.Control>
            </Form.Group>

            <Button variant="primary" type="submit" className="login-button">
              Submit
            </Button>
          </Form>
          <Row className="py-3">
            <Col>
              New Customer? <Link to="/register">Register</Link>
            </Col>
          </Row>
        </div>
      </div>
    </div>
  );
};

export default Login;
