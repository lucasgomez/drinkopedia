import React, { Component } from 'react';
import {
  Table,
  Row, Col, Card, Container,
  Tooltip,
  OverlayTrigger
} from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { API_ROOT } from '../data/apiConfig';


class TapBeersDisplay extends Component {

  constructor(props: any) {
    super(props);

    this.state = {
      items: [],
      title: "",
      description: "",
      isLoading: false
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
    this.setState({
      isLoading: true
    });

    let listUrl = `${API_ROOT}/public/beers/`;
    if (listName && listId)
      listUrl += listName + '/' + listId;

    fetch(listUrl)
      .then(response => response.json())
      .then(list =>
        this.setState({
          items: list.beers,
          title: list.name,
          isLoading: false
        })
      );
  }

  renderProducer = (beer) => {
    let producerString = beer.producerName
    if (beer.producerOriginName)
      producerString += " ("+beer.producerOriginName+")"

    return producerString
  }

  createBeerCard = (beer, isPair) => {
    let bgColor=isPair?"":"dark"
    let textColor=isPair?"":"light"
    return (
      <Col>
        <Card body bg={bgColor} text={textColor}>
          <h4 class="card-title">{beer.name}<div class="float-right"><h5>{beer.tapPriceSmall}.- / {beer.tapPriceBig}.-</h5></div></h4>
          <h7 class="card-subtitle mb-2 text-muted">{this.renderProducer(beer)}</h7>
          <div>
            {beer.abv}% <i>{beer.styleName}</i> ({beer.colorName})
            <h4 class="float-sm-right">🌟</h4>
          </div>
        </Card>
      </Col>
    );
  }

  createTable = (items, rowLength) => {
    let table = []
    debugger

    for (let rowId = 0; rowId < (items.length/rowLength); rowId++) {
      let children = []

      for (let colId = 0; colId < rowLength; colId++) {
        let isEven = (colId+rowId)%2==0
        let cellId = (rowId*rowLength)+colId

        if (cellId<items.length)
          children.push(this.createBeerCard(items[cellId], isEven))
        else
          children.push(<Col></Col>)
      }
      table.push(<Row noGutters="true">{children}</Row>)
    }
    return table
  }

  render() {
    const {
      items,
      title,
      isLoading
    } = this.state;

    if (isLoading) {
      return <p > Loading... < /p>;
    }
    return (
      <Container fluid="true">
        <Row>
          <h1 class="col text-center">🍺 Pressions - Liste de prix (0.25 / 0.5 cL) 🍻</h1>
        </Row>

        {this.createTable(items, 4)}

    		<div class="row text-center">
    			<h6 class="col">🌟 - Assortiment fixe</h6>
    			<h6 class="col">🎇 - Nouvellement ajoutée</h6>
    			<h6 class="col">⏳ - Assortiment temporaire</h6>
    			<h6 class="col">⌛ - Bientôt épuisée</h6>
    		</div>
      </Container>
    );
  }
}

export default TapBeersDisplay;
