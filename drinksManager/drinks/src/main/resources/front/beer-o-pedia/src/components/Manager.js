import React, { Component } from 'react';
import {Jumbotron} from 'react-bootstrap';
import {
  BrowserRouter as Router,
  Route,
  Link
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
						<h1>{ 'Beer-O-Pedia' }</h1>
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
      listName={match.params.listName}
      title="IPA"
      description="Les Indian Pale Ale (IPA), c'est bon. Si si! C'est bon"/>
  </div>
);

const BeerRoute = ({ match }) => (
  <div>
    <BeerId beerId={match.params.beerId}/>
  </div>
);

export default Manager;
