import React, { Component } from 'react';
import {ButtonGroup, Button, Tabs, Tab} from 'react-bootstrap';
import ButtonsList from './ButtonsList';


class Menu extends Component {
  render() {
    return (
      <div class="container">
				<ButtonGroup>
					<Button>{ 'Par nom' }</Button>
					<Button>{ 'Par bar' }</Button>
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
