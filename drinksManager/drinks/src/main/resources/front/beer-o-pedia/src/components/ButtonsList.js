import React, { Component } from 'react';
import { DropdownButton, Dropdown } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { API_ROOT } from '../data/apiConfig';

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
    let baseUrl = `${API_ROOT}/public/lists/` + this.props.listName;

    fetch(baseUrl)
      .then(response => response.json())
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
      return (
        <DropdownButton title={title} id={'buttonsList-'+title}>
          <Dropdown.Item>
            Loading...
          </Dropdown.Item>
        </DropdownButton>
      );
    }

    return (
      <DropdownButton title={title} id={'buttonsList-'+title}>
        {items.map((item: any) =>
          <Dropdown.Item key={'dropdown-'+title+'-item'+item.id} tag="a" href={'/list/'+listName+'/'+item.id}>
            {item.name}
          </Dropdown.Item>
        )}
      </DropdownButton>
    );
  }
}

export default ButtonsList;
