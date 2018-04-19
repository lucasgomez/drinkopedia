import React, { Component } from 'react';
import {ButtonGroup, Button} from 'react-bootstrap';
import ButtonsList from './ButtonsList';

class Menu extends Component {
  render() {
    return (
      <div class="container">
				<ButtonGroup>
					<Button>{ 'Par nom' }</Button>
					<ButtonsList listName="bars" title="Par bar"/>
					<ButtonsList listName="colors" title="Par couleur"/>
					<ButtonsList listName="styles" title="Par style"/>
					<ButtonsList listName="producers" title="Par producteur"/>
					<ButtonsList listName="origins" title="Par origine"/>
				</ButtonGroup>
      </div>
    );
  }
}

export default Menu;
