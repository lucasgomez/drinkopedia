import React, { Component } from 'react';
import Manager from './components/Manager';
import { CookiesProvider } from 'react-cookie';
import {
  BrowserRouter as Router,
  Route,
  Switch
} from 'react-router-dom';
import TapBeersDisplay from './components/TapBeersDisplay';

class App extends Component {
  render() {
	  return (
      <CookiesProvider>
  			<Router>
            <Switch>
              <Route path="/beamer" component={BeamerRoute}/>
              <Manager/>
            </Switch>
  			</Router>
      </CookiesProvider>
    );
  }
}

const BeamerRoute = ({ match }) => (
  <div>
    <TapBeersDisplay
      listId={666}
      listName={"bars"}/>
  </div>
);

export default App;
