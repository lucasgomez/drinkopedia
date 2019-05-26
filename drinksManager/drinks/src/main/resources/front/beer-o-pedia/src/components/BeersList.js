import React, { Component } from 'react';
import { Button } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import Emoji from './Emoji';
import ModalAvailabilityEditor from './edit/ModalAvailabilityEditor';
import axios from 'axios';
import ReactTable from 'react-table';
import 'react-table/react-table.css';
import { API_ROOT } from '../data/apiConfig';

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
    this.showModal = this.showModal.bind(this);
    this.temp = this.temp.bind(this);
  }

  toggleIsDisplayingAdditionalStuff() {
    this.setState(state => ({
      isDisplayingAdditionalStuff: !state.isDisplayingAdditionalStuff
    }));
  }

  temp() {
    let postBottleAvailabilityUrl = `${API_ROOT}/public/beers/test`;

    var self = this;
    axios.put(
      postBottleAvailabilityUrl,
      'Woot',
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

  showModal() {
    this.setState({showModal: true});
  }

  fetchData = async (listName, listId) => {
    this.setState({isLoading: true});

    let listUrl = `${API_ROOT}` + (this.props.isAuthenticated ? '/private' : '/public');
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
    this.setState({isLoading: true});

    axios.get(
      `${API_ROOT}/public/beers/search`,
      {
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

    const isAuthenticated = this.props.isAuthenticated;
    const hasTap = items.some(beer => beer.tapPriceSmall && beer.tapPriceBig);
    const hasBottle = items.some(beer => beer.bottleSellingPrice && beer.bottleVolumeInCl);

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

        <ReactTable
          data={items}
          columns={[
            {
              Header: 'Bière',
              columns: [
              {
                Header: 'Nom',
                accessor: 'name',
                Cell: row => <Link to={'/beerid/'+row.original.id}>{row.original.name}</Link>,
                sortable: true,
              },{
                Header: 'Alc. (%)',
                accessor: 'abv',
                sortable: true,
              },{
                Header: 'Type',
                accessor: 'styleName',
                Cell: row => <Link to={'/list/styles/'+row.original.styleId}>{row.original.styleName}</Link>,
                sortable: true,
              },{
                Header: 'Couleur',
                accessor: 'colorName',
                Cell: row => <Link to={'/list/colors/'+row.original.colorId}>{row.original.colorName}</Link>,
                sortable: true,
              }]
            },{
              Header: 'Brasserie',
              columns: [
              {
                Header: 'Nom',
                accessor: 'producerName',
                Cell: row => <Link to={'/list/producers/'+row.original.producerId}>{row.original.producerName}</Link>,
                sortable: true,
              },{
                Header: 'Origine',
                accessor: 'producerOriginShortName',
                Cell: row => <Link to={'/list/origins/'+row.original.producerOriginId}>{row.original.producerOriginShortName}</Link>,
                sortable: true,
              }]
            },{
              Header: 'Bouteille',
              columns: [
              {
                Header: 'Vol.',
                accessor: 'bottleVolumeInCl',
                Cell: row => row.value ? row.value+' cl' : '',
                sortable: true,
                show: hasBottle,
              },{
                Header: 'Prix',
                accessor: 'bottleSellingPrice',
                Cell: row => this.formatPrice(row.value),
                sortable: true,
                show: hasBottle,
              },{
                Header: 'Dispo.',
                accessor: 'bottleAvailability',
                Cell: row => this.formatAvailability(row.value),
                sortable: true,
                show: hasBottle,
              }]
            },{
              Header: 'Pression',
              columns: [
              {
                Header: '25cl',
                accessor: 'tapPriceSmall',
                Cell: row => this.formatPrice(row.value),
                sortable: true,
                show: hasTap,
              },{
                Header: '50cl',
                accessor: 'tapPriceBig',
                Cell: row => this.formatPrice(row.value),
                sortable: true,
                show: hasTap,
              },{
                Header: 'Dispo.',
                accessor: 'tapAvailability',
                Cell: row => this.formatAvailability(row.value),
                sortable: true,
                show: hasTap,
              }]
            },{
              Header: 'GodMode',
              Cell: row => <Button onClick={() => this.setState({beerToUpdate: row.original})}><Emoji symbol="✏" label="Edition"/></Button>,
              show: isAuthenticated === true,
            }
          ]}
          className="-striped -highlight"
        />

        <ModalAvailabilityEditor beerToUpdate={this.state.beerToUpdate}/>
        <Button className="float-right" onClick={this.temp}>Mu</Button>

      </div>
    );

  }

  formatPrice(price) {
      if (!price)
        return null;
      else
        return price.toFixed(2) + ".-";
  }

  formatAvailability(availability) {
    switch (availability) {
      case "NOT_YET_AVAILABLE":
        return <Emoji symbol="🕑" label="Pas encore disponnible"/>;
      case "NEARLY_OUT_OF_STOCK":
      case "AVAILABLE":
        return <Emoji symbol="✅" label="Disponnible"/>;
      case "OUT_OF_STOCK":
        return <Emoji symbol="❌" label="Epuisée"/>;
      default:
        return null;
    }
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
}

export default BeersList;
