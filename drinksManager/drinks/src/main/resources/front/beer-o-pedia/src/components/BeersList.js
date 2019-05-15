import React, { Component } from 'react';
import { Table, Tooltip, Button, OverlayTrigger,
  DropdownButton, Dropdown, ButtonGroup } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import Popup from 'reactjs-popup'
import Emoji from './Emoji';
import axios from 'axios';

class BeersList extends Component {

  constructor(props: any) {
    super(props);
    this.state = {
      items: [],
      title: "",
      description: "",
      searchString: null,
      isLoading: false,
      isDisplayingAdditionalStuff: false,
    };

    this.toggleIsDisplayingAdditionalStuff = this.toggleIsDisplayingAdditionalStuff.bind(this);
    this.setAvailability = this.setAvailability.bind(this);
  }

  toggleIsDisplayingAdditionalStuff() {
    this.setState(state => ({
      isDisplayingAdditionalStuff: !state.isDisplayingAdditionalStuff
    }));
  }

  submitAvailability(beer, oldStatus, newStatus, service) {
    if (window.confirm('Etes-vous sur de vouloir changer le status de "'+beer.name+'" de "'+oldStatus+'" à "'+newStatus+'"?'))
      this.setAvailability(beer.id, newStatus, service);
  }

  setAvailability(beerId, status, service) {
    let postBottleAvailabilityUrl = `/private/beers/` + beerId + '/'+service+'/availability';

    var self = this;
    axios.put(
      postBottleAvailabilityUrl,
      status,
      { withCredentials: true,
        headers: {"Content-Type": "text/plain"}})
    .then(function (response){
       self.fetchData(self.props.listName, self.props.listId);
     }).catch(function (error) {
       console.log(error);
       alert("Ebriété assumée, erreur assurée bis");
     }
    );
  }

  componentDidMount() {
    if (this.props.searchString)
      this.search(this.props.searchString);
    else
      this.fetchData(this.props.listName, this.props.listId);
  }

  componentWillReceiveProps(nextProps) {
    let oldListId = this.props.listId;
    let newListId = nextProps.listId;
    let oldListName = this.props.listName;
    let newListName = nextProps.listName;
    let oldSearchString = this.props.searchString;
    let newSearchString = nextProps.searchString;

    if (newSearchString) {
      if (oldSearchString !== newSearchString) {
        this.search(newSearchString);
      }
    } else if (oldListId !== newListId || oldListName !== newListName) {
         this.fetchData(newListName, newListId);
    }
  }

  fetchData = async (listName, listId) => {
    this.setState({isLoading: true});

    let listUrl = this.props.isAuthenticated ? '/private' : '/public';
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
          isLoading: false
        })
      );
  }

  search = async (searchString) => {
    debugger;
    this.setState({isLoading: true});

    axios.get(
      '/public/beers/search', {
        params: {
          searchedText: searchString
        },
        withCredentials: true,
        headers: {"Content-Type": "text/plain"}
      })
    .then(response => response.data)
    .then(list =>
      this.setState({
        items: list.beers,
        title: 'Résultat de la recherche',
        description: 'Boissons contenant quelque part "'+searchString+'"',
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
    } = this.state;
    if (isLoading) {
      return <p > Loading... < /p>;
    }
    //TODO Better tooltip formating with bar names or multiline prices + tooltip for bars
    return (
      <div className="container">
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
            <tr>
              <th>Bière</th>
              <th>Brasserie</th>
              <th>Origine</th>
              <th>Alc. (%)</th>
              <th>Type</th>
              <th>Couleur</th>
              {!this.state.isDisplayingAdditionalStuff && <th>Prix (25/50 cl)</th>}

              {this.state.isDisplayingAdditionalStuff && <th>Prix achat (L)</th>}

              {this.state.isDisplayingAdditionalStuff && <th>Pression 25 cl</th>}
              {this.state.isDisplayingAdditionalStuff && <th>Pression 50 cl</th>}
              {this.state.isDisplayingAdditionalStuff && <th>Bouteille</th>}

              {this.props.isAuthenticated && <th>GodMode</th>}
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
                </td>

                {!this.state.isDisplayingAdditionalStuff && <td>{this.formatPricesList(item)}</td>}

                {this.state.isDisplayingAdditionalStuff && <td>{this.formatPrice(item.tapBuyingPricePerLiter)}</td>}
                {this.state.isDisplayingAdditionalStuff && <td>{this.formatTapPriceCalculation(item.tapBuyingPricePerLiter, 25, item.tapPriceSmall, item.abv)}</td>}
                {this.state.isDisplayingAdditionalStuff && <td>{this.formatTapPriceCalculation(item.tapBuyingPricePerLiter, 50, item.tapPriceSmall, item.abv)}</td>}
                {this.state.isDisplayingAdditionalStuff && <td>{this.formatBottlePriceCalculation(item.bottleBuyingPrice, item.bottleVolumeInCl, item.bottleSellingPrice, item.abv)}</td>}

                {this.props.isAuthenticated &&
                  <td>
                    {this.displayActionButtons(item)}
                  </td>
                }
              </tr>
            )}
          </tbody>
        </Table>
      </div>
    );

  }

  displayActionButtons(beer) {
    var availabilities = [];
    availabilities.push({status : 'NOT_YET_AVAILABLE', label: 'Pas encore en service'});
    availabilities.push({status : 'AVAILABLE', label: 'En service'});
    availabilities.push({status : 'NEARLY_OUT_OF_STOCK', label: 'Bientôt épuisé'});
    availabilities.push({status : 'OUT_OF_STOCK', label: 'Epuisé'});

    let tapAvailability = beer.tapPriceSmall && (
      <Dropdown.Item key={'actionButtonsList-'+beer.id+'-tap-statuses'}>
        <Dropdown.Header>Pression</Dropdown.Header>
        {availabilities.map((availability: any) =>
          beer.tapAvailability != availability.status &&
            <Dropdown.Item
              onClick={() => this.submitAvailability(beer, beer.tapAvailability, availability.status, 'tap')}
              key={'actionButtonsList-'+beer.id+'-tap-statuses-'+availability.status}>
                {availability.label}
            </Dropdown.Item>
        )}
      </Dropdown.Item>
    );
    let bottleAvailability = beer.bottleSellingPrice && (
      <Dropdown.Item key={'actionButtonsList-'+beer.id+'-bottle-statuses'}>
        <Dropdown.Header>Bouteille</Dropdown.Header>
        {availabilities.map((availability: any) =>
          beer.bottleAvailability != availability.status &&
            <Dropdown.Item
              onClick={() => this.submitAvailability(beer, beer.bottleAvailability, availability.status, 'bottle')}
              key={'actionButtonsList-'+beer.id+'-tap-statuses-'+availability.status}>
                {availability.label}
            </Dropdown.Item>
        )}
      </Dropdown.Item>
    );

    return (
      <Dropdown as={ButtonGroup}>
        <Button><Link to={'/edit/beer/'+beer.id}>✏</Link></Button>
        <Dropdown.Toggle split id="dropdown-split-basic" />
        <Dropdown.Menu>
          {tapAvailability}
          {tapAvailability && bottleAvailability && <Dropdown.Divider/>}
          {bottleAvailability}
        </Dropdown.Menu>
      </Dropdown>
    );
  }

  formatPrice(price) {
      if (!price)
        return null;
      else
        return price.toFixed(2) + ".-";
  }

  formatBottlePriceCalculation(buyingPrice, volumeInCl, sellingPrice, abv) {
    return (
      <td>
        Achat : {this.formatPrice(buyingPrice)} {volumeInCl && '('+volumeInCl+' cL)'} (Min vente : {this.displayBottleMinPrice(buyingPrice)})<br/>
        Vente : {this.formatPrice(sellingPrice)} (prix 1cl alc. : {this.displayAlcoolPrice(sellingPrice, volumeInCl, abv)})
      </td>
    );
  }

  formatTapPriceCalculation(buyingPrice, volumeInCl, sellingPrice, abv) {
    return (
      <td>
        Achat : {this.formatPrice(buyingPrice*volumeInCl/100)} (Min vente : {this.displayTapMinPrice(buyingPrice, volumeInCl)})<br/>
        Vente : {this.formatPrice(sellingPrice)} (prix 1cl alc. : {this.displayAlcoolPrice(sellingPrice, volumeInCl, abv)})
      </td>
    );
  }

  displayTapMinPrice(buyingPrice, volumeInCl) {
    return this.formatPrice(buyingPrice*volumeInCl*3/100);
  }

  displayBottleMinPrice(buyingPrice) {
    return this.formatPrice(buyingPrice*2.3);
  }

  displayAlcoolPrice(sellingPrice, volumeInCl, abv) {
    if (!sellingPrice || !volumeInCl || !abv)
      return null;
    else
      return this.formatPrice((sellingPrice*100) / (volumeInCl*abv));
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
