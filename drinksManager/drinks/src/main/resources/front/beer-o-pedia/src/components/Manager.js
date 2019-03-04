import React, { Component } from 'react';
import {
  Jumbotron,
  Container,
  Row,
  Col
} from 'react-bootstrap';
import {
  BrowserRouter as Router,
  Route,
  Switch
} from 'react-router-dom';
import BeersList from './BeersList';
import BeerId from './BeerId';
import Menu from './Menu';
import Welcome from './Welcome';

class Manager extends Component {

  constructor(props) {
    super(props);
  }

	render() {

		return (
			<Router>
    		<div class="container">
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

export default Manager;
