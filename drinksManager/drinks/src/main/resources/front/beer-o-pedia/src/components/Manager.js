import React, { Component } from 'react';
import { Jumbotron, Container, Row, Col } from 'react-bootstrap';
import { Route, Switch } from 'react-router-dom';
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
  }

  async componentDidMount() {
    const response = await fetch(`${API_ROOT}/api/user`, {credentials: 'include'});
    const body = await response.text();
    if (body === '') {
      this.setState(({isAuthenticated: false}))
    } else {
      this.setState({isAuthenticated: true, user: JSON.parse(body)})
    }
  }

	render() {

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

        <Menu isAuthenticated={this.state.isAuthenticated} user={this.state.user} csrfToken={this.state.csrfToken}/>

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
            path="/search/:searchString"
            render={(props) =>
              <BeersList
                searchString={props.match.params.searchString}
                isAuthenticated={false}
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
