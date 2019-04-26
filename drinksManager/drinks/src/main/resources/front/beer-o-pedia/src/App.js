import React, { Component } from 'react';
import Manager2 from './components/Manager2';
import { CookiesProvider } from 'react-cookie';

class App extends Component {
  render() {
	  return (
      <CookiesProvider>
        <Manager2 />
      </CookiesProvider>
    );
  }
}

export default App;
