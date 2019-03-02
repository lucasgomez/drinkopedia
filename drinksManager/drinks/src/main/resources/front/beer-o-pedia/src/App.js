import React, { Component } from 'react';
import Manager from './components/Manager';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import { Security, ImplicitCallback } from '@okta/okta-react';

const config = {
  issuer: 'https://dev-824683.okta.com/oauth2/default',
  // redirect_uri: window.location.origin + '/implicit/callback',
  redirect_uri: 'http://localhost:8081/drinkopedia/authorization-code/callback',
  client_id: '0oabhh3e7EfXqH83B356'
}

class App extends Component {
  render() {
    debugger;
	  return (
      <Router>
        <Security issuer={config.issuer}
                  client_id={config.client_id}
                  redirect_uri={config.redirect_uri}>
          <Route path='/' exact={true} component={Manager}/>
          <Route path='/implicit/callback' component={ImplicitCallback}/>
        </Security>
      </Router>
    );
  }
}

export default App;
