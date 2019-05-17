import React, { Component } from 'react';
import { Button, Container } from 'react-bootstrap';
import { API_ROOT } from '../data/apiConfig';

class LoginManager extends Component {

  constructor(props) {
    super(props);
    this.login = this.login.bind(this);
    this.logout = this.logout.bind(this);
  }

  login() {
    let port = window.location.port ? ':' + window.location.port : '';

    if (port === ':3000') {
      port = ':8080';
    }
    window.location.href = '//' + window.location.hostname + port + '/private';
  }

  logout() {
    fetch(
      `${API_ROOT}/api/logout`,
      {
        method: 'POST',
        credentials: 'include',
        headers: {'X-XSRF-TOKEN': this.props.csrfToken}
      })
    .then(res => res.json())
    .then(response => {
      window.location.href = response.logoutUrl + "?id_token_hint=" +
          response.idToken + "&post_logout_redirect_uri=" + window.location.origin;
    });
  }

  render() {
    const message = this.props.user ?
          <h2>Welcome, {this.props.user.name}!</h2> :
          <p>Welcome, guest. Please remind me this message should disappear</p>;
debugger;
    const button = this.props.isAuthenticated ?
      <Button color="link" onClick={this.logout}>Logout</Button> :
      <Button color="primary" onClick={this.login}>Login</Button>;
    return (
      <div>
        {button}
      </div>
    );
  }
}

export default LoginManager;
