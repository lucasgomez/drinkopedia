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
    debugger;
    return (
      //<DropdownButton title={title} id={'buttonsList-'+title} onSelect={(evt, evtKey) => console.log('id', evtKey)/*this.handleItemClick(evt, evtKey)*/}>
      <DropdownButton title={title} id={'buttonsList-'+title} onSelect={(id) => this.handleItemClick(this.listName, id)}>
        {items.map((item: any) =>
          <MenuItem key={'buttonItem-'+listName+'-'+item.id} eventKey={item.id}>
              {item.name}
          </MenuItem>
        )}
      </DropdownButton>
    );

  }

  handleItemClick = (listName) => (id) => {
    debugger;
    alert('id:'+id+' list:'+listName);
    //this.props.handleItemClick(this.props.listName, evtKey);
  }
}

export default ButtonsList;
