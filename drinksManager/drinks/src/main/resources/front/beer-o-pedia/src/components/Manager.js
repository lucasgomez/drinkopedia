import React, { Component } from 'react';
import {
  Jumbotron,
  Container,
  Row,
  Col,
  Button
} from 'react-bootstrap';
import {
  BrowserRouter as Router,
  Route,
  Switch,
  Link
} from 'react-router-dom';
import BeersList from './BeersList';
import BeerId from './BeerId';
import Menu from './Menu';
import Welcome from './Welcome';
import { withAuth } from '@okta/okta-react';

class Manager extends Component {

  constructor(props) {
    super(props);
    this.state = { isAuthenticated: null };
    this.checkAuthentication = this.checkAuthentication.bind(this);
    this.checkAuthentication();
    this.login = this.login.bind(this);
    this.logout = this.logout.bind(this);
  }

  async checkAuthentication() {
    const isAuthenticated = await this.props.auth.isAuthenticated();
    if (isAuthenticated !== this.state.isAuthenticated) {
      this.setState({ isAuthenticated });
    }
  }

  componentDidUpdate() {
    this.checkAuthentication();
  }

  async login() {
    // Redirect to '/' after login
    this.props.auth.login('/');
  }

  async logout() {
    // Redirect to '/' after logout
    this.props.auth.logout('/');
  }

	render() {

    const button = this.state.isAuthenticated ?
      <Button color="link" onClick={this.logout}>Logout</Button> :
      <Button color="primary" onClick={this.login}>Login</Button>;

		return (
			<Router>
    		<div class="container">
  				<Jumbotron>
            <Container>
              <Row>{button}</Row>
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
            <Route component={Welcome}/>
          </Switch>
				</div>
			</Router>
    );
	}
}

const ListRoute = ({ match }) => (
  <div>
    <BeersList
      listId={match.params.listId}
      listName={match.params.listName}/>
  </div>
);

const BeerRoute = ({ match }) => (
  <div>
    <BeerId beerId={match.params.beerId}/>
  </div>
);

export default withAuth(Manager);
