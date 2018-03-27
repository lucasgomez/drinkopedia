import React, { Component } from 'react';
import {
  DropdownButton,
  MenuItem
} from 'react-bootstrap';
import {
  Link
} from 'react-router-dom';

class ButtonsList extends Component {
  constructor(props: any) {
    super(props);

    this.state = {
      items: [],
      isLoading: false
    };
  }

  componentDidMount() {
    this.setState({
      isLoading: true
    });

    let baseUrl = 'http://localhost:8081/drinkopedia/' + this.props.listName + '/list';

    fetch(baseUrl)
      .then(response => response.json())
      .then(body => body.entity)
      .then(items =>
        this.setState({
          items: items,
          isLoading: false
        })
      );
  }

  render() {
    const {
      items,
      isLoading
    } = this.state;
    const listName = this.props.listName;
    const title = this.props.title;

    if (isLoading) {
      return <p > Loading... < /p>;
    }
    //<DropdownButton title={title} id={'buttonsList-'+title} onSelect={(id) => this.handleItemClick(id)}>
    return (
      <DropdownButton title={title} id={'buttonsList-'+title}>
        {items.map((item: any) =>
          <MenuItem>
            <Link to={'/list/'+listName+'/'+item.id}>
              {item.name}
            </Link>
          </MenuItem>
        )}
      </DropdownButton>
    );

  }

  handleItemClick(id) {
    if (this.props.handleItemClick != null)
      this.props.handleItemClick(this.props.listName, id);
  }
}

export default ButtonsList;
