import React, { Component } from 'react';
import {
  Jumbotron,
  Container,
  Row,
  Col,
  Button
} from 'react-bootstrap';
import { Link } from 'react-router-dom';
import {
  BrowserRouter as Router,
  Route,
  Switch
} from 'react-router-dom';
import BeersList from './BeersList';
import BeerId from './BeerId';
import EditBeer from './EditBeer';
import Menu from './Menu';
import Welcome from './Welcome';
import { withCookies } from 'react-cookie';
import { API_ROOT } from '../data/apiConfig';

class Manager extends Component {

  state = {
    isLoading: true,
    isAuthenticated: false,
    user: undefined
  };

  constructor(props) {
    super(props);
    const {cookies} = props;
    this.state.csrfToken = cookies.get('XSRF-TOKEN');
    this.login = this.login.bind(this);
    this.logout = this.logout.bind(this);
  }

  async componentDidMount() {
    const response = await fetch(`/api/user`, {credentials: 'include'});
    const body = await response.text();
    if (body === '') {
      this.setState(({isAuthenticated: false}))
    } else {
      this.setState({isAuthenticated: true, user: JSON.parse(body)})
    }
  }

  login() {
    let port = window.location.port ? ':' + window.location.port : '';

    if (port === ':3000') {
      port = ':8080';
    }
    window.location.href = '//' + window.location.hostname + port + '/private';
  }

  logout() {
    fetch('/api/logout', {method: 'POST', credentials: 'include',
      headers: {'X-XSRF-TOKEN': this.state.csrfToken}}).then(res => res.json())
      .then(response => {
        window.location.href = response.logoutUrl + "?id_token_hint=" +
          response.idToken + "&post_logout_redirect_uri=" + window.location.origin;
      });
  }

	render() {
    const message = this.state.user ?
          <h2>Welcome, {this.state.user.name}!</h2> :
          <p>Welcome, guest. Please remind me this message should disappear</p>;

    const button = this.state.isAuthenticated ?
      <Button color="link" onClick={this.logout}>Logout</Button> :
      <Button color="primary" onClick={this.login}>Login</Button>;

		return (
      <Container>
        <Jumbotron>
          <Container>
            <Row>
              <Col xs={12} md={4}>
                <h1>{ 'Zythopedia' }</h1>
                <p>{ 'Tu boiras moins bête' }</p>
              </Col>
              <Col xs={12} md={4}/>
              <Col xs={12} md={4}>
                <img alt='logo sumo fetedelabiere' src={ require('./images/sumo300.png')}/>
              </Col>
            </Row>
          </Container>
        </Jumbotron>

        <Menu/>
        <Container fluid>
          {message}
          {button}
        </Container>

        <Switch>
          <Route
            path="/list/:listName/:listId"
            render={(props) =>
              <BeersList
                listId={props.match.params.listId}
                listName={props.match.params.listName}
                isAuthenticated={this.state.isAuthenticated}
              />}
          />
          <Route
            path="/list"
            render={(props) =>
              <BeersList
                listId={props.match.params.listId}
                listName={props.match.params.listName}
                isAuthenticated={this.state.isAuthenticated}
                />}
            />
          <Route
            path="/beerid/:beerId"
            render={(props) =>
              <BeerId
                beerId={props.match.params.beerId}
                isAuthenticated={this.state.isAuthenticated}
                />}
            />
          <Route
            path="/edit/beer/:beerId"
            render={(props) =>
              <EditBeer beerId={props.match.params.beerId}
                isAuthenticated={this.state.isAuthenticated}
                />}
            />
          <Route
            component={Welcome}
            />
        </Switch>
      </Container>
    );
	}
}

export default withCookies(Manager);
