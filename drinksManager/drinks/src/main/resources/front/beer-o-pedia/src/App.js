import React, { Component } from 'react';
//import logo from './logo.svg';
//import './App.css';
//import 'bootstrap/less/bootstrap.less'
import {Bootstrap, Row, Col, ButtonGroup, Button, DropdownButton, MenuItem, Tabs, Tab, Jumbotron} from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap/dist/css/bootstrap-theme.css';

class App extends Component {
  render() {
	  return (
		      <div className="game">
		        <div className="game-board">
		          <Welcome />
		        </div>
		      </div>
		    );
  }
}

class Welcome extends Component {
	render() {
		return (
	    		<container>
    				<Jumbotron>
						<h1>{ 'Beer-O-Pedia' }</h1>
						<p>{ 'Tu boiras moins bête' }</p>
					</Jumbotron>

					<WelcomeTabs/>
				</container>
	    );
	}
}

class WelcomeTabs extends Component {
  constructor(props) {
	    super();
	    this.state = {
      activeTab: props.activeTab || 1
    };
    
    this.handleSelect = this.handleSelect.bind(this);
  }
  
  render() {
    return (
		<Tabs>
		<Tab eventKey="1" title={ 'Les cartes de Bière' }>
			<p>{ 'Tu trouveras ci-dessous les listes de nos Bières classées :' }</p>
			<ButtonGroup vertical>
				<Button>{ 'Par nom' }</Button>
				<Button>{ 'Par bar' }</Button>
				<DropdownButton title="Par couleur" id="bg-vertical-dropdown-1">
					<MenuItem eventKey="1">{ 'Blanche' }</MenuItem>
					<MenuItem eventKey="2">{ 'Blonde' }</MenuItem>
					<MenuItem eventKey="3">{ 'Rousse' }</MenuItem>
					<MenuItem eventKey="4">{ 'Brune / Noire' }</MenuItem>
				</DropdownButton>
			</ButtonGroup>
		</Tab>
		<Tab eventKey="2" title={ 'En savoir plus' }>
			<p>{ 'Qui n\'a jamais rêvé de tout savoir sur la Bière? Désormais, c\'est possible!' }</p>
			</Tab>
		</Tabs>
    );
  }
  
  handleSelect(selectedTab) {
    this.setState({
      activeTab: selectedTab
    });
  }
}

export default App;
