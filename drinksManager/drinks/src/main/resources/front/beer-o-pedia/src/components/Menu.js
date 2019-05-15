import React, { Component } from 'react';
import {ButtonGroup, Button} from 'react-bootstrap';
import ButtonsList from './ButtonsList';
import { Link, Redirect } from 'react-router-dom';
import SearchField from "react-search-field";

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
      <div className="container-fluid">
        {fireRedirect && (
          <Redirect to={redirectUrl}/>
        )}

				<ButtonGroup>
					<Button>
            <Link to={'/list/'}>
              {'Toutes les Bières'}
            </Link>
          </Button>
					<ButtonsList listName="bars" title="Par bar"/>
					<ButtonsList listName="colors" title="Par couleur"/>
					<ButtonsList listName="styles" title="Par style"/>
					<ButtonsList listName="producers" title="Par producteur"/>
					<ButtonsList listName="origins" title="Par origine"/>
				</ButtonGroup>
        <SearchField
          placeholder="Rechercher..."
          onSearchClick={this.redirectToSearch}
          onEnter={this.redirectToSearch}
        />
      </div>
    );
  }
}

export default Menu;
