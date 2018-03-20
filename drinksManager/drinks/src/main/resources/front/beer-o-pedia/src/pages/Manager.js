import React, { Component } from 'react';
import {Bootstrap, Jumbotron} from 'react-bootstrap';
import Welcome from './Welcome';


class Manager extends Component {
	render() {
		return (
	    		<div>
    				<Jumbotron>
						<h1>{ 'Beer-O-Pedia' }</h1>
						<p>{ 'Tu boiras moins bête' }</p>
					</Jumbotron>

					<Welcome />
				</div>
	    );
	}
}

export default Manager;
