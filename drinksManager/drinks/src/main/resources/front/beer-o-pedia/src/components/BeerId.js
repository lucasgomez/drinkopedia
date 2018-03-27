import React, { Component } from 'react';
import {
  Well,
  Label
} from 'react-bootstrap';

class BeersId extends Component {
  constructor(props: any) {
    super(props);

    this.state = {
      beer: null,
      isLoading: false
    };
  }

  componentDidMount() {
    this.setState({
      isLoading: true
    });

    let baseUrl = 'http://localhost:8081/drinkopedia/beers/' + this.props.beerId;

    fetch(baseUrl)
      .then(response => response.json())
      .then(body => body.entity)
      .then(item =>
        this.setState({
          beer: item,
          isLoading: false
        })
      );
  }

  render() {
    const {
      beer,
      isLoading
    } = this.state;

    if (isLoading) {
      return <p > Loading... < /p>;
    }

    debugger;
    return (
      <div class="container">
        <h2>{this.props.title}</h2>
        <p>{this.props.description}</p>
        <Well>
          <Table>
            <tr>
              <td><Label>Alcool</Label></td>
              <td><Label>Couleur</Label></td>
            </tr><tr>
              <td>{beer.abv}</td>
              <td>{beer.colorName}</td>
            </tr><tr>
              <td><Label>Style</Label></td>
              <td><Label>Fermentation</Label></td>
            </tr><tr>
              <td>{beer.styleName}</td>
              <td>{beer.fermenting}</td>
            </tr>
          </Table>
        </Well>
      </div>
    );

  }
}

export default BeersId;
