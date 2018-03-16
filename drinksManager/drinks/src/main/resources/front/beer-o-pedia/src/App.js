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
	    		<div>
    				<Jumbotron>
						<h1>{ 'Beer-O-Pedia' }</h1>
						<p>{ 'Tu boiras moins bête' }</p>
					</Jumbotron>

					<WelcomeTabs/>
				</div>
	    );
	}
}

class WelcomeTabs extends Component {
  constructor(props) {
	    super();
	    this.state = {
	      activeTab: props.activeTab || 1,
	      colors: []
	    };
    
    this.handleSelect = this.handleSelect.bind(this);
  }
  
  componentDidMount() {
	  let base64 = require('base-64');
	  let url = 'http://localhost:8081/drinkopedia/api/beers/colors/list';
	  let username = 'belzeboss';
	  let password = '666';
	  var request = require('superagent');
	  
	    var self = this;

//      request
//	    .get('http://localhost:8081/drinkopedia/api/beers/colors/list')
//	    .withCredentials()
//	    .auth('belzeboss', '666', {type:'auto'})
//	    .then(response => console.log(response));
//	    .then(function(res) {
//		      // res.body, res.headers, res.status
//		   })
//		   .catch(function(err) {
//		      // err.message, err.response
//		   });
//		  .then(data => {
//			  let colorButtons = data.results.map((color) => {
//				  return (
//						  <MenuItem eventKey="1">{ color.name }</MenuItem>
//				  )
//			  })
//			  debugger;
//			  this.setState({colors: colorButtons});
//		  });
      
		let headers = new Headers();
		headers.append('Authorization', 'Basic' + base64.encode(username + ":" + password));
		
		fetch(url, {
			method:'GET',
			headers: headers,
         })
		.then(response => response.json())
		.then(json => console.log(json));
	    
//  	   .then(results => results.json())
//  	   .then(data => {
//  		  let colorButtons = data.results.map((color) => {
//  			  return (
//  					  <MenuItem eventKey="1">{ color.name }</MenuItem>
//  			  )
//  		  })
//  		  this.setState({colors: colorButtons});
//  	  });
	    
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
						{this.state.colors}
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
