import React, { Component } from 'react';
import {
  Table,
  Tooltip,
  OverlayTrigger
} from 'react-bootstrap';
import { Link } from 'react-router-dom';
import Popup from 'reactjs-popup'
import Emoji from './Emoji';

class BeersList extends Component {

  constructor(props: any) {
    super(props);

    this.state = {
      items: [],
      title: "",
      description: "",
      isLoading: false,
      isDisplayingBar: false,
      isAuthenticated: this.props.isAuthenticated
    };
  }

  componentDidMount() {
    this.fetchData(this.props.listName, this.props.listId);
  }

  componentWillReceiveProps(nextProps) {
    let oldListId = this.props.listId;
    let newListId = nextProps.listId;
    let oldListName = this.props.listName;
    let newListName = nextProps.listName;
    if (oldListId !== newListId || oldListName !== newListName) {
         this.fetchData(newListName, newListId);
    }
  }

  fetchData = async (listName, listId) => {
    this.setState({isLoading: true});

    let listUrl = '/public/beers/';
    if (listName && listId)
      listUrl += listName + '/' + listId;

    fetch(listUrl, {credentials: 'include'})
      .then(response => response.json())
      .then(list =>
        this.setState({
          items: list.beers,
          title: list.name,
          description: list.description,
          isDisplayingBar: this.props.listName && this.props.listId && this.props.listName === 'bars',
          isLoading: false
        })
      );
  }

  render() {
    const {
      items,
      title,
      description,
      isLoading,
      isAuthenticated
    } = this.state;

    if (isLoading) {
      return <p > Loading... < /p>;
    }
    //TODO Better tooltip formating with bar names or multiline prices + tooltip for bars
    return (
      <div class="container">
        <h2>{title}</h2>
        <p>{description}</p>
        <Table striped hover>
          <thead>
            <tr>
              <th>Bière</th>
              <th>Brasserie</th>
              <th>Origine</th>
              <th>Alc. (%)</th>
              <th>Type</th>
              <th>Couleur</th>
              <th>Prix (25/50 cl)</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item: any) =>
              <tr>
                  <td>
                    <Link to={'/beerid/'+item.id}>
                      {item.name}
                    </Link>
                  </td><td>
                    <Link to={'/list/producers/'+item.producerId}>
                      {item.producerName}
                    </Link>
                  </td><td>
                    <Link to={'/list/origins/'+item.producerOriginId}>
                      {item.producerOriginShortName}
                    </Link>
                  </td><td>
                    {item.abv}
                  </td><td>
                    <Link to={'/list/styles/'+item.styleId}>
                      {item.styleName}
                    </Link>
                  </td><td>
                    <Link to={'/list/colors/'+item.colorId}>
                      {item.colorName}
                    </Link>
                  </td><td>
                    {this.formatPricesList(item)}
                  </td><td>
                    {this.formatIsActive(item)}
                  </td><td>
                    {this.displayTapActivity(item)}
                  </td>
                  {this.displayPriceCaclulation(item, isAuthenticated)}
              </tr>
            )}
          </tbody>
        </Table>
      </div>
    );

  }

  displayPriceCaclulation(beer, isAuthenticated) {
    if (!isAuthenticated)
      return null;
    else
      return (<td>Woohoo</td>);
  }

  displayTapActivity(beer) {
    if (!beer.tapPriceSmall && !beer.tapPriceBig)
      return <Emoji symbol="🚫" label="Pas de service pression"/>;

    const button = beer.tapAvailability
      ? <Emoji symbol="✅" label="Désactiver le service pression"/>
      : <Emoji symbol="❌" label="Activer le service pression"/>;
    return (
      <Popup
        trigger={<button className="button"> {button} </button>}
        modal
        closeOnDocumentClick
      >
        <span> Disable? </span>
      </Popup>
    );
  }

  formatIsActive(beer) {
    if (beer.active)
      return null;
    else {
      const popover = (
        <Tooltip id={"tooltip-"+beer.id+"-inactive"}>
          <div>{beer.activationDate ? "Epuisée" : "Pas encore servie"}</div>
        </Tooltip>
      );
      return (
        <OverlayTrigger placement="left" overlay={popover}>
          <div>{beer.activationDate ? "❌" : "🕐" }</div>
        </OverlayTrigger>
      );
    }
  }

  formatPricesList(beer) {
    let pricesList = [];
    let detailsList = [];

    if (beer.bottleVolumeInCl) {
      pricesList.push(Number(beer.bottleSellingPrice).toFixed(2) + ".-");
      detailsList.push(beer.bottleVolumeInCl + "cl " + Number(beer.bottleSellingPrice).toFixed(2) + ".-");
    }
    if (beer.tapPriceSmall && beer.tapPriceBig) {
      let priceSmall = Number(beer.tapPriceSmall).toFixed(2);
      let priceBig = Number(beer.tapPriceBig).toFixed(2);
      pricesList.push(priceSmall + ".-");
      pricesList.push(priceBig + ".-");
      detailsList.push("25 cl " + priceSmall + ".-");
      detailsList.push("50 cl " + priceBig + ".-");
    }

    const popover = (
      <Tooltip id={"tooltip-"+beer.id+"-price"}>
        <ul>
          {detailsList.map((price: any) => <li>{price}</li>)}
        </ul>
      </Tooltip>
    );

    return (
      <OverlayTrigger placement="left" overlay={popover}>
        <div>{pricesList.join(" / ")}</div>
      </OverlayTrigger>
    );
  }
}

export default BeersList;
