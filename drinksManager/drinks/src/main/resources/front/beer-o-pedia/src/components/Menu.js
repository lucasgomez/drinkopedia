import React, { Component } from 'react';
import { Nav, Navbar } from 'react-bootstrap';
import ButtonsList from './ButtonsList';
import { Redirect } from 'react-router-dom';
import SearchField from "react-search-field";
import Emoji from './Emoji';
import LoginManager from './LoginManager';

class Menu extends Component {
  constructor(props: any) {
    super(props);

    this.state = {
      fireRedirect: false,
      redirectUrl: null,
    };
  }

  redirectToSearch = (value) => {
    this.setState({
      fireRedirect: true,
      redirectUrl: '/search/'+encodeURIComponent(value)
    });
  }

  render() {

    const {
      fireRedirect,
      redirectUrl,
    } = this.state;

    return (
        <Navbar bg="dark" variant="dark" sticky="top">

          {fireRedirect && (
            <Redirect to={redirectUrl}/>
          )}

          <Navbar.Brand href="/"><Emoji symbol="🍺" label="Home"/></Navbar.Brand>
          <Navbar.Toggle aria-controls="basic-navbar-nav" />
          <Navbar.Collapse id="basic-navbar-nav">
            <Nav className="mr-auto">
              <Nav.Link href='/list/'>Toutes les Bières</Nav.Link>

    					<ButtonsList listName="bars" title="Par bar"/>
    					<ButtonsList listName="colors" title="Par couleur"/>
    					<ButtonsList listName="styles" title="Par style"/>
    					<ButtonsList listName="producers" title="Par producteur"/>
    					<ButtonsList listName="origins" title="Par origine"/>
            </Nav>

            <LoginManager className="float-right" isAuthenticated={this.props.isAuthenticated} user={this.props.user} csrfToken={this.props.csrfToken}/>

            <SearchField className="float-right"
            placeholder="Rechercher..."
            onSearchClick={this.redirectToSearch}
            onEnter={this.redirectToSearch}
            />
          </Navbar.Collapse>
        </Navbar>
    );
  }
}

export default Menu;
