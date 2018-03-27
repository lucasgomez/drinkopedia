import React, { Component } from 'react';
import {Jumbotron} from 'react-bootstrap';
import {
  BrowserRouter as Router,
  Route,
  Link
} from 'react-router-dom';
import BeersList from './../components/BeersList';
import Welcome from './Welcome';

class Manager extends Component {

  constructor(props) {
    super();
    this.state = {
      listName: "",
      listId: ""
    };
  }

	render() {
		return (
			<Router>
    		<div>
  				<Jumbotron>
						<h1>{ 'Beer-O-Pedia' }</h1>
						<p>{ 'Tu boiras moins bête' }</p>
					</Jumbotron>

          <ul>
            <li><Link to="/">Home</Link></li>
            <li><Link to="/welcome">Home</Link></li>
            <li><Link to="/list">List</Link></li>
          </ul>

  				<Route path="/welcome" render={() =>
            <Welcome handleItemClick={this.handleShowList.bind(this)}/>
          }/>
          <Route path="/list/:listName/:listId" component={ListRoute}/>
				</div>
			</Router>
    );
	}

  handleShowList (listName, id) {
    debugger;
    this.setState({
      listName: listName,
      listId: id
    });
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

export default Manager;
