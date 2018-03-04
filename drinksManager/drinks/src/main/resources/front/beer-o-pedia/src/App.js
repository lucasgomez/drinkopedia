import React, { Component } from 'react';
//import logo from './logo.svg';
//import './App.css';
//import 'bootstrap/less/bootstrap.less'
import {Bootstrap, Grid, Row, Col} from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap/dist/css/bootstrap-theme.css';

class App extends Component {
  render() {
    return (
		<div className="page-header">
			<h1>Beer-O-Pedia<br/>
			<small>Tu boiras moins bete</small></h1>
		</div>
    );
  }
}

export default App;
