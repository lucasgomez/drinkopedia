import React, { Component } from 'react';
import { Table, Tooltip, Button, OverlayTrigger } from 'react-bootstrap';
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
      isDisplayingAdditionalStuff: false,
    };

    this.toggleIsDisplayingAdditionalStuff = this.toggleIsDisplayingAdditionalStuff.bind(this);
  }

  toggleIsDisplayingAdditionalStuff() {
    this.setState(state => ({
      isDisplayingAdditionalStuff: !state.isDisplayingAdditionalStuff
    }));
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

    let listUrl = this.props.isAuthenticated ? '/public' : '/private';
    listUrl += '/beers/';
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
      isDisplayingAdditionalStuff,
    } = this.state;
    if (isLoading) {
      return <p > Loading... < /p>;
    }
    //TODO Better tooltip formating with bar names or multiline prices + tooltip for bars
    return (
      <div class="container">
        <h2>{title}</h2>
        <p>{description}</p>
        {this.props.isAuthenticated &&
          <Button className="float-right" onClick={this.toggleIsDisplayingAdditionalStuff}>
            {this.state.isDisplayingAdditionalStuff
              ? <Emoji symbol="➖" label="Masquer colonnes supplementaires"/>
              : <Emoji symbol="➕" label="Afficher colonnes supplementaires"/>
            }
          </Button>
        }

        <Table striped hover>
          <thead>
            {this.state.isDisplayingAdditionalStuff ?
                <tr>
                  <th colspan="6"/>
                  <th/>
                  <th colspan="3">25 cl</th>
                  <th colspan="3">50 cl</th>
                </tr> : <div/>
            }
            <tr>
              <th>Bière</th>
              <th>Brasserie</th>
              <th>Origine</th>
              <th>Alc. (%)</th>
              <th>Type</th>
              <th>Couleur</th>
              {!this.state.isDisplayingAdditionalStuff && <th>Prix (25/50 cl)</th>}
              {this.state.isDisplayingAdditionalStuff && <th>Prix achat (L)</th>}
              {this.state.isDisplayingAdditionalStuff && <th>Prix min</th>}
              {this.state.isDisplayingAdditionalStuff && <th>Prix vente</th>}
              {this.state.isDisplayingAdditionalStuff && <th>Prix alc. / cL</th>}
              {this.state.isDisplayingAdditionalStuff && <th>Prix min</th>}
              {this.state.isDisplayingAdditionalStuff && <th>Prix vente</th>}
              {this.state.isDisplayingAdditionalStuff && <th>Prix alc. par cL</th>}
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

                  {this.state.isDisplayingAdditionalStuff && <td>{item.tapBuyingPricePerLiter}</td>}

                  {this.state.isDisplayingAdditionalStuff && <td>{item.tapBuyingPricePerLiter*0.25*3}</td>}
                  {this.state.isDisplayingAdditionalStuff && <td>{item.tapPriceSmall}</td>}
                  {this.state.isDisplayingAdditionalStuff && <td>{(item.tapPriceSmall*100) / (25*item.abv)}</td>}

                  {this.state.isDisplayingAdditionalStuff && <td>{item.tapBuyingPricePerLiter*0.5*3}</td>}
                  {this.state.isDisplayingAdditionalStuff && <td>{item.tapPriceBig}</td>}
                  {this.state.isDisplayingAdditionalStuff && <td>{(item.tapPriceBig*100) / (50*item.abv)}</td>}
              </tr>
            )}
          </tbody>
        </Table>
      </div>
    );

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
