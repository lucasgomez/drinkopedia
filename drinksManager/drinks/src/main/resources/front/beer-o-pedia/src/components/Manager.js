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
import TapBeersDisplay from './TapBeersDisplay';
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
    debugger;
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
          <p>Please log in to manage your JUG Tour.</p>;

    const button = this.state.isAuthenticated ?
      <div>
        <Button color="link"><Link to="/groups">Manage JUG Tour</Link></Button>
        <br/>
        <Button color="link" onClick={this.logout}>Logout</Button>
      </div> :
      <Button color="primary" onClick={this.login}>Login</Button>;

		return (
      <Container>
  			<Router>
            <Switch>
              <Route path="/beamer" component={BeamerRoute}/>
              <Route component={WithMenuRoute}/>
            </Switch>
  			</Router>

        <Container fluid>
          {message}
          {button}
        </Container>
      </Container>
    );
	}
}

const WithMenuRoute = ({ match }) => (
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
    
    <Switch>
      <Route path="/list/:listName/:listId" component={ListRoute}/>
      <Route path="/list" component={ListRoute}/>
      <Route path="/beerid/:beerId" component={BeerRoute}/>
      <Route path="/edit/beer/:beerId" component={EditBeerRoute}/>
      <Route component={Welcome}/>
    </Switch>
  </Container>
);

const ListRoute = ({ match }) => (
  <div>
    <BeersList
      listId={match.params.listId}
      listName={match.params.listName}/>
  </div>
);

const BeamerRoute = ({ match }) => (
  <div>
    <TapBeersDisplay
      listId={666}
      listName={"bars"}/>
  </div>
);

const BeerRoute = ({ match }) => (
  <div>
    <BeerId beerId={match.params.beerId}/>
  </div>
);

const EditBeerRoute = ({ match }) => (
  <div>
    <EditBeer beerId={match.params.beerId}/>
  </div>
);

export default withCookies(Manager);
