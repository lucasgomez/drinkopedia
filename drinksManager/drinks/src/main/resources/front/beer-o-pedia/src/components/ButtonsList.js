import React, { Component } from 'react';
import {
  Bootstrap,
  DropdownButton,
  MenuItem
} from 'react-bootstrap';

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

    return (
      <DropdownButton title={title} id={'buttonsList-'+title} onSelect={(id) => this.handleItemClick(id)}>
        {items.map((item: any) =>
          <MenuItem key={'buttonItem-'+listName+'-'+item.id} eventKey={item.id}>
              {item.name}
          </MenuItem>
        )}
      </DropdownButton>
    );

  }

  handleItemClick(id) {
    debugger;
    if (this.props.handleItemClick != null)
      this.props.handleItemClick(this.props.listName, id);
  }
}

export default ButtonsList;
