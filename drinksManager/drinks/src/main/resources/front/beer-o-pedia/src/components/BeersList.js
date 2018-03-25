import React, { Component } from 'react';
import {
  Bootstrap,
  Table
} from 'react-bootstrap';

class BeersList extends Component {
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

    let baseUrl = 'http://localhost:8081/drinkopedia/beers/' + this.props.listName + '/' + this.props.listId;

    fetch(baseUrl)
      .then(response => response.json())
      .then(body => body.entity.beers)
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

    if (isLoading) {
      return <p > Loading... < /p>;
    }
    
    return (
      <div class="container">
        <h2>{this.props.title}</h2>
        <p>{this.props.description}</p>
        <Table striped hover>
          <thead>
            <tr>
              <th>Nom</th>
              <th>Brasserie</th>
              <th>Origine</th>
              <th>Alc.</th>
              <th>IBU</th>
              <th>SRM</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item: any) =>
              <tr>
                  <td>{item.name}</td>
                  <td>{item.producerName}</td>
                  <td>{item.producerOriginShortName}</td>
                  <td>{item.abv}</td>
                  <td>{item.ibu}</td>
                  <td>{item.srm}</td>
              </tr>
            )}
          </tbody>
        </Table>
      </div>
    );

  }
}

export default BeersList;
