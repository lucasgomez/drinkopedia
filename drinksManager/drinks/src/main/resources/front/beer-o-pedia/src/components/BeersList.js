import React, { Component } from 'react';
import {
  Table,
  Popover,
  OverlayTrigger,
  Button
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
    debugger;
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
    //TODO Better tooltip formating with bar names
    return (
      <div class="container">
        <h2>{this.props.title}</h2>
        <p>{this.props.description}</p>
        <Table striped hover>
          <thead>
            <tr>
              <th>Bière</th>
              <th>Brasserie</th>
              <th>Origine</th>
              <th>Alc. (%)</th>
              <th>Type</th>
              <th>Couleur</th>
              <th>Prix</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item: any) =>
              <tr>
                  <td>{item.name}</td>
                  <td>{item.producerName}</td>
                  <td>{item.producerOriginShortName}</td>
                  <td>{item.abv}</td>
                  <td>{item.styleName}</td>
                  <td>{item.colorName}</td>
                  <td>{this.formatPricesList(item)}</td>
              </tr>
            )}
          </tbody>
        </Table>
      </div>
    );

  }

  formatPricesList(beer) {
    let pricesList = [];
    let detailsList = [];

    if (beer.bottleVolumeInCl) {
      pricesList.push(beer.bottleSellingPrice + ".-");
      detailsList.push(beer.bottleVolumeInCl + "cl " + beer.bottleSellingPrice + ".-");
    }
    if (beer.tapPriceSmall) {
      pricesList.push(beer.tapPriceSmall + ".-");
      pricesList.push(beer.tapPriceBig + ".-");
      detailsList.push("30 cl " + beer.tapPriceSmall + ".-");
      detailsList.push("50 cl " + beer.tapPriceBig + ".-");
    }

    const popover = (
      <Popover id={"popover-"+beer.id+"-price"} title="Vente">
        <ul>
          {detailsList.map((price: any) => <li>{price}</li>)}
        </ul>
      </Popover>
    );

    return (
      <OverlayTrigger trigger="click" placement="left" overlay={popover}>
        <div>{pricesList.join(" / ")}</div>
      </OverlayTrigger>
    );
  }
}

export default BeersList;
