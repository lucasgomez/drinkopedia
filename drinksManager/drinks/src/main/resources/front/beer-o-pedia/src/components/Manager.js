import React, { Component } from 'react';
import {Jumbotron} from 'react-bootstrap';
import {
  BrowserRouter as Router,
  Route
} from 'react-router-dom';
import BeersList from './BeersList';
import BeerId from './BeerId';
import Menu from './Menu';
import Welcome from './Welcome';

class Manager extends Component {

  constructor(props) {
    super();
  }

	render() {
		return (
			<Router>
    		<div>
  				<Jumbotron>
						<h1>{ 'Zytopedia' }</h1>
						<p>{ 'Tu boiras moins bête' }</p>
					</Jumbotron>

  				<Route path="/" component={Menu}/>
          <Route exactPath="/" component={Welcome}/>
          <Route path="/list/:listName/:listId" component={ListRoute}/>
          <Route path="/beerid/:beerId" component={BeerRoute}/>
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
